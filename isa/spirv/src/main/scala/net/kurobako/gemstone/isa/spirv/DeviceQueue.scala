package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import cats.syntax.all.*
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object DeviceQueue {
  type Write = (Pointer, Array[Byte]) => Either[Fault, Unit]

  inline val Complete       = 0
  inline val Running        = 1
  inline val Submitted      = 2
  inline val Queued         = 3
  inline val FailedWaitList = -14

  inline val Success              = 0
  inline val InvalidQueue         = -102
  inline val InvalidNDRange       = -160
  inline val InvalidEventWaitList = -57
  inline val InvalidArgSize       = -51

  case class NDRange(dimensions: Int, offset: Vector[Long], global: Vector[Long], local: Vector[Long])

  enum Command {
    case Kernel(
        event: Long,
        waits: Vector[Long],
        function: Int,
        range: NDRange,
        literal: Array[Byte],
        literalAlignment: Int,
        locals: Vector[Long]
    )
    case Marker(event: Long, waits: Vector[Long])

    def event: Long
    def waits: Vector[Long]
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class EventState(
      var status: Int,
      var references: Int,
      val user: Boolean,
      val parent: Option[Long],
      val queued: Long
  ) {
    var children: Int                          = 0
    var executed: Boolean                      = false
    var start: Long                            = 0L
    var end: Long                              = 0L
    var complete: Long                         = 0L
    val captures: mutable.ArrayBuffer[Pointer] = mutable.ArrayBuffer.empty
  }
}

/** Commands that work-items enqueue to device queues. A dispatch runs them once its own work-items finish, in an order
  * that respects their wait lists; an event completes once its command and every command that command enqueued have
  * completed.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class DeviceQueue {
  import DeviceQueue.*

  private val lock                  = ReentrantLock()
  private val pending               = mutable.ArrayBuffer.empty[Command]
  private val events                = mutable.HashMap.empty[Long, EventState]
  private var nextEvent             = 1L
  private var running: Option[Long] = None

  def isEmpty: Boolean = lock.withLock(pending.isEmpty)

  private def allocate(user: Boolean, references: Int): Long = {
    val handle = nextEvent
    nextEvent += 1
    val parent = if (user) None else running
    parent.foreach(events(_).children += 1)
    events.update(
      handle,
      EventState(if (user) Submitted else Queued, references, user, parent, platform.monotonicNanos)
    )
    handle
  }

  private def waitable(waits: Vector[Long]): Boolean = waits.forall(events.contains)

  /** Queues a command and returns its event, or the enqueue status that rejected it. */
  def enqueue(waits: Vector[Long], returned: Boolean)(command: Long => Command): Either[Int, Long] = lock.withLock {
    if (!waitable(waits)) Left(InvalidEventWaitList)
    else {
      val handle = allocate(user = false, if (returned) 1 else 0)
      pending += command(handle)
      Right(handle)
    }
  }

  def userEvent(): Long = lock.withLock(allocate(user = true, 1))

  def valid(handle: Long): Boolean = lock.withLock(events.get(handle).exists(_.references > 0))

  def retain(handle: Long): Unit = lock.withLock(events.get(handle).foreach(_.references += 1))

  def release(handle: Long): Unit = lock.withLock(events.get(handle).foreach(event =>
    event.references = math.max(0, event.references - 1)
  ))

  def setStatus(handle: Long, status: Int, write: Write): Either[Fault, Unit] = lock.withLock {
    events.get(handle) match {
      case Some(event) if event.user && event.status > Complete =>
        complete(handle, if (status < Complete) status else Complete, write)
      case Some(_) => Left(Fault.InvalidRequest("set_user_event_status requires a user event that has not completed"))
      case None    => Left(Fault.InvalidRequest(s"device event $handle does not exist"))
    }
  }

  def capture(handle: Long, pointer: Pointer, write: Write): Either[Fault, Unit] = lock.withLock {
    events.get(handle) match {
      case Some(event) if event.status <= Complete => profile(event, pointer, write)
      case Some(event)                             =>
        event.captures += pointer
        Right(())
      case None => Left(Fault.InvalidRequest(s"device event $handle does not exist"))
    }
  }

  /** The next command whose wait list has completed. Commands waiting on failed events fail in turn. */
  def next(write: Write): Either[Fault, Option[Command]] = lock.withLock {
    def loop(): Either[Fault, Option[Command]] =
      pending.indexWhere(_.waits.forall(events(_).status <= Complete)) match {
        case -1    => Right(None)
        case index =>
          val command = pending.remove(index)
          if (command.waits.exists(events(_).status < Complete))
            executed(command.event, FailedWaitList, write).flatMap(_ => loop())
          else Right(Some(command))
      }
    loop()
  }

  def begin(command: Command): Unit = lock.withLock {
    val event = events(command.event)
    event.status = Running
    event.start = platform.monotonicNanos
    running = Some(command.event)
  }

  def finish(command: Command, write: Write): Either[Fault, Unit] = lock.withLock {
    running = None
    executed(command.event, Complete, write)
  }

  private def executed(handle: Long, status: Int, write: Write): Either[Fault, Unit] = {
    val event = events(handle)
    event.end = platform.monotonicNanos
    event.executed = true
    event.status = if (status < Complete) status else Running
    if (event.children == 0) complete(handle, status, write) else Right(())
  }

  private def complete(handle: Long, status: Int, write: Write): Either[Fault, Unit] = {
    val event = events(handle)
    event.status = status
    if (event.end == 0L) event.end = platform.monotonicNanos
    event.complete = platform.monotonicNanos
    for {
      _ <- event.captures.toVector.traverse_(profile(event, _, write))
      _ <- event.parent.fold[Either[Fault, Unit]](Right(())) { parentHandle =>
        val parent = events(parentHandle)
        parent.children -= 1
        if (parent.executed && parent.children == 0) complete(parentHandle, parent.status.min(Complete), write)
        else Right(())
      }
    } yield ()
  }

  private def profile(event: EventState, pointer: Pointer, write: Write): Either[Fault, Unit] = {
    val start     = if (event.start == 0L) event.queued else event.start
    val execution = math.max(1L, event.end - start)
    val bytes     = Array.ofDim[Byte](16)
    LittleEndian.write(execution, bytes, 0, 8)
    LittleEndian.write(math.max(execution, event.complete - start), bytes, 8, 8)
    write(pointer, bytes)
  }
}
