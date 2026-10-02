package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*

object Target {
  def parse(source: String): Either[String, Target] = {
    val number = source match {
      case s"sm_$value" => value
      case s"SM$value"  => value
      case _            => source
    }
    for {
      sm      <- number.toIntOption.toRight(s"invalid SASS target $source")
      checked <- sm.refine[Int >= 35].left.map(_.raw)
      target = Target(checked)
      _ <- Either.cond(Metadata.catalogue.targets.exists(_.name == target.name), (), s"unsupported SASS target $source")
    } yield target
  }
}

case class Target private (sm: Int >= 35) {
  def name: String = s"SM$sm"
}
