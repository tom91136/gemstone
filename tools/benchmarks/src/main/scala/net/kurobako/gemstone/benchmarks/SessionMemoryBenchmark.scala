package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.util.concurrent.ConcurrentLinkedQueue
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Memory traffic through a real session and device heap, without instruction decoding or socket traffic. */
object SessionMemoryBenchmark {
  private case class Options(iterations: Int > 0, allocations: Int > 0, workers: Int > 0, shared: Boolean)
  private val name    = BenchmarkName("session-memory")
  private val options = (
    Opts.option[Int]("iterations", help = "Memory transactions per worker").withDefault(10000)
      .refined[Int > 0]("iterations must be positive"),
    Opts.option[Int]("allocations", help = "Registered allocations to rotate through").withDefault(128)
      .refined[Int > 0]("allocations must be positive"),
    Opts.option[Int]("workers", help = "Concurrent memory clients").withDefault(4)
      .refined[Int > 0]("workers must be positive"),
    Opts.flag("shared", help = "Contend on the same words instead of independent cache lines").orFalse
  ).mapN(Options(_, _, _, _))

  private def checked[E, A](result: Either[E, A]): A =
    result.fold(error => throw IllegalStateException(error.toString), identity)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    // Separate independent clients by one storage-lock stripe; shared mode deliberately uses the same word.
    val bytes     = Math.multiplyExact(parsed.workers.raw, 64).unsafe[Int > 0]
    val addresses = Array.tabulate(parsed.allocations.raw)(index =>
      GuestAddress(0x100000L + index.toLong * (bytes.raw.toLong + 4096L))
    )
    val completed = ConcurrentLinkedQueue[Measurement[Long]]()
    Using.resource(WorkerPool(parsed.workers, minGroups = 1L, minWorkItems = 1L)) { workers =>
      val backend = MemoryBackend(parsed, addresses, workers, completed)
      Using.resource(ExecutionSession(List(backend))) { session =>
        val allocations = addresses.map { address =>
          val id = checked(session.registerAllocation(bytes.toLong, AllocationKind.Device))
          checked(session.registerAllocationAddress(id, address))
          id
        }
        val program = checked(session.load(backend.encoding, Vec.empty))
        val task    = checked(KernelTask.validated(
          kernelName = "memory",
          groupSize = LaunchGeometry.Dimensions.One,
          groupCount = LaunchGeometry.Dimensions(parsed.workers, 1, 1),
          arguments = Vec.empty
        ))
        val measured = Measurement {
          val completion = checked(session.launch(program, task))
          checked(session.awaitCompletion(completion))
          checked(session.releaseCompletion(completion))
        }
        // One in four transactions accesses the device heap. Check every registered word, including untouched ones.
        val expected = Array.fill(parsed.allocations.raw, parsed.workers.raw)(0L)
        var worker   = 0
        while (worker < parsed.workers.raw) {
          var index = 0
          while (index < parsed.iterations.raw) {
            if (index % 4 != 0) expected(index % parsed.allocations.raw)(if (parsed.shared) 0 else worker) += 1L
            index += 1
          }
          worker += 1
        }
        allocations.zipWithIndex.foreach { (allocation, index) =>
          expected(index).zipWithIndex.foreach { (wanted, slot) =>
            val actual = checked(session.readValue(allocation, (slot.toLong * 64L).unsafe[Long >= 0], 8))
            require(actual == wanted, s"allocation $index slot $slot: $actual != $wanted")
          }
        }
        val measurements = completed.asScala.toVector
        require(measurements.size == parsed.workers.raw, "missing worker measurements")
        measurements.foreach(m => require(m.value == (parsed.iterations.raw.toLong + 3L) / 4L, "device heap result"))
        val allocated = measurements.map(_.allocatedBytes)
        val total     = Option.when(allocated.forall(_.nonEmpty))(
          allocated.flatten.map(_.raw).sum.unsafe[Long >= 0]
        )
        measured.sample((parsed.iterations.raw.toLong * parsed.workers.raw).unsafe[Long > 0]).copy(
          allocatedBytes = total,
          allocationScope = if (total.nonEmpty) BenchmarkSample.AllocationScope.Workers
          else BenchmarkSample.AllocationScope.Unavailable
        )
      }
    }
  }

  private class MemoryBackend(
      options: Options,
      addresses: Array[GuestAddress],
      workers: Workgroups,
      completed: ConcurrentLinkedQueue[Measurement[Long]]
  ) extends RuntimeBackend {
    override val encoding: String                                                         = "benchmark-memory"
    override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(Program())

    private class Program extends RuntimeBackend.Program {
      override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
        KernelMetadata.validated(0, 0, Vec.empty).left.map(ExecutionFailure.InvalidRequest(_))

      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      override def execute(
          task: KernelTask,
          memory: Memory,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[ExecutionFailure, Unit] = {
        val addressSpace = memory match {
          case value: AddressSpace => value
          case _                   => throw IllegalStateException("session did not provide an address space")
        }
        val heap = DynamicAddressMemory(VMMemory(addressSpace))
        workers.parallelFor(options.workers.toLong) { worker =>
          val offset   = if (options.shared) 0L else worker.raw * 64L
          val ranges   = addresses.map(address => Vec((address.offset(offset), 8: Int >= 0)))
          val measured = Measurement {
            val scratch = checked(heap.allocate(8))
            var index   = 0
            while (index < options.iterations.raw) {
              if (index % 4 == 0) {
                val previous = checked(heap.readLittleEndian(scratch, 8))
                checked(heap.writeLittleEndian(scratch, previous + 1L, 8))
              } else {
                val selected = index % addresses.length
                checked(heap.prepareReads(ranges(selected)))
                val _ = checked(heap.atomicModifyLittleEndian(addresses(selected).offset(offset), 8)(_ + 1L))
              }
              index += 1
            }
            val result = checked(heap.readLittleEndian(scratch, 8))
            require(heap.free(scratch), "device allocation was lost")
            result
          }
          val _ = completed.add(measured)
          Right(())
        }.left.map(ExecutionFailure.fromFault(_))
      }
    }
  }
}
