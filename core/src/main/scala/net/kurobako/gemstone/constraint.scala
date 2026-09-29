package net.kurobako.gemstone

import scala.annotation.publicInBinary
import scala.annotation.targetName
import scala.language.implicitConversions

/** Numeric predicates and nonempty values. Import `constraint.*`; add `constraint.auto.*` for proven implicit widening.
  * Numeric representations stay opaque so primitive methods cannot discard predicates. Validate runtime inputs at their
  * first domain boundary and retain the refined type in fields and helper parameters.
  */
object constraint {
  sealed trait GreaterThan[N <: Int | Long]
  sealed trait AtLeast[N <: Int | Long]
  sealed trait LessThan[N <: Int | Long]
  sealed trait AtMost[N <: Int | Long]
  sealed trait In[T <: NonEmptyTuple]
  type Between[L <: Int | Long, H <: Int | Long] = AtLeast[L] & AtMost[H]
  sealed trait MultipleOf[N <: Int | Long]

  // Numeric shorthands share the same opaque representation and predicate implementation.
  infix type >[A <: Int | Long, N <: Int | Long]     = A where GreaterThan[N]
  infix type >=[A <: Int | Long, N <: Int | Long]    = A where AtLeast[N]
  infix type <[A <: Int | Long, N <: Int | Long]     = A where LessThan[N]
  infix type <=[A <: Int | Long, N <: Int | Long]    = A where AtMost[N]
  infix type in[A <: Int | Long, T <: NonEmptyTuple] = A where In[T]

  object where {
    given [A, P](using base: Ordering[A]): Ordering[A where P] = base

    implicit inline def literal[A <: Int | Long, P](inline value: A): A where P = {
      checkNumericLiteral[A, P](value)
      value
    }

    // Quoted by ConstraintMacros; publicInBinary requires qualified visibility.
    @SuppressWarnings(Array("scalafix:GemstoneNoPackagePrivate"))
    @publicInBinary
    private[gemstone] inline def validate[A, P](value: A): A where P =
      if (accepts[A, P](value)) value
      else throw IllegalArgumentException(s"${description[A, P]}: $value")
  }

  extension [A](value: A) {

