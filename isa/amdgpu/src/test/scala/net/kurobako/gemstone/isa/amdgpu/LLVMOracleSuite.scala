package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*
import net.kurobako.gemstone.isa.{Printer as _, *}

class LLVMOracleSuite extends munit.FunSuite {
  test("LLVM selection covers every ISA and includes instruction pairs") {
    assertEquals(oracle.sourceFileCount, 770)
    assertEquals(oracle.sourceCaseCount, 379589)
    assertEquals(oracle.cases.size, oracle.selectedCaseCount)
    assert(oracle.cases.size >= 4154)
    assertEquals((oracle.targets ++ oracle.diagnosticTargets).distinct.size, 46)
    assertEquals(oracle.targets.view.map(_.isa).toSet, ISA.values.toSet)
    assert(oracle.cases.count(_.assembly.contains("::")) >= 30)
  }

  oracle.cases.groupBy(value => (value.target.name, value.waveSize.raw)).toVector.sortBy(_._1).foreach {
    case ((target, wave), cases) => test(s"LLVM text and exact-byte round trips for $target wave$wave") {
        cases.foreach { golden =>
          val clue   = s"${golden.source}: ${golden.target.name}, wave${golden.waveSize}: ${golden.assembly}"
          val parsed = Parser.parse(golden.assembly).fold(error => fail(s"$clue: $error"), identity)
          val text   = Printer.render(parsed)
          assertEquals(Parser.parse(text), Right(parsed), clue)
          val statement = parsed.statements match {
            case Vec(value) => value
            case other      => fail(s"$clue: expected one instruction or pair, found $other")
          }
          val actual = Binary.encodeStatement(statement, golden.target, golden.waveSize)
            .fold(error => fail(s"$clue: $error"), identity)
          assertEquals(actual, golden.bytes, clue)
          val decoded = Binary.decodeStatementAt(actual, 0, golden.target, golden.waveSize)
            .fold(error => fail(s"$clue: $error"), identity)
          assertEquals(decoded.size.raw, actual.size, clue)
          assertEquals(Binary.encodeStatement(decoded.statement, golden.target, golden.waveSize), Right(actual), clue)
          (statement, decoded.statement) match {
            case (Statement.Instruction(expected), Statement.Instruction(found)) =>
              // Legacy names may share an opcode; the decoded instruction must retain the encoding identity.
              val expectedEncodings =
                Metadata.encodingsFor(expected.definition, golden.target.isa).map(value => value.format -> value.opcode)
              assert(
                Metadata.encodingsFor(found.definition, golden.target.isa).exists(value =>
                  expectedEncodings.contains(value.format -> value.opcode)
                ),
                clue
              )
            case (Statement.InstructionPair(_, _), Statement.InstructionPair(_, _)) => ()
            case _ => fail(s"$clue: instruction boundary changed")
          }
          val rendered = Printer.render(Program(Vec(decoded.statement)))
          val reparsed = Parser.parse(rendered).fold(error => fail(s"$clue: $rendered: $error"), identity)
          assertEquals(
            Binary.encodeStatement(reparsed.statements.head, golden.target, golden.waveSize),
            Right(actual),
            clue
          )
        }
      }

  }

  test("LLVM diagnostics never pass conservative target validation") {
    assertEquals(oracle.sourceDiagnosticCount, 98714)
    assertEquals(oracle.diagnostics.size, oracle.selectedDiagnosticCount)
    assert(oracle.diagnostics.size >= 300)
    oracle.diagnostics.foreach { diagnostic =>
      Parser.parse(diagnostic.assembly) match {
        case Left(_)        => ()
        case Right(program) =>
          Validation.validate(program, diagnostic.target) match {
            case ValidationOutcome.Accepted =>
              fail(
                s"${diagnostic.source}: accepted LLVM diagnostic for ${diagnostic.target.name}: " +
                  s"${diagnostic.assembly}\nexpected: ${diagnostic.message}"
              )
            case ValidationOutcome.Rejected(_)   => ()
            case ValidationOutcome.Incomplete(_) => ()
          }
      }
    }
  }

}
