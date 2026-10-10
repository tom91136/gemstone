package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Lowers decoded SASS with `VMCompiler` and runs it on the NVIDIA interpreter against a local address space, without a
  * cubin image or backend. Parameters are laid out from the launch arguments with natural alignment.
  */
object Harness extends munit.Assertions {
  def operand(text: String)(using munit.Location): Operand       = OperandParser.parse(text).value
  def operands(text: String)(using munit.Location): Vec[Operand] = OperandParser.parseMany(text).value

  object Arg {
    def u32(value: Long): Arg     = Scalar(LittleEndian.toVec(value & 0xffffffffL, 4))
    def u64(value: Long): Arg     = Scalar(LittleEndian.toVec(value, 8))
    def f32(value: Float): Arg    = u32(java.lang.Float.floatToRawIntBits(value).toLong)
    def zeros(size: Int): Arg     = Buffer(Vec.fill(size)(0.toByte))
    def words(values: Long*): Arg = Buffer(Harness.words(Vec.from(values)))
  }

  enum Arg {
    case Scalar(bytes: Vec[Byte])
    case Buffer(bytes: Vec[Byte], offset: Long >= 0 = 0L)
    case Region(id: AllocationId, offset: Long >= 0 = 0L)

    def size: Int = this match {
      case Scalar(bytes) => bytes.size
      case _             => 8
    }
  }

  def words(values: Vec[Long], width: Int = 4): Vec[Byte] =
    values.flatMap(LittleEndian.toVec(_, width.unsafe[Int in 0 ~ 8]))

  val fullLayout: VMRegisters.Layout = VMRegisters.Layout(255, 255)

  def lowerStatements(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      boundSymbols: Map[NonEmpty[String], GuestAddress] = Map.empty
  ): Either[NonEmpty[String], Vec[VMAssembler.Statement]] =
    VMCompiler.lowerKernel(module, kernel, functions, boundSymbols).map(_._1)

  def parameterBase(sm: Int): Int = if (sm < 70) 0x140 else if (sm < 90) 0x160 else if (sm < 100) 0x210 else 0x380

  case class Entry(
      name: NonEmpty[String],
      parameterBase: Int >= 0,
      parameters: Vec[Int > 0],
      registers: Int = 0,
      shared: Int >= 0 = 0
  ) {
    def offsets: Vec[Int >= 0] = parameters.foldLeft(Vec.empty[Int >= 0]) { (offsets, size) =>
      val end = offsets.lastOption.fold(0)(offset => offset.raw + parameters(offsets.size - 1).raw)
      offsets :+ ((end + size.raw - 1) / size.raw * size.raw).unsafe[Int >= 0]
    }
  }

  def module(
      sm: Int,
      entries: Vec[Entry],
      constants: Vec[(String, Vec[Byte])] = Vec.empty,
      globals: Vec[Cubin.Global] = Vec.empty
  )(using munit.Location): Cubin.Module = {
    val layouts = entries.map { entry =>
      val parameters = entry.offsets.zip(entry.parameters).zip(Vec.indices(entry.parameters.nonNegativeSize)).map {
        case ((offset, size), ordinal) => Cubin.Parameter(ordinal, offset, size)
      }
      val size = parameters.foldLeft[Int >= 0](0)((size, parameter) => size.max(parameter.offset + parameter.size))
      (entry, parameters, size)
    }
    val sections = layouts.foldLeft(Vec.empty[Cubin.Section]) { case (sections, (entry, _, size)) =>
      val text =
        sections :+
          Cubin.Section(sections.nonNegativeSize, s".text.${entry.name}", 1, 0L, 0, 0, entry.registers << 24, Vec.empty)
      val shared = if (entry.shared.raw > 0)
        text :+ Cubin.Section(text.nonNegativeSize, s".nv.shared.${entry.name}", 8, 3L, entry.shared, 0, 0, Vec.empty)
      else text
      val bank = entry.parameterBase + size
      if (bank.raw > 0)
        shared :+ Cubin.Section(
          shared.nonNegativeSize,
          s".nv.constant0.${entry.name}",
          1,
          2L,
          bank,
          0,
          0,
          Vec.fill(bank.raw)(0.toByte)
        )
      else shared
    }
    val kernels = layouts.map { (entry, parameters, size) =>
      val text =
        sections.indexWhere(_.name == s".text.${entry.name}").refine[Int >= 0].fold(error => fail(error.raw), identity)
      Cubin.Kernel(entry.name, text, entry.parameterBase, size, parameters)
    }
    val banks = constants.zip(Vec.indices(constants.nonNegativeSize)).map { case ((name, bytes), index) =>
      Cubin.Section(sections.nonNegativeSize + index, name, 1, 2L, bytes.nonNegativeSize, 0, 0, bytes)
    }
    Cubin.Module(sm.refine[Int >= 35].fold(error => fail(error.raw), identity), sections ++ banks, kernels, globals)
  }

