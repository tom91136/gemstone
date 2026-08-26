package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.Database
import net.kurobako.gemstone.isa.amdgpu.common.generated.GoldenCorpus

class GoldenCorpusSuite extends munit.FunSuite {
  test("parse and round-trip the exhaustive exact-target LLVM MC corpus") {
    assert(GoldenCorpus.sourceFileCount >= 700)
    assert(GoldenCorpus.cases.size >= 10000)
    GoldenCorpus.cases.foreach { golden =>
      val parsed = Assembly.parse(golden.assembly).fold(
        error => fail(s"${golden.source}: ${golden.target.name}: $error\n${golden.assembly}"),
        identity
      )
      val canonical = Assembly.print(parsed)
      val reparsed  = Assembly.parse(canonical).fold(
        error => fail(s"${golden.source}: failed to parse canonical output: $error\n$canonical"),
        identity
      )
      assertEquals(reparsed, parsed, s"${golden.source}: ${golden.target.name}: ${golden.assembly}")
    }
  }

  test("every positive oracle is encoded exactly or classified as an unsupported format") {
    var encoded           = 0
    var unsupportedFormat = 0
    var paired            = 0
    GoldenCorpus.cases.foreach { golden =>
      val program = Assembly.parse(golden.assembly).toOption.get
      program.statements match {
        case Vector(Statement.Instruction(instruction)) =>
          val definitions = Database.lookup(instruction.opcode.value, golden.target)
          val formats     = definitions.map(_.format.toUpperCase).distinct
          Binary.encode(instruction, golden.target, Database) match {
            case Right(actual) =>
              assert(formats.contains("SOPP"), s"encoded an unimplemented format at ${golden.source}: $formats")
              assertEquals(actual, golden.bytes, s"${golden.source}: ${golden.target.name}: ${golden.assembly}")
              encoded += 1
            case Left(CodecError.UnsupportedFormat(_, _, declared)) =>
              assert(!formats.contains("SOPP"), s"SOPP case escaped encoding at ${golden.source}: ${golden.assembly}")
              assertEquals(declared, formats.sorted)
              unsupportedFormat += 1
            case Left(CodecError.UnsupportedInstruction(_, _)) =>
              assert(definitions.isEmpty, s"known instruction was classified unknown at ${golden.source}")
              unsupportedFormat += 1
            case Left(CodecError.UnsupportedOperandSyntax(_, _, _)) =>
              assert(formats.contains("SOPP"), s"non-SOPP syntax classification at ${golden.source}: $formats")
              unsupportedFormat += 1
            case Left(error) => fail(s"${golden.source}: unclassified codec result $error\n${golden.assembly}")
          }
        case Vector(Statement.InstructionPair(_, _)) => paired += 1
        case other => fail(s"${golden.source}: expected one instruction or pair, found $other")
      }
    }
    assert(encoded >= 50, s"expected at least fifty exact SOPP checks, found $encoded")
    assertEquals(encoded + unsupportedFormat + paired, GoldenCorpus.cases.size)
  }

  test("LLVM diagnostics never pass conservative target validation") {
    assert(GoldenCorpus.diagnostics.size >= 1000)
    GoldenCorpus.diagnostics.foreach { diagnostic =>
      Assembly.parse(diagnostic.assembly) match {
        case Left(_)        => ()
        case Right(program) =>
          Validator.validate(program, diagnostic.target, Database) match {
            case ValidationResult.Valid =>
              fail(
                s"${diagnostic.source}: accepted LLVM diagnostic for ${diagnostic.target.name}: " +
                  s"${diagnostic.assembly}\nexpected: ${diagnostic.message}"
              )
            case _: ValidationResult.Invalid     => ()
            case _: ValidationResult.Unsupported => ()
          }
      }
    }
  }
}
