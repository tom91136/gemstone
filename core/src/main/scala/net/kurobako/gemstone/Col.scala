package net.kurobako.gemstone

// scalafix:off GemstoneRuntimeCollections; this file is the single backing-collection boundary

import scala.reflect.ClassTag
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

/** The immutable, indexed collection used by Gemstone runtime models.
  *
  * Runtime code constructs collections through [[Col]] rather than naming the backing representation. This keeps
  * collection experiments local to this file and prevents mutable arrays from crossing the ownership boundary.
  */
type Col[+A] = ArraySeq[A]

object Col {
  def empty[A: ClassTag]: Col[A] = ArraySeq.empty[A]

  def apply[A: ClassTag](values: A*): Col[A] = ArraySeq(values*)

  def from[A: ClassTag](values: IterableOnce[A]): Col[A] = ArraySeq.from(values)

  def fill[A: ClassTag](count: Int)(value: => A): Col[A] = ArraySeq.fill(count)(value)

  def tabulate[A: ClassTag](count: Int)(value: Int => A): Col[A] = ArraySeq.tabulate(count)(value)

  def newBuilder[A: ClassTag]: mutable.Builder[A, Col[A]] = ArraySeq.newBuilder[A]

  extension [A: ClassTag](values: IterableOnce[A]) def toCol: Col[A] = from(values)

  /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
  def toArray[A: ClassTag](values: Col[A]): Array[A] = values.toArray

  /** Transfers ownership of an array that must never be mutated again. */
  private[gemstone] def unsafeFromArray[A](values: Array[A]): Col[A] = ArraySeq.unsafeWrapArray(values)

  def unapplySeq[A](values: Col[A]): Some[Seq[A]] = Some(values)

  def sameElements[A](left: Col[A], right: Col[A]): Boolean = left.sameElements(right)
}

// scalafix:on GemstoneRuntimeCollections
