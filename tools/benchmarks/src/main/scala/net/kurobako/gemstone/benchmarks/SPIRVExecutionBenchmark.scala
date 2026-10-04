package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Concurrent arithmetic dispatches representative of the SPIR-V interpreter's instruction loop. */
object SPIRVExecutionBenchmark {
  private enum Mode {
    case Arithmetic, Calls
  }

  private case class Options(
      mode: Mode,
      iterations: Int > 0,
      groups: Int > 0,
      instructions: Int > 0,
      parallelism: Int > 0,
      recording: Option[Path]
  )

  private val outputRegion = AllocationId(1)

  private class OutputMemory(invocations: Int) extends Memory {
    private val output = Array.fill[Byte](Math.multiplyExact(invocations, 4))(0xa5.toByte)

    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      Left(Fault.Unsupported("benchmark kernel attempted a memory read"))

    override def write(
        region: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      if (region != outputRegion) Left(Fault.InvalidTask("unknown benchmark allocation"))
      else if (!ByteRange.contains(output.length, offset.raw, bytes.length.toLong))
        Left(Fault.InvalidTask("benchmark write outside allocation"))
      else {
        val _ = bytes.copyToArray(output, offset.raw.toInt, bytes.length)
        Right(())
      }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def verify(groups: Int > 0, instructions: Int > 0): Unit = {
      var invocation = 0
      while (invocation < invocations) {
        val actual   = LittleEndian.decodeInt32(output, invocation * 4)
        val expected = invocation % groups.raw + instructions.raw
        if (actual != expected)
          throw IllegalStateException(s"SPIR-V result mismatch at invocation $invocation: $actual != $expected")
        invocation += 1
      }
    }
  }

  private val name    = BenchmarkName("spirv-execution")
  private val options = (
    Opts.option[String]("mode", help = "Execution mode: arithmetic or calls").withDefault("arithmetic")
      .validate("mode must be arithmetic or calls")(value => value == "arithmetic" || value == "calls")
      .map(value => if (value == "calls") Mode.Calls else Mode.Arithmetic),
    Opts.option[Int]("iterations", help = "Measured dispatches").withDefault(20)
      .refined[Int > 0]("iterations must be positive"),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(256)
      .refined[Int > 0]("groups must be positive"),
    Opts.option[Int]("instructions", help = "Integer additions or function calls per invocation").withDefault(64)
      .refined[Int > 0]("instructions must be positive"),
    Opts.option[Int]("parallelism", help = "VMInterpreter worker count")
      .withDefault(sys.runtime.availableProcessors())
      .refined[Int > 0]("parallelism must be positive"),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val source = module(parsed.instructions, parsed.mode)
    val memory = OutputMemory(Math.multiplyExact(parsed.iterations.raw, parsed.groups.raw))
    val tasks  = Vec.tabulate(parsed.iterations.raw) { iteration =>
      KernelTask.validated(
        kernelName = "arithmetic",
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions(parsed.groups, 1, 1),
        arguments = Vec(KernelTask.Argument.Memory(
          outputRegion,
          (iteration.toLong * parsed.groups.raw * 4L).unsafe[Long >= 0]
        ))
      ).fold(error => throw IllegalArgumentException(error), identity)
    }

    Using.resources(
      WorkerPool(parsed.parallelism, minGroups = 1L, minWorkItems = 1L),
      PlatformMetrics.startRecording(parsed.recording)
    ) { (workgroups, _) =>
      val program = LoadedProgram.load(
        source,
        Validation.Environment.OpenCLCompute
      ).fold(error => throw IllegalArgumentException(error.message), identity)
      val measurement = Measurement {
        var iteration = 0
        while (iteration < parsed.iterations.raw) {
          val control = TaskControl()
          program.execute(tasks(iteration), memory, control, workgroups)
            .fold(error => throw IllegalStateException(error.message), _ => ())
          iteration += 1
        }
      }
      memory.verify(parsed.groups, parsed.instructions)
      measurement.sample(parsed.iterations.toLong)
    }
  }

