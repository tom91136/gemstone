package net.kurobako.gemstone

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

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

  def nonEmpty[A: ClassTag](first: A, rest: A*): Vec[A] :| MinLength[1] =
    ArraySeq.from(first +: rest).refineUnsafe[MinLength[1]]

  def from[A: ClassTag](values: IterableOnce[A]): Vec[A] = ArraySeq.from(values)

  def fill[A: ClassTag](count: Int)(value: => A): Vec[A] = ArraySeq.fill(count)(value)

  def tabulate[A: ClassTag](count: Int)(value: Int => A): Vec[A] = ArraySeq.tabulate(count)(value)

  private[gemstone] def newBuilder[A: ClassTag]: mutable.Builder[A, Vec[A]] = ArraySeq.newBuilder[A]

  extension [A: ClassTag](values: IterableOnce[A]) def toVec: Vec[A] = from(values)

  extension [A](values: Vec[A]) {
    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    def nonNegativeSize: Int :| Positive0 = values.size.assume[Positive0]
  }

  extension [A](values: Vec[A] :| MinLength[1]) {
    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    def positiveSize: Int :| Positive = values.size.assume[Positive]
  }

  /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
  private[gemstone] def toArray[A: ClassTag](values: Vec[A]): Array[A] = values.toArray

  /** Transfers ownership of an array that must never be mutated again. */
  private[gemstone] def unsafeFromArray[A](values: Array[A]): Vec[A] = ArraySeq.unsafeWrapArray(values)

  def unapplySeq[A](values: Vec[A]): Some[Seq[A]] = Some(values)

  private[gemstone] def sameElements[A](left: Vec[A], right: Vec[A]): Boolean = left.sameElements(right)
}
