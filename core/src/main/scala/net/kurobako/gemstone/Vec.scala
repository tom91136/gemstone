package net.kurobako.gemstone

import scala.reflect.ClassTag
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

/** The immutable, indexed collection used by Gemstone runtime models.
  *
  * Runtime code constructs collections through [[Vec]] rather than naming the backing representation. This keeps
  * collection experiments local to this file and prevents mutable arrays from crossing the ownership boundary.
  */
type Vec[+A] = ArraySeq[A]

object Vec {
  def empty[A: ClassTag]: Vec[A] = ArraySeq.empty[A]

  def apply[A: ClassTag](values: A*): Vec[A] = ArraySeq(values*)

  def nonEmpty[A: ClassTag](first: A, rest: A*): NonEmptyVec[A] =
    NonEmptyVec.unsafeFromVec(ArraySeq.from(first +: rest))

  def from[A: ClassTag](values: IterableOnce[A]): Vec[A] = ArraySeq.from(values)

  def fill[A: ClassTag](count: Int)(value: => A): Vec[A] = ArraySeq.fill(count)(value)

  def tabulate[A: ClassTag](count: Int)(value: Int => A): Vec[A] = ArraySeq.tabulate(count)(value)

  private[gemstone] def newBuilder[A: ClassTag]: mutable.Builder[A, Vec[A]] = ArraySeq.newBuilder[A]

  extension [A: ClassTag](values: IterableOnce[A]) def toVec: Vec[A] = from(values)

  extension [A](values: Vec[A]) {
    def nonNegativeSize: NonNegativeInt = NonNegativeInt.unsafe(values.size)
  }

  /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
  private[gemstone] def toArray[A: ClassTag](values: Vec[A]): Array[A] = values.toArray

  /** Transfers ownership of an array that must never be mutated again. */
  private[gemstone] def unsafeFromArray[A](values: Array[A]): Vec[A] = ArraySeq.unsafeWrapArray(values)

  def unapplySeq[A](values: Vec[A]): Some[Seq[A]] = Some(values)

  private[gemstone] def sameElements[A](left: Vec[A], right: Vec[A]): Boolean = left.sameElements(right)
}

/** A [[Vec]] that is statically known to hold at least one element. */
type NonEmptyVec[+A] = cats.data.NonEmptyVector[A]

object NonEmptyVec {
  def apply[A](first: A, rest: A*): NonEmptyVec[A] = cats.data.NonEmptyVector.of(first, rest*)

  def fromVec[A](values: Vec[A]): Either[String, NonEmptyVec[A]] =
    fromVector(values.toVector)

  def fromVector[A](values: Vector[A]): Either[String, NonEmptyVec[A]] =
    cats.data.NonEmptyVector.fromVector(values).toRight("collection must not be empty")

  def unsafeFromVec[A](values: Vec[A]): NonEmptyVec[A] =
    fromVec(values).fold(message => throw IllegalArgumentException(message), identity)

  extension [A](values: NonEmptyVec[A]) {
    def toVec(using ClassTag[A]): Vec[A] = Vec.from(values.toVector)
    def positiveSize: PositiveInt        = PositiveInt.unsafe(values.length)
  }
}
