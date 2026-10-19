package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*

// The kernels and expectations are TensorCopyCases', shared with the SASS suite that runs ptxas's output for them.
class TensorCopySuite extends munit.FunSuite {
  private def map(bytes: Array[Byte]): Argument = Argument(Vec.from(bytes).unsafe[NonEmpty[Vec[Byte]]])

  private def place(memory: DeviceMemory, at: Long, values: Vec[Long]): Unit =
    assertEquals(memory.writeBytes(memory.start.offset(at), values.flatMap(LittleEndian.toVec(_, 4))), Right(()))

  private def run(source: String, blocks: Int, threads: Int, memory: DeviceMemory, arguments: (String, Argument)*)
      : Unit =
    assertEquals(
      execute(
        compileKernel(source),
        blocks.unsafe[Int > 0],
        threads.unsafe[Int > 0],
        arguments.map((name, value) => name.unsafe[NonEmpty[String]] -> value).toMap,
        memory
      ),
      Right(())
    )

  test("tile::gather4 reads four rows into one box and tile::scatter4 writes them back to four rows") {
    val memory                = DeviceMemory(16 * 32 + 20 * 32 + 64)
    val (source, target, out) = (0L, 16 * 32L, 36 * 32L)
    place(memory, source, TensorCopyCases.rows(16))
    run(
      TensorCopyCases.gatherPtx,
      blocks = 1,
      threads = 16,
      memory,
      "load"  -> map(TensorCopyCases.gatherMap(memory.start.raw + source, 16)),
      "store" -> map(TensorCopyCases.gatherMap(memory.start.raw + target, 20)),
      "out"   -> Argument.fromLong(memory.start.raw + out)
    )
    assertEquals(memory.words(out, 16), TensorCopyCases.gather.box)
    assertEquals(memory.words(target, 20 * 8), TensorCopyCases.gather.scattered)
  }

  for (shift <- Vec(0, 1))
    test(s"im2col::w and im2col::w::128 walk W alone with wHalo 2 and wOffset $shift") {
      val tensor = TensorCopyCases.pixels
      val memory = DeviceMemory((tensor.size * 4 + 608 * 4).unsafe[Int >= 0])
      place(memory, 0L, tensor)
      val maps = TensorCopyCases.wideMaps(memory.start.raw)
      run(
        TensorCopyCases.widePtx,
        blocks = 1,
        threads = 128,
        memory,
        "narrow" -> map(maps.narrow),
        "full"   -> map(maps.full),
        "out"    -> Argument.fromLong(memory.start.raw + tensor.size * 4L),
        "halo"   -> Argument.fromLong(2L),
        "shift"  -> Argument.fromLong(shift.toLong)
      )
      assertEquals(memory.words(tensor.size * 4L, 608), TensorCopyCases.wide(shift))
    }

  test("cta_group::2 loads complete on the mbarrier of the even CTA of the pair") {
    val memory = DeviceMemory(64 * 4 * 2)
    place(memory, 0L, TensorCopyCases.rows(8))
    run(
      TensorCopyCases.pairPtx,
      blocks = 2,
      threads = 32,
      memory,
      "load" -> map(TensorCopyCases.pairMap(memory.start.raw)),
      "out"  -> Argument.fromLong(memory.start.raw + 256)
    )
    assertEquals(memory.words(256L, 64), TensorCopyCases.rows(8))
  }

  test("packed sub-byte loads pad their units in shared memory and .b6p2x16 stores pack 6-bit values") {
    val memory = DeviceMemory(4 * 256 + 576 + 96)
    val bytes  = Vec.tabulate(256)(index => TensorCopyCases.source(index).toByte)
    (0 until 3).foreach(tensor => assertEquals(memory.writeBytes(memory.start.offset(tensor * 256L), bytes), Right(())))
    val base = memory.start.raw
    val maps = TensorCopyCases.packedMaps(Vec.tabulate(4)(tensor => base + tensor * 256L))
    run(
      TensorCopyCases.packedPtx,
      blocks = 1,
      threads = 32,
      memory,
      "four"   -> map(maps.four),
      "padded" -> map(maps.padded),
      "six"    -> map(maps.six),
      "spread" -> map(maps.spread),
      "out"    -> Argument.fromLong(base + 1024)
    )
    val loaded = memory.readBytes(memory.start.offset(1024), 576.unsafe[Int >= 0]).toOption.get
    TensorCopyCases.packed.loaded.zipWithIndex.foreach((expected, index) =>
      expected.foreach(value => assertEquals(loaded(index) & 0xff, value, clues(index)))
    )
    assertEquals(
      memory.readBytes(memory.start.offset(768), 96.unsafe[Int >= 0]).toOption.get.map(_ & 0xff),
      TensorCopyCases.packed.stored
    )
  }
}
