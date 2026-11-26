package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.testing.*

class SASSBlockCostSuite extends Suite {

  import SASSFixtures.arithmetic
  import SASSFixtures.cubin
  import SASSFixtures.kernel
  import SASSFixtures.launch

  private val targetName = SASSFixtures.target

  private def decoded(image: Vec[Byte]): Vec[DecodedInstruction] =
    SASSModel.staticCode(image, kernel).fold(fail(_), _._3)

  private def windowOf(image: Vec[Byte]): Window = SASSFixtures.first(image)

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

  test("an instruction that does nothing never costs a region less") {
    val window = windowOf(arithmetic)
    val body   = decoded(arithmetic).slice(window.first, window.last + 1)
    val cost   = SASSBlockCost.of(arithmetic, kernel, targetName, launch).fold(fail(_), identity)
    val text   = (sequence: Vec[DecodedInstruction]) =>
      sequence.map(instruction => Printer.print(instruction.operation).getOrElse(instruction.mnemonic.raw))
    val floored  = RewriteSearch.floored(text, body)(cost.cost)
    val original = floored(window, body).fold(fail(_), _.cycles)
    (0 to body.size).foreach { at =>
      val grown = floored(window, body.patch(at, Vec(instruction("IADD3 R8, R2, 0x1, RZ ;")), 0))
        .fold(fail(_), _.cycles)
      assert(grown >= original, s"a dead instruction at $at costs $grown against $original")
    }
  }

  private def laidOver(source: String, last: Option[String] = None): Vec[Byte] = {
    val target = Target.parse(targetName).fold(fail(_), identity)
    val parsed = Parser.parseProgram(source).fold(error => fail(error.toString), identity)
    // a word as ptxas encodes it, which the assembler keeps
    val appended = last.map { word =>
      val bytes   = Hex.parse(word).fold(fail(_), identity).toArray
      val address = (parsed.statements.size * 16).unsafe[Int >= 0]
      val decoded = InstructionDecoder.decode(address, bytes, target).fold(fail(_), identity)
      Program.Statement.Operation(decoded.operation, Some(decoded))
    }
    val code = new Assembler(target).encode(parsed.copy(statements = parsed.statements ++ appended))
      .fold(error => fail(error.toString), identity)
    Cubin.replaceKernel(cubin("intchain.sm_89.cubin"), kernel.unsafe[NonEmpty[String]], code)
      .fold(error => fail(error.raw), identity)
  }

  private def costOf(image: Vec[Byte]): SASSBlockCost =
    SASSBlockCost.of(image, kernel, targetName, launch).fold(fail(_), identity)

  private def shrunk(image: Vec[Byte], first: Int, last: Int) =
    costOf(image).cost(Window(0, first, last, Set.empty, Set.empty), decoded(image).slice(first + 1, last + 1))

  private def assertAnchored(result: Either[String, AMDGPUBlockCost.Cost]): Unit =
    assert(result.left.exists(_.contains("relative to the program counter")), result)

  test("a return refuses a size-changing rewrite") {
    val image = laidOver(
      """IADD3 R2, R2, 0x1, RZ ;
        |IADD3 R4, R2, 0x2, RZ ;
        |IADD3 R16, RZ, 0x40, RZ ;
        |CALL.REL.NOINC 0x50 ;
        |EXIT ;
        |IADD3 R6, R2, 0x3, RZ ;
        |IADD3 R8, R2, 0x4, RZ ;
        |""".stripMargin,
      // RET.REL.NODEC R16 to the start of its function
      Some("5079001060dcffffffffc30300ec0f00")
    )
    assertAnchored(shrunk(image, 0, 1))
  }

  test("an indirect branch refuses a size-changing rewrite") {
    val image = laidOver(
      """IADD3 R2, R2, 0x1, RZ ;
        |IADD3 R4, R2, 0x2, RZ ;
        |BRX R4 ;
        |EXIT ;
        |""".stripMargin
    )
    assertAnchored(shrunk(image, 0, 1))
  }

