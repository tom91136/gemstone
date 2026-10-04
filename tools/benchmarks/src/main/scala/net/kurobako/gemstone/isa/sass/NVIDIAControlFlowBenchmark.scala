package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.v9_3 as PTX
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** Equivalent PTX and encoded SASS kernels exercise divergent branches, predicate guards, loops and memory. */
object NVIDIAControlFlowBenchmark {
  private enum ISA { case PTX, SASS }
  private case class Options(
      isa: ISA,
      iterations: Int > 0,
      rounds: Int > 0,
      lanes: Int in 1 ~ 1024,
      groups: Int > 0,
      workers: Int > 0
  )
  private val name    = BenchmarkName("nvidia-control-flow")
  private val options = (
    Opts.option[String]("isa", help = "ptx or sass").withDefault("ptx")
      .validate("isa must be ptx or sass")(value => value == "ptx" || value == "sass")
      .map(value => if (value == "ptx") ISA.PTX else ISA.SASS),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(5)
      .refined[Int > 0]("iterations must be positive"),
    Opts.option[Int]("rounds", help = "Predicate and memory rounds per lane").withDefault(16)
      .refined[Int > 0]("rounds must be positive"),
    Opts.option[Int]("lanes", help = "Lanes per group, at most 1024").withDefault(128)
      .refined[Int in 1 ~ 1024]("lanes must be between 1 and 1024"),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(16)
      .refined[Int > 0]("groups must be positive"),
    Opts.option[Int]("workers", help = "Host execution workers").withDefault(1)
      .refined[Int > 0]("workers must be positive")
  ).mapN(Options(_, _, _, _, _, _))

  // The SM80 launch parameter bank starts at 0x160: pointer, rounds, then lane count.
  private val parameterBase: Int >= 0 = 0x160
  private val sass                    = s"""MOV R20, 0;
    S2R R0, SR_TID.X;
    S2R R1, SR_CTAID.X;
    LDC R2, c[0x0][R20+0x${(parameterBase + 12).raw.toHexString}];
    IMAD R4, R1, R2, R0;
    LDC.64 R10, c[0x0][R20+0x${parameterBase.raw.toHexString}];
    IMAD.WIDE.U32 R10, R4, 4, R10;
    MOV R6, R4;
    LDC R8, c[0x0][R20+0x${(parameterBase + 8).raw.toHexString}];
    LOP3.LUT R9, R0, 1, RZ, 0xc0, !PT;
    MOV R7, 0;
  loop:
    ISETP.EQ.U32.AND P0, PT, R9, RZ, PT;
    BSSY B0, joined;
    @P0 BRA even;
    IADD3 R6, R6, 2, RZ;
    BRA reconverge;
  even:
    IADD3 R6, R6, 1, RZ;
  reconverge:
    BSYNC B0;
  joined:
    ISETP.NE.U32.AND P1, PT, R7, RZ, PT;
    @P1 IADD3 R6, R6, 1, RZ;
    STG.E [R10+0x4], R6;
    BAR.SYNC 0;
    IADD3 R7, R7, 1, RZ;
    ISETP.LT.U32.AND P2, PT, R7, R8, PT;
    @P2 BRA loop;
    EXIT;"""

  private def sassKernel(): VMIR.Kernel = {
    val target  = Target.parse("SM80").fold(error => throw IllegalArgumentException(error), identity)
    val program = Parser.parseProgram(sass).fold(error => throw IllegalArgumentException(error.toString), identity)
    val bytes   =
      Assembler(target).encode(program).fold(error => throw IllegalArgumentException(error.toString), identity)
    val decoded =
      CodeSection.decode(bytes.toArray, target.sm.raw).fold(error => throw IllegalArgumentException(error), identity)
    val entry = Cubin.Kernel(
      name = "control",
      section = 0,
      parameterBase = parameterBase,
      parameterSize = 16,
      parameters = Vec(Cubin.Parameter(0, 0, 8), Cubin.Parameter(1, 8, 4), Cubin.Parameter(2, 12, 4))
    )
    // ELF PROGBITS text; all execution metadata is constructed alongside the encoded instructions.
    val section = Cubin.Section(
      index = 0,
      name = ".text.control",
      kind = 1,
      flags = 0L,
      size = bytes.nonNegativeSize,
      link = 0,
      info = 0,
      bytes = bytes
    )
    val constantBytes = Vec.fill((entry.parameterBase + entry.parameterSize).raw)(0.toByte)
    val constants     = Cubin.Section(
      index = 1,
      name = ".nv.constant0.control",
      kind = 1,
      flags = 2L,
      size = constantBytes.nonNegativeSize,
      link = 0,
      info = 0,
      bytes = constantBytes
    )
    val module =
      Cubin.Module(sm = target.sm, sections = Vec(section, constants), kernels = Vec(entry), globals = Vec.empty)
    VMCompiler.compile(module, entry, Vec(DecodedFunction("control", decoded.instructions)))
      .fold(error => throw IllegalArgumentException(error), identity)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val kernel = parsed.isa match {
      case ISA.PTX  => PTX.PTXControlFlowFixture.compile()
      case ISA.SASS => sassKernel()
    }
    val geometry = LaunchGeometry.forLaunch(Dimensions(parsed.lanes, 1, 1), Dimensions(parsed.groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val count: Int > 0 = parsed.lanes * parsed.groups
    val memory         = ArrayCheckedMemory.allocate((count + 1) * 4)
    val parameters     = parsed.isa match {
      case ISA.PTX => Map(
          ("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw),
          ("rounds": NonEmpty[String]) -> Argument.fromLong(parsed.rounds.raw.toLong)
        )
      case ISA.SASS => Map(
          ("param0": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw),
          ("param1": NonEmpty[String]) -> Argument(LittleEndian.toVec(parsed.rounds.raw.toLong, 4).unsafe),
          ("param2": NonEmpty[String]) -> Argument(LittleEndian.toVec(parsed.lanes.raw.toLong, 4).unsafe)
        )
    }
    Using.resource(WorkerPool(parallelism = parsed.workers, minGroups = 1, minWorkItems = 1)) { workers =>
      val sample = Measurement.sample(parsed.iterations.toLong) {
        var iteration = 0
        while (iteration < parsed.iterations.raw) {
          VMInterpreter.execute(
            kernel = kernel,
            geometry = geometry,
            arguments = parameters,
            memory = memory,
            workgroups = workers
          )
            .fold(error => throw IllegalStateException(error.message), _ => ())
          iteration += 1
        }
      }
      var lane = 0
      while (lane < count.raw) {
        val actual = memory.readInt32(memory.allocation.start.offset((lane.toLong + 1) * 4))
          .fold(error => throw IllegalStateException(error.toString), identity)
        val increment = if ((lane % parsed.lanes.raw & 1) == 0) 1 else 2
        val expected  = lane + parsed.rounds.raw * (increment + 1) - 1
        require(actual == expected, s"lane $lane: expected $expected, got $actual")
        lane += 1
      }
      require(memory.readInt32(memory.allocation.start).contains(0), "leading guard word was overwritten")
      sample
    }
  }
}
