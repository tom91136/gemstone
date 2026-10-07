package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*

object Target {
  def parse(source: String): Either[String, Target] = {
    val number = source match {
      case s"sm_$value" => value
      case s"SM$value"  => value
      case _            => source
    }
    number.toIntOption.flatMap(_.refine[Int >= 35].toOption).toRight(s"invalid SASS target $source").flatMap(of(_))
  }

  def of(sm: Int >= 35): Either[String, Target] =
    Either.cond(NVDisasm.supports(s"SM$sm"), Target(sm), s"unsupported SASS target sm_$sm")
}

/** An architecture with recovered decoding tables. */
case class Target private (sm: Int >= 35) {
  def name: String                = s"SM$sm"
  def generation: Generation      = Generation.of(sm)
  def database: NVDisasm.Database = NVDisasm.database(name)
}
