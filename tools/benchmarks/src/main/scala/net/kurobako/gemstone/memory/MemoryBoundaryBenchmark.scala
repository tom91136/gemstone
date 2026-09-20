package net.kurobako.gemstone.memory
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.execution.*

/** Allocation-only warmed probe for the generic execution address-space bridge. */
object MemoryBoundaryBenchmark {
  private final class Memory extends ExecutionMemory {
    val bytes                                                                     = Array.ofDim[Byte](32)
    def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) =
      Right(Vec.from(bytes.slice(offset.toInt, offset.toInt + size)))
    def write(id: AllocationId, offset: Long :| Positive0, value: Vec[Byte]) = {
      val _ = value.copyToArray(bytes, offset.toInt, value.size)
      Right(())
    }
    override def readValue(id: AllocationId, offset: Long :| Positive0, size: MemoryValueByteWidth) =
      if (ByteRange.contains(bytes.length, offset, size.value.toLong))
        Right(LittleEndian.decode(bytes, offset.toInt, size.scalarByteWidth))
      else Left(ExecutionFailure.InvalidRequest("benchmark read range"))
    override def writeValue(
        id: AllocationId,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ) =
      if (ByteRange.contains(bytes.length, offset, size.value.toLong)) {
        LittleEndian.write(value, bytes, offset.toInt, size.scalarByteWidth)
        Right(())
      } else Left(ExecutionFailure.InvalidRequest("benchmark write range"))
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  def run(args: Array[String]): Unit = {
    val iterations = BenchmarkArguments.iterations(BenchmarkName("memory-boundary"), args.toSeq, 100000)
    val base       = GuestAddress(0x1000)
    val memory     = Memory()
    val space      = ExecutionAddressSpace(
      memory,
      Vec(AddressBinding(base, AllocationId(1), 0, 32))
    )
    val dynamic = DynamicAddressMemory(space)
    val _       = dynamic.allocate(DeviceAllocationByteSize(8L))
      .fold(error => throw IllegalStateException(error.toString), value => value)
    // This is the allocation-sensitive loop the benchmark measures.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def run(n: Int :| Positive): Long = {
      var i     = 0
      var total = 0L
      while (i < n) {
        dynamic.writeLittleEndian(base, i.toLong, MemoryValueByteWidth(8))
          .fold(e => throw IllegalStateException(e.toString), value => value)
        total += dynamic.readLittleEndian(base, MemoryValueByteWidth(8))
          .fold(error => throw IllegalStateException(error.toString), value => value)
        i += 1
      }
      total
    }
    val _         = run(iterations)
    val before    = PlatformMetrics.allocatedBytes
    val checksum  = run(iterations)
    val allocated = for {
      start <- before
      end   <- PlatformMetrics.allocatedBytes
      if end >= start
    } yield (end - start).refineUnsafe[Positive0]
    if (checksum != iterations.toLong * (iterations - 1) / 2)
      throw IllegalStateException(s"unexpected checksum: $checksum")
    Console.out.println(
      s"iterations=$iterations allocated=${allocated.getOrElse(-1L)} " +
        s"bytes_per_pair=${allocated.fold(Double.NaN)(_.toDouble / iterations)} checksum=$checksum"
    )
  }
}
