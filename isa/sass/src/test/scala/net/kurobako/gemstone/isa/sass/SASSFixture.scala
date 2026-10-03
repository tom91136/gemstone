package net.kurobako.gemstone.isa.sass

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.ptx.v9_3 as PTX
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Small in-process harness for SASS semantics tests. It compiles decoded instructions directly and runs the NVIDIA
  * interpreter against a local address space; no server backend or serialized test-program registry is involved.
  */
object SASSFixture extends munit.Assertions {
  def operand(source: String)(using munit.Location): Operand = SASSOperandParser.parse(source).fold(fail(_), identity)
  def operands(source: String)(using munit.Location): Vec[Operand] =
    SASSOperandParser.parseMany(source).fold(fail(_), identity)

  def instruction(
      address: Int,
      opcode: String,
      operands: Vec[String],
      predicate: Option[String] = None,
      encodingWord: Option[Long] = None
  ): DecodedInstruction = {
    val guard     = predicate.fold("")(value => s"$value ")
    val args      = if (operands.isEmpty) "" else s" ${operands.mkString(", ")}"
    val operation = Parser.parse(s"$guard$opcode$args;").fold(
      error => throw IllegalArgumentException(s"$opcode $args: $error"),
      identity
    )
    val checkedAddress = address.refine[Int >= 0].fold(error => throw IllegalArgumentException(error.raw), identity)
    DecodedInstruction(
      checkedAddress,
      operation,
      encodingWord.map(value => BigInt(java.lang.Long.toUnsignedString(value))),
      None
    )
  }
  case class Kernel(
      name: NonEmpty[String],
      parameterBase: Int >= 0,
      parameters: Vec[(Int >= 0, Int > 0)],
      registers: Int = 0,
      shared: Int >= 0 = 0
  )

  def module(
      sm: Int,
      kernels: Vec[Kernel],
      constants: Vec[(String, Vec[Byte])] = Vec.empty,
      globals: Vec[Cubin.Global] = Vec.empty
  ): Cubin.Module = {
    val metadata = kernels.map { kernel =>
      val parameters = kernel.parameters.zip(Vec.indices(kernel.parameters.nonNegativeSize)).map {
        case ((offset, size), ordinal) =>
          Cubin.Parameter(ordinal, offset, size)
      }
      val parameterSize = kernel.parameters.foldLeft[Int >= 0](0) { case (size, (offset, width)) =>
        size.max(offset + width)
      }
      (kernel, parameters, parameterSize)
    }
    val generated = metadata.foldLeft(Vec.empty[Cubin.Section]) { case (sections, (kernel, _, parameterSize)) =>
      val text =
        Cubin.Section(sections.nonNegativeSize, s".text.${kernel.name}", 1, 0L, 0, 0, kernel.registers << 24, Vec.empty)
      val withText   = sections :+ text
      val withShared = if (kernel.shared.raw > 0)
        withText :+
          Cubin.Section(withText.nonNegativeSize, s".nv.shared.${kernel.name}", 8, 3L, kernel.shared, 0, 0, Vec.empty)
      else withText
      val constantSize = kernel.parameterBase + parameterSize
      if (constantSize.raw > 0)
        withShared :+ Cubin.Section(
          withShared.nonNegativeSize,
          s".nv.constant0.${kernel.name}",
          1,
          2L,
          constantSize,
          0,
          0,
          Vec.fill(constantSize.raw)(0.toByte)
        )
      else withShared
    }
    val entries = metadata.map { case (kernel, parameters, parameterSize) =>
      val textIndex = generated.indexWhere(_.name == s".text.${kernel.name}").refine[Int >= 0].fold(
        error => fail(error.raw),
        identity
      )
      Cubin.Kernel(kernel.name, textIndex, kernel.parameterBase, parameterSize, parameters)
    }
    val constantSections = constants.zip(Vec.indices(constants.nonNegativeSize)).map { case ((name, bytes), index) =>
      Cubin.Section(generated.nonNegativeSize + index, name, 1, 2L, bytes.nonNegativeSize, 0, 0, bytes)
    }
    Cubin.Module(
      sm.refine[Int >= 35].fold(error => fail(error.raw), identity),
      generated ++ constantSections,
      entries,
      globals
    )
  }

