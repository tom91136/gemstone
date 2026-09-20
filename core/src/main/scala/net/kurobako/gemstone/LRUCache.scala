package net.kurobako.gemstone

import net.kurobako.gemstone.concurrent.withLock

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable
import scala.util.Try

/** Bounded LRU reuse of immutable results. Different keys compute concurrently; identical keys share in-flight work. */
private[gemstone] final class LRUCache[K, V] private (
    capacity: Int :| Positive,
    retain: V => (() => Option[V])
) {
  private final class Entry {
    private val lock      = ReentrantLock()
    private val completed = lock.newCondition()

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var result: Option[Either[Throwable, () => Option[V]]] = None

    def complete(value: Either[Throwable, V]): Unit = lock.withLock {
      result = Some(value.map(retain))
      completed.signalAll()
    }

    def await(): Either[Throwable, Option[V]] = lock.withLock {
      while (result.isEmpty) completed.await()
      result.getOrElse(throw IllegalStateException("completed cache entry has no result"))
        .map(value => value())
    }
  }

  private val lock = ReentrantLock()

  // Java's LinkedHashMap supplies access ordering; Scala's mutable LinkedHashMap is insertion ordered.
  private val mutableEntries  = java.util.LinkedHashMap[K, Entry](capacity, 0.75f, true)
  private val mutableInFlight = mutable.HashMap.empty[K, Entry]

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def get(key: K)(compute: => V): V = {
    var value: Option[V] = None
    while (value.isEmpty)
      value = getOnce(key)(compute)
    value.getOrElse(throw IllegalStateException("live cache entry has no value"))
  }

  private def getOnce(key: K)(compute: => V): Option[V] = {
    val (entry, owner) = lock.withLock {
      Option(mutableEntries.get(key)) match {
        case Some(entry) => entry -> false
        case None        => mutableInFlight.get(key) match {
            case Some(entry) => entry -> false
            case None        =>
              val entry = Entry()
              val _     = mutableInFlight.put(key, entry)
              entry -> true
          }
      }
    }
    if (owner) {
      val computed = Try(compute).toEither
      entry.complete(computed)
      lock.withLock {
        val _        = mutableInFlight.remove(key)
        val _        = mutableEntries.put(key, entry)
        val iterator = mutableEntries.entrySet().iterator()
        while (mutableEntries.size() > capacity && iterator.hasNext) {
          val _ = iterator.next()
          iterator.remove()
        }
      }
      computed.fold(error => throw error, value => Some(value))
    } else {
      entry.await().fold(error => throw error, identity) match {
        case value @ Some(_) => value
        case None            =>
          lock.withLock {
            if (mutableEntries.get(key) eq entry) {
              val _ = mutableEntries.remove(key)
            }
          }
          None
      }
    }
  }
}

private[gemstone] object LRUCache {
  def apply[K, V](capacity: Int :| Positive): LRUCache[K, V] =
    retained(capacity, value => () => Some(value))

  def softValues[K, V <: AnyRef](capacity: Int :| Positive): LRUCache[K, V] =
    retained(
      capacity,
      value => {
        val reference = java.lang.ref.SoftReference(value)
        () => Option(reference.get())
      }
    )

  @SuppressWarnings(Array("scalafix:GemstoneNoExplicitNew"))
  private def retained[K, V](capacity: Int :| Positive, retain: V => (() => Option[V])): LRUCache[K, V] =
    new LRUCache(capacity, retain)
}
