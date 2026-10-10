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
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*
import scala.runtime.LongRef
import scala.util.Using

/** Memory traffic through a real session and device heap, without instruction decoding or socket traffic. */
object SessionMemoryBenchmark {
  private case class Options(iterations: Int > 0, allocations: Int > 0, workers: Int > 0, shared: Boolean)
  val benchmark: Case = (
    positive("iterations", "Memory transactions per worker", 10000),
    positive("allocations", "Registered allocations to rotate through", 128),
    positive("workers", "Concurrent memory clients", 4),
    Opts.flag("shared", help = "Contend on the same words instead of independent cache lines").orFalse
  ).mapN(Options(_, _, _, _)).map(options => () => run(options))

  private def checked[E, A](result: Either[E, A]): A =
    result.fold(error => throw IllegalStateException(error.toString), identity)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def run(parsed: Options): BenchmarkSample = {
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
          val id = checked(session.registerAllocation(bytes))
          checked(session.registerAllocationAddress(id, address))
          id
        }
        val program = checked(session.load(ProgramEncoding.PTXText, Vec.empty))
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
  ) extends Backend {
    override val format: ProgramFormat                                  = ProgramFormat.PTXText
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(Program())

    private class Program extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 0, Vec.empty).left.map(Fault.InvalidRequest(_))

      @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
      override def execute(
          task: KernelTask,
          memory: AddressSpace,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[Fault, Unit] = {
        val heap = DeviceHeap(memory)
        workers.parallelFor(options.workers.toLong) { worker =>
          val offset = if (options.shared) 0L else worker.raw * 64L
          val ranges = addresses.map { address =>
            val range = AddressSpace.ReadRangeBuffer(1)
            range.add(address.offset(offset), 8)
            range
          }
          val loaded                            = LongRef(0L)
          def read(address: GuestAddress): Long = {
            val fault = heap.readValueInto(address, 8, loaded)
            if (fault != null) throw IllegalStateException(fault.toString)
            loaded.elem
          }
          val measured = Measurement {
            val scratch = checked(heap.allocate(8))
            var index   = 0
            while (index < options.iterations.raw) {
              if (index % 4 == 0) {
                val previous = read(scratch)
                checked(heap.writeValue(scratch, previous + 1L, 8))
              } else {
                val selected = index % addresses.length
                checked(heap.prepareReads(ranges(selected)))
                val _ = checked(heap.atomicModifyValue(addresses(selected).offset(offset), 8)(_ + 1L))
              }
              index += 1
            }
            val result = read(scratch)
            require(heap.free(scratch), "device allocation was lost")
            result
          }
          val _ = completed.add(measured)
          Right(())
        }
      }
    }
  }
}