  def lower(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction]
  ): Either[String, SASSFixture] = {
    val fixture = SASSFixture(module, kernel, functions)
    fixture.lowered.map(_ => fixture)
  }

  def task(
      kernelName: NonEmpty[String],
      groupSize: LaunchGeometry.Dimensions,
      groupCount: LaunchGeometry.Dimensions,
      arguments: Vec[KernelTask.Argument]
  )(using munit.Location): KernelTask =
    KernelTask.validated(kernelName, groupSize, groupCount, arguments).fold(fail(_), identity)

  class Execution extends AddressSpace {
    private case class Region(bytes: Array[Byte], base: Long)
    private val regions    = mutable.Map.empty[AllocationId, Region]
    private val atomicLock = ReentrantLock()

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextId = AllocationId(1)

    def allocate(size: Long > 0)(using munit.Location): AllocationId =
      registerAllocation(size, AllocationKind.Shared).fold(error => fail(error.message), identity)

    def allocate(bytes: Vec[Byte])(using munit.Location): AllocationId = {
      val size = bytes.size.toLong.refine[Long > 0].fold(error => fail(error.toString), identity)
      val id   = allocate(size)
      write(id, 0L, bytes).fold(error => fail(error.message), identity)
      id
    }

    def readWords(region: AllocationId, count: Int >= 0, width: Int in 1 ~ 8 = 4)(using
        munit.Location
    ): Vec[Long] =
      Vec.indices(count).map(index =>
        readValue(
          region,
          index.toLong.widen[Long >= 0] * width.toLong,
          width
        ).fold(error => fail(error.message), identity)
      )

    def registerAllocation(
        size: Long > 0,
        @scala.annotation.unused kind: AllocationKind
    ): Either[ExecutionFailure, AllocationId] =
      if (size.raw > Int.MaxValue) Left(ExecutionFailure.InvalidRequest("test allocation exceeds Int range"))
      else {
        val id = nextId
        nextId = nextId.next
        // Keep host allocations above the VM's local and shared address apertures.
        regions.update(id, Region(Array.ofDim[Byte](size.raw.toInt), 0x100000000000L + id.toLong * 0x100000000L))
        Right(id)
      }

    def registerAllocationAddress(id: AllocationId, address: GuestAddress): Either[ExecutionFailure, Unit] =
      regions.get(id).toRight(ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")).map { region =>
        regions.update(id, region.copy(base = address.raw))
      }

    def compile(fixture: SASSFixture): Either[ExecutionFailure, LoadedProgram.Compiled] =
      LoadedProgram.compile(fixture.module, fixture.kernel, fixture.functions)
        .left.map(fault => ExecutionFailure.Unsupported(fault.message))

    def run(program: LoadedProgram.Compiled, task: KernelTask): Either[ExecutionFailure, Unit] =
      LoadedProgram.execute(program, task, this, TaskControl(), Workgroups.Sequential)
        .left.map(ExecutionFailure.fromFault(_))

    def runPtx(source: String, task: KernelTask): Either[ExecutionFailure, Unit] =
      for {
        program <- PTX.Parser.decode(source)
          .left.map(error => ExecutionFailure.Unsupported(error.message))
        loaded <- PTX.LoadedProgram.load(program)
          .left.map(error => ExecutionFailure.Unsupported(error.message))
        _ <- loaded.execute(task, this, TaskControl(), Workgroups.Sequential)
          .left.map(ExecutionFailure.fromFault(_))
      } yield ()

    override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      regions.get(region).toRight(Fault.InvalidTask(s"unknown allocation ${region.toLong}")).flatMap { value =>
        Either.cond(
          offset.raw <= value.bytes.length,
          GuestAddress(value.base + offset.raw),
          Fault.InvalidTask("allocation offset out of range")
        )
      }

    override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      regions.view.collectFirst {
        case (id, value)
            if address.raw >= value.base && address.raw - value.base <= value.bytes.length &&
              size.raw <= value.bytes.length - (address.raw - value.base) =>
          AddressSpace.Location.Within(id, (address.raw - value.base).unsafe[Long >= 0])
      }.toRight(Fault.Memory(s"address ${address.show} is outside test allocations"))

    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      regions.get(region).toRight(Fault.InvalidTask(s"unknown allocation ${region.toLong}")).flatMap { value =>
        Either.cond(
          offset.raw <= value.bytes.length && size.raw <= value.bytes.length - offset.raw,
          Vec.from(value.bytes.slice(offset.raw.toInt, offset.raw.toInt + size.raw)),
          Fault.Memory("test allocation read is out of bounds")
        )
      }

    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      regions.get(region).toRight(Fault.InvalidTask(s"unknown allocation ${region.toLong}")).flatMap { value =>
        Either.cond(
          offset.raw <= value.bytes.length && bytes.size <= value.bytes.length - offset.raw,
          { val _ = bytes.copyToArray(value.bytes, offset.raw.toInt); () },
          Fault.Memory("test allocation write is out of bounds")
        )
      }

    override def atomicModifyValue(
        region: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
    )(update: Long => Long): Either[Fault, Long] = atomicLock.withLock {
      for {
        previous <- readValue(region, offset, size)
        _        <- writeValue(region, offset, update(previous), size)
      } yield previous
    }
  }
}

case class SASSFixture(
    module: Cubin.Module,
    kernel: Cubin.Kernel,
    functions: Vec[DecodedFunction]
) {
  private lazy val lowered: Either[String, Vec[VMAssembler.Statement]] = VMCompiler.lower(module, kernel, functions)
  lazy val statements: Vec[VMAssembler.Statement]                      = lowered.fold(SASSFixture.fail(_), identity)
  def instructions: Vec[VMInstruction]                                 = statements.collect {
    case VMAssembler.Statement.Execute(instruction, _) => instruction
  }
}
