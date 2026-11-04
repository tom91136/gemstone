package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.concurrent.duration.*
import scala.util.Using

class HostLocalAtomicSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val encoding = ProgramEncoding.PTXText
  private val base     = 0x10000000L

  private case class Outcome(syncs: Int, published: Map[Long, Byte], failure: Option[String])

  private def initial(address: Long): Byte = if ((address & 7L) == 0L) 3.toByte else 0.toByte

  private def run(lanes: Int, rounds: Int, local: Boolean): Outcome = {
    val failure = AtomicReference(Option.empty[Fault])
    class FakeProgram extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(Fault.InvalidRequest(_))
      override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = {
        def add(address: Long): Either[Fault, Long] =
          if (local) context.memory.atomicModifyLocalValue(GuestAddress(address), 8)(_ + 1)
          else context.memory.atomicModifyValue(GuestAddress(address), 8)(_ + 1)
        val threads = Vector.tabulate(lanes) { lane =>
          Thread(() =>
            (0 until rounds).foreach { round =>
              val own = base + 4096L * (1 + lane) + 8L * round
              Seq(add(base), add(own)).collectFirst { case Left(fault) => fault }.foreach(f => failure.set(Some(f)))
            }
          )
        }
        threads.foreach(_.start())
        threads.foreach { thread =>
          thread.join(15000L)
          assert(!thread.isAlive, "lane did not finish")
        }
        failure.get().toLeft(())
      }
      override def close(): Unit = ()
    }
    class FakeBackend extends Backend {
      override val format: ProgramFormat                                  = encoding.format
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(FakeProgram())
    }
    val backend = FakeBackend()
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(ExecutionSession(List(backend), executionContext = workers, parallelism = 1)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val kernel  =
          KernelTask.validated("atomics", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
        val completion = service.launch(program, kernel.toOption.get).toOption.get
        val published  = mutable.Map.empty[Long, Byte]
        @annotation.tailrec
        def pump(syncs: Int): Int = {
          val batch = service.pollHostMemory(completion).getOrElse(HostMemoryBatch(Vec.empty, Vec.empty, true))
          batch.writes.foreach(write =>
            write.bytes.zipWithIndex.foreach((byte, index) => published.update(write.address.raw + index, byte))
          )
          if (batch.reads.nonEmpty)
            assertEquals(
              service.supplyHostMemory(
                completion,
                batch.reads.map(range =>
                  HostMemoryBytes(
                    range.address,
                    Vec.tabulate(range.size.raw)(index => initial(range.address.raw + index))
                  )
                )
              ),
              Right(())
            )
          batch.sync.foreach { sync =>
            val observed =
              sync.atomic.fold(0L)(atom => LittleEndian.decode(Vec.fill(8)(initial(atom.address.raw)), 0, 8))
            assertEquals(service.supplyHostSync(completion, sync.ticket, observed), Right(()))
          }
          val next = syncs + batch.sync.size
          if (batch.completed) next else pump(next)
        }
        val syncs = pump(0)
        Outcome(
          syncs,
          published.toMap,
          failure.get().map(_.message).orElse(service.awaitCompletion(completion).left.toOption.map(_.message))
        )
      }
    }
  }

  private def word(published: Map[Long, Byte], address: Long): Long = (0 until 8).foldLeft(0L)((sum, index) =>
    sum | (published.getOrElse(address + index, 0.toByte) & 0xffL) << (8 * index)
  )

  test("workgroup-scope atomics on host memory stay in the mirror and publish the totals once at retirement") {
    val lanes   = 6
    val rounds  = 500
    val outcome = run(lanes, rounds, local = true)
    assertEquals(outcome.failure, None)
    assertEquals(outcome.syncs, 1)
    assertEquals(word(outcome.published, base), 3L + lanes * rounds)
    for {
      lane  <- 0 until lanes
      round <- 0 until rounds by 37
    }
      assertEquals(word(outcome.published, base + 4096L * (1 + lane) + 8L * round), 4L)
  }

  test("device-scope atomics on host memory still reach the host one by one") {
    val outcome = run(lanes = 2, rounds = 20, local = false)
    assertEquals(outcome.failure, None)
    assert(outcome.syncs >= 2 * 20 * 2, s"${outcome.syncs}")
  }
}
