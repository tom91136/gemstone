package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.spirv.v1_6.ISA as SPIRVISA
import net.kurobako.gemstone.isa.spirv.v1_6.{SPIRV as _, *}
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.util.Using

class SPIRVProgramExecutorSuite extends munit.FunSuite {
  private object PrintMemory extends Memory {
    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Left(Fault.Memory("unexpected external read"))
    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      Left(Fault.Memory("unexpected external write"))
  }

  test("real SPIR-V printf reaches the handler and preserves handler failures") {
    val module = Module(
      Header(SPIRVISA.version, Word(0), Word(18), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        Instruction.ExtInstImport(id(1), stringOperand("OpenCL.std")),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(14),
          stringOperand("print_value"),
          Vec.empty
        ),
        Instruction.TypeVoid(id(2)),
        Instruction.TypeInt(id(3), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(id(4), operand(OperandKind.LiteralInteger, 8), operand(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(id(3), id(5), operand(OperandKind.LiteralContextDependentNumber, 3)),
        Instruction.Constant(id(4), id(6), operand(OperandKind.LiteralContextDependentNumber, 37)),
        Instruction.Constant(id(4), id(7), operand(OperandKind.LiteralContextDependentNumber, 117)),
        Instruction.Constant(id(4), id(8), operand(OperandKind.LiteralContextDependentNumber, 0)),
        Instruction.TypeArray(id(9), id(4), id(5)),
        Instruction.ConstantComposite(id(9), id(10), Vec(id(6), id(7), id(8))),
        Instruction.TypePointer(id(11), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(9)),
        Instruction.Variable(
          id(11),
          id(12),
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          Some(id(10))
        ),
        Instruction.TypeFunction(id(13), id(2), Vec.empty),
        Instruction.Constant(id(3), id(17), operand(OperandKind.LiteralContextDependentNumber, 7)),
        Instruction.Function(id(2), id(14), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(13)),
        Instruction.Label(id(15)),
        Instruction.ExtInst(
          id(3),
          id(16),
          id(1),
          operand(OperandKind.LiteralExtInstInteger, ExtendedInstruction.OpenClStd.Printf),
          Vec(id(12), id(17))
        ),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val bytes   = SPIRVISA.encode(module).fold(error => fail(error.message), identity)
    val backend = SPIRV(Validation.Environment.OpenCLCompute)
    Using.resource(backend.load(bytes).fold(error => fail(error.message), identity)) { program =>
      val task = KernelTask.validated(
        "print_value",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec.empty
      ).fold(message => fail(message), identity)
      val events = scala.collection.mutable.ArrayBuffer.empty[Event]
      assertEquals(
        program.execute(task, PrintMemory, TaskControl(), event => { events += event; Right(()) }),
        Right(())
      )
      assertEquals(events.toVector, Vector(Event.Print("%u", Vec(Event.PrintArgument.Integer(Vec(7L), 32)))))
      val rejected = java.util.concurrent.atomic.AtomicInteger()
      assertEquals(
        program.execute(
          task,
          PrintMemory,
          TaskControl(),
          _ => {
            val _ = rejected.incrementAndGet()
            Left(Fault.EventHandling("sink closed"))
          }
        ),
        Left(ExecutionFailure.Failed("sink closed"))
      )
      assertEquals(rejected.get(), 1)
    }
  }

  private val encoding = SPIRV(Validation.Environment.OpenCLCompute).encoding

  test("the external SPIR-V load boundary does not treat non-kernel entry points as libraries") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val functionType = operand(OperandKind.IdResult, 2)
    val function     = operand(OperandKind.IdResult, 3)
    val module       = Module(
      Header(SPIRVISA.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Vertex),
          function,
          stringOperand("vertex"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(operand(OperandKind.IdResult, 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      service.load(encoding, SPIRVISA.encode(module).toOption.get) match {
        case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpEntryPoint Kernel"))
        case result => fail(s"expected kernel entry-point validation failure, found $result")
      }
    }
  }

  test("the external SPIR-V load boundary rejects modules outside the OpenCL compute environment") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val functionType = operand(OperandKind.IdResult, 2)
    val function     = operand(OperandKind.IdResult, 3)
    val module       = Module(
      Header(SPIRVISA.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("invalid_environment"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(operand(OperandKind.IdResult, 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      service.load(encoding, SPIRVISA.encode(module).toOption.get) match {
        case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpCapability Kernel"))
        case result => fail(s"expected OpenCL environment validation failure, found $result")
      }
    }
  }

  test("typed SPIR-V executes through the binary program service boundary") {
    val voidType    = operand(OperandKind.IdResult, 1)
    val u32         = operand(OperandKind.IdResult, 2)
    val pointerType = operand(OperandKind.IdResult, 3)
    val function    = operand(OperandKind.IdResult, 5)
    val output      = operand(OperandKind.IdResult, 6)
    val value       = operand(OperandKind.IdResult, 7)
    val module      = Module(
      Header(SPIRVISA.version, Word(0), Word(16), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(operand(OperandKind.IdResult, 4), voidType, Vec(pointerType, u32)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("store"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 4)
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, value),
        Instruction.Label(operand(OperandKind.IdResult, 8)),
        Instruction.Store(output, value, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val image = SPIRVISA.encode(module).toOption.get
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      val allocation = service.registerAllocation(4L, AllocationKind.Shared).toOption.get
      val program    = service.load(encoding, image).toOption.get
      val task       = KernelTask
        .validated(
          "store",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Scalar(Vec[Byte](0x78, 0x56, 0x34, 0x12))
          )
        )
        .toOption
        .get

      val completion = service.launch(program, task).toOption.get
      assertEquals(awaitCompletion(service, completion), Right(CompletionStatus.Succeeded))
      assertEquals(
        service.read(allocation, 0L, 4),
        Right(Vec[Byte](0x78, 0x56, 0x34, 0x12))
      )
    }
  }

  test("multi-workgroup SPIR-V completion publishes every dirty region on repeated queued launches") {
    import java.util.concurrent.atomic.AtomicReference
    val image = SPIRVISA.encode(sparseGroupModule).toOption.get
    for {
      groups   <- Vec(1, 2, 3, 5, 9)
      parallel <- Vec(false, true)
    } {
      class ParallelGroups extends Workgroups {

        override def shouldParallel(tasks: Long >= 0, items: Long >= 0): Boolean = tasks.raw > 1L
        override def parallelFor(tasks: Long >= 0)(
            task: Long >= 0 => Either[Fault, Unit]
        )
            : Either[Fault, Unit] = {
          val failure = AtomicReference(Option.empty[Fault])
          (0L until tasks.raw).grouped(4).foreach { batch =>
            val threads = batch.map(index =>
              Thread(() =>
                try task(index.unsafe[Long >= 0]).left.foreach(error => failure.set(Some(error)))
                catch {
                  case error: Throwable =>
                    failure.set(Some(Fault.Backend(s"worker task failed: $error")))
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
      val queued     = AtomicReference(Option.empty[Runnable])
      val workgroups = if (parallel) ParallelGroups() else Workgroups.Sequential
      class CapturingSubmissions extends scala.concurrent.ExecutionContext {
        override def execute(runnable: Runnable): Unit     = assert(queued.compareAndSet(None, Some(runnable)))
        override def reportFailure(error: Throwable): Unit = throw error
      }
      Using.resource(ExecutionSession(
        Backends.default(workgroups).programs,
        executionContext = CapturingSubmissions()
      )) {
        service =>
          val size           = ((groups + 3) * 65536 + 8).unsafe[Int > 0]
          val allocationSize = size.raw.toLong
          val allocation     =
            service.registerAllocation(allocationSize.unsafe[Long > 0], AllocationKind.Shared).toOption.get
          val program = service.load(encoding, image).fold(error => fail(error.message), value => value)
          val host    = Array.ofDim[Byte](size.raw)
          for (seed <- Vec(3L, 7L)) {
            val before     = service.read(allocation, 0L, size).toOption.get
            val groupCount = groups.refine[Int > 0]
              .fold(
                message => fail(message),
                value => LaunchGeometry.Dimensions(value, 1, 1)
              )
            val task = KernelTask.validated(
              "groups",
              LaunchGeometry.Dimensions.One,
              groupCount,
              Vec(
                KernelTask.Argument.Memory(allocation, 4L),
                KernelTask.Argument.Scalar(LittleEndian.toVec(seed, 4))
              ),
              globalOffset =
                LaunchGeometry.Coordinates(2, 0, 0)
            ).toOption.get
            val completion = service.launch(program, task).toOption.get
            assertEquals(service.completion(completion), Right(CompletionStatus.Pending))
            assert(service.completionDirtyRanges(completion, 0).isLeft)
            assertEquals(service.read(allocation, 0L, size), Right(before))
            queued.getAndSet(None).get.run()
            assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
            val ranges = service.completionDirtyRanges(completion, 0).toOption.get
            assertEquals(service.completionDirtyRanges(completion, 0), Right(ranges))
            ranges.foreach { range =>
              assertEquals(range.allocation, allocation)
              assert(range.offset.raw >= 0 && range.offset.raw + range.size.raw <= size.raw)
              val bytes = service.read(allocation, range.offset, range.size).toOption.get
              bytes.copyToArray(host, range.offset.raw.toInt, range.size.raw)
            }
            val expected = Array.ofDim[Byte](size.raw)
            (0 until groups).foreach { group =>
              val address = 4L + (2L + group) * 65536L
              assert(ranges.exists(range =>
                range.offset.raw <= address && address + 4 <= range.offset.raw + range.size.raw
              ))
              val offset = address.toInt.refine[Int >= 0]
                .fold(message => fail(message), value => value)
              LittleEndian.write(seed, expected, offset.raw, 4)
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
      Header(SPIRVISA.version, Word(0), Word(48), Word(0)),
      Vec[Instruction](
        Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        TypeVoid(id(1)),
        TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        TypeInt(id(3), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        TypeVector(id(5), id(3), operand(OperandKind.LiteralInteger, 3)),
        TypePointer(id(6), operand(OperandKind.StorageClass, spv.StorageClass.Input), id(5)),
        TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        TypeFunction(id(9), id(1), Vec(id(7), id(2))),
        Constant(id(3), id(10), operand(OperandKind.LiteralContextDependentNumber, 16384, 0)),
        Variable(id(6), id(15), operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
        Decorate(id(15), operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)),
        EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(20),
          stringOperand("groups"),
          Vec(id(15))
        ),
        Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
        FunctionParameter(id(7), id(21)),
        FunctionParameter(id(2), id(22)),
        Label(id(24)),
        Load(id(5), id(25), id(15), None),
        CompositeExtract(id(3), id(28), id(25), Vec(operand(OperandKind.LiteralInteger, 0))),
        IMul(id(3), id(44), id(28), id(10)),
        PtrAccessChain(id(7), id(43), id(21), id(44), Vec.empty),
        Store(id(43), id(22), None),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
  }

  private def id(value: Int): Operand = operand(OperandKind.IdRef, value)

  private def awaitCompletion(
      service: ExecutionSession,
      completion: CompletionId,
      deadline: Long = platform.monotonicNanos + 5_000_000_000L
  ): Either[ExecutionFailure, CompletionStatus] = {
    @tailrec def loop(): Either[ExecutionFailure, CompletionStatus] = service.completion(completion) match {
      case _ @Right(CompletionStatus.Pending) if platform.monotonicNanos < deadline =>
        Thread.sleep(1L)
        loop()
      case result => result
    }
    loop()
  }

  private def operand(kind: OperandKind, value: Int, remaining: Int*): Operand =
    Operand(
      kind,
      Vec.nonEmpty(Word(value), remaining.map(Word(_))*)
    )

  private def stringOperand(value: String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      Word(LittleEndian.decode(group, 0, group.length.unsafe[Int where Between[0, 8]]).toInt)
    }
    Operand(OperandKind.LiteralString, Vec.from(words).unsafe)
  }
}
