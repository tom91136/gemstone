package net.kurobako.gemstone.isa.ptx.v9_3

import scala.compiletime.{constValue, error}

import Program.*

import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

import cats.syntax.either.*

object ISA {
  final case class Version(major: PositiveInt, minor: NonNegativeInt) extends Ordered[Version] {
    override def compare(that: Version): Int =
      Ordering[(PositiveInt, NonNegativeInt)].compare((major, minor), (that.major, that.minor))
    override def toString: String = s"${major.raw}.${minor.raw}"
  }

  object Version {
    def parse(value: String): Either[String, Version] = value match {
      case s"$major.$minor" =>
        for {
          majorValue <- major.toIntOption.toRight(s"invalid PTX version '$value'")
            .flatMap(PositiveInt.either(_).leftMap(_ => s"invalid PTX version '$value'"))
          minorValue <- minor.toIntOption.toRight(s"invalid PTX version '$value'")
            .flatMap(NonNegativeInt.either(_).leftMap(_ => s"invalid PTX version '$value'"))
        } yield Version(majorValue, minorValue)
      case _ => Left(s"invalid PTX version '$value'")
    }
  }

  opaque type Target = Int
  object Target {
    inline def either(value: Int): Either[String, Target] =
      if (value >= 10) Right(value) else Left(s"SM target must be at least 10: $value")

    inline def unsafe(value: Int): Target =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): Target =
      inline if constValue[A] >= 10 then value else error("SM target must be at least 10")

    def parse(value: String): Either[String, Target] = {
      val normalized = value.map(_.toLower).stripPrefix("sm_").stripSuffix("a").stripSuffix("f")
      normalized.toIntOption.toRight(s"invalid SM target '$value'")
        .flatMap(Target.either(_).leftMap(_ => s"invalid SM target '$value'"))
    }

    extension (target: Target) {
      inline def raw: Int             = target
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

  def parse(source: String): Either[ParseError, Program] = Parser.parse(source)

  def render(program: Program): String = Printer.render(program)

  def validate(program: Program, target: Target): ValidationOutcome[Validator.Issue] =
    Validator.validate(program, version, target)

}
