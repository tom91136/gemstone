package net.kurobako.gemstone

import net.kurobako.gemstone.Constraint.NonNegative

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
    def nonNegativeSize: NonNegative[Int] = NonNegative.unsafe(values.size)
  }

  /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
  private[gemstone] def toArray[A: ClassTag](values: Vec[A]): Array[A] = values.toArray

  /** Transfers ownership of an array that must never be mutated again. */
  private[gemstone] def unsafeFromArray[A](values: Array[A]): Vec[A] = ArraySeq.unsafeWrapArray(values)

  def unapplySeq[A](values: Vec[A]): Some[Seq[A]] = Some(values)

  private[gemstone] def sameElements[A](left: Vec[A], right: Vec[A]): Boolean = left.sameElements(right)
}
