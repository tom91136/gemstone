package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import java.lang.management.ManagementFactory

/** Warmed vector-load allocation probe using owned snapshots, matching ExecutionService read ownership. */
object VectorMemoryBenchmark {
  private val input  = AllocationId.fromLong(1).toOption.get
  private val output = AllocationId.fromLong(2).toOption.get
  private class Memory extends ExecutionMemory {
    val source                                          = Array.tabulate[Byte](1024)(i => (i * 17).toByte)
    val destination                                     = Array.ofDim[Byte](1024)
    def read(id: AllocationId, offset: Long, size: Int) =
      Right(Col.unsafeFromArray(java.util.Arrays.copyOfRange(source, offset.toInt, offset.toInt + size)))
    def write(id: AllocationId, offset: Long, bytes: Col[Byte]) = {
      val _ = bytes.copyToArray(destination, offset.toInt, bytes.size); Right(())
    }
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  def main(args: Array[String]): Unit = {
    val iterations = args.headOption.fold(2000)(_.toInt)
    val code = Col[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toCol(w, 4))
    val kernel = DispatchInterpreter.prepare(CodeObjectKernel(
      "kernel",
      Target.unsafe("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code
    )).toOption.get
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1).toOption.get,
      "kernel",
      Dimensions.create(64, 1, 1).toOption.get,
      Dimensions.One,
      Col(KernelArgument.Allocation(input, 0), KernelArgument.Allocation(output, 0))
    ).toOption.get
    val memory            = Memory()
    def run(n: Int): Unit = {
      var i = 0;
      while (i < n) {
        DispatchInterpreter.execute(kernel, dispatch, memory, ExecutionControl.default).fold(
          e => throw IllegalStateException(e.toString),
          identity
        );
        i += 1
      }
    }
    run(iterations)
    val bean = ManagementFactory.getThreadMXBean.asInstanceOf[com.sun.management.ThreadMXBean]
    bean.setThreadAllocatedMemoryEnabled(true)
    val before = bean.getThreadAllocatedBytes(Thread.currentThread().threadId())
    run(iterations)
    val allocated = bean.getThreadAllocatedBytes(Thread.currentThread().threadId()) - before
    require(memory.source.sameElements(memory.destination))
    println(
      s"iterations=$iterations allocated=$allocated bytes_per_dispatch=${allocated.toDouble / iterations} checksum=${memory.destination.map(_.toLong).sum}"
    )
  }
}
