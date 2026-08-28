package net.kurobako.gemstone

import scala.reflect.ClassTag
import scala.collection.mutable

/** The immutable, indexed collection used by Gemstone runtime models.
  *
  * Runtime code constructs collections through [[Col]] rather than naming the backing representation. This keeps
  * collection experiments local to this file and prevents mutable arrays from crossing the ownership boundary.
  */
type Col[+A] = IArray[A]

object Col {
  def empty[A: ClassTag]: Col[A] = IArray.empty[A]

  def apply[A: ClassTag](values: A*): Col[A] = IArray(values*)

  def from[A: ClassTag](values: IterableOnce[A]): Col[A] = IArray.from(values)

  def fill[A: ClassTag](count: Int)(value: => A): Col[A] = IArray.fill(count)(value)

  def tabulate[A: ClassTag](count: Int)(value: Int => A): Col[A] = IArray.tabulate(count)(value)

  def newBuilder[A: ClassTag]: mutable.Builder[A, Col[A]] = new mutable.Builder[A, Col[A]] {
    private val values = mutable.ArrayBuilder.make[A]

    override def addOne(value: A): this.type = {
      values += value
      this
    }

    override def clear(): Unit = values.clear()

    override def result(): Col[A] = IArray.unsafeFromArray(values.result())
  }

  extension [A: ClassTag](values: IterableOnce[A]) def toCol: Col[A] = from(values)

  /** Copies a runtime collection into mutable storage for a Java/native interop boundary. */
  def toArray[A: ClassTag](values: Col[A]): Array[A] = IArray.genericWrapArray(values).toArray

  /** Transfers ownership of an array that must never be mutated again. */
  private[gemstone] def unsafeFromArray[A](values: Array[A]): Col[A] = IArray.unsafeFromArray(values)

  def unapplySeq[A](values: Col[A]): Some[Seq[A]] = Some(values.toSeq)

  def sameElements[A](left: Col[A], right: Col[A]): Boolean = left.sameElements(right)
}
