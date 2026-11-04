package net.kurobako.gemstone

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException

/** Computes each key's value at most once and keeps it. Different keys compute concurrently; a computation that throws
  * is dropped, so the next caller retries.
  */
class Memo[K, V] {
  private val entries = ConcurrentHashMap[K, PlatformConcurrency.Once[V]]()

  def get(key: K)(compute: => V): V = {
    val once = Option(entries.get(key)).getOrElse {
      val fresh = PlatformConcurrency.once(() => compute)
      Option(entries.putIfAbsent(key, fresh)).getOrElse {
        fresh.run()
        fresh
      }
    }
    try once.get()
    catch {
      case error: ExecutionException =>
        val _ = entries.remove(key, once)
        throw error.getCause
    }
  }
}