  test("a read of the program counter refuses a size-changing rewrite") {
    val image = laidOver(
      """IADD3 R2, R2, 0x1, RZ ;
        |IADD3 R4, R2, 0x2, RZ ;
        |LEPC R6 ;
        |EXIT ;
        |""".stripMargin
    )
    assertAnchored(shrunk(image, 0, 1))
  }

  test("a deletion that empties its block is refused with the rewrites that move code") {
    val image = laidOver(
      """@P0 BRA 0x30 ;
        |IADD3 R2, R2, 0x1, RZ ;
        |IADD3 R4, R2, 0x2, RZ ;
        |LEPC R6 ;
        |EXIT ;
        |""".stripMargin
    )
    assertAnchored(costOf(image).cost(Window(1, 1, 2, Set.empty, Set.empty), Vec.empty[DecodedInstruction]))
  }

  test("a target strictly inside a window refuses the window") {
    val image = laidOver(
      """BSSY B0, 0x30 ;
        |IADD3 R2, R2, 0x1, RZ ;
        |BSYNC B0 ;
        |IADD3 R4, R2, 0x2, RZ ;
        |IADD3 R6, R2, 0x3, RZ ;
        |EXIT ;
        |""".stripMargin
    )
    assertAnchored(shrunk(image, 2, 3))
  }

  private val branched =
    """IADD3 R2, R2, 0x1, RZ ;
      |IADD3 R4, R2, 0x2, RZ ;
      |IADD3 R6, R2, 0x3, RZ ;
      |IADD3 R8, R2, 0x4, RZ ;
      |@P0 BRA 0x20 ;
      |EXIT ;
      |""".stripMargin

  private def branchTarget(code: Vec[DecodedInstruction]): Option[BigInt] =
    code.find(_.mnemonic.raw == "BRA").flatMap(_.operands.lastOption).collect {
      case Operand.Integer(value) => value
      case Operand.Bits(value, _) => value
    }

  test("a branch to the start of a window keeps reaching the start when the rewrite reorders it") {
    val image = laidOver(branched)
    val body  = decoded(image)
    val laid  =
      costOf(image).laidOut(Window(1, 2, 3, Set.empty, Set.empty), Vec(body(3), body(2))).fold(fail(_), identity)
    assertEquals(branchTarget(laid), Some(BigInt(0x20)))
  }

  test("a branch target moves with the code a shorter window leaves before it") {
    val image = laidOver(branched)
    val body  = decoded(image)
    val laid  = costOf(image).laidOut(Window(0, 0, 1, Set.empty, Set.empty), Vec(body(1))).fold(fail(_), identity)
    assertEquals(branchTarget(laid), Some(BigInt(0x10)))
  }

  test("a region that reaches memory is not costed under a stated launch") {
    val image  = cubin("intchain.sm_89.cubin")
    val window =
      SASSWindows.of(image, kernel, targetName).fold(fail(_), identity).headOption.getOrElse(fail("no window"))
    val result = SASSBlockCost.of(image, kernel, targetName, launch)
      .flatMap(_.cost(window, decoded(image).slice(window.first, window.last + 1)))
    assert(result.left.exists(_.contains("reaches memory")), result)
  }

  private val sum       = SASSFixtures.sum
  private val loops     = SASSFixtures.cubin(SASSFixtures.loops)
  private val trips     = 3L
  private val arguments = LaunchFixtures.arguments(s"buffer:4096,buffer:4096,i32:${4L + trips}")

  private def launched(image: Vec[Byte]): ProfileStream =
    LaunchRecording.record(Language.SASS, image, sum, 32, 1, arguments).fold(fail(_), identity)

  private def recorded(image: Vec[Byte]): AMDGPUBlockCost.Launch =
    AMDGPUBlockCost.Launch.Recorded(launched(image), None)