  private def operand(kind: OperandKind, values: Int*): Operand =
    Operand(
      kind,
      Vec.from(values.map(Word(_))).unsafe
    )

  private def id(value: Int): Operand = operand(OperandKind.IdRef, value)

  private def literalString(value: String): Operand = {
    val bytes = value.getBytes(StandardCharsets.UTF_8) :+ 0.toByte
    Operand(
      OperandKind.LiteralString,
      Vec.from(bytes.grouped(4).map { group =>
        val width = group.length.refine[Int in 0 ~ 8]
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(group, 0, width).toInt)
      }).unsafe
    )
  }

  private def module(instructionCount: Int > 0, mode: Mode): Module = {
    import Instruction.*
    val firstResult     = 16
    val outputPointer   = Math.addExact(instructionCount.raw, firstResult)
    val helperType      = Math.addExact(outputPointer, 1)
    val helperFunction  = Math.addExact(outputPointer, 2)
    val helperParameter = Math.addExact(outputPointer, 3)
    val helperLabel     = Math.addExact(outputPointer, 4)
    val helperResult    = Math.addExact(outputPointer, 5)
    val arithmetic      = Vec.tabulate[Instruction](instructionCount.raw) { index =>
      val destination = Math.addExact(index, firstResult)
      val source      = if (index == 0) 14 else destination - 1
      mode match {
        case Mode.Arithmetic => IAdd(id(2), id(destination), id(source), id(4))
        case Mode.Calls      => FunctionCall(id(2), id(destination), id(helperFunction), Vec(id(source)))
      }
    }
    val instructions = Vec[Instruction](
      Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
      Capability(operand(OperandKind.Capability, spv.Capability.Int64)),
      Capability(operand(OperandKind.Capability, spv.Capability.Addresses)),
      MemoryModel(
        operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
        operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
      ),
      EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        id(5),
        literalString("arithmetic"),
        Vec(id(10))
      ),
      Decorate(id(10), operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)),
      TypeVoid(id(1)),
      TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      TypeInt(id(7), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
      TypeVector(id(8), id(7), operand(OperandKind.LiteralInteger, 3)),
      TypePointer(id(9), operand(OperandKind.StorageClass, spv.StorageClass.Input), id(8)),
      TypePointer(id(11), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
      TypeFunction(id(3), id(1), Vec(id(11)))
    ) ++
      (if (mode == Mode.Calls) Vec[Instruction](TypeFunction(id(helperType), id(2), Vec(id(2))))
       else Vec.empty[Instruction]) ++ Vec[Instruction](
        Constant(id(2), id(4), operand(OperandKind.LiteralContextDependentNumber, 1)),
        Variable(id(9), id(10), operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
        Function(id(1), id(5), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(3)),
        FunctionParameter(id(11), id(12)),
        Label(id(6)),
        Load(id(8), id(13), id(10), None),
        CompositeExtract(id(7), id(15), id(13), Vec(operand(OperandKind.LiteralInteger, 0))),
        UConvert(id(2), id(14), id(15))
      ) ++ arithmetic ++ Vec[Instruction](
        PtrAccessChain(id(11), id(outputPointer), id(12), id(15), Vec.empty),
        Store(id(outputPointer), id(outputPointer - 1), None),
        Return(),
        FunctionEnd()
      ) ++
      (if (mode == Mode.Calls) Vec[Instruction](
         Function(
           id(2),
           id(helperFunction),
           operand(OperandKind.FunctionControl, spv.FunctionControl.None),
           id(helperType)
         ),
         FunctionParameter(id(2), id(helperParameter)),
         Label(id(helperLabel)),
         IAdd(id(2), id(helperResult), id(helperParameter), id(4)),
         ReturnValue(id(helperResult)),
         FunctionEnd()
       )
       else Vec.empty[Instruction])
    Module(
      Header(
        ISA.version,
        Word(0),
        Word(Math.addExact(if (mode == Mode.Calls) helperResult else outputPointer, 1)),
        Word(0)
      ),
      instructions,
      Endianness.Little
    )
  }
}
