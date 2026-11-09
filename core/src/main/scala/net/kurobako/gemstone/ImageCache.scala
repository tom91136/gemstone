package net.kurobako.gemstone

import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

object ImageCache {

  /** A counted reference to a cached value. Releasing keeps the value retained while memory allows once no reference
    * remains; discarding drops it. Only the first call has an effect.
    */
  @SuppressWarnings(Array("scalafix:QualifiedPrivate.redundant-qualifier"))
  class Lease[V] private[ImageCache] (val value: V, finish: Boolean => Unit) {
    private val finished = AtomicBoolean(false)

    def release(): Unit = if (finished.compareAndSet(false, true)) finish(true)

    def discard(): Unit = if (finished.compareAndSet(false, true)) finish(false)
  }
}

/** Shares the immutable product of loading an image between concurrent and successive loads of equal content. Loads of
  * different images run concurrently; equal images wait on one load. Failed loads are never kept, so the next caller
  * retries. Values without a lease stay retained under memory pressure only.
  */
class ImageCache[E, V](retainReleased: Boolean = true) {
  private class Entry(val key: ImageKey, load: () => Either[E, V]) {
    val result: PlatformConcurrency.Once[Either[E, V]] = PlatformConcurrency.once(load)

    // Includes callers still waiting for the load, so a completed load cannot evict their entry.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var references: Int >= 0 = 0
  }

  private val lock     = ReentrantLock()
  private val live     = mutable.HashMap.empty[ImageKey, Entry]
  private val retained = Retained[Entry]()

  private def evict(entry: Entry): Unit = if (live.get(entry.key).exists(_ eq entry)) {
    val _ = live.remove(entry.key)
  }

  private def unreference(entry: Entry, retain: Boolean): Unit = lock.withLock {
    entry.references -= 1
    if (entry.references == 0) { evict(entry); if (retain && retainReleased) retained.put(entry.key.hashCode, entry) }
  }

  /** The loaded value for `key`, running `load` unless an equal image is loaded or loading. Thrown failures propagate
    * to every waiter.
    */
  def acquire(key: ImageKey)(load: => Either[E, V]): Either[E, ImageCache.Lease[V]] = {
    val entry = lock.withLock {
      val found = live.getOrElseUpdate(key, retained.take(key.hashCode)(_.key == key).getOrElse(Entry(key, () => load)))
      found.references += 1
      found
    }
    val loaded =
      try {
        entry.result.run()
        try entry.result.get()
        catch { case error: ExecutionException => throw error.getCause }
      } catch {
        case error: Throwable =>
          unreference(entry, retain = false)
          throw error
      }
    loaded match {
      case Right(value) => Right(ImageCache.Lease(value, unreference(entry, _)))
      case Left(error)  =>
        lock.withLock(evict(entry))
        unreference(entry, retain = false)
        Left(error)
    }
  }
}
