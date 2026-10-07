package net.kurobako.gemstone

import scala.annotation.publicInBinary
import scala.annotation.targetName
import scala.language.implicitConversions

/** Numeric predicates and nonempty values. Import `constraint.*`; add `constraint.auto.*` for proven implicit widening.
  * Numeric representations stay opaque so primitive methods cannot discard predicates. Validate runtime inputs at their
  * first domain boundary and retain the refined type in fields and helper parameters. Empty numeric domains fail at
  * compile time when validation, proofs or codecs are materialised; a type alias alone does not run these checks.
  */
object constraint {
  sealed trait GreaterThan[N <: Int | Long]
  sealed trait AtLeast[N <: Int | Long]
  sealed trait LessThan[N <: Int | Long]
  sealed trait AtMost[N <: Int | Long]
  sealed trait In[T <: NonEmptyTuple]
  type Between[L <: Int | Long, H <: Int | Long] = AtLeast[L] & AtMost[H]
  sealed trait MultipleOf[N <: Int | Long]

  sealed trait Interval

  /** Inclusive bounds. Symbolic precedence lets `Int in 1 ~ 3` omit parentheses. */
  infix sealed trait ~[L <: Int | Long, H <: Int | Long] extends Interval

  type Membership[T <: NonEmptyTuple | Interval] = T match {
    case NonEmptyTuple => In[T & NonEmptyTuple]
    case l ~ h         => Between[l, h]
  }

  // Numeric shorthands share the same opaque representation and predicate implementation.
  infix type >[A <: Byte | Short | Int | Long, N <: Int | Long]                = A where GreaterThan[N]
  infix type >=[A <: Byte | Short | Int | Long, N <: Int | Long]               = A where AtLeast[N]
  infix type <[A <: Byte | Short | Int | Long, N <: Int | Long]                = A where LessThan[N]
  infix type <=[A <: Byte | Short | Int | Long, N <: Int | Long]               = A where AtMost[N]
  infix type in[A <: Byte | Short | Int | Long, T <: NonEmptyTuple | Interval] = A where Membership[T]

  object where {
    given [A, P](using base: Ordering[A]): Ordering[A where P] = base

    implicit inline def literal[A <: Byte | Short | Int | Long, P](inline value: A): A where P = {
      checkNumericLiteral[A, P](value)
      value
    }

    // Quoted by macros; publicInBinary requires qualified visibility.
    @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
    @publicInBinary
    private[gemstone] inline def validate[A, P](value: A): A where P =
      if (accepts[A, P](value)) value else throw IllegalArgumentException(failure[A, P](value))

    @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
    @publicInBinary
    private[gemstone] inline def check[A, P](value: A): Either[NonEmpty[String], A where P] =
      if (accepts[A, P](value)) Right(value) else Left(failure[A, P](value))

    private inline def failure[A, P](value: A): String = s"${description[A, P]}: $value"
  }

  extension [A](value: A) {

