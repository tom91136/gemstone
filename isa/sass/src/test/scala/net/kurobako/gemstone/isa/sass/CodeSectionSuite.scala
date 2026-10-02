package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class CodeSectionSuite extends munit.FunSuite {
  private case class Fixture(sm: Int, code: String)

  private val fixtures = Vec(
    Fixture(
      35,
      """
        |c01080109cb0a00806009c08003cc06402001c29003cc06426009c29003cc064
        |0a001c28003cc0640e009c28003cc064fe031c2a00008c6012249c2a005c985a
        |10808080acb8b80816249c2a005c905a10081c00000080e41a249c2a005cb85a
        |1e249c2a005cc85a26249c2a005ce85a14081c02000080e418081c04000080e4
        |b810bc00000000081c081c06000080e424081c08000080e43c001c0000000018
        |3c001cfcff7f0012023c1c0000008085023c1c0000008085023c1c0000008085
        |""".stripMargin
    ),
    Fixture(
      50,
      """
        |f60720e200fc1c00010087008007984c000057020000c8f0020017020000c8f0
        |f10fc2fe42d81f0003002700807f104f020027000001004e000037001801305b
        |f107a2fc00d81f080a003700000048380000d70100002838020a07050080104c
        |f20702fe40d81f00030017050008104c040a27050080104c020207000020d5ee
        |f20702fe00d81f00050037050008104c060a47050080104c040407000020d5ee
        |e20720f600d81f00070057050008104c060607000020d5ee0a0a67050080104c
        |f707c0fe20c81f000b0077050008104c080647000081c85c090757008001c95c
        |f507e0ff00fc1f00080a07000020ddee0f000700000000e30f0087ffff0f40e2
        |""".stripMargin
    ),
    Fixture(
      89,
      """
        |247601ff000a0000ff008e0700e40f0019790200000000000021000000240e00
        |0c780002010000007042f00300da1f004d090000000000000000800300ea0f00
        |2578020204000000ff028e0700e20f00b97a040000460000000a000000c60f00
        |247200ffff00000002008e0700e20f00107a020200580000ffe0f10700c60f00
        |827b05000000c0000008000000220e00107a030300590000ffe47f0000ca0f00
        |86790002050000000419100c00e21f004d790000000000000000800300ea0f00
        |47790000f0ffffffffff830300c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin
    )
  )

  private def target(sm: Int): Target = Target.parse(s"SM$sm").fold(fail(_), identity(_))

  test("public assembly APIs preserve independent Kepler, Maxwell, and Ada fixture bytes") {
    fixtures.foreach { fixture =>
      val code    = FixtureBytes.readHex(fixture.code).toVec
      val program = Disassembler(target(fixture.sm)).decode(code).fold(error => fail(error.message), identity(_))
      val encoded = Assembler(target(fixture.sm)).encode(program).fold(error => fail(error.message), identity(_))
      assertEquals(encoded, code, s"SM${fixture.sm}")
    }
  }

  test("encoding rejects a changed instruction sequence that no longer matches retained addresses") {
    fixtures.foreach { fixture =>
      val code    = FixtureBytes.readHex(fixture.code).toVec
      val program = Disassembler(target(fixture.sm)).decode(code).fold(error => fail(error.message), identity(_))
      val first   = program.statements.indexWhere {
        case _: Program.Statement.Operation => true
        case _                              => false
      }
      val changed = program.copy(statements = program.statements.take(first) ++ program.statements.drop(first + 1))
      assert(Assembler(target(fixture.sm)).encode(changed).isLeft)
    }
  }

  test("editing an instruction across a bundle boundary preserves the other slots") {
    fixtures.foreach { fixture =>
      val code    = FixtureBytes.readHex(fixture.code)
      val section = CodeSection.decode(code, fixture.sm).fold(fail(_), identity(_))
      val program = Disassembler(target(fixture.sm)).decode(code.toVec)
        .fold(error => fail(error.message), identity(_))
      val index          = section.layout.bundles.head.words.size
      val statementIndex = program.statements.zipWithIndex.collect {
        case (_: Program.Statement.Operation, offset) => offset
      }(index)
      val replacement = Parser.parse("MOV R3, R7;").fold(fail(_), identity(_))
      val replaced    = program.statements.updated(statementIndex, Program.Statement.Operation(replacement))
      // A replacement receives its own scheduling fields; untouched slots retain their exact controls.
      val statements = replaced.lift(statementIndex - 1) match {
        case Some(Program.Statement.Bundle(SASSCodeWords.BundleControl.Maxwell(slots, reserved))) =>
          val fresh  = Program(Some(target(fixture.sm)), Vec(Program.Statement.Operation(replacement)))
          val bytes  = Assembler(target(fixture.sm)).encode(fresh).fold(error => fail(error.message), identity(_))
          val layout = SASSCodeWords.split(bytes.toArray, fixture.sm).fold(fail(_), identity(_))
          layout.bundles.head.control match {
            case SASSCodeWords.BundleControl.Maxwell(controls, _) =>
              replaced.updated(
                statementIndex - 1,
                Program.Statement.Bundle(SASSCodeWords.BundleControl.Maxwell(slots.updated(0, controls.head), reserved))
              )
            case _ => fail("expected Maxwell replacement controls")
          }
        case _ => replaced
      }
      val bytes = Assembler(target(fixture.sm)).encode(program.copy(statements = statements))
        .fold(error => fail(error.message), identity(_))
      val decoded = CodeSection.decode(bytes.toArray, fixture.sm).fold(fail(_), identity(_))
      assertEquals(decoded.instructions(index).operation, replacement)
      assertEquals(
        decoded.layout.bundles.map(_.words.map(_.address)),
        section.layout.bundles.map(_.words.map(_.address))
      )
      section.instructions.indices.filter(_ != index).foreach { slot =>
        assertEquals(decoded.instructions(slot), section.instructions(slot), s"SM${fixture.sm} slot $slot")
      }
    }
  }
}
