package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx as PTX
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Equivalent PTX and encoded SASS kernels exercise divergent branches, predicate guards, loops and memory. */
object NVIDIAControlFlowBenchmark {
  private enum ISA { case PTX, SASS }

  val benchmark: Case = (
    choice("isa", "ptx" -> ISA.PTX, "sass" -> ISA.SASS),
    iterations(5),
    positive("rounds", "Predicate and memory rounds per lane", 16),
    positive("lanes", "Lanes per group, at most 1024", 128).mapValidated(
      _.raw.refine[Int in 1 ~ 1024].leftMap(_ => "lanes must be at most 1024").toValidatedNel
    ),
    positive("groups", "Workgroups per dispatch", 16),
    workers
  ).mapN((isa, iterations, rounds, lanes, groups, workers) =>
    () => run(isa, iterations, rounds, lanes, groups, workers)
  )

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
      CodeSection.decode(bytes.toArray, target).fold(error => throw IllegalArgumentException(error), identity)
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

  private def run(
      isa: ISA,
      iterations: Int > 0,
      rounds: Int > 0,
      lanes: Int in 1 ~ 1024,
      groups: Int > 0,
      workers: Int > 0
  )
      : BenchmarkSample = {
    val kernel = isa match {
      case ISA.PTX  => PTX.PTXControlFlowFixture.compile()
      case ISA.SASS => sassKernel()
    }
    val geometry = LaunchGeometry.forLaunch(Dimensions(lanes, 1, 1), Dimensions(groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val count      = Math.multiplyExact(lanes.raw, groups.raw)
    val memory     = BenchmarkMemory((count + 1) * 4)
    val output     = memory.ids(0)
    val parameters = isa match {
      case ISA.PTX => Map(
          ("output": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw),
          ("rounds": NonEmpty[String]) -> Argument.fromLong(rounds.raw.toLong)
        )
      case ISA.SASS => Map(
          ("param0": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw),
          ("param1": NonEmpty[String]) -> Argument(LittleEndian.toVec(rounds.raw.toLong, 4).unsafe),
          ("param2": NonEmpty[String]) -> Argument(LittleEndian.toVec(lanes.raw.toLong, 4).unsafe)
        )
    }
    val sample = Measurement.repeatPooled(iterations, workers)(pool =>
      VMInterpreter.execute(kernel, geometry, parameters, memory, workgroups = pool)
    )
    (0 until count).foreach { lane =>
      val actual    = memory.getInt(output, (lane + 1) * 4)
      val increment = if ((lane % lanes.raw & 1) == 0) 1 else 2
      val expected  = lane + rounds.raw * (increment + 1) - 1
      require(actual == expected, s"lane $lane: expected $expected, got $actual")
    }
    require(memory.getInt(output, 0) == 0, "leading guard word was overwritten")
    sample
  }
}
