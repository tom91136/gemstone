package net.kurobako.gemstone.server

/** Bounded reuse of immutable preparation results. Different images prepare concurrently; identical images share work.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
private[server] final class PreparedCache[K, V](capacity: Int) {
  require(capacity > 0)
  private final class Entry {
    private var result: Option[Either[Throwable, V]] = None
    def ready: Boolean                               = synchronized(result.nonEmpty)
    def complete(value: Either[Throwable, V]): Unit  = synchronized { result = Some(value); notifyAll() }
    def await(): V                                   = synchronized {
      while (result.isEmpty) wait()
      result.get.fold(error => throw error, identity)
    }
  }
  private val entries               = java.util.LinkedHashMap[K, Entry](capacity, 0.75f, true)
  def get(key: K)(prepare: => V): V = {
    val (entry, owner) = synchronized {
      Option(entries.get(key)) match {
        case Some(entry) => entry -> false
        case None        =>
          val entry    = Entry()
          val iterator = entries.entrySet().iterator()
          while (entries.size() >= capacity && iterator.hasNext)
            if (iterator.next().getValue.ready) iterator.remove()
          if (entries.size() < capacity) { val _ = entries.put(key, entry) }
          entry -> true
      }
    }
    if (owner) {
      val result = try Right(prepare)
      catch { case scala.util.control.NonFatal(error) => Left(error) }
      entry.complete(result)
    }
    entry.await()
  }
}
