package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.Event
import net.kurobako.gemstone.vm.ObservationContext
import net.kurobako.gemstone.vm.Observer

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable

final class CompletionObserver extends Observer[Event] {
  private val lock  = ReentrantLock()
  private val sinks = mutable.Map.empty[ObservationContext, Event => Either[Observer.Error, Unit]]

  def register(token: TaskToken)(sink: Event => Either[Observer.Error, Unit]): Either[RuntimeError, Unit] = {
    lock.lock()
    try
      if (sinks.contains(token.observation))
        Left(RuntimeError.Lifecycle(s"task token is already registered: ${token.value}"))
      else {
        sinks(token.observation) = sink
        Right(())
      }
    finally lock.unlock()
  }

  def remove(token: TaskToken): Unit = {
    lock.lock()
    try {
      val _ = sinks.remove(token.observation)
      ()
    } finally lock.unlock()
  }

  override def observe(context: ObservationContext, event: Event): Either[Observer.Error, Unit] = {
    lock.lock()
    val sink = try sinks.get(context)
    finally lock.unlock()
    sink.toRight(Observer.Error(s"no observer registered for execution ${context.value}")).flatMap(_(event))
  }
}
