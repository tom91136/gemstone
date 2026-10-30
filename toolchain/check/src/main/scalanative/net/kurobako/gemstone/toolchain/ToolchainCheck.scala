package net.kurobako.gemstone.toolchain

import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import scala.scalanative.posix.unistd

/** Reproduces Scala Native defects, one per check: `ToolchainCheck <check> [--workarounds]` prints `passed` and exits
  * 0, or prints `stalled` and exits 1, or hangs. The allocating threads never stop, so every outcome leaves through
  * `_exit`, which a deadlocked runtime cannot block.
  */
object ToolchainCheck {
  def main(arguments: Array[String]): Unit = {
    if (arguments.contains("--workarounds")) workarounds.install()
    arguments.head match {
      case "reference-handler" => referenceHandler()
      case "reference-burst"   => referenceBurst()
      case "heap-growth"       => heapGrowth()
      case other               => throw IllegalArgumentException(s"unknown check '$other'")
    }
  }

  private def daemon(body: () => Unit): Unit = {
    val thread = Thread(() => body())
    thread.setDaemon(true)
    thread.start()
  }

  private def finish(message: String, status: Int): Unit = {
    println(message)
    unistd._exit(status)
  }

  /** One thread churns a small heap and drops weak references. Under Commix the reference handler deadlocks itself
    * within seconds.
    */
  private def referenceHandler(): Unit = {
    val progress = AtomicLong()
    val retained = new Array[Array[Byte]](64)
    daemon { () =>
      while (true) {
        val i = progress.getAndIncrement()
        retained((i % retained.length).toInt) = new Array[Byte](1024 + (i % 4096).toInt)
        if (i % 64 == 0) { val _ = WeakReference(new Array[Byte](64)) }
      }
    }
    val (last, _) = (1 to 10).foldLeft((-1L, 0)) { case ((previous, still), _) =>
      Thread.sleep(1000)
      val now  = progress.get()
      val next = if (now == previous) still + 1 else 0
      if (next >= 5) finish(s"stalled after $now allocations", 1)
      (now, next)
    }
    finish(s"passed with $last allocations", 0)
  }

  /** Clears many weak references in one collection, then starts threads that allocate. Boehm runs the queued callbacks
    * from finalizers on whichever thread allocates, including one that is inside `unpark`, which deadlocks.
    */
  private def referenceBurst(): Unit = {
    val references = Array.fill(100000)(WeakReference(new Array[Byte](32)))
    val starters   = 64
    val done       = CountDownLatch(starters)
    System.gc()
    for (_ <- 0 until starters) daemon { () =>
      for (_ <- 0 until 1000) { val _ = new Array[Byte](64) }
      done.countDown()
    }
    done.await()
    finish(s"passed with ${references.count(reference => Option(reference.get()).isEmpty)} references cleared", 0)
  }

  /** Eight threads each keep 512 arrays of 64 short strings, so live data stays near 32 MiB, until they have built
    * 100000 chunks between them. Immix and Commix outgrow a 128 MiB maximum first; counting work rather than time keeps
    * that true on a loaded host.
    */
  private def heapGrowth(): Unit = {
    val progress = AtomicLong()
    for (id <- 0 until 8) daemon { () =>
      val live = new Array[Array[String]](512)
      while (true) {
        val i = progress.getAndIncrement()
        live((i % live.length).toInt) = Array.tabulate(64)(j => s"$id-$i-$j")
      }
    }
    while (progress.get() < 100000) Thread.sleep(10)
    finish(s"passed with ${progress.get()} chunks", 0)
  }
}
