package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite

class CodeSectionSuite extends Suite {
  private val sms = Vec(35, 50, 89)

  test("public assembly APIs preserve independent Kepler, Maxwell, and Ada fixture bytes") {
    sms.foreach { sm =>
      val code    = Fixture.saxpy(sm)
      val program = Disassembler(target(sm)).decode(code).fold(error => fail(error.message), identity(_))
      val encoded = Assembler(target(sm)).encode(program).fold(error => fail(error.message), identity(_))
      assertEquals(encoded, code, s"SM$sm")
    }
  }

  test("encoding rejects a changed instruction sequence that no longer matches retained addresses") {
    sms.foreach { sm =>
      val code    = Fixture.saxpy(sm)
      val program = Disassembler(target(sm)).decode(code).fold(error => fail(error.message), identity(_))
      val first   = program.statements.indexWhere {
        case Program.Statement.Operation(_, _, _) => true
        case _                                    => false
      }
      val changed = program.copy(statements = program.statements.take(first) ++ program.statements.drop(first + 1))
      assert(Assembler(target(sm)).encode(changed).isLeft)
    }
  }

  test("editing an instruction across a bundle boundary preserves the other slots") {
    sms.foreach { sm =>
      val code    = Fixture.saxpy(sm).toArray
      val section = CodeSection.decode(code, target(sm)).fold(fail(_), identity(_))
      val program = Disassembler(target(sm)).decode(code.toVec)
        .fold(error => fail(error.message), identity(_))
      val index          = section.layout.bundles.head.words.size
      val statementIndex = program.statements.zipWithIndex.collect {
        case (Program.Statement.Operation(_, _, _), offset) => offset
      }(index)
      val replacement = Parser.parse("MOV R3, R7;").fold(fail(_), identity(_))
      val replaced    = program.statements.updated(statementIndex, Program.Statement.Operation(replacement))
      // A replacement receives its own scheduling fields; untouched slots retain their exact controls.
      val statements = replaced.lift(statementIndex - 1) match {
        case Some(Program.Statement.Bundle(CodeWords.BundleControl.Maxwell(slots, reserved))) =>
          val fresh  = Program(Some(target(sm)), Vec(Program.Statement.Operation(replacement)))
          val bytes  = Assembler(target(sm)).encode(fresh).fold(error => fail(error.message), identity(_))
          val layout = CodeWords.split(bytes.toArray, target(sm).generation).fold(fail(_), identity(_))
          layout.bundles.head.control match {
            case CodeWords.BundleControl.Maxwell(controls, _) =>
              replaced.updated(
                statementIndex - 1,
                Program.Statement.Bundle(CodeWords.BundleControl.Maxwell(slots.updated(0, controls.head), reserved))
              )
            case _ => fail("expected Maxwell replacement controls")
          }
        case _ => replaced
      }
      val bytes = Assembler(target(sm)).encode(program.copy(statements = statements))
        .fold(error => fail(error.message), identity(_))
      val decoded = CodeSection.decode(bytes.toArray, target(sm)).fold(fail(_), identity(_))
      assertEquals(decoded.instructions(index).operation, replacement)
      assertEquals(
        decoded.layout.bundles.map(_.words.map(_.address)),
        section.layout.bundles.map(_.words.map(_.address))
      )
      section.instructions.indices.filter(_ != index).foreach { slot =>
        assertEquals(decoded.instructions(slot), section.instructions(slot), s"SM$sm slot $slot")
      }
    }
  }
}
