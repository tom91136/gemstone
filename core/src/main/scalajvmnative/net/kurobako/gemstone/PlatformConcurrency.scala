package net.kurobako.gemstone

import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/** Threads coordinate through the JDK's concurrency primitives. */
private object PlatformConcurrency {
  type Once[A]       = FutureTask[A]
  type ReadWriteLock = ReentrantReadWriteLock
  type AtomicInts    = AtomicIntegerArray
  type Condition     = java.util.concurrent.locks.Condition

  /** Runs `compute` at most once; `get` waits for it and wraps its failure in an `ExecutionException`. */
  def once[A](compute: () => A): Once[A] = FutureTask[A](() => compute())

  def readWriteLock(): ReadWriteLock = ReentrantReadWriteLock()

  def condition(lock: ReentrantLock): Condition = lock.newCondition()

  def atomicInts(length: Int): AtomicInts = AtomicIntegerArray(length)

  inline def getOpaque(flag: AtomicBoolean): Boolean = flag.getOpaque

  inline def getAcquire(value: AtomicInteger): Int = value.getAcquire

  inline def setRelease(value: AtomicInteger, next: Int): Unit = value.setRelease(next)

  inline def threadId(): Long = Thread.currentThread().threadId()
}