  case class Kernel(
      sm: Int,
      name: NonEmpty[String],
      functions: Vec[DecodedFunction],
      registers: Int = 0,
      shared: Int >= 0 = 0,
      base: Int >= 0 = 0x160,
      constants: Vec[(String, Vec[Byte])] = Vec.empty,
      globals: Vec[Cubin.Global] = Vec.empty
  ) {
    def function: DecodedFunction = functions.head

    def map(f: DecodedInstruction => DecodedInstruction): Kernel =
      copy(functions = functions.map(function => function.copy(instructions = function.instructions.map(f))))

    def module(parameters: Vec[Int])(using munit.Location): Cubin.Module = Harness.module(
      sm,
      Vec(Entry(
        name,
        base,
        parameters.map(_.refine[Int > 0].fold(error => fail(error.raw), identity)),
        registers,
        shared
      )),
      constants,
      globals
    )

    def lower(parameters: Int*)(using munit.Location): Either[String, Vec[VMAssembler.Statement]] = {
      val image = module(Vec.from(parameters))
      lowerStatements(image, image.kernels.head, functions)
    }

    def instructions(parameters: Int*)(using munit.Location): Vec[VMInstruction] =
      lower(parameters*).value.collect { case VMAssembler.Statement.Execute(instruction, _) => instruction }

    def compile(parameters: Int*)(using munit.Location): Either[String, VMIR.Kernel] = {
      val image = module(Vec.from(parameters))
      VMCompiler.compile(image, image.kernels.head, functions)
    }

    def run(
        block: Int | LaunchGeometry.Dimensions,
        grid: Int | LaunchGeometry.Dimensions = 1,
        memory: Memory = Memory()
    )(
        args: Arg*
    )(using munit.Location): Either[Fault, Run] = {
      val regions = Vec.from(args).map {
        case Arg.Buffer(bytes, offset) => Some(memory.allocate(if (bytes.isEmpty) Vec(0.toByte) else bytes) -> offset)
        case Arg.Region(id, offset)    => Some(id -> offset)
        case Arg.Scalar(_)             => None
      }
      val arguments = Vec.from(args).zip(regions).map {
        case (_, Some((region, offset))) => KernelTask.Argument.Memory(region, offset)
        case (Arg.Scalar(bytes), None)   => KernelTask.Argument.Scalar(bytes.refine[NonEmpty[Vec[Byte]]].value)
        case (other, None)               => fail(s"unallocated argument $other")
      }
      for {
        program <- compile(args.map(_.size)*).left.map(message => Fault.Unsupported(message.unsafe[NonEmpty[String]]))
        task    <-
          KernelTask.validated(name, dimensions(block), dimensions(grid), arguments).left.map(Fault.InvalidRequest(_))
        _ <- memory.run(program, task)
      } yield Run(memory, regions.map(_.map(_._1)))
    }

    def launch(
        block: Int | LaunchGeometry.Dimensions,
        grid: Int | LaunchGeometry.Dimensions = 1,
        memory: Memory = Memory()
    )(
        args: Arg*
    )(using munit.Location): Run = run(block, grid, memory)(args*).fold(error => fail(error.message), identity)
  }

