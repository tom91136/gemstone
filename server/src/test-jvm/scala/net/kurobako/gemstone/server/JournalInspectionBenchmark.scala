package net.kurobako.gemstone.server

import java.io.File
import java.lang.management.ManagementFactory

/** Repeated identical frames measure inspection work separately from capture and replay. */
object JournalInspectionBenchmark {
  def main(arguments: Array[String]): Unit = {
    val file   = File(arguments(0))
    val writer = RPCJournal.Writer(file, RPCJournal.Header(JournalFixtures.schema))
    try {
      val connection = writer.connection(Object())
      val request    = JournalFixtures.execution(3, Array.fill[Byte](256 * 1024)(17))
      for (_ <- 0 until 200) {
        connection.request(request)
        connection.response(JournalFixtures.response())
      }
      connection.close()
    } finally writer.close()
    val bean = ManagementFactory.getThreadMXBean match {
      case value: com.sun.management.ThreadMXBean => value
      case _ => throw UnsupportedOperationException("thread allocation measurement is unavailable")
    }
    bean.setThreadAllocatedMemoryEnabled(true)
    for (_ <- 0 until 2) RPCJournal.inspect(file)
    for (sample <- 0 until 5) {
      val allocated = bean.getThreadAllocatedBytes(Thread.currentThread().threadId())
      val started   = System.nanoTime()
      val summary   = RPCJournal.inspect(file)
      val nanos     = System.nanoTime() - started
      val bytes     = bean.getThreadAllocatedBytes(Thread.currentThread().threadId()) - allocated
      require(summary.events == 401 && summary.blobs == 2 && summary.replayable)
      println(
        s"INSPECT sample=$sample nanos=$nanos allocated=$bytes events=${summary.events} blobs=${summary.blobs} archive=${file.length()}"
      )
    }
  }
}
