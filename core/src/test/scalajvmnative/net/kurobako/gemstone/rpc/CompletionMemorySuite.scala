package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.runtime.LongRef
import scala.util.Using

class CompletionMemorySuite extends Suite {
  private def checked[A](result: Either[Any, A]): A =
    result.fold(error => fail(s"unexpected failure: $error"), identity)

  private type Kernel = AddressSpace => Either[Fault, Unit]

  private class KernelProgram(body: Kernel) extends Backend.Program {
    override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
      KernelMetadata.validated(0, 0, Vec.empty).left.map(Fault.InvalidRequest(_))
    override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = body(context.memory)
  }

  /** Loads the kernel body set for the next dispatch. */
  private class KernelBackend extends Backend {
    val body: AtomicReference[Kernel]                                   = AtomicReference(_ => Right(()))
    override val format: ProgramFormat                                  = ProgramFormat.PTXText
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(KernelProgram(body.get()))
  }

  private def withSession(test: (ExecutionSession, KernelBackend) => Unit): Unit = {
    val backend = KernelBackend()
    Using.resource(ExecutionSession(List(backend)))(test(_, backend))
  }

  private def dispatch(session: ExecutionSession, backend: KernelBackend, device: Int = 0)(
      body: AddressSpace => Either[Fault, Unit]
  ): Unit = {
    backend.body.set(body)
    val program = checked(session.load(ProgramEncoding.PTXText, Vec.empty))
    val task    = checked(
      KernelTask.validated("kernel", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
    )
    val completion = checked(session.launch(program, task, device = device))
    assertEquals(session.awaitCompletion(completion), Right(()))
  }

  private def aliased(session: ExecutionSession, address: Long, value: Long): AllocationId = {
    val id = checked(session.registerAllocation(64))
    checked(session.registerAllocationAddress(id, GuestAddress(address)))
    checked(session.writeValue(AllocationScalar(id, 0L, 8), value))
    id
  }

  private val Address = GuestAddress(0x100000L)

  test("a dispatch faults on device memory that its device was not granted, at every access form") {
    withSession { (session, backend) =>
      val id = checked(session.registerAllocation(64))
      checked(session.registerAllocationAddress(id, Address, devices = 1 << 1))
      checked(session.writeValue(AllocationScalar(id, 0L, 8), 7L))

      dispatch(session, backend, device = 1) { memory =>
        assertEquals(memory.readValue(Address, 8), Right(7L))
        assertEquals(memory.writeValue(Address, 9L, 8), Right(()))
        assertEquals(memory.readValue(id, 0L, 8), Right(9L))
        Right(())
      }

      dispatch(session, backend, device = 0) { memory =>
        assert(memory.read(Address, 8).isLeft)
        assert(memory.readValue(Address, 8).isLeft)
        assert(memory.writeValue(Address, 1L, 8).isLeft)
        assert(memory.write(Address, Vec.fill(8)(1.toByte)).isLeft)
        assert(memory.atomicModifyValue(Address, 8)(_ + 1).isLeft)
        assert(Option(memory.readCoherentValueInto(Address, 8, LongRef(0L))).nonEmpty)
        assert(memory.read(id, 0L, 8).isLeft)
        assert(memory.readValue(id, 0L, 8).isLeft)
        assert(memory.writeValue(id, 0L, 1L, 8).isLeft)
        assert(memory.atomicModifyValue(id, 0L, 8)(_ + 1).isLeft)
        assert(Option(memory.readCoherentValueInto(id, 0L, 8, LongRef(0L))).nonEmpty)
        memory.read(Address, 8) match {
          case Left(fault) => assert(fault.description.contains("device 0 cannot access"), fault.description)
          case Right(_)    => fail("expected a fault")
        }
        Right(())
      }
      assertEquals(session.read(AllocationRange(id, 0L, 8)).map(_.toList), Right(List[Byte](9, 0, 0, 0, 0, 0, 0, 0)))
    }
  }

  test("granting a device makes the allocation reachable and a fault surfaces as an illegal address") {
    withSession { (session, backend) =>
      val id = checked(session.registerAllocation(64))
      checked(session.registerAllocationAddress(id, Address, devices = 1 << 1))
      checked(session.setAllocationDevices(id, (1 << 1) | (1 << 0)))
      dispatch(session, backend, device = 0)(memory => memory.writeValue(Address, 5L, 8))
      assertEquals(
        session.read(AllocationRange(id, 0L, 8)).map(_.toList),
        Right(List[Byte](5, 0, 0, 0, 0, 0, 0, 0))
      )
      checked(session.setAllocationDevices(id, 1 << 1))
      backend.body.set(memory => memory.writeValue(Address, 6L, 8))
      val program = checked(session.load(ProgramEncoding.PTXText, Vec.empty))
      val task    =
        checked(KernelTask.validated("kernel", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty))
      val completion = checked(session.launch(program, task, device = 0))
      session.awaitCompletion(completion) match {
        case Left(ExecutionRPC.ExecutionFailure.IllegalAddress(_)) => ()
        case other => fail(s"expected an illegal address failure, got $other")
      }
    }
  }

  test("allocations registered without a device set stay reachable from every device") {
    withSession { (session, backend) =>
      val id = aliased(session, Address.raw, 3L)
      (0 until 16).foreach(device =>
        dispatch(session, backend, device)(memory => memory.readValue(Address, 8).map(_ => ()))
      )
      checked(session.setAllocationDevices(id, 0))
      dispatch(session, backend, device = 0)(memory => Right(assert(memory.readValue(Address, 8).isLeft)))
    }
  }

  test("an allocation freed and reallocated at the same range resolves to the new allocation") {
    withSession { (session, backend) =>
      val first = aliased(session, Address.raw, 1L)
      dispatch(session, backend) { memory =>
        assertEquals(memory.readValue(Address, 8), Right(1L))
        assertEquals(memory.writeValue(Address, 3L, 8), Right(()))
        checked(session.releaseAllocation(first))
        assert(memory.readValue(Address, 8).isLeft)
        val second = aliased(session, Address.raw, 2L)
        assertEquals(memory.readValue(Address, 8), Right(2L))
        assertEquals(memory.writeValue(Address.offset(8), 4L, 8), Right(()))
        assertEquals(session.readValue(AllocationScalar(second, 8L, 8)), Right(4L))
        Right(())
      }
    }
  }

  test("an alias registered over a synthetic range takes precedence within a running dispatch") {
    withSession { (session, backend) =>
      val synthetic = checked(session.registerAllocation(64))
      checked(session.writeValue(AllocationScalar(synthetic, 0L, 8), 5L))
      dispatch(session, backend) { memory =>
        val base = checked(memory.address(synthetic, 0L))
        assertEquals(memory.readValue(base, 8), Right(5L))
        val _ = aliased(session, base.raw, 6L)
        assertEquals(memory.readValue(base, 8), Right(6L))
        Right(())
      }
    }
  }

  test("a zero-width lookup at a range end resolves to the adjacent range") {
    withSession { (session, backend) =>
      val first  = aliased(session, Address.raw, 1L)
      val second = aliased(session, Address.raw + 64L, 2L)
      dispatch(session, backend) { memory =>
        assertEquals(memory.readValue(Address, 8), Right(1L))
        assertEquals(memory.locate(Address.offset(64), 0), Right(AddressSpace.Location.Within(second, 0L)))
        assertEquals(memory.locate(Address.offset(56), 8), Right(AddressSpace.Location.Within(first, 56L)))
        Right(())
      }
    }
  }

  test("concurrent sessions and workers resolve the same address to their own allocations") {
    val sessions = 4
    val ready    = CountDownLatch(sessions)
    Using.resource(WorkerPool(4, minGroups = 1L, minWorkItems = 1L)) { pool =>
      Using.resource(threads.Pool(sessions)) { threadPool =>
        val runs = (0 until sessions).map { index =>
          val run: Runnable = () =>
            withSession { (session, backend) =>
              val bases = (0 until 3).map(slot => Address.raw + slot * 4096L)
              bases.zipWithIndex.foreach((base, slot) => aliased(session, base, index * 10L + slot))
              ready.countDown()
              assert(ready.await(10, TimeUnit.SECONDS))
              dispatch(session, backend) { memory =>
                pool.parallelFor(8L) { _ =>
                  (0 until 10000).foldLeft[Either[Fault, Unit]](Right(())) { (result, iteration) =>
                    result.flatMap { _ =>
                      val slot = iteration % 3
                      memory.readValue(GuestAddress(bases(slot)), 8).flatMap(value =>
                        if (value == index * 10L + slot) Right(())
                        else Left(Fault.Memory(s"session $index slot $slot read $value"))
                      )
                    }
                  }
                }
              }
            }
          threadPool.executor.submit(run)
        }
        runs.foreach(_.get(60, TimeUnit.SECONDS))
      }
    }
  }

  test("unlocked reads stay linearisable against concurrent atomics and plain stores") {
    val workers    = 4
    val iterations = 20000
    // Both words share one cache line, and so one stripe; the pattern is unaligned so a torn read would mix halves.
    val counter  = Address.offset(40)
    val pattern  = Address.offset(3)
    val observed = java.util.concurrent.ConcurrentLinkedQueue[Long]()
    Using.resource(WorkerPool(4, minGroups = 1L, minWorkItems = 1L)) { pool =>
      withSession { (session, backend) =>
        val id = aliased(session, Address.raw, 0L)
        dispatch(session, backend) { memory =>
          pool.parallelFor(4L) { worker =>
            (0 until iterations).foldLeft[Either[Fault, Long]](Right(0L)) { (previous, iteration) =>
              previous.flatMap { last =>
                if (worker.raw % 2 == 0) {
                  val stored = if (iteration % 2 == 0) 0L else -1L
                  val write  =
                    if (iteration % 3 == 0) memory.atomicModifyValue(pattern, 8)(_ => stored).map(_ => ())
                    else memory.writeValue(pattern, stored, 8)
                  write.flatMap(_ => memory.atomicModifyValue(counter, 8)(_ + 1L)).map { before =>
                    observed.add(before)
                    last
                  }
                } else {
                  memory.readValue(pattern, 8).flatMap { value =>
                    if (value != 0L && value != -1L) Left(Fault.Memory(s"torn read $value"))
                    else
                      memory.readValue(counter, 8).flatMap(count =>
                        if (count < last) Left(Fault.Memory(s"counter went back from $last to $count"))
                        else Right(count)
                      )
                  }
                }
              }
            }.map(_ => ())
          }
        }
        val increments = workers / 2 * iterations
        assertEquals(session.readValue(AllocationScalar(id, 40L, 8)), Right(increments.toLong))
        assertEquals(observed.asScala.toVector.sorted, Vector.range(0L, increments.toLong))
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def coherent(memory: AddressSpace, address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
    val into = LongRef(0L)
    memory.readCoherentValueInto(address, size, into) match {
      case null         => Right(into.elem)
      case fault: Fault => Left(fault)
    }
  }

  test("a coherent load returns what the latest store wrote, at both widths and through a region") {
    withSession { (session, backend) =>
      val id = aliased(session, Address.raw, 0L)
      dispatch(session, backend) { memory =>
        assertEquals(memory.writeValue(Address, 0x1122334455667788L, 8), Right(()))
        assertEquals(coherent(memory, Address, 8), Right(0x1122334455667788L))
        assertEquals(coherent(memory, Address, 4), Right(0x55667788L))
        assertEquals(coherent(memory, Address.offset(4), 4), Right(0x11223344L))
        assertEquals(memory.writeValue(Address.offset(4), 9L, 4), Right(()))
        val into = LongRef(0L)
        assert(Option(memory.readCoherentValueInto(id, 0L, 8, into)).isEmpty)
        assertEquals(into.elem, 0x0000000955667788L)
        assert(coherent(memory, Address.offset(64), 4).isLeft)
        Right(())
      }
    }
  }

  test("a coherent load observes an atomic update and a store made by another worker") {
    Using.resource(WorkerPool(2, minGroups = 1L, minWorkItems = 1L)) { pool =>
      withSession { (session, backend) =>
        val _      = aliased(session, Address.raw, 0L)
        val stored = CountDownLatch(1)
        dispatch(session, backend) { memory =>
          pool.parallelFor(2L) { worker =>
            if (worker.raw == 0L)
              memory.writeValue(Address.offset(8), 5L, 8).flatMap(_ =>
                memory.atomicModifyValue(Address, 8)(_ + 3L)
              ).map { _ =>
                stored.countDown()
              }
            else if (!stored.await(10, TimeUnit.SECONDS)) Left(Fault.Cancelled)
            else
              coherent(memory, Address, 8).flatMap(first =>
                coherent(memory, Address.offset(8), 8).flatMap(second =>
                  if (first == 3L && second == 5L) Right(())
                  else Left(Fault.Memory(s"coherent loads saw $first and $second"))
                )
              )
          }
        }
      }
    }
  }

  test("message passing: a coherent load of the flag makes the earlier data store visible") {
    val slots = 512
    Using.resource(WorkerPool(2, minGroups = 1L, minWorkItems = 1L)) { pool =>
      withSession { (session, backend) =>
        val id = checked(session.registerAllocation(32768))
        checked(session.registerAllocationAddress(id, Address))
        dispatch(session, backend) { memory =>
          pool.parallelFor(2L) { worker =>
            (0 until slots).foldLeft[Either[Fault, Unit]](Right(())) { (result, slot) =>
              val data = Address.offset(slot * 64L)
              val flag = Address.offset(slot * 64L + 32L)
              result.flatMap { _ =>
                if (worker.raw == 0L)
                  memory.writeValue(data, slot + 1L, 8).flatMap(_ => memory.writeValue(flag, 1L, 8))
                else {
                  val deadline = 20.seconds.fromNow
                  @annotation.tailrec
                  def await(): Either[Fault, Long] = {
                    val seen = coherent(memory, flag, 8)
                    if (seen == Right(0L) && deadline.hasTimeLeft()) await() else seen
                  }
                  await().flatMap(value =>
                    if (value != 1L) Left(Fault.Memory(s"slot $slot flag was $value"))
                    else
                      memory.readValue(data, 8).flatMap(payload =>
                        if (payload == slot + 1L) Right(())
                        else Left(Fault.Memory(s"slot $slot saw flag before data: $payload"))
                      )
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
