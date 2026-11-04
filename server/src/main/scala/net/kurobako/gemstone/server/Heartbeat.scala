package net.kurobako.gemstone.server

import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.rpc.*
import scala.util.control.NonFatal

/** Tells a client blocked on a long request that the server is alive. Responses and heartbeats share one lock and a
  * heartbeat is only written between `begin` and `respond`, so the client sees them in order, ahead of the response.
  */
class Heartbeat(raw: OutputStream) extends AutoCloseable {
  private val lock   = ReentrantLock()
  private val closed = AtomicBoolean(false)
  private val frame  = ByteBuffer.allocate(Integer.BYTES).putInt(ExecutionRPC.HeartbeatMagic).array()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var executing = false
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastWrite = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var thread = Option.empty[Thread]

  private val interval = TimeUnit.MILLISECONDS.toNanos(ExecutionRPC.HeartbeatMillis.raw.toLong)

  def begin(): Unit = lock.withLock {
    executing = true
    lastWrite = platform.monotonicNanos
    if (thread.isEmpty && !closed.get()) {
      val started = Thread(() => run(), "gemstone-rpc-heartbeat")
      started.setDaemon(true)
      started.start()
      thread = Some(started)
    }
  }

  def respond(write: => Unit): Unit = lock.withLock {
    executing = false
    write
  }

  private def run(): Unit =
    try
      while (!closed.get()) {
        Thread.sleep(ExecutionRPC.HeartbeatMillis.raw / 2L)
        lock.withLock {
          val now = platform.monotonicNanos
          if (executing && now - lastWrite >= interval) {
            raw.write(frame)
            raw.flush()
            lastWrite = now
          }
        }
      }
    catch { case NonFatal(_) | _: InterruptedException => () }

  override def close(): Unit = if (closed.compareAndSet(false, true)) {
    val running = lock.withLock(thread)
    running.foreach { value =>
      value.interrupt()
      value.join(1000L)
    }
  }
}
