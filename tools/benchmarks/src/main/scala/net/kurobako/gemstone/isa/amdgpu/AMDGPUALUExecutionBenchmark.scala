package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.file.Path
import scala.util.Using

/** ALU-heavy companion to [[AMDGPUExecutionBenchmark]], isolating instruction and lane dispatch from guest memory. */
object AMDGPUALUExecutionBenchmark {
  private enum Mode {
    case Move, Binary
  }

  private final case class Options(
      measured: Int :| Positive,
      warmup: Option[Int :| Positive0],
      instructionCount: Int :| Positive0,
      mode: Mode
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(50)
      .validate("measured dispatches must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(_.refineUnsafe[Positive0]).orNone,
    Opts.option[Int]("instructions", help = "ALU instructions per dispatch").withDefault(512)
      .validate("instruction count must be non-negative")(_ >= 0).map(_.refineUnsafe[Positive0]),
    Opts.option[String]("mode", help = "Instruction mode: move or binary").withDefault("move")
      .validate("mode must be move or binary")(value => value == "move" || value == "binary")
      .map(value => if (value == "binary") Mode.Binary else Mode.Move)
  ).mapN(Options(_, _, _, _))

  private object RejectingMemory extends Memory with BenchmarkAddressSpace {
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] = Seq.empty
    override def read(
        id: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] =
      Left(Fault.Backend("ALU benchmark unexpectedly read memory"))
    override def write(
        id: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      Left(Fault.Backend("ALU benchmark unexpectedly wrote memory"))
  }

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed           = BenchmarkArguments.parse(BenchmarkName("amdgpu-alu"), arguments.toSeq, options)
    val measured         = parsed.measured
    val warmup           = parsed.warmup.getOrElse(math.max(10, measured / 10).refineUnsafe[Positive0])
    val instructionCount = parsed.instructionCount
    val mode             = parsed.mode
    val instruction      = mode match {
      case Mode.Move => 0x7e0002ebL
      // gfx9 VOP2 v_add_f32 v0, v1, v2
      case Mode.Binary => 0x02000501L
    }
    val words    = Vec.from(Iterator.fill(instructionCount)(instruction) ++ Iterator.single(0xbf810000L))
    val code     = words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    val kernel   = CodeObjectKernel("alu", Target("gfx900"), 0, 0, 0, 0, 0, code)
    val prepared =
      VMCompiler.compile(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val dispatch = KernelTask.validated(
      KernelName("alu"),
      Dimensions(64, 1, 1),
      Dimensions.One,
      Vec.empty
    ).fold(message => throw IllegalArgumentException(message), value => value)
    def execute(): Unit =
      VMInterpreter.execute(
        prepared,
        dispatch,
        RejectingMemory,
        RejectingMemory,
        TaskControl.default,
        Scheduler.Sequential,
        Vec.empty
      ).fold(
        failure => throw RuntimeException(failure.message),
        value => value
      )

    var iteration = 0
    while (iteration < warmup) {
      execute()
      iteration += 1
    }
    val (allocatedBefore, elapsed, allocatedAfter) =
      Using.resource(PlatformMetrics.startRecording(sys.env.get("GEMSTONE_BENCHMARK_JFR").map(Path.of(_)))) { _ =>
        val before  = PlatformMetrics.allocatedBytes
        val started = PlatformTime.monotonicNanos
        iteration = 0
        while (iteration < measured) {
          execute()
          iteration += 1
        }
        (
          before,
          math.max(0L, PlatformTime.monotonicNanos - started).refineUnsafe[Positive0],
          PlatformMetrics.allocatedBytes
        )
      }
    Console.out.println(
      f"amdgpu-alu benchmark mode=${mode.toString.toLowerCase} dispatches=$measured warmup=$warmup instructions_per_dispatch=${instructionCount +
          1} " +
        f"elapsed_ms=${elapsed / 1000000.0}%.3f ns_per_dispatch=${elapsed.toDouble / measured.toDouble}%.1f " +
        f"allocated_bytes_per_dispatch=${(allocatedBefore, allocatedAfter) match {
            case (Some(before), Some(after)) if after >= before => (after - before).toDouble / measured.toDouble
            case _                                              => Double.NaN
          }}%.1f"
    )
  }
}
