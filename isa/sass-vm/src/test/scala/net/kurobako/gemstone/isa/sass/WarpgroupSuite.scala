package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

/** HGMMA, IGMMA, QGMMA and BGMMA as ptxas emits them for `wgmma.mma_async` on sm_90a, against the CPU product of the
  * PTX kernels they come from.
  */
class WarpgroupSuite extends Suite {
  WarpgroupFixtures.products.foreach { (listing, product) =>
    test(s"$listing: $product") {
      val found  = WarpgroupProducts.cases.find(_.name == product).getOrElse(fail(s"no product $product"))
      val launch = WarpgroupProducts.launch(found)
      val kernel = Kernel(
        90,
        "wgmma",
        Vec(Listing.function(Fixture.listing(listing), "wgmma", 90).value),
        registers = 255,
        shared = WarpgroupProducts.SharedBytes.unsafe[Int >= 0],
        base = parameterBase(90).unsafe[Int >= 0]
      )
      val run = kernel.launch(128)(
        Arg.Buffer(launch.image),
        Arg.u32(launch.image.size.toLong),
        Arg.u64(launch.descA),
        Arg.u64(launch.descB),
        Arg.words(launch.lanes*),
        Arg.zeros(128 * launch.registers * 4),
        Arg.u32(if (found.scale == "!p") 0L else 1L)
      )
      assertEquals(WarpgroupProducts.mismatches(found, launch.registers, run.words(5)).take(4), Vec.empty[String])
    }
  }

  // ptxas 13.3 -arch=sm_90a emits it for a wgmma.mma_async whose accumulators nothing reads
  test("a product into RZ writes nothing") {
    val discarded = program(90, "HGMMA.64x8x16.F16 RZ, gdesc[URZ], RZ, !UPT, gsb0; EXIT", base = 0x210)
    val plain     = program(90, "EXIT", base = 0x210)
    assertEquals(discarded.instructions().diff(plain.instructions()), Vec(VMInstruction.NoOp))
  }

  test("WARPGROUP.ARRIVE and WARPGROUP.DEPBAR leave nothing to wait on") {
    val fenced = program(90, "WARPGROUP.ARRIVE; WARPGROUP.DEPBAR.LE gsb0, 0x0; EXIT", base = 0x210)
    val plain  = program(90, "EXIT", base = 0x210)
    assertEquals(fenced.instructions().diff(plain.instructions()), Vec(VMInstruction.NoOp, VMInstruction.NoOp))
  }
}
