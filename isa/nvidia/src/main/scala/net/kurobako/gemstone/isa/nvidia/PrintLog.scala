package net.kurobako.gemstone.isa.nvidia

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*

/** The events a grid's workgroups printed, replayed in workgroup order however the workgroups were scheduled. */
private class PrintLog {
  private val groups = ConcurrentHashMap[Long, Vec[Event]]()

  def add(group: Long, events: Iterable[Event]): Unit = if (events.nonEmpty) { groups.put(group, Vec.from(events)); () }

  def flush(handler: Event => Either[Fault, Unit]): Either[Fault, Unit] =
    groups.keySet.asScala.toVector.sorted.view.flatMap(groups.get(_)).foldLeft[Either[Fault, Unit]](Right(())) {
      (result, event) => result.flatMap(_ => handler(event))
    }
}
