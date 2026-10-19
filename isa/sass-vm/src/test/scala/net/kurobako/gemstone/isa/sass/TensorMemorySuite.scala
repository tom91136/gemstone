package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*

// ptxas 13.3 output for the kernels of TensorMemoryCases, decoded from its machine code as Gemstone loads a cubin:
// LDTM/STTM, UTCATOMSWS and UVIRTCOUNT, UTCCP, UTCSHIFT, UTCBAR and UTCHMMA/UTCIMMA/UTCQMMA/UTCOMMA, USTGR/UREDGR and
// STAS.128, and SYNCS.ARRIVE.TMASK with its RPCMOV scratch moves.
class TensorMemorySuite extends munit.FunSuite {
  private def kernel(fixture: String, name: String, sm: Int)(using munit.Location): Kernel =
    fromHex(TensorMemoryFixtures.code(fixture), sm, name)

  test("sm_100a LDTM and STTM place each shape's registers as the data movement figures show") {
    val run = kernel("movementSm100a", "movement", 100).launch(128)(Arg.zeros(128 * 40))
    assertEquals(run.words(0).grouped(10).toVec, TensorMemoryCases.movementExpected)
  }

  TensorMemoryCases.products.foreach { product =>
    test(s"sm_100a ${product.name} with enable-input-d ${product.enable}") {
      val setup = product.setup
      val run   = kernel(s"${product.name}Sm100a", "mma", 100).launch(128)(
        Arg.zeros(128 * setup.columns * 4),
        Arg.Buffer(Vec.from(setup.image)),
        Arg.words(setup.cells*),
        Arg.u64(product.adesc),
        Arg.u64(product.bdesc),
        Arg.u32(product.idesc),
        Arg.u32(32768L),
        Arg.u32(setup.columns.toLong),
        Arg.u32(if (product.enable) 1L else 0L),
        Arg.u64(product.extra)
      )
      assertEquals(run.words(0), product.expected)
    }
  }

  test("sm_100a UTCHMMA.2CTA multiplies M = 256 across a CTA pair, committing to both CTAs' mbarriers") {
    val pair       = TensorMemoryCases.pair
    val memory     = Memory()
    val out        = memory.allocate(Vec.fill(2 * 32768)(0.toByte))
    val image      = memory.allocate(pair.images)
    val cells      = memory.allocate(Harness.words(pair.cells))
    val descriptor = Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2)
    val result     = kernel("pairSm100a", "pair", 100).copy(cluster = 2).run(block = 128, grid = 2, memory)(
      Arg.Region(out),
      Arg.Region(image),
      Arg.Region(cells),
      Arg.u64(descriptor),
      Arg.u64(descriptor),
      Arg.u32(pair.idesc)
    )
    assertEquals(result.map(_ => ()), Right(()))
    assertEquals(memory.readWords(out, (256 * 64).unsafe[Int >= 0]), pair.expected)
  }

  test("sm_103a LDTM.STAT reduces each thread's loaded values") {
    val input = Vec.from(TensorMemoryCases.reduceInputs.flatMap(row => row ++ Vec.fill(4)(0L)))
    val run   = kernel("reduceSm103a", "reduce", 103).launch(32)(Arg.words(input*))
    assertEquals(run.words(0).grouped(8).map(_.drop(4)).toVec, TensorMemoryCases.reduceExpected)
  }

  test("sm_100a release stores and reductions, STAS.128 and multimem bulk copies complete as issued") {
    val release = TensorMemoryCases.release
    val run     = kernel("releaseSm100a", "release", 100).launch(1)(Arg.Buffer(Harness.words(release.initial, 8)))
    assertEquals(run.words(argument = 0, width = 8), release.expected)
  }

  test("sm_100a warp-aggregated noComplete arrivals give each thread its own token") {
    val run = kernel("noCompleteSm100a", "arrivals", 100).launch(8)(Arg.zeros(32))
    assertEquals(run.words(0), TensorMemoryCases.noCompleteExpected)
  }
}
