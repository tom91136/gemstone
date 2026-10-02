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
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Concurrent arithmetic dispatches representative of the SPIR-V interpreter's instruction loop. */
object SPIRVExecutionBenchmark {
  private case class Options(
      iterations: Int > 0,
      groups: Int > 0,
      instructions: Int > 0,
      parallelism: Int > 0,
      recording: Option[Path]
  )

  private object EmptyMemory extends Memory {
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
      Left(Fault.Unsupported("benchmark kernel attempted a memory write"))
  }

  private val name    = BenchmarkName("spirv-execution")
  private val options = (
    Opts.option[Int]("iterations", help = "Measured dispatches").withDefault(20)
      .refined[Int > 0]("iterations must be positive"),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(256)
      .refined[Int > 0]("groups must be positive"),
    Opts.option[Int]("instructions", help = "Integer additions per invocation").withDefault(64)
      .refined[Int > 0]("instructions must be positive"),
    Opts.option[Int]("parallelism", help = "VMInterpreter worker count")
      .withDefault(sys.runtime.availableProcessors())
      .refined[Int > 0]("parallelism must be positive"),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val source = module(parsed.instructions)
    val task   = KernelTask.validated(
      kernelName = "arithmetic",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions(parsed.groups, 1, 1),
      arguments = Vec.empty
    ).fold(error => throw IllegalArgumentException(error), value => value)

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
          program.execute(task, EmptyMemory, control, workgroups)
            .fold(error => throw IllegalStateException(error.message), _ => ())
          iteration += 1
        }
      }
      Console.out.println(
        s"iterations=${parsed.iterations} groups=${parsed.groups} instructions=${parsed.instructions} " +
          s"parallelism=${parsed.parallelism} nsPerDispatch=${measurement.elapsedNanos.raw.toDouble /
              parsed.iterations.raw} " +
          s"bytesPerDispatch=${measurement.allocatedBytes.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)}"
      )
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
        val width = group.length.refine[Int where Between[0, 8]]
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(group, 0, width).toInt)
      }).unsafe
    )
  }

  private def module(instructionCount: Int > 0): Module = {
    import Instruction.*
    val arithmetic = Vector.tabulate(instructionCount.raw) { index =>
      val destination = index + 7
      val source      = if (index == 0) 4 else destination - 1
      IAdd(id(2), id(destination), id(source), id(4))
    }
    val instructions = Vec[Instruction](
      Capability(operand(OperandKind.Capability, 6)),
      MemoryModel(operand(OperandKind.AddressingModel, 2), operand(OperandKind.MemoryModel, 2)),
      TypeVoid(id(1)),
      TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      TypeFunction(id(3), id(1), Vec.empty),
      Constant(id(2), id(4), operand(OperandKind.LiteralContextDependentNumber, 1)),
      EntryPoint(operand(OperandKind.ExecutionModel, 6), id(5), literalString("arithmetic"), Vec.empty),
      Function(id(1), id(5), operand(OperandKind.FunctionControl, 0), id(3)),
      Label(id(6))
    ) ++ Vec.from(arithmetic) ++ Vec[Instruction](Return(), FunctionEnd())
    Module(
      Header(ISA.version, Word(0), Word(instructionCount.raw + 7), Word(0)),
      instructions,
      Endianness.Little
    )
  }
}