  case class Run(memory: Memory, buffers: Vec[Option[AllocationId]]) {
    def bytes(argument: Int): Vec[Byte] =
      memory.bytes(buffers(argument).getOrElse(fail(s"argument $argument is a scalar")))

    def words(argument: Int, width: Int = 4): Vec[Long] =
      bytes(argument).grouped(width).filter(_.size == width).map(LittleEndian.decode(
        _,
        0,
        width.unsafe[Int in 0 ~ 8]
      )).toVec
  }

  private def dimensions(value: Int | LaunchGeometry.Dimensions)(using munit.Location): LaunchGeometry.Dimensions =
    value match {
      case dimensions: LaunchGeometry.Dimensions => dimensions
      case x: Int => LaunchGeometry.Dimensions(x.refine[Int > 0].fold(error => fail(error.raw), identity), 1, 1)
    }

  /** Registers come from the kernel's `SHI_REGISTERS` and the parameter base from the architecture. */
  def load(sm: Int, listing: String, kernel: String, shared: Int >= 0 = 0)(using munit.Location): Kernel = {
    val section   = listing.linesIterator.dropWhile(!_.contains(s".text.$kernel ")).take(4).mkString("\n")
    val registers = """SHI_REGISTERS=(\d+)""".r.findFirstMatchIn(section).fold(0)(_.group(1).toInt)
    Kernel(
      sm,
      kernel.refine[NonEmpty[String]].value,
      Vec(Listing.function(listing, kernel, sm).value),
      registers,
      shared,
      parameterBase(sm).unsafe[Int >= 0]
    )
  }

  /** Address-free SASS text; parameters start at the sm_70 bank offset `c[0x0][0x160]` unless `base` moves them. */
  def program(sm: Int, text: String, shared: Int >= 0 = 0, base: Int >= 0 = 0x160)(using munit.Location): Kernel =
    Kernel(sm, "kernel", Vec(Listing.program(text).value), shared = shared, base = base)

  /** Runs `text`, by default with one thread per expected word. Parameters mirror the SAXPY signature: two pointers to
    * the output, a zero word and the element count, so kernels store through `c[0x0][0x160]`.
    */
  def check(
      sm: Int,
      text: String,
      expected: Vec[Int],
      grid: Int | LaunchGeometry.Dimensions = 1,
      block: Option[Int] = None,
      shared: Int >= 0 = 0,
      rewrite: DecodedInstruction => DecodedInstruction = identity
  )(using munit.Location): Unit = {
    val kernel = program(sm, text, shared).map(rewrite)
    val memory = Memory()
    val output = memory.allocate(Vec.fill(expected.size.max(1) * 4)(0.toByte))
    val task   = KernelTask.validated(
      kernel.name,
      dimensions(block.getOrElse(expected.size)),
      dimensions(grid),
      Vec(
        KernelTask.Argument.Memory(output, 0L),
        KernelTask.Argument.Memory(output, 0L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4).refine[NonEmpty[Vec[Byte]]].value),
        KernelTask.Argument.Scalar(LittleEndian.toVec(expected.size.toLong, 4).refine[NonEmpty[Vec[Byte]]].value)
      )
    ).value
    val compiled = kernel.compile(8, 8, 4, 4).fold(fail(_), identity)
    memory.run(compiled, task).fold(error => fail(error.message), identity)
    assertEquals(memory.readWords(output, expected.nonNegativeSize).map(_.toInt), expected)
  }

  class Memory extends RegionMemory() {
    def readWords(region: AllocationId, count: Int >= 0, width: Int in 1 ~ 8 = 4)(using munit.Location): Vec[Long] =
      Vec.indices(count).map(index => readValue(region, index.toLong.widen[Long >= 0] * width.toLong, width).value)

    def run(program: VMIR.Kernel, task: KernelTask): Either[Fault, Unit] =
      VMKernelLaunch.execute(program, task, this, TaskControl(), Workgroups.Sequential)
  }
}
