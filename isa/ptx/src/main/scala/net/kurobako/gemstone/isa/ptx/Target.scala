package net.kurobako.gemstone.isa.ptx

import cats.syntax.either.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*

type Target = Target.Type
object Target extends Newtype[Int >= 10] {
  def parse(value: String): Either[String, Target] = {
    val normalised = value.toLowerCaseRoot.stripPrefix("sm_").stripSuffix("a").stripSuffix("f")
    normalised.toIntOption.toRight(s"invalid SM target '$value'")
      .flatMap(_.refine[Target].leftMap(_ => s"invalid SM target '$value'"))
  }

  extension (target: Target) {
    def compare(other: Target): Int = java.lang.Integer.compare(target.raw, other.raw)
    def <(other: Target): Boolean   = compare(other) < 0
    def <=(other: Target): Boolean  = compare(other) <= 0
    def >(other: Target): Boolean   = compare(other) > 0
    def >=(other: Target): Boolean  = compare(other) >= 0
    def render: String              = s"sm_$target"
  }
}
