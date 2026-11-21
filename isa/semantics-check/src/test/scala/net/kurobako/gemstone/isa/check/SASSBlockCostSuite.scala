package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*

class SASSBlockCostSuite extends Suite {

  private val targetName = "sm_89"
  private val kernel     = "intchain"

  private def cubin(name: String): Vec[Byte] =
    Vec.unsafeWrapArray(
      getClass.getResourceAsStream(s"/net/kurobako/gemstone/isa/sass/controlwords/$name").readAllBytes()
    )

  private val source =
    """IADD3 R2, R2, 0x1, RZ ;
      |IADD3 R4, R2, 0x2, RZ ;
      |IMAD R6, R2, R2, RZ ;
      |ISETP.GT.U32.AND P0, PT, R6, 0x10, PT ;
      |@P0 EXIT ;
      |EXIT ;
      |""".stripMargin

  /** `intchain` with its code replaced by a block that no load or store reaches. */
  private lazy val arithmetic: Vec[Byte] = {
    val target  = Target.parse(targetName).fold(fail(_), identity)
    val program = Parser.parseProgram(source).fold(error => fail(error.toString), identity)
    val code    = new Assembler(target).encode(program).fold(error => fail(error.toString), identity)
    Cubin.replaceKernel(cubin("intchain.sm_89.cubin"), kernel.unsafe[NonEmpty[String]], code)
      .fold(error => fail(error.raw), identity)
  }

  private val launch = AMDGPUBlockCost.Launch.Stated(32.unsafe[Int > 0], 0L.unsafe[Long >= 0], None)

  private def decoded(image: Vec[Byte]): Vec[DecodedInstruction] =
    SASSModel.staticCode(image, kernel).fold(fail(_), _._3)

  private def windowOf(image: Vec[Byte]): Window =
    SASSWindows.of(image, kernel, targetName).fold(fail(_), identity).find(window =>
      window.first == 0 && window.last >= 3
    )
      .getOrElse(fail("no window over the first block"))

  private def instruction(text: String): DecodedInstruction =
    DecodedInstruction(0.unsafe[Int >= 0], Parser.parse(text).fold(fail(_), identity), None, None, None)

  private def cycles(image: Vec[Byte], window: Window, sequence: Vec[DecodedInstruction]): Double =
    SASSBlockCost.of(image, kernel, targetName, launch).flatMap(_.cost(window, sequence)).fold(fail(_), _.cycles)

  test("deleting a dead instruction costs fewer cycles than keeping it, and inserting one costs more") {
    val window = windowOf(arithmetic)
    val body   = decoded(arithmetic).slice(window.first, window.last + 1)
    val kept   = cycles(arithmetic, window, body)
    assert(kept > 0.0, kept)
    assert(cycles(arithmetic, window, body.patch(1, Vec.empty[DecodedInstruction], 1)) < kept)
    assert(cycles(arithmetic, window, body.patch(2, Vec(instruction("IADD3 R8, R2, 0x1, RZ ;")), 0)) > kept)
  }

  test("a region that reaches memory is not costed under a stated launch") {
    val image  = cubin("intchain.sm_89.cubin")
    val window =
      SASSWindows.of(image, kernel, targetName).fold(fail(_), identity).headOption.getOrElse(fail("no window"))
    val result = SASSBlockCost.of(image, kernel, targetName, launch)
      .flatMap(_.cost(window, decoded(image).slice(window.first, window.last + 1)))
    assert(result.left.exists(_.contains("reaches memory")), result)
  }

  test("a recorded launch of an SASS kernel is refused") {
    val recorded = AMDGPUBlockCost.Launch.Recorded(net.kurobako.gemstone.profile.ProfileRecorder().stream, None)
    assert(SASSBlockCost.of(arithmetic, kernel, targetName, recorded).isLeft)
  }
}
