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

  def fixed(sm: Int, producer: Producer): Option[Architecture.Sourced[Int]] =
    for {
      harvest <- harvests.get(sm)
      stall   <- harvest.stalls.get(producer)
    } yield Architecture.Sourced(stall, Architecture.Source.Compiled("ptxas", harvest.ptxas, s"sm_$sm"))
}
