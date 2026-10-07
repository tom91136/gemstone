package net.kurobako.gemstone.testing

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Runs every task of a parallel dispatch on owned workers and reports the first failure in task order. */
class PooledWorkgroups(val parallelism: Int > 0 = 4) extends RecordingWorkgroups with AutoCloseable {
  protected val pool: threads.Pool = threads.Pool(parallelism.raw)

  override protected def run(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] = {
    val futures = Vec.tabulate(tasks.raw.toInt) { index =>
      val work: Callable[Either[Fault, Unit]] = () => task(index.toLong.unsafe[Long >= 0])
      pool.executor.submit(work)
    }
    futures.map(_.get(15, TimeUnit.SECONDS)).collectFirst { case Left(fault) => Left(fault) }.getOrElse(Right(()))
  }

  override def close(): Unit = pool.close()
}
