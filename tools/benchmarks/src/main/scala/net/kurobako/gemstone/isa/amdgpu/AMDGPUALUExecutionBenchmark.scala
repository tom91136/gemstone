package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import CodeObject.*
import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts

import java.nio.file.Path
import scala.util.Using

/** ALU-heavy companion to [[AMDGPUExecutionBenchmark]], isolating instruction and lane dispatch from guest memory. */
object AMDGPUALUExecutionBenchmark {
  private enum Mode {
    case Move, Binary
  }

  private case class Options(
      measured: Positive[Int],
      warmup: Option[NonNegative[Int]],
      instructionCount: NonNegative[Int],
      mode: Mode
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(50)
      .validate("measured dispatches must be positive")(_ > 0).map(Positive.unsafe),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(NonNegative.unsafe).orNone,
    Opts.option[Int]("instructions", help = "ALU instructions per dispatch").withDefault(512)
      .validate("instruction count must be non-negative")(_ >= 0).map(NonNegative.unsafe),
    Opts.option[String]("mode", help = "Instruction mode: move or binary").withDefault("move")
      .validate("mode must be move or binary")(value => value == "move" || value == "binary")
      .map(value => if (value == "binary") Mode.Binary else Mode.Move)
  ).mapN(Options(_, _, _, _))

  private object RejectingMemory extends Memory with BenchmarkAddressSpace {
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] = Seq.empty
    override def read(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] =
      Left(Fault.Backend("ALU benchmark unexpectedly read memory"))
    override def write(
        id: MemoryRegion,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      Left(Fault.Backend("ALU benchmark unexpectedly wrote memory"))
  }

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed           = BenchmarkArguments.parse(BenchmarkName("amdgpu-alu"), arguments.toSeq, options)
    val measured         = parsed.measured
    val warmup           = parsed.warmup.getOrElse(NonNegative.unsafe(math.max(10, measured.raw / 10)))
    val instructionCount = parsed.instructionCount
    val mode             = parsed.mode
    val instruction      = mode match {
      case Mode.Move => 0x7e0002ebL
      // gfx9 VOP2 v_add_f32 v0, v1, v2
      case Mode.Binary => 0x02000501L
    }
    val words    = Vec.from(Iterator.fill(instructionCount.raw)(instruction) ++ Iterator.single(0xbf810000L))
    val code     = words.flatMap(word => LittleEndian.toVec(word, 4))
    val kernel   = CodeObjectKernel("alu", Target("gfx900"), 0, 0, 0, 0, 0, code)
    val prepared =
      VMCompiler.compile(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val dispatch = KernelTask.validated(
      NonEmptyString("alu"),
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
    while (iteration < warmup.raw) {
      execute()
      iteration += 1
    }
    val (allocatedBefore, elapsed, allocatedAfter) =
      Using.resource(PlatformMetrics.startRecording(sys.env.get("GEMSTONE_BENCHMARK_JFR").map(Path.of(_)))) { _ =>
        val before  = PlatformMetrics.allocatedBytes
        val started = PlatformTime.monotonicNanos
        iteration = 0
        while (iteration < measured.raw) {
          execute()
          iteration += 1
        }
        (
          before,
          NonNegative.unsafe(math.max(0L, PlatformTime.monotonicNanos - started)),
          PlatformMetrics.allocatedBytes
        )
      }
    Console.out.println(
      f"amdgpu-alu benchmark mode=${mode.toString.toLowerCase(java.util.Locale.ROOT)} dispatches=$measured warmup=$warmup instructions_per_dispatch=${instructionCount.raw +
          1} " +
        f"elapsed_ms=${elapsed.raw / 1000000.0}%.3f ns_per_dispatch=${elapsed.raw.toDouble / measured.raw.toDouble}%.1f " +
        f"allocated_bytes_per_dispatch=${(allocatedBefore, allocatedAfter) match {
            case (Some(before), Some(after)) if after >= before =>
              (after - before).raw.toDouble / measured.raw.toDouble
            case _ => Double.NaN
          }}%.1f"
    )
  }
}
