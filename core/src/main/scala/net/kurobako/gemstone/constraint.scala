package net.kurobako.gemstone

import scala.annotation.publicInBinary
import scala.annotation.targetName
import scala.language.implicitConversions
import scala.util.NotGiven
import upickle.default.ReadWriter

/** Numeric predicates and nonempty values. Import `constraint.*`; add `constraint.auto.*` for proven implicit widening.
  * A refined value converts implicitly to its carrier wherever the carrier is expected; carrier operations return the
  * bare carrier, and only proofs, validation and the checked count arithmetic below retain a predicate. Validate
  * runtime inputs at their first domain boundary and retain the refined type in fields and helper parameters. Empty
  * numeric domains fail at compile time when validation, proofs or codecs are materialised; a type alias alone does not
  * run these checks.
  */
object constraint extends Unrefined {

  /** The literal types a bound takes. Integer carriers take `Int` and `Long` literals; `Float` and `Double` take any,
    * and a NaN fails every bound. An `Int` or `Long` bound on a `Float` or `Double` must be exactly representable.
    */
  type Bound = Int | Long | Float | Double

  sealed trait GreaterThan[N <: Bound]
  sealed trait AtLeast[N <: Bound]
  sealed trait LessThan[N <: Bound]
  sealed trait AtMost[N <: Bound]
  sealed trait MultipleOf[N <: Int | Long]

  sealed trait Interval

  /** Inclusive bounds. Symbolic precedence lets `Int in 1 ~ 3` omit parentheses. */
  infix sealed trait ~[L <: Bound, H <: Bound] extends Interval

  /** Any of the values `T` admits: a literal, an interval, a tuple of literals, or a union of these, such as
    * `(0 ~ 17) | (24 ~ 31)`. Precedence lets `UInt[5] in (0 ~ 17) | (24 ~ 31)` omit the outer parentheses.
    */
  sealed trait Membership[T <: NonEmptyTuple | Interval | Bound]
  type Between[L <: Bound, H <: Bound] = Membership[L ~ H]

  // Shorthands for Float, Double and any carrier with an IntegerView.
  infix type >[A, N <: Bound]                             = A where GreaterThan[N]
  infix type >=[A, N <: Bound]                            = A where AtLeast[N]
  infix type <[A, N <: Bound]                             = A where LessThan[N]
  infix type <=[A, N <: Bound]                            = A where AtMost[N]
  infix type in[A, T <: NonEmptyTuple | Interval | Bound] = A where Membership[T]

  object IntegerView {
    given IntegerView[Byte, 8, true]   = _.toLong
    given IntegerView[Short, 16, true] = _.toLong
    given IntegerView[Int, 32, true]   = _.toLong
    given IntegerView[Long, 64, true]  = value => value
  }

  /** Refinements of an integer type `A` read its values as the integers of a `W`-bit two's complement representation,
    * or an unsigned one unless `S`. `toLong` gives the bits; an unsigned 64-bit value above `Long.MaxValue` reads as
    * negative.
    */
  trait IntegerView[A, W <: Int, S <: Boolean] {
    def toLong(value: A): Long
  }

  object where {
    given [A, P](using base: Ordering[A]): Ordering[A where P] = base

    // Refined values compare boxed, which for Float and Double is not IEEE equality, so those compare through .raw.
    given [A, P, B, Q](using
        CanEqual[A, B],
        NotGiven[A <:< (Float | Double)],
        NotGiven[B <:< (Float | Double)]
    ): CanEqual[A where P, B where Q] = CanEqual.derived

    // An integer literal compares with, or as a pattern matches, a refined integer. The search sees both operands
    // widened, so the macro reads the literal from the source; it does not expand in an inline method's body, which
    // therefore goes unchecked.
    transparent inline given [A, P, B <: AnyVal](using
        CanEqual[A, B],
        NotGiven[A <:< (Float | Double)]
    ): CanEqual[A where P, B] = ${ macros.literalComparison[A where P, B](leading = false) }

    transparent inline given [A, P, B <: AnyVal](using
        CanEqual[B, A],
        NotGiven[A <:< (Float | Double)]
    ): CanEqual[B, A where P] = ${ macros.literalComparison[B, A where P](leading = true) }

    /** Reading checks the refinement, failing with the violated predicate and value; writing emits the carrier. */
    inline given [A, P](using base: ReadWriter[A]): ReadWriter[A where P] = base.bimap(_.raw, validate[A, P](_))

