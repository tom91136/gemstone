package net.kurobako.gemstone

import net.kurobako.gemstone.concurrent.withLock

import java.util.concurrent.locks.ReentrantLock
import scala.jdk.CollectionConverters.*
import scala.collection.mutable
import scala.util.Try

/** Bounded LRU reuse of immutable results. Different keys compute concurrently; identical keys share in-flight work. */
private[gemstone] final class LRUCache[K, V] private (capacity: PositiveInt) {
  private final class Entry {
    private val lock      = ReentrantLock()
    private val completed = lock.newCondition()

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var result: Option[Either[Throwable, V]] = None

    def complete(value: Either[Throwable, V]): Unit = lock.withLock {
      result = Some(value)
      completed.signalAll()
    }

    def await(): Either[Throwable, V] = lock.withLock {
      while (result.isEmpty) completed.await()
      result.getOrElse(throw IllegalStateException("completed cache entry has no result"))
    }
  }

  private val lock = ReentrantLock()

  // Java's LinkedHashMap supplies access ordering; Scala's mutable LinkedHashMap is insertion ordered.
  private val mutableEntries  = java.util.LinkedHashMap[K, Entry](capacity.raw, 0.75f, true)
  private val mutableInFlight = mutable.HashMap.empty[K, Entry]

  def get(key: K)(compute: => V): V = getOnce(key)(compute)

  private def getOnce(key: K)(compute: => V): V = {
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
        val _ = mutableInFlight.remove(key)
        val _ = mutableEntries.put(key, entry)
        while (mutableEntries.size() > capacity.raw)
          mutableEntries.keySet().asScala.headOption.foreach(oldest => mutableEntries.remove(oldest))
      }
      computed.fold(error => throw error, identity)
    } else entry.await().fold(error => throw error, identity)
  }
}

private[gemstone] object LRUCache {
  @SuppressWarnings(Array("scalafix:GemstoneNoExplicitNew"))
  def apply[K, V](capacity: PositiveInt): LRUCache[K, V] = new LRUCache(capacity)
}
