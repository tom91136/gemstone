package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

import cats.syntax.either.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object ISA {
  final case class Version(major: Int :| Positive, minor: Int :| Positive0) extends Ordered[Version] {
    override def compare(that: Version): Int = Ordering[(Int, Int)].compare((major, minor), (that.major, that.minor))
    override def toString: String            = s"$major.$minor"
  }

  object Version {
    def parse(value: String): Either[String, Version] = value match {
      case s"$major.$minor" =>
        for {
          majorValue <- major.toIntOption.toRight(s"invalid PTX version '$value'")
            .flatMap(_.refineEither[Positive].leftMap(_ => s"invalid PTX version '$value'"))
          minorValue <- minor.toIntOption.toRight(s"invalid PTX version '$value'")
            .flatMap(_.refineEither[Positive0].leftMap(_ => s"invalid PTX version '$value'"))
        } yield Version(majorValue, minorValue)
      case _ => Left(s"invalid PTX version '$value'")
    }
  }

  type Target = Target.T
  object Target extends RefinedType[Int, GreaterEqual[10]] {
    extension (target: Target) {
      def compare(other: Target): Int = target.value.compare(other.value)
      def <(other: Target): Boolean   = compare(other) < 0
      def <=(other: Target): Boolean  = compare(other) <= 0
      def >(other: Target): Boolean   = compare(other) > 0
      def >=(other: Target): Boolean  = compare(other) >= 0
      def render: String              = s"sm_${target.value}"
    }

    def parse(value: String): Either[String, Target] = {
      val normalized = value.map(_.toLower).stripPrefix("sm_").stripSuffix("a").stripSuffix("f")
      normalized.toIntOption.toRight(s"invalid SM target '$value'")
        .flatMap(Target.either(_).leftMap(_ => s"invalid SM target '$value'"))
    }
  }

  val version: Version                          = Instruction.version
  val instructions: Vec[Instruction.Definition] = Instruction.definitions

  def parse(source: String): Either[ParseError, Program] = Parser.parse(source)

  def render(program: Program): String = Printer.render(program)

  def validate(program: Program, target: Target): ValidationOutcome[Validator.Issue] =
    Validator.validate(program, version, target)

}
