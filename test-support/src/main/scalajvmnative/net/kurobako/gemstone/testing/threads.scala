package net.kurobako.gemstone.testing

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object threads {

  /** Owned test workers. Failed tests must not leave non-daemon threads keeping the test process alive. */
  class Pool(size: Int) extends AutoCloseable {
    val executor: ExecutorService = Executors.newFixedThreadPool(
      size,
      (task: Runnable) => {
        val thread = Thread(task)
        thread.setDaemon(true)
        thread
      }
    )

    override def close(): Unit = {
      val _ = executor.shutdownNow()
      if (!executor.awaitTermination(5, TimeUnit.SECONDS))
        throw IllegalStateException("test workers did not terminate after interruption")
    }
  }
}
