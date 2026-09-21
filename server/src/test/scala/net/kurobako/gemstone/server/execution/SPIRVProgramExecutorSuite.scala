package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NoNul, PlatformTime, Vec}
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.KernelName as ISAKernelName
import net.kurobako.gemstone.isa.spirv.v1_6.Endianness
import net.kurobako.gemstone.isa.spirv.v1_6.Header
import net.kurobako.gemstone.isa.spirv.v1_6.ISA as SPIRV
import net.kurobako.gemstone.isa.spirv.v1_6.Instruction
import net.kurobako.gemstone.isa.spirv.v1_6.Module
import net.kurobako.gemstone.isa.spirv.v1_6.Operand
import net.kurobako.gemstone.isa.spirv.v1_6.SPIRVOperandKind
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.isa.spirv.v1_6.Word
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.{Fault, KernelTask, LaunchGeometry, MemoryRegion, Scheduler}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.tailrec
import scala.util.Using

final class SPIRVProgramExecutorSuite extends munit.FunSuite {
  private val encoding = SPIRVRuntimeBackend(Validation.Environment.OpenCLCompute).encoding

  test("the external SPIR-V load boundary does not treat non-kernel entry points as libraries") {
    val voidType     = operand("IdResult", 1)
    val functionType = operand("IdResult", 2)
    val function     = operand("IdResult", 3)
    val module       = Module(
      Header(SPIRV.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(operand("ExecutionModel", 0), function, stringOperand("vertex"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    Using.resource(ExecutionService(RuntimeBackend.defaults)) { service =>
      service.load(encoding, SPIRV.encode(module).toOption.get) match {
        case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpEntryPoint Kernel"))
        case result => fail(s"expected kernel entry-point validation failure, found $result")
      }
    }
  }

  test("the external SPIR-V load boundary rejects modules outside the OpenCL compute environment") {
    val voidType     = operand("IdResult", 1)
    val functionType = operand("IdResult", 2)
    val function     = operand("IdResult", 3)
    val module       = Module(
      Header(SPIRV.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("invalid_environment"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    Using.resource(ExecutionService(RuntimeBackend.defaults)) { service =>
      service.load(encoding, SPIRV.encode(module).toOption.get) match {
        case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpCapability Kernel"))
        case result => fail(s"expected OpenCL environment validation failure, found $result")
      }
    }
  }

  test("typed SPIR-V executes through the binary program service boundary") {
    val voidType    = operand("IdResult", 1)
    val u32         = operand("IdResult", 2)
    val pointerType = operand("IdResult", 3)
    val function    = operand("IdResult", 5)
    val output      = operand("IdResult", 6)
    val value       = operand("IdResult", 7)
    val module      = Module(
      Header(SPIRV.version, Word(0), Word(16), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 4), voidType, Vec(pointerType, u32)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("store"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 4)),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, value),
        Instruction.Label(operand("IdResult", 8)),
        Instruction.Store(output, value, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val image = SPIRV.encode(module).toOption.get
    Using.resource(ExecutionService(RuntimeBackend.defaults)) { service =>
      val allocation = service.registerAllocation(4L, AllocationKind.Shared).toOption.get
      val program    = service.load(encoding, image).toOption.get
      val task       = KernelTask
        .validated(
          ISAKernelName("store"),
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(MemoryRegion(allocation.value), 0L),
            KernelTask.Argument.Scalar(Vec[Byte](0x78, 0x56, 0x34, 0x12))
          )
        )
        .toOption
        .get

      val completion = service.launch(program, task).toOption.get
      assertEquals(awaitCompletion(service, completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 0L, 4), Right(Vec[Byte](0x78, 0x56, 0x34, 0x12)))
    }
  }

  test("multi-workgroup SPIR-V completion publishes every dirty region on repeated queued launches") {
    import java.util.concurrent.atomic.AtomicReference
    val image = SPIRV.encode(sparseGroupModule).toOption.get
    for {
      groups   <- Vec(1, 2, 3, 5, 9)
      parallel <- Vec(false, true)
    } {
      final class ParallelGroups extends Scheduler {
        override val parallelism                                                                 = 4
        override def shouldParallel(tasks: Long :| Positive0, items: Long :| Positive0): Boolean = tasks > 1L
        override def parallelFor(tasks: Long :| Positive0)(
            task: Long :| Positive0 => Either[Fault, Unit]
        )
            : Either[Fault, Unit] = {
          val failure = AtomicReference(Option.empty[Fault])
          (0L until tasks).grouped(4).foreach { batch =>
            val threads = batch.map(index =>
              Thread(() =>
                try task(index.refineUnsafe[Positive0]).left.foreach(error => failure.set(Some(error)))
                catch {
                  case error: Throwable =>
                    failure.set(Some(Fault.Backend(error.toString)))
                }
              )
            )
            threads.foreach(_.setDaemon(true))
            threads.foreach(_.start()); threads.foreach(_.join(5000L))
            val timedOut = threads.exists(_.isAlive)
            if (timedOut) { threads.foreach(_.interrupt()); threads.foreach(_.join(1000L)) }
            assert(!timedOut && threads.forall(!_.isAlive), "SPIR-V group worker timed out")
          }
          failure.get().toLeft(())
        }
      }
      final class Capturing(delegate: Scheduler, queued: AtomicReference[Option[Runnable]]) extends Scheduler {
        override def parallelism                                                                 = delegate.parallelism
        override def shouldParallel(tasks: Long :| Positive0, items: Long :| Positive0): Boolean =
          delegate.shouldParallel(tasks, items)
        override def parallelFor(tasks: Long :| Positive0)(
            task: Long :| Positive0 => Either[Fault, Unit]
        ): Either[Fault, Unit]                          = delegate.parallelFor(tasks)(task)
        override def schedule(runnable: Runnable): Unit = assert(queued.compareAndSet(None, Some(runnable)))
      }
      val queued    = AtomicReference(Option.empty[Runnable])
      val delegate  = if (parallel) ParallelGroups() else Scheduler.Sequential
      val scheduler = Capturing(delegate, queued)
      Using.resource(ExecutionService(RuntimeBackend.defaults, scheduler = scheduler)) { service =>
        val size           = ((groups + 3) * 65536 + 8).refineUnsafe[Positive]
        val allocationSize = size.toLong.refineUnsafe[Positive]
        val allocation     = service.registerAllocation(allocationSize, AllocationKind.Shared).toOption.get
        val program        = service.load(encoding, image).fold(error => fail(error.message), value => value)
        val host           = Array.ofDim[Byte](size)
        for (seed <- Vec(3L, 7L)) {
          val before     = service.read(allocation, 0L, size.refineUnsafe[Positive0]).toOption.get
          val groupCount = groups.refineEither[Positive]
            .fold(message => fail(message), value => LaunchGeometry.Dimensions(value, 1, 1))
          val task = KernelTask.validated(
            ISAKernelName("groups"),
            LaunchGeometry.Dimensions.One,
            groupCount,
            Vec(
              KernelTask.Argument.Memory(MemoryRegion(allocation.value), 4L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(seed, ScalarByteWidth(4)))
            ),
            globalOffset = LaunchGeometry.Coordinates(2, 0, 0)
          ).toOption.get
          val completion = service.launch(program, task).toOption.get
          assertEquals(service.completion(completion), Right(CompletionStatus.Pending))
          assert(service.completionDirtyRanges(completion).isLeft)
          assertEquals(service.read(allocation, 0L, size.refineUnsafe[Positive0]), Right(before))
          queued.getAndSet(None).get.run()
          assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
          val ranges = service.completionDirtyRanges(completion).toOption.get
          assertEquals(service.completionDirtyRanges(completion), Right(ranges))
          ranges.foreach { range =>
            assertEquals(range.allocation, allocation)
            assert(range.offset >= 0 && range.offset + range.size <= size)
            val bytes = service.read(allocation, range.offset, range.size.refineUnsafe[Positive0]).toOption.get
            bytes.copyToArray(host, range.offset.toInt, range.size)
          }
          val expected = Array.ofDim[Byte](size)
          (0 until groups).foreach { group =>
            val address = 4L + (2L + group) * 65536L
            assert(ranges.exists(range => range.offset <= address && address + 4 <= range.offset + range.size))
            val offset = address.toInt.refineEither[Positive0]
              .fold(message => fail(message), value => value)
            LittleEndian.write(seed, expected, offset, ScalarByteWidth(4))
          }
          assertEquals(Vec.from(host), Vec.from(expected))
          assertEquals(service.releaseCompletion(completion), Right(()))
          assert(service.completion(completion).isLeft)
        }
      }
    }
  }

  private def sparseGroupModule: Module = {
    import Instruction.*
    Module(
      Header(SPIRV.version, Word(0), Word(48), Word(0)),
      Vec[Instruction](
        Capability(operand("Capability", 6)),
        MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        TypeVoid(id(1)),
        TypeInt(id(2), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        TypeInt(id(3), operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        TypeVector(id(5), id(3), operand("LiteralInteger", 3)),
        TypePointer(id(6), operand("StorageClass", 1), id(5)),
        TypePointer(id(7), operand("StorageClass", 5), id(2)),
        TypeFunction(id(9), id(1), Vec(id(7), id(2))),
        Constant(id(3), id(10), operand("LiteralContextDependentNumber", 16384, 0)),
        Variable(id(6), id(15), operand("StorageClass", 1), None),
        Decorate(id(15), operand("Decoration", 11, 28)),
        EntryPoint(operand("ExecutionModel", 6), id(20), stringOperand("groups"), Vec(id(15))),
        Function(id(1), id(20), operand("FunctionControl", 0), id(9)),
        FunctionParameter(id(7), id(21)),
        FunctionParameter(id(2), id(22)),
        Label(id(24)),
        Load(id(5), id(25), id(15), None),
        CompositeExtract(id(3), id(28), id(25), Vec(operand("LiteralInteger", 0))),
        IMul(id(3), id(44), id(28), id(10)),
        PtrAccessChain(id(7), id(43), id(21), id(44), Vec.empty),
        Store(id(43), id(22), None),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
  }

  private def id(value: Int): Operand = operand("IdRef", value)

  private def awaitCompletion(
      service: ExecutionService,
      completion: CompletionId,
      deadline: Long = PlatformTime.monotonicNanos + 5_000_000_000L
  ): Either[ExecutionFailure, CompletionStatus] = {
    @tailrec def loop(): Either[ExecutionFailure, CompletionStatus] = service.completion(completion) match {
      case pending @ Right(CompletionStatus.Pending) if PlatformTime.monotonicNanos < deadline =>
        Thread.sleep(1L)
        loop()
      case result => result
    }
    loop()
  }

  private def operand(kind: String, value: Int, remaining: Int*): Operand =
    Operand(
      SPIRVOperandKind.either(kind).fold(message => fail(message), value => value),
      (Word(value) +: Vec.from(remaining.map(Word(_)))).refineUnsafe[MinLength[1]]
    )

  private def stringOperand(value: String :| (MinLength[1] & NoNul)): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      Word(LittleEndian.decode(group, 0, ScalarByteWidth.applyUnsafe(group.length)).toInt)
    }
    Operand(SPIRVOperandKind("LiteralString"), Vec.from(words).refineUnsafe[MinLength[1]])
  }
}
