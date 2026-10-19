package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*

// ptxas 13.3 output for the kernels of TensorCopyCases, decoded from its machine code as Gemstone loads a cubin:
// UTMALDG.GATHER4 and UTMASTG.SCATTER4, UTMALDG.W and UTMALDG.W128 with their packed wHalo and wOffset, UTMALDG.2CTA,
// and tile copies of the packed sub-byte types.
class TensorCopySuite extends munit.FunSuite {
  private def run(fixture: String, name: String, shared: Int, threads: Int, memory: Memory, arguments: Arg*)(
      blocks: Int = 1
  )(using munit.Location): Unit = {
    val kernel = fromHex(TensorCopyFixtures.code(fixture), 100, name)
      .copy(shared = shared.unsafe[Int >= 0], cluster = blocks.unsafe[Int > 0])
    assertEquals(kernel.run(block = threads, grid = blocks, memory)(arguments*).map(_ => ()), Right(()))
  }

  private def address(memory: Memory, region: AllocationId): Long = memory.address(region, 0L).value.raw

  private def map(bytes: Array[Byte]): Arg = Arg.Scalar(Vec.unsafeWrapArray(bytes))

  test("sm_100a UTMALDG.GATHER4 and UTMASTG.SCATTER4 move four rows of a 2D tensor") {
    val memory = Memory()
    val source = memory.allocate(Harness.words(TensorCopyCases.rows(16)))
    val target = memory.allocate(Vec.fill(20 * 32)(0.toByte))
    val out    = memory.allocate(Vec.fill(64)(0.toByte))
    run(
      "gatherSm100a",
      "gather",
      shared = 0x448,
      threads = 16,
      memory,
      map(TensorCopyCases.gatherMap(address(memory, source), 16)),
      map(TensorCopyCases.gatherMap(address(memory, target), 20)),
      Arg.Region(out)
    )()
    assertEquals(memory.readWords(out, 16), TensorCopyCases.gather.box)
    assertEquals(memory.readWords(target, 160), TensorCopyCases.gather.scattered)
  }

  for (shift <- Vec(0, 1))
    test(s"sm_100a UTMALDG.W and UTMALDG.W128 unpack wHalo 2 and wOffset $shift from their uniform word") {
      val memory = Memory()
      val tensor = memory.allocate(Harness.words(TensorCopyCases.pixels))
      val out    = memory.allocate(Vec.fill(608 * 4)(0.toByte))
      val maps   = TensorCopyCases.wideMaps(address(memory, tensor))
      run(
        "wideSm100a",
        "wide",
        shared = 0xd88,
        threads = 128,
        memory,
        map(maps.narrow),
        map(maps.full),
        Arg.Region(out),
        Arg.u32(2L),
        Arg.u32(shift.toLong)
      )()
      assertEquals(memory.readWords(out, 608), TensorCopyCases.wide(shift))
    }

  test("sm_100a UTMALDG.2CTA completes on the mbarrier of the even CTA of the pair") {
    val memory = Memory()
    val source = memory.allocate(Harness.words(TensorCopyCases.rows(8)))
    val out    = memory.allocate(Vec.fill(256)(0.toByte))
    run(
      "pairSm100a",
      "pair",
      shared = 0x488,
      threads = 32,
      memory,
      map(TensorCopyCases.pairMap(address(memory, source))),
      Arg.Region(out)
    )(
      2
    )
    assertEquals(memory.readWords(out, 64), TensorCopyCases.rows(8))
  }

  test("sm_100a tile copies of the packed sub-byte types pad loads and pack .b6p2x16 stores") {
    val memory  = Memory()
    val bytes   = Vec.tabulate(256)(index => TensorCopyCases.source(index).toByte)
    val tensors = Vec.fill(3)(memory.allocate(bytes))
    val spread  = memory.allocate(Vec.fill(96)(0.toByte))
    val out     = memory.allocate(Vec.fill(576)(0.toByte))
    val maps    = TensorCopyCases.packedMaps((tensors :+ spread).map(address(memory, _)))
    run(
      "packedSm100a",
      "packed",
      shared = 0x6c8,
      threads = 32,
      memory,
      map(maps.four),
      map(maps.padded),
      map(maps.six),
      map(maps.spread),
      Arg.Region(out)
    )()
    val loaded = memory.readWords(out, 576, 1)
    TensorCopyCases.packed.loaded.zipWithIndex.foreach((expected, index) =>
      expected.foreach(value => assertEquals(loaded(index), value.toLong, clues(index)))
    )
    assertEquals(memory.readWords(spread, 96, 1), TensorCopyCases.packed.stored.map(_.toLong))
  }
}
