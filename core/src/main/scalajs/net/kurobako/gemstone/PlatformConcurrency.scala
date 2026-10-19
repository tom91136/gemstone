package net.kurobako.gemstone

import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/** JavaScript runs one thread per realm, so locks never contend and nothing waits on another thread. */
private object PlatformConcurrency {
  class Once[A](compute: () => A) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var result: Option[Either[Throwable, A]] = None

    def run(): Unit = if (result.isEmpty) result = Some(outcome())

    private def outcome(): Either[Throwable, A] =
      try Right(compute())
      catch { case error: Throwable => Left(error) }

    def get(): A = result match {
      case Some(Right(value)) => value
      case Some(Left(error))  => throw ExecutionException(error)
      case None               => throw IllegalStateException("task has not run")
    }
  }

  class ReadWriteLock {
    private val lock      = ReentrantLock()
    def readLock(): Lock  = lock
    def writeLock(): Lock = lock
  }

  /** Waiting on one thread could only end by a signal from another, which never comes. */
  class Condition {
    def await(): Unit     = throw IllegalStateException("no other thread can signal this condition")
    def signalAll(): Unit = ()
  }

  class AtomicInts(length: Int) {
    private val values                           = Array.ofDim[Int](length)
    def get(index: Int): Int                     = values(index)
    def getAcquire(index: Int): Int              = values(index)
    def setRelease(index: Int, value: Int): Unit = values(index) = value
    def getAndIncrement(index: Int): Int         = {
      val value = values(index)
      values(index) = value + 1
      value
    }
  }

  def once[A](compute: () => A): Once[A] = Once(compute)

  def readWriteLock(): ReadWriteLock = ReadWriteLock()

  def condition(lock: ReentrantLock): Condition = { val _ = lock; Condition() }

  def atomicInts(length: Int): AtomicInts = AtomicInts(length)

  inline def getOpaque(flag: AtomicBoolean): Boolean = flag.get()

  inline def getAcquire(value: AtomicInteger): Int = value.get()

  inline def setRelease(value: AtomicInteger, next: Int): Unit = value.set(next)

  inline def threadId(): Long = 0L
}
