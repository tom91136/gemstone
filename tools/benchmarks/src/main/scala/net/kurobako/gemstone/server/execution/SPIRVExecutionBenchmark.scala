package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.{Fault, KernelTask, LaunchGeometry, Memory, MemoryRegion, Observer, TaskControl}

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import scala.util.Using

/** Concurrent arithmetic dispatches representative of the SPIR-V interpreter's instruction loop. */
object SPIRVExecutionBenchmark {
  private final case class Options(
      iterations: Int :| Positive,
      groups: Int :| Positive,
      instructions: Int :| Positive,
      parallelism: Int :| Positive,
      recording: Option[Path]
  )

  private object EmptyMemory extends Memory {
    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] =
      Left(Fault.Unsupported("benchmark kernel attempted a memory read"))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      Left(Fault.Unsupported("benchmark kernel attempted a memory write"))
  }

  private val name    = BenchmarkName("spirv-execution")
  private val options = (
    Opts.option[Int]("iterations", help = "Measured dispatches").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(256)
      .validate("groups must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("instructions", help = "Integer additions per invocation").withDefault(64)
      .validate("instructions must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("parallelism", help = "VMInterpreter worker count")
      .withDefault(sys.runtime.availableProcessors())
      .validate("parallelism must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val source = module(parsed.instructions)
    val task   = KernelTask.validated(
      kernelName = KernelName("arithmetic"),
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions(parsed.groups, 1, 1),
      arguments = Vec.empty
    ).fold(error => throw IllegalArgumentException(error), value => value)

    Using.resources(
      InterpreterScheduler(parsed.parallelism, minGroups = 1L, minWorkItems = 1L),
      PlatformMetrics.startRecording(parsed.recording)
    ) { (scheduler, _) =>
      val machine         = VirtualMachine(Validation.Environment.OpenCLCompute, Observer.discard, scheduler)
      val program         = machine.load(source).fold(error => throw IllegalArgumentException(error.message), identity)
      val allocatedBefore = PlatformMetrics.allocatedBytes
      val started         = PlatformTime.monotonicNanos
      var iteration       = 0
      while (iteration < parsed.iterations) {
        val control = TaskControl.forScheduler(scheduler = scheduler)
        machine.execute(program, task, EmptyMemory, control)
          .fold(error => throw IllegalStateException(error.message), _ => ())
        iteration += 1
      }
      val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
      val allocated = for {
        before <- allocatedBefore
        after  <- PlatformMetrics.allocatedBytes
        if after >= before
      } yield after - before
      Console.out.println(
        s"iterations=${parsed.iterations} groups=${parsed.groups} instructions=${parsed.instructions} " +
          s"parallelism=${parsed.parallelism} nsPerDispatch=${elapsed.toDouble / parsed.iterations} " +
          s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations)}"
      )
    }
  }

  private def operand(kind: String, values: Int*): Operand =
    Operand(
      SPIRVOperandKind.either(kind).fold(message => throw IllegalArgumentException(message), value => value),
      Vec.from(values.map(Word(_))).refineUnsafe[MinLength[1]]
    )

  private def id(value: Int): Operand = operand("IdRef", value)

  private def literalString(value: String): Operand = {
    val bytes = value.getBytes(StandardCharsets.UTF_8) :+ 0.toByte
    Operand(
      SPIRVOperandKind("LiteralString"),
      Vec.from(bytes.grouped(4).map { group =>
        val width = ScalarByteWidth.either(group.length)
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(group, 0, width).toInt)
      }).refineUnsafe[MinLength[1]]
    )
  }

  private def module(instructionCount: Int :| Positive): Module = {
    import Instruction.*
    val arithmetic = Vector.tabulate(instructionCount) { index =>
      val destination = index + 7
      val source      = if (index == 0) 4 else destination - 1
      IAdd(id(2), id(destination), id(source), id(4))
    }
    val instructions = Vec[Instruction](
      Capability(operand("Capability", 6)),
      MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
      TypeVoid(id(1)),
      TypeInt(id(2), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      TypeFunction(id(3), id(1), Vec.empty),
      Constant(id(2), id(4), operand("LiteralContextDependentNumber", 1)),
      EntryPoint(operand("ExecutionModel", 6), id(5), literalString("arithmetic"), Vec.empty),
      Function(id(1), id(5), operand("FunctionControl", 0), id(3)),
      Label(id(6))
    ) ++ Vec.from(arithmetic) ++ Vec[Instruction](Return(), FunctionEnd())
    Module(
      Header(ISA.version, Word(0), Word(instructionCount + 7), Word(0)),
      instructions,
      Endianness.Little
    )
  }
}
