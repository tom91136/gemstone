package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.nvidia.fixtures.WarpgroupProducts.*
import net.kurobako.gemstone.memory.*

/** `wgmma.mma_async` on one warpgroup against a CPU product. The kernels are hand-written PTX that ptxas 13.3 assembles
  * for sm_90a.
  */
class WarpgroupSuite extends munit.FunSuite {
  private def run(test: Case, edit: String => String = identity)(using munit.Location): Unit = {
    val launch  = WarpgroupProducts.launch(test)
    val lanesAt = launch.image.size
    val outAt   = lanesAt + launch.lanes.size * 4
    val memory  = DeviceMemory((outAt + 128 * launch.registers * 4).unsafe[Int >= 0])
    assertEquals(memory.writeBytes(memory.start, launch.image), Right(()))
    launch.lanes.zipWithIndex.foreach { (word, index) =>
      assertEquals(memory.writeInt32(memory.start.offset(lanesAt.toLong + 4L * index), word.toInt), Right(()))
    }
    val arguments = Map[NonEmpty[String], Argument](
      ("image": NonEmpty[String]) -> Argument.fromLong(memory.start.raw),
      ("size": NonEmpty[String])  -> Argument.fromLong(launch.image.size.toLong),
      ("descA": NonEmpty[String]) -> Argument.fromLong(launch.descA),
      ("descB": NonEmpty[String]) -> Argument.fromLong(launch.descB),
      ("lanes": NonEmpty[String]) -> Argument.fromLong(memory.start.offset(lanesAt.toLong).raw),
      ("out": NonEmpty[String])   -> Argument.fromLong(memory.start.offset(outAt.toLong).raw),
      ("keep": NonEmpty[String])  -> Argument.fromLong(if (test.scale == "!p") 0L else 1L)
    )
    assertEquals(execute(compileKernel(edit(kernel(test))), 1, 128, arguments, memory), Right(()))
    val stored = memory.words(outAt.toLong, 128 * launch.registers)
    assertEquals(mismatches(test, launch.registers, stored).take(4), Vec.empty[String])
  }

  cases.foreach(value => test(value.name)(run(value)))

  // the lanes `skip` selects reach the product first; the rest take a longer path to it
  for ((what, skip) <- Vec("warps" -> "setp.ge.u32 %done, %t, 32;", "lanes" -> "setp.lt.u32 %done, %t, 16;"))
    test(s"a product waits for the $what of its warpgroup still in a divergent branch") {
      run(
        Case(8, Type.F32, Type.F16, Type.F16),
        _.replace(
          "  wgmma.fence.sync.aligned;",
          s"  $skip\n  @%done bra JOINED;\n  add.u32 %q, %q, 1;\n  add.u32 %q, %q, 1;\nJOINED:\n  wgmma.fence.sync.aligned;"
        )
      )
    }

  test("wgmma.fence, commit_group and wait_group lower to nothing the VM waits on") {
    val kernel = compileKernel(""".version 8.7
.target sm_90a
.address_size 64
.visible .entry fences()
{
  wgmma.fence.sync.aligned;
  wgmma.commit_group.sync.aligned;
  wgmma.wait_group.sync.aligned 1;
  ret;
}
""")
    assertEquals(execute(kernel, 1, 128, Map.empty, DeviceMemory(0)), Right(()))
  }

  test("a product needs every lane of its warpgroup") {
    assert(execute(
      compileKernel(kernel(Case(8, Type.F32, Type.F16, Type.F16))),
      1,
      64,
      Map.empty,
      DeviceMemory(0)
    ).isLeft)
  }
}
