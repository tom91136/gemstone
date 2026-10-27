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
import scala.jdk.CollectionConverters.*
import scala.util.Using

class CompletionMemorySuite extends munit.FunSuite {
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

  private def dispatch(session: ExecutionSession, backend: KernelBackend)(body: AddressSpace => Either[Fault, Unit])
      : Unit = {
    backend.body.set(body)
    val program = checked(session.load(ProgramEncoding.PTXText, Vec.empty))
    val task    = checked(
      KernelTask.validated("kernel", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
    )
    val completion = checked(session.launch(program, task))
    assertEquals(session.awaitCompletion(completion), Right(()))
  }

  private def aliased(session: ExecutionSession, address: Long, value: Long): AllocationId = {
    val id = checked(session.registerAllocation(64))
    checked(session.registerAllocationAddress(id, GuestAddress(address)))
    checked(session.writeValue(AllocationScalar(id, 0L, 8), value))
    id
  }

  private val Address = GuestAddress(0x100000L)

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
}