    /** Check a runtime value, throwing IllegalArgumentException on failure. The target's primitive carrier must match
      * A; convert numeric representations explicitly first. Literals and provably nonempty interpolations need no
      * `.unsafe` when their expected type supplies the refinement.
      */
    transparent inline def unsafe[R]: R = ${ macros.unsafe[A, R]('value) }

    /** The same validation as `.unsafe`, returning a recoverable error. */
    inline def refine[R]: Either[NonEmpty[String], R] =
      try Right(value.unsafe[R])
      catch {
        case error: IllegalArgumentException =>
          Left(NonEmpty.validate(Option(error.getMessage).filter(_.nonEmpty).getOrElse("invalid refined value")))
      }
  }

  infix opaque type where[A, P] = A

  type OneOf[T <: NonEmptyTuple] = Int in T

  /** Only proven implications are implicit. Runtime values and narrowing stay explicit. */
  object auto {
    implicit inline def implication[A, P, Q](value: A where P): A where Q = {
      prove[A, P, A, Q]
      value
    }

    implicit inline def intToLong[P, Q](value: Int where P): Long where Q = {
      prove[Int, P, Long, Q]
      value.toLong
    }
  }

  extension [A, P](value: A where P) {
    inline def raw: A              = value
    inline def widen[Q]: A where Q = {
      prove[A, P, A, Q]
      value
    }
  }

  extension [P](value: Int where P) {
    inline def toLong: Long where P = {
      prove[Int, P, Long, P]
      value.toLong
    }
    def <[Q](other: Int where Q): Boolean  = value < other
    def <=[Q](other: Int where Q): Boolean = value <= other
    def >[Q](other: Int where Q): Boolean  = value > other
    def >=[Q](other: Int where Q): Boolean = value >= other
  }

  extension [P](value: Long where P) {
    @targetName("lessLong")

    def <[Q](other: Long where Q): Boolean = value < other
    @targetName("lessEqualLong")

    def <=[Q](other: Long where Q): Boolean = value <= other
    @targetName("greaterLong")

    def >[Q](other: Long where Q): Boolean = value > other
    @targetName("greaterEqualLong")

    def >=[Q](other: Long where Q): Boolean = value >= other
  }

  // The count arithmetic used by Gemstone. Exact operations preserve these signs.
  extension (value: Int where AtLeast[0]) {
    @targetName("addNonnegativeInt")

    def +(other: Int >= 0): Int where AtLeast[0] = Math.addExact(value, other)
    @targetName("multiplyNonnegativeInt")

    def *(other: Int where AtLeast[0]): Int where AtLeast[0] =
      Math.multiplyExact(value, other)
    @targetName("minNonnegativeInt")

    def min(other: Int where AtLeast[0]): Int where AtLeast[0] = math.min(value, other)
    @targetName("maxNonnegativeInt")

    def max(other: Int where AtLeast[0]): Int where AtLeast[0] = math.max(value, other)
    @targetName("subtractNonnegativeInt")

    def -(other: Int >= 0): Int >= 0 = {
      require(value >= other, s"negative difference: $value - $other")
      value - other
    }
  }

  extension (value: Int where GreaterThan[0]) {
    @targetName("addPositiveInt")

    def +(other: Int >= 0): Int where GreaterThan[0] = Math.addExact(value, other)
    @targetName("multiplyPositiveInt")

    def *(other: Int where GreaterThan[0]): Int where GreaterThan[0] =
      Math.multiplyExact(value, other)
    @targetName("minPositiveInt")

    def min(other: Int where GreaterThan[0]): Int where GreaterThan[0] =
      math.min(value, other)
    @targetName("maxPositiveInt")

    def max(other: Int where GreaterThan[0]): Int where GreaterThan[0] =
      math.max(value, other)
  }

  extension (value: Long where AtLeast[0]) {
    @targetName("addNonnegativeLong")

    def +(other: Long >= 0): Long where AtLeast[0] = Math.addExact(value, other)
    @targetName("multiplyNonnegativeLong")

    def *(other: Long where AtLeast[0]): Long where AtLeast[0] =
      Math.multiplyExact(value, other)
    @targetName("minNonnegativeLong")

    def min(other: Long where AtLeast[0]): Long where AtLeast[0] =
      math.min(value, other)
    @targetName("maxNonnegativeLong")

    def max(other: Long where AtLeast[0]): Long where AtLeast[0] =
      math.max(value, other)
    @targetName("subtractNonnegativeLong")

    def -(other: Long >= 0): Long >= 0 = {
      require(value >= other, s"negative difference: $value - $other")
      value - other
    }
  }

  extension (value: Long where GreaterThan[0]) {
    @targetName("addPositiveLong")

    def +(other: Long >= 0): Long where GreaterThan[0] = Math.addExact(value, other)
    @targetName("multiplyPositiveLong")

    def *(other: Long where GreaterThan[0]): Long where GreaterThan[0] =
      Math.multiplyExact(value, other)
    @targetName("minPositiveLong")

    def min(other: Long where GreaterThan[0]): Long where GreaterThan[0] =
      math.min(value, other)
    @targetName("maxPositiveLong")

    def max(other: Long where GreaterThan[0]): Long where GreaterThan[0] =
      math.max(value, other)
  }

  private inline def checkNumericLiteral[A, P](inline value: A): Unit =
    ${ macros.checkNumericLiteral[A, P]('value) }
  private inline def checkStringLiteral(inline value: String): Unit =
    ${ macros.checkStringLiteral('value) }

  private inline def accepts[A, P](value: A): Boolean = ${ macros.accepts[A, P]('value) }
  private inline def description[A, P]: String        = ${ macros.description[A, P] }
  private inline def prove[A, P, B, Q]: Unit          = ${ macros.prove[A, P, B, Q] }

  object HasElements {
    given HasElements[String] with {
      @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
      def nonEmpty(value: String): Boolean = value != null && value.nonEmpty
    }

    given [A <: scala.collection.immutable.Iterable[?]]: HasElements[A] with {
      @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
      def nonEmpty(value: A): Boolean = value != null && value.nonEmpty
    }
  }

  /** Only immutable collections receive an instance: mutation could break the refinement.
    */
  trait HasElements[-A] {
    def nonEmpty(value: A): Boolean
  }

  object NonEmpty {
    implicit inline def literal(inline value: String): NonEmpty[String] = {
      checkStringLiteral(value)
      value
    }

    // Quoted by ConstraintMacros; publicInBinary requires qualified visibility.
    @SuppressWarnings(Array("scalafix:GemstoneNoPackagePrivate"))
    @publicInBinary
    private[gemstone] def validate[A](value: A)(using HasElements[A]): NonEmpty[A] =
      if (summon[HasElements[A]].nonEmpty(value)) value
      else throw IllegalArgumentException("value must not be empty")

    extension [A](value: NonEmpty[A]) transparent inline def raw = rawValue[A](value)
  }

  /** Nonempty strings and immutable collections. NUL restrictions belong at process and native symbol boundaries. */
  opaque type NonEmpty[A] <: A = A

  private transparent inline def rawValue[A](value: A): Any = ${ macros.raw[A]('value) }

  private inline def newtypeValue[A, C](value: A): C = ${ macros.newtypeValue[A, C]('value) }

  object Newtype {

    /** A validated representation bridge for generic serializers. */
    trait Representation[A, C] {
      def wrap(value: C): Either[NonEmpty[String], A]
      def unwrap(value: A): C
    }
  }

  /** A distinct name for a value of C; any refinement is checked before wrapping. All constructors also call validate
    * for additional domain rules. Declare `type Name = Name.Type` before `object Name extends Newtype[C]`.
    */
  trait Newtype[C] {
    opaque type Type = C

    given (using base: Ordering[C]): Ordering[Type] = base

    given representation: Newtype.Representation[Type, C] with {
      def wrap(value: C): Either[NonEmpty[String], Type] = attempt(value)
      def unwrap(value: Type): C                         = value
    }

    protected def validate(value: C): Unit = ()

    inline def apply(value: C): Type = {
      validate(value)
      value
    }

    private def attempt(value: => C): Either[NonEmpty[String], Type] =
      try Right(apply(value))
      catch { case error: IllegalArgumentException => Left(s"invalid value: ${error.getMessage}") }

    inline def either[A](value: A): Either[NonEmpty[String], Type] =
      attempt(newtypeValue[A, C](value))

    extension (value: Type) {

      /** Remove the domain name while retaining its declared carrier and refinements. */
      inline def unwrap: C = value

      /** Extract the representation beneath all known newtype and refinement layers. */
      transparent inline def raw = {
        val carrier: C = value.unwrap
        rawValue[C](carrier)
      }
    }
  }
}
