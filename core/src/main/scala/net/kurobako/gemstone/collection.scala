package net.kurobako.gemstone

import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
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

    def tabulate[A: ClassTag](count: Int)(value: Int => A): Vec[A] = ArraySeq.tabulate(count)(value)

    def tabulateNonEmpty[A: ClassTag](count: Int > 0)(value: Int >= 0 => A): NonEmpty[Vec[A]] =
      ArraySeq.tabulate(count.raw)(index => value(index.unsafe[Int >= 0])).unsafe[NonEmpty[Vec[A]]]

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
      inline if (scala.compiletime.constValue[From] >= scala.compiletime.constValue[Until])
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
      ArraySeq.tabulate[Int >= 0](count.raw)(index => index.unsafe[Int >= 0])

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
  }

  extension [A](values: Vec[A]) {

    /** Every adjacent pair satisfies ordered; empty and singleton vectors are monotonic. */
    def monotonic(ordered: (A, A) => Boolean): Boolean = {
      @scala.annotation.tailrec
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

  object LRUCache {
    def apply[K, V](capacity: Int > 0): LRUCache[K, V] = new LRUCache(capacity)
  }

  /** Bounded LRU reuse of immutable results. Different keys compute concurrently; identical keys share in-flight work.
    */
  class LRUCache[K, V] private (capacity: Int > 0) {
    private val lock = ReentrantLock()

    private case class Key(value: K) {
      private val hash             = value.##
      override def hashCode(): Int = hash
    }

    // Java's LinkedHashMap supplies access ordering; Scala's mutable LinkedHashMap is insertion ordered.
    private val mutableEntries  = java.util.LinkedHashMap[Key, FutureTask[V]](capacity.raw, 0.75f, true)
    private val mutableInFlight = mutable.HashMap.empty[Key, FutureTask[V]]

    def get(key: K)(compute: => V): V = {
      // Large immutable byte keys must be hashed once, before taking the shared lock. Publication,
      // in-flight lookup and eviction all reuse that hash instead of rescanning the module bytes.
      val lookup         = Key(key)
      val (entry, owner) = lock.withLock {
        Option(mutableEntries.get(lookup)) match {
          case Some(entry) => entry -> false
          case None        => mutableInFlight.get(lookup) match {
              case Some(entry) => entry -> false
              case None        =>
                val entry = FutureTask[V](() => compute)
                val _     = mutableInFlight.put(lookup, entry)
                entry -> true
            }
        }
      }
      if (owner) {
        entry.run()
        lock.withLock {
          val _ = mutableInFlight.remove(lookup)
          val _ = mutableEntries.put(lookup, entry)
          while (mutableEntries.size() > capacity.raw)
            mutableEntries.keySet().asScala.headOption.foreach(oldest => mutableEntries.remove(oldest))
        }
      }
      try entry.get()
      catch { case error: ExecutionException => throw error.getCause }
    }
  }
}
