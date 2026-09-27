package net.kurobako.gemstone

import net.kurobako.gemstone.Constraint.{IntBetween, OneOf}

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import scala.compiletime.ops.boolean.&&
import scala.compiletime.ops.int.{%, <=, >, >=}
import scala.compiletime.ops.long.{<= as longLe, >= as longGe}
import scala.compiletime.ops.string.{Length, Matches}
import scala.compiletime.{constValue, constValueTuple, erasedValue}

import scala.annotation.targetName

/** Reusable, zero-wrapper refinements and nominal newtype companions. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoTypeCasting"))
object Constraint {

  /** Total widenings between numeric refinements: constraint entailment and numeric widening. */
  trait Widens[A, B] {
    def apply(value: A): B
  }

  object Widens {
    given [A]: Widens[A, A] with {
      def apply(value: A): A = value
    }

    given [N]: Widens[Positive[N], NonNegative[N]] with {
      def apply(value: Positive[N]): NonNegative[N] = value
    }

    given positiveIntToPositiveLong: Widens[Positive[Int], Positive[Long]] with {
      def apply(value: Positive[Int]): Positive[Long] = value.toLong
    }

    given nonNegativeIntToNonNegativeLong: Widens[NonNegative[Int], NonNegative[Long]] with {
      def apply(value: NonNegative[Int]): NonNegative[Long] = value.toLong
    }

    given positiveIntToNonNegativeLong: Widens[Positive[Int], NonNegative[Long]] with {
      def apply(value: Positive[Int]): NonNegative[Long] = value.toLong
    }

    given [N]: Widens[Positive[N], N] with {
      def apply(value: Positive[N]): N = value
    }

    given [N]: Widens[NonNegative[N], N] with {
      def apply(value: NonNegative[N]): N = value
    }

    given positiveIntToLong: Widens[Positive[Int], Long] with {
      def apply(value: Positive[Int]): Long = value.toLong
    }

    given nonNegativeIntToLong: Widens[NonNegative[Int], Long] with {
      def apply(value: NonNegative[Int]): Long = value.toLong
    }
  }

  into opaque type IntBetween[L <: Int, H <: Int] <: Int = Int
  object IntBetween {
    given [L <: Int, H <: Int]: Ordering[IntBetween[L, H]] =
      Ordering.Int.asInstanceOf[Ordering[IntBetween[L, H]]]

    given [L <: Int, H <: Int, N <: Int & Singleton](using
        ((L <= N) && (N <= H)) =:= true
    ): Conversion[N, IntBetween[L, H]] with {
      inline def apply(value: N): IntBetween[L, H] = value
    }

    def either[L <: Int, H <: Int](value: Int)(using
        low: ValueOf[L],
        high: ValueOf[H]
    ): Either[String, IntBetween[L, H]] =
      if (low.value > high.value) Left("range lower bound exceeds upper bound")
      else if (value >= low.value && value <= high.value) Right(value)
      else Left(s"value must be between ${low.value} and ${high.value}: $value")

    /** Throws on invalid input; never constructs an invalid value. */
    def unsafe[L <: Int, H <: Int](value: Int)(using
        low: ValueOf[L],
        high: ValueOf[H]
    ): IntBetween[L, H] =
      if (low.value <= high.value && value >= low.value && value <= high.value)
        value
      else
        either[L, H](value)
          .fold(message => throw IllegalArgumentException(message), identity)

    extension [L <: Int, H <: Int](value: IntBetween[L, H]) inline def raw: Int = value
  }

  into opaque type LongBetween[L <: Long, H <: Long] <: Long = Long
  object LongBetween {
    given [L <: Long, H <: Long]: Ordering[LongBetween[L, H]] =
      Ordering.Long.asInstanceOf[Ordering[LongBetween[L, H]]]

    given [L <: Long, H <: Long, N <: Long & Singleton](using
        ((longLe[L, N]) && (longLe[N, H])) =:= true
    ): Conversion[N, LongBetween[L, H]] with {
      inline def apply(value: N): LongBetween[L, H] = value
    }

    def either[L <: Long, H <: Long](value: Long)(using
        low: ValueOf[L],
        high: ValueOf[H]
    ): Either[String, LongBetween[L, H]] =
      if (low.value > high.value) Left("range lower bound exceeds upper bound")
      else if (value >= low.value && value <= high.value) Right(value)
      else Left(s"value must be between ${low.value} and ${high.value}: $value")

    def unsafe[L <: Long, H <: Long](value: Long)(using
        low: ValueOf[L],
        high: ValueOf[H]
    ): LongBetween[L, H] =
      if (low.value <= high.value && value >= low.value && value <= high.value)
        value
      else
        either[L, H](value)
          .fold(message => throw IllegalArgumentException(message), identity)
  }

  /** An inclusive Int range whose members are divisible by a positive factor.
    */
  into opaque type MultipleIn[L <: Int, H <: Int, F <: Int] <: Int = Int
  object MultipleIn {
    given [L <: Int, H <: Int, F <: Int]: Ordering[MultipleIn[L, H, F]] =
      Ordering.Int.asInstanceOf[Ordering[MultipleIn[L, H, F]]]

    given [L <: Int, H <: Int, F <: Int, N <: Int & Singleton](using
        ((L <= N) && (N <= H)) =:= true,
        (F > 0) =:= true,
        (N % F) =:= 0
    ): Conversion[N, MultipleIn[L, H, F]] with {
      inline def apply(value: N): MultipleIn[L, H, F] = value
    }

    def either[L <: Int, H <: Int, F <: Int](value: Int)(using
        low: ValueOf[L],
        high: ValueOf[H],
        factor: ValueOf[F]
    ): Either[String, MultipleIn[L, H, F]] =
      if (low.value > high.value || factor.value <= 0)
        Left("invalid multiple range bounds")
      else if (value >= low.value && value <= high.value && value % factor.value == 0) Right(value)
      else
        Left(
          s"value must be a multiple of ${factor.value} between ${low.value} and ${high.value}: $value"
        )

    def unsafe[L <: Int, H <: Int, F <: Int](value: Int)(using
        low: ValueOf[L],
        high: ValueOf[H],
        factor: ValueOf[F]
    ): MultipleIn[L, H, F] =
      if (
        low.value <= high.value && factor.value > 0 &&
        value >= low.value && value <= high.value && value % factor.value == 0
      ) value
      else
        either[L, H, F](value)
          .fold(message => throw IllegalArgumentException(message), identity)

    extension [L <: Int, H <: Int, F <: Int](value: MultipleIn[L, H, F]) inline def raw: Int = value
  }

  /** Tuple syntax supplies arbitrary arity: `OneOf[(1, 2, 4, 8)]`. */
  type TupleUnion[T <: Tuple] = T match {
    case EmptyTuple => Nothing
    case h *: t     => h | TupleUnion[t]
  }

  sealed trait IntChoices[T <: Tuple] {
    def contains(value: Int): Boolean
    def values: List[Int]
  }

  object IntChoices {
    given IntChoices[EmptyTuple] with {
      def contains(value: Int): Boolean = false
      def values: List[Int]             = Nil
    }

    given [H <: Int & Singleton, T <: Tuple](using
        head: ValueOf[H],
        tail: IntChoices[T]
    ): IntChoices[H *: T] with {
      def contains(value: Int): Boolean =
        value == head.value || tail.contains(value)
      def values: List[Int] = head.value :: tail.values
    }
  }

  /** Compile-time proof that every tuple member has a concrete literal value.
    */
  opaque type LiteralIntChoices[T <: Tuple] = Unit
  object LiteralIntChoices {
    inline def prove[T <: Tuple]: Unit =
      inline erasedValue[T] match {
        case _: EmptyTuple => ()
        case _: (h *: t)   =>
          constValue[h]
          prove[t]
      }

    inline given [T <: Tuple]: LiteralIntChoices[T] = {
      prove[T]
      ()
    }
  }

  into opaque type OneOf[T <: NonEmptyTuple] <: Int = Int

  object OneOf {
    given [T <: NonEmptyTuple]: Ordering[OneOf[T]] =
      Ordering.Int.asInstanceOf[Ordering[OneOf[T]]]

    private inline def containsLiteral[T <: Tuple](value: Int): Boolean =
      inline erasedValue[T] match {
        case _: EmptyTuple => false
        case _: (h *: t)   =>
          value == constValue[h].asInstanceOf[Int] ||
          containsLiteral[t](value)
      }

    inline given [T <: NonEmptyTuple, N <: Int & Singleton](using
        LiteralIntChoices[T],
        TupleUnion[T] <:< Int,
        N <:< TupleUnion[T]
    ): Conversion[N, OneOf[T]] with {
      inline def apply(value: N): OneOf[T] = value
    }

    inline def either[T <: NonEmptyTuple](value: Int)(using
        TupleUnion[T] <:< Int
    ): Either[String, OneOf[T]] =
      if (containsLiteral[T](value)) Right(value)
      else
        Left(
          s"value must be one of ${constValueTuple[T].productIterator.mkString(", ")}: $value"
        )

    inline def unsafe[T <: NonEmptyTuple](
        value: Int
    )(using TupleUnion[T] <:< Int): OneOf[T] =
      if (containsLiteral[T](value)) value
      else
        throw IllegalArgumentException(
          s"value must be one of ${constValueTuple[T].productIterator.mkString(", ")}: $value"
        )

    /** Fallback when the tuple type is abstract rather than known. */
    def eitherDynamic[T <: NonEmptyTuple](value: Int)(using
        choices: IntChoices[T]
    ): Either[String, OneOf[T]] =
      if (choices.contains(value)) Right(value)
      else
        Left(s"value must be one of ${choices.values.mkString(", ")}: $value")

    def unsafeDynamic[T <: NonEmptyTuple](value: Int)(using
        choices: IntChoices[T]
    ): OneOf[T] =
      if (choices.contains(value)) value
      else
        throw IllegalArgumentException(
          s"value must be one of ${choices.values.mkString(", ")}: $value"
        )

    extension [T <: NonEmptyTuple](value: OneOf[T]) inline def raw: Int = value
  }

  /** Generic runtime validation requires Numeric[N]; literal proofs cover Int and Long.
    */
  into opaque type NonNegative[N] = N

  object NonNegative {

    /** Preserve ordering even when the underlying numeric type is abstract. */
    given [N](using base: Ordering[N]): Ordering[NonNegative[N]] =
      base

    extension [N](value: NonNegative[N]) {
      def <(other: NonNegative[N])(using ordering: Ordering[N]): Boolean =
        ordering.lt(value, other)
      def <=(other: NonNegative[N])(using ordering: Ordering[N]): Boolean =
        ordering.lteq(value, other)
      def >(other: NonNegative[N])(using ordering: Ordering[N]): Boolean =
        ordering.gt(value, other)
      def >=(other: NonNegative[N])(using ordering: Ordering[N]): Boolean =
        ordering.gteq(value, other)
    }

    extension (value: NonNegative[Int]) {
      @targetName("nonNegativeIntAddNonNegative")
      def +(other: NonNegative[Int]): NonNegative[Int] = NonNegative.unsafe(Math.addExact(value, other))

      @targetName("nonNegativeIntAddPositive")
      def +(other: Positive[Int]): Positive[Int] = Positive.unsafe(Math.addExact(value, other))

      def *(other: NonNegative[Int]): NonNegative[Int] = NonNegative.unsafe(Math.multiplyExact(value, other))
      def -(other: NonNegative[Int]): NonNegative[Int] = NonNegative.unsafe(Math.subtractExact(value, other))

      def min(other: NonNegative[Int]): NonNegative[Int] = if (value <= other) value else other
      def max(other: NonNegative[Int]): NonNegative[Int] = if (value >= other) value else other
    }

    extension (value: NonNegative[Long]) {
      def toNonNegativeInt: Either[String, NonNegative[Int]] =
        if (value <= Int.MaxValue.toLong) NonNegative.either(value.toInt)
        else Left(s"value exceeds Int range: $value")

      @targetName("nonNegativeLongAddNonNegative")
      def +(other: NonNegative[Long]): NonNegative[Long] = NonNegative.unsafe(Math.addExact(value, other))

      @targetName("nonNegativeLongAddPositive")
      def +(other: Positive[Long]): Positive[Long] = Positive.unsafe(Math.addExact(value, other))

      def *(other: NonNegative[Long]): NonNegative[Long] = NonNegative.unsafe(Math.multiplyExact(value, other))
      def -(other: NonNegative[Long]): NonNegative[Long] = NonNegative.unsafe(Math.subtractExact(value, other))

      def min(other: NonNegative[Long]): NonNegative[Long] = if (value <= other) value else other
      def max(other: NonNegative[Long]): NonNegative[Long] = if (value >= other) value else other
    }

    given intLiteral[N <: Int & Singleton](using (N >= 0) =:= true): Conversion[
      N,
      NonNegative[Int]
    ] with {
      inline def apply(value: N): NonNegative[Int] = value
    }

    given longLiteral[N <: Long & Singleton](using
        longGe[N, 0L] =:= true
    ): Conversion[N, NonNegative[Long]] with {
      inline def apply(value: N): NonNegative[Long] = value
    }

    def either[N: {Numeric}](value: N): Either[String, NonNegative[N]] = {
      val numeric = summon[Numeric[N]]
      if (value != null && numeric.gteq(value, numeric.zero)) Right(value)
      else Left(s"value must be nonnegative: $value")
    }

    def unsafe[N: {Numeric}](value: N): NonNegative[N] = {
      val numeric = summon[Numeric[N]]
      if (value != null && numeric.gteq(value, numeric.zero))
        value
      else throw IllegalArgumentException(s"value must be nonnegative: $value")
    }

    extension [N](value: NonNegative[N]) def raw: N = value

    extension [N](value: NonNegative[N]) def widen[B](using w: Widens[NonNegative[N], B]): B = w(value)
  }

  /** A strictly positive value; literal proofs cover Int and Long. */
  into opaque type Positive[N] = N

  object Positive {
    given [N](using base: Ordering[N]): Ordering[Positive[N]] =
      base.asInstanceOf[Ordering[Positive[N]]]

    extension [N](value: Positive[N]) {
      def <(other: Positive[N])(using ordering: Ordering[N]): Boolean =
        ordering.lt(value, other)
      def <=(other: Positive[N])(using ordering: Ordering[N]): Boolean =
        ordering.lteq(value, other)
      def >(other: Positive[N])(using ordering: Ordering[N]): Boolean =
        ordering.gt(value, other)
      def >=(other: Positive[N])(using ordering: Ordering[N]): Boolean =
        ordering.gteq(value, other)
    }

    extension (value: Positive[Int]) {
      @targetName("positiveIntAddPositive")
      def +(other: Positive[Int]): Positive[Int] = Positive.unsafe(Math.addExact(value, other))

      @targetName("positiveIntAddNonNegative")
      def +(other: NonNegative[Int]): Positive[Int] = Positive.unsafe(Math.addExact(value, other))

      def *(other: Positive[Int]): Positive[Int] = Positive.unsafe(Math.multiplyExact(value, other))

      def min(other: Positive[Int]): Positive[Int] = if (value <= other) value else other
      def max(other: Positive[Int]): Positive[Int] = if (value >= other) value else other
    }

    extension (value: Positive[Long]) {
      def toPositiveInt: Either[String, Positive[Int]] =
        if (value <= Int.MaxValue.toLong) Positive.either(value.toInt)
        else Left(s"value exceeds Int range: $value")

      def +(other: Positive[Long]): Positive[Long] = Positive.unsafe(Math.addExact(value, other))
      def *(other: Positive[Long]): Positive[Long] = Positive.unsafe(Math.multiplyExact(value, other))

      def min(other: Positive[Long]): Positive[Long] = if (value <= other) value else other
      def max(other: Positive[Long]): Positive[Long] = if (value >= other) value else other
    }

    given intLiteral[N <: Int & Singleton](using (N >= 1) =:= true): Conversion[N, Positive[Int]] with {
      inline def apply(value: N): Positive[Int] = value
    }

    given longLiteral[N <: Long & Singleton](using longGe[N, 1L] =:= true): Conversion[N, Positive[Long]] with {
      inline def apply(value: N): Positive[Long] = value
    }

    def either[N: {Numeric}](value: N): Either[String, Positive[N]] = {
      val numeric = summon[Numeric[N]]
      if (value != null && numeric.gt(value, numeric.zero)) Right(value)
      else Left(s"value must be strictly positive: $value")
    }

    def unsafe[N: {Numeric}](value: N): Positive[N] = {
      val numeric = summon[Numeric[N]]
      if (value != null && numeric.gt(value, numeric.zero)) value
      else throw IllegalArgumentException(s"value must be strictly positive: $value")
    }

    extension [N](value: Positive[N]) def raw: N = value

    extension [N](value: Positive[N]) def widen[B](using w: Widens[Positive[N], B]): B = w(value)
  }

  /** Only immutable collections receive an instance: mutation could break the refinement.
    */
  trait HasElements[-A] {
    def nonEmpty(value: A): Boolean
  }

  object HasElements {
    given HasElements[String] with {
      def nonEmpty(value: String): Boolean = value != null && value.nonEmpty
    }

    given [A <: scala.collection.immutable.Iterable[?]]: HasElements[A] with {
      def nonEmpty(value: A): Boolean = value != null && value.nonEmpty
    }
  }

  into opaque type NonEmpty[A] <: A = A

  object NonEmpty {
    given [S <: String & Singleton](using
        (Length[S] > 0) =:= true
    ): Conversion[S, NonEmpty[String]] with {
      inline def apply(value: S): NonEmpty[String] = value
    }

    def either[A](
        value: A
    )(using check: HasElements[A]): Either[String, NonEmpty[A]] =
      if (check.nonEmpty(value)) Right(value)
      else Left("value must not be empty")

    def unsafe[A](value: A)(using HasElements[A]): NonEmpty[A] =
      if (summon[HasElements[A]].nonEmpty(value)) value
      else throw IllegalArgumentException("value must not be empty")

    extension [A](value: NonEmpty[A]) def raw: A = value
  }

  /** A string with no NUL character; the empty string is valid. */
  into opaque type NoNulString <: String = String

  object NoNulString {
    type ValidLiteral[S <: String] = Matches[S, "[^\u0000]*"] =:= true

    given [S <: String & Singleton](using ValidLiteral[S]): Conversion[
      S,
      NoNulString
    ] with {
      inline def apply(value: S): NoNulString = value
    }

    inline def apply[S <: String & Singleton](inline value: S)(using
        ValidLiteral[S]
    ): NoNulString = value

    def either(value: String): Either[String, NoNulString] =
      if (value == null || value.contains('\u0000'))
        Left("value must not contain a NUL character")
      else Right(value)

    def unsafe(value: String): NoNulString =
      if (value != null && !value.contains('\u0000')) value
      else
        throw IllegalArgumentException("value must not contain a NUL character")

    extension (value: NoNulString) inline def raw: String = value
  }

  /** A string that is both non-empty and NUL-free. */
  into opaque type NonEmptyString <: String = String

  object NonEmptyString {
    type ValidLiteral[S <: String] = ((Length[S] > 0) && Matches[S, "[^\u0000]*"]) =:= true

    given [S <: String & Singleton](using ValidLiteral[S]): Conversion[
      S,
      NonEmptyString
    ] with {
      inline def apply(value: S): NonEmptyString = value
    }

    inline def apply[S <: String & Singleton](inline value: S)(using
        ValidLiteral[S]
    ): NonEmptyString = value

    def either(value: String): Either[String, NonEmptyString] =
      if (value == null || value.isEmpty) Left("value must not be empty")
      else if (value.contains('\u0000'))
        Left("value must not contain a NUL character")
      else Right(value)

    def unsafe(value: String): NonEmptyString =
      if (value == null || value.isEmpty)
        throw IllegalArgumentException("value must not be empty")
      else if (value.contains('\u0000'))
        throw IllegalArgumentException("value must not contain a NUL character")
      else value

    extension (value: NonEmptyString) {
      inline def raw: String                = value
      inline def toNoNulString: NoNulString = value
    }
  }

  /** How to validate a raw value as a reusable structural constraint. */
  trait Checked[C, R] {
    def either(value: R): Either[String, C]
    def raw(value: C): R
    def unsafe(value: R): C =
      either(value).fold(
        message => throw IllegalArgumentException(message),
        identity
      )
  }

  object Checked {

    /** Primitive entry points avoid an extra box at generic typeclass dispatch.
      */
    trait IntValue[C] extends Checked[C, Int] {
      def eitherInt(value: Int): Either[String, C]
      def unsafeInt(value: Int): C
      final def either(value: Int): Either[String, C] = eitherInt(value)
      final override def unsafe(value: Int): C        = unsafeInt(value)
    }

    trait LongValue[C] extends Checked[C, Long] {
      def eitherLong(value: Long): Either[String, C]
      def unsafeLong(value: Long): C
      final def either(value: Long): Either[String, C] = eitherLong(value)
      final override def unsafe(value: Long): C        = unsafeLong(value)
    }

    given [L <: Int, H <: Int](using
        ValueOf[L],
        ValueOf[H],
        (L <= H) =:= true
    ): IntValue[IntBetween[L, H]] with {
      def eitherInt(value: Int): Either[String, IntBetween[L, H]] =
        IntBetween.either[L, H](value)
      def unsafeInt(value: Int): IntBetween[L, H] =
        IntBetween.unsafe[L, H](value)
      def raw(value: IntBetween[L, H]): Int = value
    }

    given [L <: Long, H <: Long](using
        ValueOf[L],
        ValueOf[H],
        longLe[L, H] =:= true
    ): LongValue[LongBetween[L, H]] with {
      def eitherLong(value: Long): Either[String, LongBetween[L, H]] =
        LongBetween.either[L, H](value)
      def unsafeLong(value: Long): LongBetween[L, H] =
        LongBetween.unsafe[L, H](value)
      def raw(value: LongBetween[L, H]): Long = value
    }

    given [L <: Int, H <: Int, F <: Int](using
        ValueOf[L],
        ValueOf[H],
        ValueOf[F],
        (L <= H) =:= true,
        (F > 0) =:= true
    ): IntValue[MultipleIn[L, H, F]] with {
      def eitherInt(value: Int): Either[String, MultipleIn[L, H, F]] =
        MultipleIn.either[L, H, F](value)
      def unsafeInt(value: Int): MultipleIn[L, H, F] =
        MultipleIn.unsafe[L, H, F](value)
      def raw(value: MultipleIn[L, H, F]): Int = value
    }

    given [T <: NonEmptyTuple](using
        IntChoices[T]
    ): IntValue[OneOf[T]] with {
      def eitherInt(value: Int): Either[String, OneOf[T]] =
        OneOf.eitherDynamic[T](value)
      def unsafeInt(value: Int): OneOf[T] =
        OneOf.unsafeDynamic[T](value)
      def raw(value: OneOf[T]): Int = value
    }

    given IntValue[NonNegative[Int]] with {
      def eitherInt(value: Int): Either[String, NonNegative[Int]] =
        NonNegative.either(value)
      def unsafeInt(value: Int): NonNegative[Int] = NonNegative.unsafe(value)
      def raw(value: NonNegative[Int]): Int       = value
    }

    given LongValue[NonNegative[Long]] with {
      def eitherLong(value: Long): Either[String, NonNegative[Long]] =
        NonNegative.either(value)
      def unsafeLong(value: Long): NonNegative[Long] =
        NonNegative.unsafe(value)
      def raw(value: NonNegative[Long]): Long = value
    }

    given [N: {Numeric}]: Checked[NonNegative[N], N] with {
      def either(value: N): Either[String, NonNegative[N]] =
        NonNegative.either(value)
      override def unsafe(value: N): NonNegative[N] = NonNegative.unsafe(value)
      def raw(value: NonNegative[N]): N             = value
    }

    given IntValue[Positive[Int]] with {
      def eitherInt(value: Int): Either[String, Positive[Int]] =
        Positive.either(value)
      def unsafeInt(value: Int): Positive[Int] = Positive.unsafe(value)
      def raw(value: Positive[Int]): Int       = value
    }

    given LongValue[Positive[Long]] with {
      def eitherLong(value: Long): Either[String, Positive[Long]] =
        Positive.either(value)
      def unsafeLong(value: Long): Positive[Long] = Positive.unsafe(value)
      def raw(value: Positive[Long]): Long        = value
    }

    given [N: {Numeric}]: Checked[Positive[N], N] with {
      def either(value: N): Either[String, Positive[N]] = Positive.either(value)
      override def unsafe(value: N): Positive[N]        = Positive.unsafe(value)
      def raw(value: Positive[N]): N                    = value
    }

    given [A](using HasElements[A]): Checked[NonEmpty[A], A] with {
      def either(value: A): Either[String, NonEmpty[A]] = NonEmpty.either(value)
      override def unsafe(value: A): NonEmpty[A]        = NonEmpty.unsafe(value)
      def raw(value: NonEmpty[A]): A                    = value
    }

    given Checked[NoNulString, String] with {
      def either(value: String): Either[String, NoNulString] =
        NoNulString.either(value)
      override def unsafe(value: String): NoNulString =
        NoNulString.unsafe(value)
      def raw(value: NoNulString): String = value
    }

    given Checked[NonEmptyString, String] with {
      def either(value: String): Either[String, NonEmptyString] =
        NonEmptyString.either(value)
      override def unsafe(value: String): NonEmptyString =
        NonEmptyString.unsafe(value)
      def raw(value: NonEmptyString): String = value
    }
  }

  /** A distinct nominal type over an existing checked constraint. */
  trait Newtype[C](val label: String)(using
      private val baseCheck: Checked[C, ?]
  ) {
    opaque type Type = C

    /** A nominal type inherits ordering only when its constraint has one. */
    given (using base: Ordering[C]): Ordering[Type] =
      base.asInstanceOf[Ordering[Type]]

    inline def either[R](value: R)(using
        check: Checked[C, R]
    ): Either[String, Type] =
      check.either(value).left.map(message => s"$label $message")

    inline def either(value: Int)(using
        check: Checked.IntValue[C]
    ): Either[String, Type] =
      check.eitherInt(value).left.map(message => s"$label $message")

    inline def either(value: Long)(using
        check: Checked.LongValue[C]
    ): Either[String, Type] =
      check.eitherLong(value).left.map(message => s"$label $message")

    /** Throws on invalid input; never bypasses validation. */
    inline def unsafe[R](value: R)(using check: Checked[C, R]): Type =
      try check.unsafe(value)
      catch {
        case failure: IllegalArgumentException =>
          throw IllegalArgumentException(s"$label ${failure.getMessage}")
      }

    inline def unsafe(value: Int)(using check: Checked.IntValue[C]): Type =
      try check.unsafeInt(value)
      catch {
        case failure: IllegalArgumentException =>
          throw IllegalArgumentException(s"$label ${failure.getMessage}")
      }

    inline def unsafe(value: Long)(using check: Checked.LongValue[C]): Type =
      try check.unsafeLong(value)
      catch {
        case failure: IllegalArgumentException =>
          throw IllegalArgumentException(s"$label ${failure.getMessage}")
      }

    inline def apply[N <: Singleton](inline value: N)(using
        inline conversion: Conversion[N, C]
    ): Type =
      conversion(value): C

    /** Revalidate at the nominal boundary, including externally supplied constraints.
      */
    inline def fromConstraint(value: C): Type =
      try baseCheck.unsafe(baseCheck.raw(value))
      catch {
        case failure: IllegalArgumentException =>
          throw IllegalArgumentException(s"$label ${failure.getMessage}")
      }

    extension (value: Type) {
      inline def raw: C = value
    }
  }

  object Newtype {

    /** An unconstrained nominal type, where every value of `A` is valid. */
    trait Plain[A](val label: String) {
      opaque type Type = A

      given (using base: Ordering[A]): Ordering[Type] =
        base.asInstanceOf[Ordering[Type]]

      inline def either(value: A): Either[String, Type] = Right(value)
      inline def unsafe(value: A): Type                 = value
      inline def apply(value: A): Type                  = value

      extension (value: Type) {
        inline def raw: A = value
      }
    }
  }
}
