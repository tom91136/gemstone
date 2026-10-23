package net.kurobako.gemstone.isa.ptx

import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import scala.annotation.tailrec
import scala.collection.mutable

object Retained {
  private class Entry[V](value: V, val hash: Int, queue: ReferenceQueue[V]) extends SoftReference[V](value, queue)
}

/** Values a cache no longer uses, kept until the collector needs their memory. Buckets hold only soft references, so a
  * retained value keeps nothing alive. Callers serialise access.
  */
class Retained[V] {
  private val cleared = ReferenceQueue[V]()
  private val buckets = mutable.HashMap.empty[Int, List[Retained.Entry[V]]]

  private def remove(entry: Retained.Entry[?]): Unit = buckets.get(entry.hash).foreach { bucket =>
    val remaining = bucket.filterNot(_ eq entry)
    if (remaining.isEmpty) {
      val _ = buckets.remove(entry.hash)
    } else buckets.update(entry.hash, remaining)
  }

  @tailrec private def reclaim(): Unit = Option(cleared.poll()) match {
    case None                           => ()
    case Some(entry: Retained.Entry[?]) =>
      remove(entry)
      reclaim()
    case Some(_) => reclaim()
  }

  def put(hash: Int, value: V): Unit = {
    reclaim()
    buckets.update(hash, Retained.Entry(value, hash, cleared) :: buckets.getOrElse(hash, Nil))
  }

  def take(hash: Int)(matches: V => Boolean): Option[V] = {
    reclaim()
    val found =
      buckets.getOrElse(hash, Nil).view.flatMap(entry => Option(entry.get()).filter(matches).map((entry, _))).headOption
    found.foreach((entry, _) => remove(entry))
    found.map(_._2)
  }
}
