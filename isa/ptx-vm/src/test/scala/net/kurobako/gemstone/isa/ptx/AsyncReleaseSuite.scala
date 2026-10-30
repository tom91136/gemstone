package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite

// The release forms of st.async and red.async, their multimem twins and the multimem bulk copies of
// TensorMemoryCases.releasePtx, which ptxas lowers to USTGR, UREDGR, STAS.128, UBLKCP and UBLKRED. One device's multimem
// address names its own memory.
class AsyncReleaseSuite extends Suite {
  private def longs(memory: DeviceMemory, from: Long, count: Int): Vec[Long] =
    Vec.tabulate(count)(index => memory.readLittleEndian(memory.start.offset(from + index * 8L), 8).toOption.get)

  test("release stores and reductions, multimem stores and reductions, and multimem bulk copies complete as issued") {
    val release = TensorMemoryCases.release
    val memory  = DeviceMemory(128)
    release.initial.zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeLittleEndian(memory.start.offset(index * 8L), value, 8), Right(()))
    )
    assertEquals(
      execute(
        compileKernel(TensorMemoryCases.releasePtx),
        1,
        1,
        Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(longs(memory, 0, 16), release.expected)
  }
}
