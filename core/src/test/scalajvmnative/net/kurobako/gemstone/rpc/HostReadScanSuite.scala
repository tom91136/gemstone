package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

class HostReadScanSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val encoding = ProgramEncoding.PTXText
  private val base     = 0x10000000L

  private def pattern(address: Long): Byte = ((address * 31L) ^ (address >>> 7)).toByte

  private case class Traffic(roundTrips: Int, requestedBytes: Long)

  private case class Outcome(traffic: Traffic, failure: Option[String])

  private def scan(memory: AddressSpace, group: Int, lanes: Int, steps: Int): Either[Fault, Unit] = {
    val offset = group.toLong * lanes * steps * 4
    (0 until steps).foldLeft[Either[Fault, Unit]](Right(())) { (result, step) =>
      result.flatMap { _ =>
        val addresses = Vector.tabulate(lanes)(lane => base + offset + (step.toLong * lanes + lane) * 4)
        @annotation.tailrec
        def drain(pending: Vector[Long]): Either[Fault, Unit] = {
          val outcomes = pending.map(address => address -> memory.readDeferred(GuestAddress(address), 4))
          val failed   = outcomes.collectFirst { case (_, Left(fault: Fault)) => fault }
          val wrong    = outcomes.collectFirst {
            case (address, Right(bytes)) if bytes != Vec.tabulate(4)(index => pattern(address + index)) => address
          }
          val retry = outcomes.collect { case (address, Left(_: AddressSpace.Pending.type)) => address }
          if (failed.isDefined) Left(failed.get)
          else if (wrong.isDefined) Left(Fault.Backend(s"wrong host bytes at ${wrong.get}"))
          else if (retry.isEmpty) Right(())
          else
            memory.flushHostReads() match {
              case Left(fault) => Left(fault)
              case Right(())   => drain(retry)
            }
        }
        drain(addresses)
      }
    }
  }

  private def traffic(groups: Int, lanes: Int, steps: Int, readableUntil: Long = Long.MaxValue): Outcome = {
    val failure = AtomicReference(Option.empty[Fault])
    class FakeProgram extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(Fault.InvalidRequest(_))
      override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = {
        val threads = Vector.tabulate(groups) { group =>
          Thread(() => scan(context.memory, group, lanes, steps).left.foreach(fault => failure.set(Some(fault))))
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
          KernelTask.validated("scan", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
        val completion = service.launch(program, kernel.toOption.get).toOption.get
        @annotation.tailrec
        def pump(traffic: Traffic): Traffic = {
          val polled = service.pollHostMemory(completion)
          val batch  = polled.getOrElse(HostMemoryBatch(Vec.empty, Vec.empty, true))
          val next   =
            if (batch.reads.isEmpty) traffic
            else {
              val replies = batch.reads.map { range =>
                val readable = math.max(0L, math.min(range.size.raw.toLong, readableUntil - range.address.raw)).toInt
                HostMemoryBytes(range.address, Vec.tabulate(readable)(index => pattern(range.address.raw + index)))
              }
              assertEquals(service.supplyHostMemory(completion, replies), Right(()))
              Traffic(traffic.roundTrips + 1, traffic.requestedBytes + batch.reads.map(_.size.raw.toLong).sum)
            }
          if (batch.completed) next else pump(next)
        }
        val result = pump(Traffic(0, 0L))
        Outcome(
          result,
          failure.get().map(_.message).orElse(service.awaitCompletion(completion).left.toOption.map(_.message))
        )
      }
    }
  }

  private val readaheadLimit = 1048576L

  test("a scalar scan of shared host memory costs a round trip per window, not per page or access") {
    val groups  = 4
    val lanes   = 32
    val steps   = 2048
    val bytes   = groups.toLong * lanes * steps * 4
    val outcome = traffic(groups, lanes, steps)
    assertEquals(outcome.failure, None)
    assert(outcome.traffic.roundTrips <= bytes / 4096 / 8, s"$outcome")
    assert(outcome.traffic.requestedBytes <= bytes + groups * readaheadLimit, s"$outcome")
  }

  test("a scan ends cleanly where the client stops reading, and a read past that point faults") {
    val groups  = 4
    val lanes   = 32
    val steps   = 2048
    val bytes   = groups.toLong * lanes * steps * 4
    val outcome = traffic(groups, lanes, steps, readableUntil = base + bytes)
    assertEquals(outcome.failure, None)
    assert(outcome.traffic.requestedBytes <= bytes + groups * readaheadLimit, s"$outcome")
    val cut = traffic(1, lanes, steps, readableUntil = base + 8192)
    assert(cut.failure.exists(_.contains("not readable")), s"$cut")
  }
}
