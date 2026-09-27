package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.vm.{Event, ObservationContext, Observer}

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable

final class CompletionObserver extends Observer[Event] {
  private val lock  = ReentrantLock()
  private val sinks = mutable.Map.empty[ObservationContext, Event => Either[Observer.Error, Unit]]

  def register(
      context: ObservationContext
  )(sink: Event => Either[Observer.Error, Unit]): Either[ExecutionFailure, Unit] =
    lock.withLock {
      if (sinks.contains(context))
        Left(ExecutionFailure.Failed(s"task token is already registered: ${context.value}"))
      else {
        sinks(context) = sink
        Right(())
      }
    }

  def remove(context: ObservationContext): Unit = lock.withLock { val _ = sinks.remove(context) }

  override def observe(context: ObservationContext, event: Event): Either[Observer.Error, Unit] =
    lock.withLock(sinks.get(context))
      .toRight(Observer.Error(s"no observer registered for execution ${context.value}"))
      .flatMap(_(event))
}