    implicit inline def literal[A <: Byte | Short | Int | Long | Float | Double, P](inline value: A): A where P = {
      checkNumericLiteral[A, P](value)
      value
    }

    // Quoted by macros; publicInBinary requires qualified visibility.
    @publicInBinary
    private[gemstone] inline def validate[A, P](value: A): A where P =
      if (accepts[A, P](value)) value else throw IllegalArgumentException(failure[A, P](value))

    @publicInBinary
    private[gemstone] inline def check[A, P](value: A): Either[NonEmpty[String], A where P] =
      if (accepts[A, P](value)) Right(value) else Left(failure[A, P](value))

    private inline def failure[A, P](value: A): String = s"${description[A, P]}: $value"
  }

  extension [A](value: A) {

    /** Check a runtime value, throwing IllegalArgumentException on failure. A must be the target's carrier or a
      * refinement of it; convert numeric representations explicitly first. Literals and provably nonempty
      * interpolations need no `.unsafe` when their expected type supplies the refinement.
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

    /** The value typed as its carrier, where no expected type selects the conversion, as in `.map(_.raw)`. */
    inline def raw: A = value

    /** Prove that every source value satisfies the complete target refinement, for example `.widen[Int >= 0]`. The
      * carrier may stay the same or widen from Byte to Short, Int or Long, Short to Int or Long, or Int to Long. No
      * runtime validation or narrowing is performed.
      */
    transparent inline def widen[R]: R = ${ macros.widen[A, P, R]('value) }
  }

  extension [F[+_], A, P](values: F[A where P]) {

    /** The elements typed as their carrier, without copying, as `.sum` needs. Only a covariant container qualifies: it
      * offers no way to store a value outside the refinement through the original.
      */
    inline def rawElements: F[A] = values
  }

  extension [P](value: Int where P) {

    /** Narrow without truncation, retaining the predicate because the numeric value does not change. */
    def toShortExact: Short where P = {
      if (value < Short.MinValue || value > Short.MaxValue) throw ArithmeticException("Short overflow")
      value.toShort
    }
  }

  extension [P](value: Long where P) {

    /** Narrow exactly, retaining the predicates because the numeric value does not change. */
    def toIntExact: Int where P = Math.toIntExact(value)
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

    /** The least multiple of `alignment` that is at least this, failing on overflow. */
    @targetName("alignUpNonnegativeInt")
    def alignUp(alignment: Int > 0): Int >= 0 = {
      val remainder = value % alignment
      if (remainder == 0) value else Math.addExact(value, alignment - remainder)
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

    /** The least multiple of `alignment` that is at least this, failing on overflow. */
    @targetName("alignUpNonnegativeLong")
    def alignUp(alignment: Long > 0): Long >= 0 = {
      val remainder = value % alignment
      if (remainder == 0L) value else Math.addExact(value, alignment - remainder)
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

  extension (value: Int) {

    /** The least multiple of the positive `alignment` that is at least this nonnegative value, failing on overflow. */
    @targetName("alignUpInt")
    def alignUp(alignment: Int): Int = {
      val remainder = value % alignment
      if (remainder == 0) value else Math.addExact(value, alignment - remainder)
    }
  }

  extension (value: Long) {

    /** The least multiple of the positive `alignment` that is at least this nonnegative value, failing on overflow. */
    @targetName("alignUpLong")
    def alignUp(alignment: Long): Long = {
      val remainder = value % alignment
      if (remainder == 0L) value else Math.addExact(value, alignment - remainder)
    }
  }

  private inline def checkNumericLiteral[A, P](inline value: A): Unit =
    ${ macros.checkNumericLiteral[A, P]('value) }
  private inline def checkStringLiteral(inline value: String): Unit =
    ${ macros.checkStringLiteral('value) }

  private inline def accepts[A, P](value: A): Boolean = ${ macros.accepts[A, P]('value) }
  private inline def description[A, P]: String        = ${ macros.description[A, P] }

  // Quoted by macros; publicInBinary requires qualified visibility.
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
    @publicInBinary
    private[gemstone] def validate[A](value: A)(using HasElements[A]): NonEmpty[A] =
      check(value).fold(message => throw IllegalArgumentException(message), identity)

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

    given (using CanEqual[C, C]): CanEqual[Type, Type] = CanEqual.derived

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
