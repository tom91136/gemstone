package net.kurobako.gemstone.memory
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import java.lang.management.ManagementFactory

/** Allocation-only warmed probe for the generic execution address-space bridge. */
object MemoryBoundaryBenchmark {
  private final class Memory extends ExecutionMemory {
    val bytes                                           = Array.ofDim[Byte](32)
    def read(id: AllocationId, offset: Long, size: Int) =
      Right(Col.from(bytes.slice(offset.toInt, offset.toInt + size)))
    def write(id: AllocationId, offset: Long, value: Col[Byte]) = {
      val _ = value.copyToArray(bytes, offset.toInt, value.size); Right(())
    }
    override def readValue(id: AllocationId, offset: Long, size: Int) =
      Right(LittleEndian.decode(bytes, offset.toInt, size))
    override def writeValue(id: AllocationId, offset: Long, value: Long, size: Int) = {
      LittleEndian.write(value, bytes, offset.toInt, size); Right(())
    }
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  def main(args: Array[String]): Unit = {
    val iterations = args.headOption.fold(100000)(_.toInt)
    val base       = GuestAddress(0x1000)
    val memory     = Memory()
    val space      = ExecutionAddressSpace.create(
      memory,
      Col(AddressBinding(base, AllocationId.fromLong(1).toOption.get, 0, 32))
    ).toOption.get
    val dynamic = DynamicAddressMemory(space); require(dynamic.allocate(8) != 0)
    val bean    = ManagementFactory.getThreadMXBean.asInstanceOf[com.sun.management.ThreadMXBean]
    bean.setThreadAllocatedMemoryEnabled(true)
    def run(n: Int): Long = {
      var i = 0; var total = 0L;
      while (i < n) {
        dynamic.writeLittleEndian(base, i.toLong, 8).fold(e => throw IllegalStateException(e.toString), identity);
        total += dynamic.readLittleEndian(base, 8).toOption.get; i += 1
      };
      total
    }
    val _         = run(iterations)
    val before    = bean.getThreadAllocatedBytes(Thread.currentThread().threadId())
    val checksum  = run(iterations)
    val allocated = bean.getThreadAllocatedBytes(Thread.currentThread().threadId()) - before
    require(checksum == iterations.toLong * (iterations - 1) / 2)
    println(
      s"iterations=$iterations allocated=$allocated bytes_per_pair=${allocated.toDouble / iterations} checksum=$checksum"
    )
  }
}
