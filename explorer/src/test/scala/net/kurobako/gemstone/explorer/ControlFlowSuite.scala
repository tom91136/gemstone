package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.testing.*

class ControlFlowSuite extends Suite {

  private def bytes(text: String): Vec[Byte] = Hex.parse(text).toOption.get

  private def listed(image: Vec[Byte], language: Language, target: Option[String]): Vec[ProgramLine] =
    ProgramListing.of(ProgramListing.Image(language, image, target)).toOption.get

  private def checkLoops(flow: ControlFlow): Unit =
    flow.loops.foreach { loop =>
      assert(loop.body.contains(loop.header), loop)
      assert(loop.latches.forall(latch => flow.blocks(latch).successors.contains(loop.header)), loop)
    }

  test("SASS control flow partitions the sample kernel into blocks and lists its natural loops") {
    val image     = bytes(SassExample.cubin)
    val lines     = listed(image, Language.SASS, None)
    val function  = lines.flatMap(_.function).head
    val addresses = lines.filter(_.function.contains(function)).map(_.address)
    val flow      = SASSModel.controlFlow(image, function).toOption.get
    val positions = addresses.zipWithIndex.toMap
    val ranges    = flow.blocks.map(block => (positions(block.first), positions(block.last)))
    assert(ranges.nonEmpty)
    assertEquals(ranges.head._1, 0)
    assertEquals(ranges.last._2, addresses.size - 1)
    assert(ranges.zip(ranges.drop(1)).forall((left, right) => right._1 == left._2 + 1), ranges)
    checkLoops(flow)
  }

  test("AMDGPU control flow lists the blocks and the loop of a counted loop in the sample kernel") {
    val source =
      """s_mov_b32 s0, 0
        |loop:
        |s_add_u32 s0, s0, 1
        |s_cmp_lt_u32 s0, 4
        |s_cbranch_scc1 loop
        |s_endpgm
        |""".stripMargin
    val code  = AMDGPUModel.assemble(source, Some("gfx900")).map(bytes(_)).toOption.get
    val image = CodeObject.decode(bytes(AmdgpuExample.codeObject))
      .flatMap(_.replaceKernel(AmdgpuExample.kernel.unsafe[NonEmpty[String]], code))
      .toOption.get.image
    val mnemonics = Vec("s_mov_b32", "s_add_u32", "s_cmp_lt_u32", "s_cbranch_scc1", "s_endpgm")
    val lines     = listed(image, Language.AMDGPU, Some("gfx900"))
    val start     = lines.map(_.mnemonic).indexOfSlice(mnemonics)
    assert(start >= 0, lines.map(_.text))
    val at   = lines.slice(start, start + mnemonics.size).map(_.address)
    val flow = AMDGPUModel.controlFlow(image, AmdgpuExample.kernel).toOption.get
    assertEquals(
      flow.blocks.map(block => (block.first, block.last, block.successors.sorted, block.open)),
      Vec(
        (at(0), at(0), Vec(1), false),
        (at(1), at(3), Vec(1, 2), false),
        (at(4), at(4), Vec.empty[Int], false)
      )
    )
    assertEquals(flow.loops.map(loop => (loop.header, loop.body, loop.latches)), Vec((1, Vec(1), Vec(1))))
  }
}
