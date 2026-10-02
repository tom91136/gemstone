package net.kurobako.gemstone.isa.ptx.v9_3

import cats.syntax.either.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*

object ISA {
  type Target = Target.Type
  object Target extends Newtype[Int >= 10] {
    def parse(value: String): Either[String, Target] = {
      val normalized = value.toLowerCaseRoot.stripPrefix("sm_").stripSuffix("a").stripSuffix("f")
      normalized.toIntOption.toRight(s"invalid SM target '$value'")
        .flatMap(Target.either(_).leftMap(_ => s"invalid SM target '$value'"))
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

  val version: Version                          = Instruction.version
  val instructions: Vec[Instruction.Definition] = Instruction.definitions

  def validate(program: Program, target: Target): ValidationOutcome[Validation.Issue] =
    Validation.validate(program, version, target)

}
