/*
rule = GemstoneNoIntrinsicLocks
 */
package fix

import java.util.concurrent.locks.ReentrantLock

object NoIntrinsicLocks {
  private val monitor = Object()
  private val lock    = ReentrantLock()

  def rejected(): Unit = monitor.synchronized(()) // assert: GemstoneNoIntrinsicLocks.intrinsic-lock
  def accepted(): Unit = {
    lock.lock()
    try ()
    finally lock.unlock()
  }
  def suppressed(): Unit = monitor.synchronized(()) // scalafix:ok GemstoneNoIntrinsicLocks
}