  private val tail = {
    val (graph, _, _) = SASSModel.staticCode(loops, sum).fold(fail(_), identity)
    val block         = graph.blocks.indexWhere(block => block.first <= 111 && 115 <= block.last)
    Window(block, 111, 115, Set.empty, Set.empty)
  }

  private def body(image: Vec[Byte], window: Window): Vec[DecodedInstruction] =
    SASSModel.staticCode(image, sum).fold(fail(_), _._3.slice(window.first, window.last + 1))

  private def dynamic(image: Vec[Byte], window: Window): Double = {
    val (graph, _, code) = SASSModel.staticCode(image, sum).fold(fail(_), identity)
    val offsets          = code.map(_.address.raw.toInt)
    val region           = BlockRegion.of(sum, graph, offsets, window.first)
    BlockRegion.timing(launched(image), Architecture.named(targetName).get, None, region.label)
      .fold(fail(_), _.block.cyclesPerIteration)
  }

  test("a recorded launch costs a loop that loads as the dynamic view of the launch does") {
    val stated = SASSBlockCost.of(loops, sum, targetName, launch).flatMap(_.cost(tail, body(loops, tail)))
    assert(stated.left.exists(_.contains("reaches memory")), stated)
    val regenerated = SASSFixtures.rewritten(loops, sum, tail, body(loops, tail))
    val cost        = SASSBlockCost.of(loops, sum, targetName, recorded(loops)).flatMap(_.cost(tail, body(loops, tail)))
    assertEquals(cost.map(_.cycles), Right(dynamic(regenerated, tail)))
    assertEquals(cost.map(_.shift), Right(0))
  }

  test("a rewrite of a recorded loop costs what a launch of the rewritten kernel records") {
    val code = body(loops, tail)
    val cost = SASSBlockCost.of(loops, sum, targetName, recorded(loops)).fold(fail(_), identity)
    Vec(
      "deleted"  -> (code.take(4), -16),
      "changed"  -> (code.take(4) :+ instruction("FADD R5, R2, R5 ;"), 0),
      "inserted" -> (code.take(2) ++ Vec(instruction("IADD3 R20, R5, 0x1, RZ ;")) ++ code.drop(2), 16)
    ).foreach { case (kind, (replacement, shift)) =>
      val image = SASSFixtures.rewritten(loops, sum, tail, replacement)
      assertEquals(
        cost.cost(tail, replacement).map(found => (found.cycles, found.shift)),
        Right((dynamic(image, tail), shift)),
        kind
      )
    }
  }

  test("a rewrite whose memory instructions the recorded launch does not cover is refused with the reason") {
    val window = Window(tail.block, 108, 111, Set.empty, Set.empty)
    val code   = body(loops, window)
    val cost   = SASSBlockCost.of(loops, sum, targetName, recorded(loops)).fold(fail(_), identity)
    val whole  = cost.cost(window, code)
    assert(whole.isRight, whole)
    val moved = cost.cost(window, instruction("MOV R2, UR5 ;") +: code.tail)
    assert(moved.left.exists(_.contains("which the rewrite changes in the window 108..111")), moved)
    val loaded = cost.cost(window, code.take(1) ++ Vec(instruction("LDG.E R20, [R2.64] ;")))
    assert(loaded.left.exists(_.contains("no recorded memory traffic for LDG.E R20, [R2.64]")), loaded)
  }

  test("a recording of another kernel is refused") {
    val other = LaunchRecording.record(
      Language.SASS,
      loops,
      "collatz",
      32,
      1,
      LaunchFixtures.arguments("buffer:4096,i32:5")
    ).fold(fail(_), identity)
    val found = SASSBlockCost.of(loops, sum, targetName, AMDGPUBlockCost.Launch.Recorded(other, None))
    assert(found.left.exists(_.contains("is not a launch of this sumloop")), found.map(_ => ()))
  }
}
