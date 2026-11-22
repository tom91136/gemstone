package net.kurobako.gemstone.profile

object NvidiaLatency {

  enum Producer derives CanEqual {
    case Fadd32, Fmul32, Ffma32, Iadd32, Imul32, Lop32, Shl32, I2f32, Fma64, Rcp32, Ldg32, Lds32, Isetp, S2rThread,
      S2rBlock, S2ur, LdgConstant, ShflDown, Dmul64, Dadd64, Rcp64H
  }

  import Producer.*

  private case class Harvest(ptxas: String, stalls: Map[Producer, Int])

  private val ptxas129 = "cuda_12.9.r12.9/compiler.36037853_0"
  private val ptxas133 = "cuda_13.3.r13.3/compiler.38244171_0"

  private val harvests: Map[Int, Harvest] = Map(
    50 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    52 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    53 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    60 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    61 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    62 -> Harvest(ptxas129, Map(Fadd32 -> 6, Fmul32 -> 6, Ffma32 -> 6, Iadd32 -> 6, Lop32 -> 6)),
    70 -> Harvest(ptxas129, Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4)),
    72 -> Harvest(ptxas129, Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4)),
    75 -> Harvest(
      ptxas129,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, Shl32 -> 4)
    ),
    80 -> Harvest(ptxas133, Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4)),
    86 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, I2f32 -> 5)
    ),
    87 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, I2f32 -> 5)
    ),
    88 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, I2f32 -> 5)
    ),
    89 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, I2f32 -> 5)
    ),
    90  -> Harvest(ptxas133, Map(Shl32 -> 4, Fma64 -> 8, I2f32 -> 5)),
    100 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, Shl32 -> 4, I2f32 -> 5)
    ),
    101 -> Harvest(
      ptxas129,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, Shl32 -> 4, I2f32 -> 5)
    ),
    103 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, Shl32 -> 4, I2f32 -> 5)
    ),
    110 -> Harvest(
      ptxas133,
      Map(Fadd32 -> 4, Fmul32 -> 4, Ffma32 -> 4, Iadd32 -> 4, Imul32 -> 4, Lop32 -> 4, Shl32 -> 4, I2f32 -> 5)
    ),
    120 -> Harvest(ptxas133, Map(Fadd32 -> 4, Fmul32 -> 4, Imul32 -> 4, Iadd32 -> 5, Lop32 -> 5, I2f32 -> 5)),
    121 -> Harvest(ptxas133, Map(Fadd32 -> 4, Fmul32 -> 4, Imul32 -> 4, Iadd32 -> 5, Lop32 -> 5, I2f32 -> 5))
  )

  private val mnemonics: Map[String, Producer] = Map(
    "FADD"           -> Fadd32,
    "FMUL"           -> Fmul32,
    "FFMA"           -> Ffma32,
    "IADD"           -> Iadd32,
    "IADD3"          -> Iadd32,
    "IMAD"           -> Imul32,
    "LOP3"           -> Lop32,
    "SHL"            -> Shl32,
    "I2F"            -> I2f32,
    "I2FP"           -> I2f32,
    "DFMA"           -> Fma64,
    "MUFU.RCP"       -> Rcp32,
    "LDG"            -> Ldg32,
    "LDS"            -> Lds32,
    "ISETP"          -> Isetp,
    "LDG.E.CONSTANT" -> LdgConstant,
    "SHFL.DOWN"      -> ShflDown,
    "DMUL"           -> Dmul64,
    "DADD"           -> Dadd64,
    "MUFU.RCP64H"    -> Rcp64H
  )

  private val specials: Map[(String, String), Producer] = Map(
    ("S2R", "SR_TID")    -> S2rThread,
    ("S2R", "SR_LANEID") -> S2rThread,
    ("S2R", "SR_CTAID")  -> S2rBlock,
    ("S2UR", "SR_CTAID") -> S2ur
  )

  private val specialRegister = """\bSR_[A-Z]+""".r

  def producer(text: String): Option[Producer] = {
    val opcode = text.trim.split("\\s+").dropWhile(_.startsWith("@")).headOption.getOrElse("")
    val parts  = opcode.stripSuffix(";").split('.')
    specialRegister.findFirstIn(text).flatMap(register => specials.get(parts.head -> register))
      .orElse(mnemonics.get(parts.take(3).mkString(".")))
      .orElse(mnemonics.get(parts.take(2).mkString(".")))
      .orElse(mnemonics.get(parts.head))
  }

  def sm(architecture: Architecture): Option[Int] =
    Option.when(architecture.name.startsWith("sm_"))(architecture.name.drop(3).toIntOption).flatten

  def fixed(sm: Int, producer: Producer): Option[Architecture.Sourced[Int]] =
    for {
      harvest <- harvests.get(sm)
      stall   <- harvest.stalls.get(producer)
    } yield Architecture.Sourced(stall, Architecture.Source.Compiled("ptxas", harvest.ptxas, s"sm_$sm"))

  private val probed = Architecture.Source.Probed(
    "clock64 dependent chain, median over 101 launches, one thread",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 13.3"
  )

  private val probedFma64 = Architecture.Source.Probed(
    "clock64 dependent fma.rn.f64 chain, median over 6000 launches, one thread, 2026-11-10",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 13.3"
  )

  private val probedIsetp = Architecture.Source.Probed(
    "clock64 loop_control, 4096 trips, median of 201 launches: 27.01 cycles per trip against 17 for the back-edge " +
      "of an FFMA loop, leaving 13 for the predicate after IADD3 and the 4-cycle integer result",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 13.3"
  )

  private val probedS2r = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of an S2R or S2UR and its first consumer, bracket subtracted " +
      "with a control run, median over 201 launches in each of 3 runs, 1 to 34 blocks of 1 to 128 threads",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedLdgConstant = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a repeated LDG.E.CONSTANT of one address (L1 hit) and its " +
      "first consumer, control run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedShflDown = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a SHFL.DOWN of a full warp and its first consumer, control " +
      "run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedDmulDadd = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a DMUL or DADD and its first consumer, control run " +
      "subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor (4 warps give 48 to 104)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedRcp64H = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a MUFU.RCP64H and its first consumer, control run " +
      "subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val measured89: Map[Producer, Architecture.Sourced[Double]] = Map(
    Lds32       -> Architecture.Sourced(24.00, probed),
    Rcp32       -> Architecture.Sourced(28.00, probed),
    Fma64       -> Architecture.Sourced(48.01, probedFma64),
    Isetp       -> Architecture.Sourced(13.00, probedIsetp),
    S2rThread   -> Architecture.Sourced(23.00, probedS2r),
    S2rBlock    -> Architecture.Sourced(26.00, probedS2r),
    S2ur        -> Architecture.Sourced(36.00, probedS2r),
    LdgConstant -> Architecture.Sourced(32.00, probedLdgConstant),
    ShflDown    -> Architecture.Sourced(25.00, probedShflDown),
    Dmul64      -> Architecture.Sourced(48.00, probedDmulDadd),
    Dadd64      -> Architecture.Sourced(48.00, probedDmulDadd),
    Rcp64H      -> Architecture.Sourced(17.00, probedRcp64H)
  )

  def measured(sm: Int, producer: Producer): Option[Architecture.Sourced[Double]] =
    if (sm == 89) measured89.get(producer) else None

  def operandRate(architecture: Architecture, text: String): Option[Architecture.Sourced[Double]] =
    Option.when(producer(text).contains(Fma64) && text.contains("c[0"))(
      architecture.throughput.fp64Constant
    ).flatten
}