    /** Check a runtime value, throwing IllegalArgumentException on failure. The target's primitive carrier must match
      * A; convert numeric representations explicitly first. Literals and provably nonempty interpolations need no
      * `.unsafe` when their expected type supplies the refinement.
      */
    transparent inline def unsafe[R]: R = ${ macros.unsafe[A, R]('value) }

    /** The same validation as `.unsafe`, returning the failure instead of throwing it. */
    inline def refine[R]: Either[NonEmpty[String], R] = ${ macros.refine[A, R]('value) }
  }

  infix opaque type where[A, P] = A

  /** Only proven implications are implicit. Runtime values and narrowing stay explicit. One conversion per carrier pair
    * keeps narrowing inapplicable, so overload resolution still prefers the narrower carrier.
    */
  object auto {
    implicit inline def implication[A, P, Q](value: A where P): A where Q     = convert[A, P, A, Q](value)
    implicit inline def byteToShort[P, Q](value: Byte where P): Short where Q = convert[Byte, P, Short, Q](value)
    implicit inline def byteToInt[P, Q](value: Byte where P): Int where Q     = convert[Byte, P, Int, Q](value)
    implicit inline def byteToLong[P, Q](value: Byte where P): Long where Q   = convert[Byte, P, Long, Q](value)
    implicit inline def shortToInt[P, Q](value: Short where P): Int where Q   = convert[Short, P, Int, Q](value)
    implicit inline def shortToLong[P, Q](value: Short where P): Long where Q = convert[Short, P, Long, Q](value)
    implicit inline def intToLong[P, Q](value: Int where P): Long where Q     = convert[Int, P, Long, Q](value)
  }

  extension [A, P](value: A where P) {
    inline def raw: A = value

    /** Prove that every source value satisfies the complete target refinement, for example `.widen[Int >= 0]`. The
      * carrier may stay the same or widen from Byte to Short, Int or Long, Short to Int or Long, or Int to Long. No
      * runtime validation or narrowing is performed.
      */
    transparent inline def widen[R]: R = ${ macros.widen[A, P, R]('value) }
  }

  // Lossless conversions retain the source carrier bounds as well as the explicit predicate.
  extension [P](value: Byte where P) {
    @targetName("byteToShortRefined")
    inline def toShort: Short where (P & Between[-128, 127]) = convert[Byte, P, Short, P & Between[-128, 127]](value)
    @targetName("byteToIntRefined")
    inline def toInt: Int where (P & Between[-128, 127]) = convert[Byte, P, Int, P & Between[-128, 127]](value)
    @targetName("byteToLongRefined")
    inline def toLong: Long where (P & Between[-128, 127]) = convert[Byte, P, Long, P & Between[-128, 127]](value)
  }

  extension [P](value: Short where P) {
    @targetName("shortToIntRefined")
    inline def toInt: Int where (P & Between[-32768, 32767]) =
      convert[Short, P, Int, P & Between[-32768, 32767]](value)
    @targetName("shortToLongRefined")
    inline def toLong: Long where (P & Between[-32768, 32767]) =
      convert[Short, P, Long, P & Between[-32768, 32767]](value)
  }

  extension [P](value: Int where P) {

    /** Narrow without truncation, retaining the predicate because the numeric value does not change. */
    def toShortExact: Short where P = {
      if (value < Short.MinValue || value > Short.MaxValue) throw ArithmeticException("Short overflow")
      value.toShort
    }
    inline def toLong: Long where (P & Between[-2147483648, 2147483647]) =
      convert[Int, P, Long, P & Between[-2147483648, 2147483647]](value)
    def <[Q](other: Int where Q): Boolean  = value < other
    def <=[Q](other: Int where Q): Boolean = value <= other
    def >[Q](other: Int where Q): Boolean  = value > other
    def >=[Q](other: Int where Q): Boolean = value >= other
  }

  extension [P](value: Long where P) {

    /** Narrow exactly, retaining the predicates because the numeric value does not change. */
    def toIntExact: Int where P = Math.toIntExact(value)
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
  extension (value: Int >= 0) {
    @targetName("divideNonnegativeInt")
    def /(other: Int > 0): Int >= 0 = value / other
    @targetName("remainderNonnegativeInt")
    def %(other: Int > 0): Int >= 0 = value % other
    @targetName("addNonnegativeInt")
    def +(other: Int >= 0): Int >= 0 = Math.addExact(value, other)
    @targetName("multiplyNonnegativeInt")
    def *(other: Int >= 0): Int >= 0 = Math.multiplyExact(value, other)
    @targetName("minNonnegativeInt")
    def min(other: Int >= 0): Int >= 0 = math.min(value, other)
    @targetName("maxNonnegativeInt")
    def max(other: Int >= 0): Int >= 0 = math.max(value, other)
    @targetName("subtractNonnegativeInt")
    def -(other: Int >= 0): Int >= 0 = {
      require(value >= other, s"negative difference: $value - $other")
      value - other
    }
  }

  extension (value: Int > 0) {

    /** Round a positive count up to whole groups without overflowing the numerator. */
    @targetName("ceilDividePositiveInt")
    def ceilDiv(other: Int > 0): Int > 0 = (value - 1) / other + 1
    @targetName("addPositiveInt")
    def +(other: Int >= 0): Int > 0 = Math.addExact(value, other)
    @targetName("multiplyPositiveInt")
    def *(other: Int > 0): Int > 0 = Math.multiplyExact(value, other)
    @targetName("minPositiveInt")
    def min(other: Int > 0): Int > 0 = math.min(value, other)
    @targetName("maxPositiveInt")
    def max(other: Int >= 0): Int > 0 = math.max(value, other)
  }

  extension (value: Long >= 0) {
    @targetName("divideNonnegativeLong")
    def /(other: Long > 0): Long >= 0 = value / other
    @targetName("remainderNonnegativeLong")
    def %(other: Long > 0): Long >= 0 = value % other
    @targetName("addNonnegativeLong")
    def +(other: Long >= 0): Long >= 0 = Math.addExact(value, other)
    @targetName("multiplyNonnegativeLong")
    def *(other: Long >= 0): Long >= 0 = Math.multiplyExact(value, other)
    @targetName("minNonnegativeLong")
    def min(other: Long >= 0): Long >= 0 = math.min(value, other)
    @targetName("maxNonnegativeLong")
    def max(other: Long >= 0): Long >= 0 = math.max(value, other)
    @targetName("subtractNonnegativeLong")
    def -(other: Long >= 0): Long >= 0 = {
      require(value >= other, s"negative difference: $value - $other")
      value - other
    }
  }

  extension (value: Long > 0) {
    @targetName("ceilDividePositiveLong")
    def ceilDiv(other: Long > 0): Long > 0 = (value - 1L) / other + 1L
    @targetName("addPositiveLong")
    def +(other: Long >= 0): Long > 0 = Math.addExact(value, other)
    @targetName("multiplyPositiveLong")
    def *(other: Long > 0): Long > 0 = Math.multiplyExact(value, other)
    @targetName("minPositiveLong")
    def min(other: Long > 0): Long > 0 = math.min(value, other)
    @targetName("maxPositiveLong")
    def max(other: Long >= 0): Long > 0 = math.max(value, other)
  }

  private inline def checkNumericLiteral[A, P](inline value: A): Unit =
    ${ macros.checkNumericLiteral[A, P]('value) }
  private inline def checkStringLiteral(inline value: String): Unit =
    ${ macros.checkStringLiteral('value) }

  private inline def accepts[A, P](value: A): Boolean = ${ macros.accepts[A, P]('value) }
  private inline def description[A, P]: String        = ${ macros.description[A, P] }

  // Quoted by macros; publicInBinary requires qualified visibility.
  @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
  @publicInBinary
  private[gemstone] inline def convert[A, P, B, Q](value: A where P): B where Q =
    ${ macros.convert[A, P, B, Q]('value) }

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

    // Quoted by macros; publicInBinary requires qualified visibility.
    @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
    @publicInBinary
    private[gemstone] def validate[A](value: A)(using HasElements[A]): NonEmpty[A] =
      check(value).fold(message => throw IllegalArgumentException(message), identity)

    @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
    @publicInBinary
    private[gemstone] def check[A](value: A)(using elements: HasElements[A]): Either[NonEmpty[String], NonEmpty[A]] =
      Either.cond(elements.nonEmpty(value), value, "value must not be empty")

    extension [A](value: NonEmpty[A]) transparent inline def raw = rawValue[A](value)
  }

  /** Nonempty strings and immutable collections. NUL restrictions belong at process and native symbol boundaries. */
  opaque type NonEmpty[A] <: A = A

  private transparent inline def rawValue[A](value: A): Any = ${ macros.raw[A]('value) }

  object Newtype {

    /** A validated representation bridge for generic serialisers. */
    trait Representation[A, C] {
      def wrap(value: C): Either[NonEmpty[String], A]
      def unwrap(value: A): C
    }
  }

  /** A distinct name for a value of C; any refinement is checked before wrapping, and every constructor rejects values
    * with a violation. Declare `type Name = Name.Type` before `object Name extends Newtype[C]`.
    */
  trait Newtype[C] {
    opaque type Type = C

    given (using base: Ordering[C]): Ordering[Type] = base

    given representation: Newtype.Representation[Type, C] with {
      def wrap(value: C): Either[NonEmpty[String], Type] = attempt(value)
      def unwrap(value: Type): C                         = value
    }

    /** Additional domain rules beyond the carrier refinement. */
    protected def violation(value: C): Option[NonEmpty[String]] = None

    inline def apply(value: C): Type = violation(value) match {
      case Some(message) => throw IllegalArgumentException(message)
      case None          => value
    }

    // Quoted by macros; publicInBinary requires qualified visibility.
    @SuppressWarnings(Array("scalafix:NoPackagePrivate"))
    @publicInBinary
    private[gemstone] def attempt(value: C): Either[NonEmpty[String], Type] = violation(value).toLeft(value)

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
