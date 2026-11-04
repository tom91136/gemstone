package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.compiletime.constValue
import scala.reflect.ClassTag

object collection {

  object Vec {
    def empty[A: ClassTag]: Vec[A] = ArraySeq.empty[A]

    def apply[A: ClassTag](values: A*): Vec[A] = ArraySeq(values*)

    def nonEmpty[A: ClassTag](first: A, rest: A*): NonEmpty[Vec[A]] =
      ArraySeq.from(first +: rest).unsafe

    def from[A: ClassTag](values: IterableOnce[A]): Vec[A] = ArraySeq.from(values)

    /** Wraps the array without copying. The caller must transfer ownership and never mutate the array again. */
    def unsafeWrapArray[A](values: Array[A]): Vec[A] = ArraySeq.unsafeWrapArray(values)

    def fill[A: ClassTag](count: Int)(value: => A): Vec[A] = ArraySeq.fill(count)(value)

    inline def tabulate[A: ClassTag](count: Int)(inline value: Int => A): Vec[A] = ArraySeq.tabulate(count)(value)

    def tabulateNonEmpty[A: ClassTag](count: Int > 0)(value: Int >= 0 => A): NonEmpty[Vec[A]] =
      ArraySeq.tabulate(count)(index => value(index.unsafe[Int >= 0])).unsafe[NonEmpty[Vec[A]]]

    /** Construct a half-open integer range whose elements retain the compile-time bounds. Empty ranges are allowed;
      * reversed ranges and ranges too large for an indexed Vec are rejected before allocation.
      */
    inline def range[From <: Int, Until <: Int](using
        fromValue: ValueOf[From],
        untilValue: ValueOf[Until],
        tag: ClassTag[Int where (AtLeast[From] & LessThan[Until])]
    ): Vec[Int where (AtLeast[From] & LessThan[Until])] = {
      val from  = fromValue.value
      val until = untilValue.value
      if (from > until) throw IllegalArgumentException(s"invalid integer range [$from, $until)")
      // Empty ranges have no elements whose refinement needs to be materialised.
      inline if (constValue[From] >= constValue[Until])
        ArraySeq.empty[Int where (AtLeast[From] & LessThan[Until])](using tag)
      else {
        val count = until.toLong - from.toLong
        if (count > Int.MaxValue.toLong)
          throw IllegalArgumentException(s"integer range [$from, $until) exceeds Vec capacity")
        ArraySeq.tabulate[Int where (AtLeast[From] & LessThan[Until])](count.toInt) { index =>
          (from.toLong + index.toLong).toInt.unsafe[Int where (AtLeast[From] & LessThan[Until])]
        }(using tag)
      }
    }

    /** Generate the zero-based indexes for a runtime-sized sequence, retaining nonnegative evidence. */
    def indices(count: Int >= 0): Vec[Int >= 0] =
      ArraySeq.tabulate[Int >= 0](count)(index => index.unsafe[Int >= 0])

    def newBuilder[A: ClassTag]: mutable.Builder[A, Vec[A]] = ArraySeq.newBuilder[A]

    extension [A: ClassTag](values: IterableOnce[A]) def toVec: Vec[A] = from(values)

    extension [A](values: Vec[A]) {
      def nonNegativeSize: Int >= 0 = values.size.unsafe[Int >= 0]
    }

    extension [A](values: NonEmpty[Vec[A]]) {
      def positiveSize: Int > 0 = values.size.unsafe[Int > 0]

      def mapNonEmpty[B](f: A => B): NonEmpty[Vec[B]] = values.map(f).unsafe[NonEmpty[Vec[B]]]

      def flatMapNonEmpty[B](f: A => NonEmpty[Vec[B]]): NonEmpty[Vec[B]] =
        values.flatMap(f).unsafe[NonEmpty[Vec[B]]]

      def concatNonEmpty[B >: A](other: Vec[B]): NonEmpty[Vec[B]] = (values ++ other).unsafe[NonEmpty[Vec[B]]]
    }

    /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
    def toArray[A: ClassTag](values: Vec[A]): Array[A] = values.toArray

    def unapplySeq[A](values: Vec[A]): Some[Seq[A]] = Some(values)

    def sameElements[A](left: Vec[A], right: Vec[A]): Boolean = left.sameElements(right)

    /** The maximal runs of consecutive integers in `values`, each as its first and last value, in ascending order. */
    def runs(values: IterableOnce[Int]): Vec[(Int, Int)] =
      from(Vector.from(values).sorted.distinct.foldLeft(Vector.empty[(Int, Int)]) {
        case (done :+ ((first, last)), value) if value == last + 1 => done :+ ((first, value))
        case (done, value)                                         => done :+ ((value, value))
      })
  }

  extension [A](values: Vec[A]) {

    /** Every adjacent pair satisfies ordered; empty and singleton vectors are monotonic. */
    def monotonic(ordered: (A, A) => Boolean): Boolean = {
      @tailrec
      def loop(index: Int): Boolean =
        index >= values.size || (ordered(values(index - 1), values(index)) && loop(index + 1))
      loop(1)
    }
  }

  /** The immutable, indexed collection used by Gemstone runtime models.
    *
    * Runtime code constructs collections through [[Vec]] rather than naming the backing representation. This keeps
    * collection experiments local to this namespace. Array ownership transfer is explicit through `unsafeWrapArray`.
    */
  type Vec[+A] = ArraySeq[A]
}
