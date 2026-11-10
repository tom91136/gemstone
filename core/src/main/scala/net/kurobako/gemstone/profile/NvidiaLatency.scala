package net.kurobako.gemstone.profile

object NvidiaLatency {

  enum Producer derives CanEqual {
    case Fadd32, Fmul32, Ffma32, Iadd32, Imul32, Lop32, Shl32, I2f32, Fma64, Rcp32, Ldg32, Lds32
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
    "FADD"     -> Fadd32,
    "FMUL"     -> Fmul32,
    "FFMA"     -> Ffma32,
    "IADD"     -> Iadd32,
    "IADD3"    -> Iadd32,
    "IMAD"     -> Imul32,
    "LOP3"     -> Lop32,
    "SHL"      -> Shl32,
    "I2F"      -> I2f32,
    "I2FP"     -> I2f32,
    "DFMA"     -> Fma64,
    "MUFU.RCP" -> Rcp32,
    "LDG"      -> Ldg32,
    "LDS"      -> Lds32
  )

  def producer(text: String): Option[Producer] = {
    val opcode = text.trim.split("\\s+").dropWhile(_.startsWith("@")).headOption.getOrElse("")
    val parts  = opcode.stripSuffix(";").split('.')
    mnemonics.get(parts.take(2).mkString(".")).orElse(mnemonics.get(parts.head))
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

  private val measured89: Map[Producer, Double] = Map(Lds32 -> 24.00, Rcp32 -> 28.00)

  def measured(sm: Int, producer: Producer): Option[Architecture.Sourced[Double]] =
    if sm == 89 then measured89.get(producer).map(Architecture.Sourced(_, probed)) else None
}
