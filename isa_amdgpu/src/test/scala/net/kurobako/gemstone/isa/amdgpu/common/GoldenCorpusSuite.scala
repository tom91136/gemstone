package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

import scala.collection.mutable

class GoldenCorpusSuite extends munit.FunSuite {
  test("round-trip every LLVM MC oracle instruction covered by the pinned production metadata") {
    assert(GoldenCorpus.sourceFileCount >= 700)
    assert(GoldenCorpus.cases.size >= 10000)
    var roundTripped = 0
    val unsupported  = mutable.Map.empty[String, Int].withDefaultValue(0)
    GoldenCorpus.cases.foreach { golden =>
      Assembly.parse(golden.assembly) match {
        case Left(error) if error.message.startsWith("unknown AMDGPU opcode") => recordUnsupported(error, unsupported)
        case Left(error)   => fail(s"${golden.source}: ${golden.target.name}: $error\n${golden.assembly}")
        case Right(parsed) =>
          val canonical = Assembly.print(parsed)
          val reparsed  = Assembly.parse(canonical).fold(
            error => fail(s"${golden.source}: failed to parse canonical output: $error\n$canonical"),
            identity
          )
          assertEquals(
            Assembly.print(reparsed),
            canonical,
            s"${golden.source}: ${golden.target.name}: ${golden.assembly}"
          )
          roundTripped += 1
      }
    }
    assert(roundTripped >= 300000, s"expected broad LLVM oracle coverage, found $roundTripped")
    assertEquals(unsupported.toMap, ExpectedUnsupportedOracleOpcodes)
    assertEquals(roundTripped + unsupported.values.sum, GoldenCorpus.cases.size)
  }

  test("every positive oracle is encoded exactly or classified as an unsupported format") {
    var encoded           = 0
    var unsupportedFormat = 0
    val unsupported       = mutable.Map.empty[String, Int].withDefaultValue(0)
    var paired            = 0
    GoldenCorpus.cases.foreach { golden =>
      Assembly.parse(golden.assembly) match {
        case Left(error) if error.message.startsWith("unknown AMDGPU opcode") => recordUnsupported(error, unsupported)
        case Left(error)    => fail(s"${golden.source}: ${golden.target.name}: $error\n${golden.assembly}")
        case Right(program) => program.statements match {
            case Col(Statement.Instruction(instruction)) =>
              val definitions = encodingsFor(instruction, golden.target)
              val formats     = definitions.map(_.format.toUpperCase).distinct
              Binary.encode(instruction, golden.target) match {
                case Right(actual) =>
                  assert(formats.contains("SOPP"), s"encoded an unimplemented format at ${golden.source}: $formats")
                  assertEquals(
                    actual.toSeq,
                    golden.bytes.toSeq,
                    s"${golden.source}: ${golden.target.name}: ${golden.assembly}"
                  )
                  encoded += 1
                case Left(CodecError.UnsupportedFormat(_, _, declared)) =>
                  assert(
                    !formats.contains("SOPP"),
                    s"SOPP case escaped encoding at ${golden.source}: ${golden.assembly}"
                  )
                  assertEquals(declared.toSeq, formats.sorted.toSeq)
                  unsupportedFormat += 1
                case Left(CodecError.UnsupportedInstruction(_, _)) =>
                  assert(definitions.isEmpty, s"known instruction was classified unknown at ${golden.source}")
                  unsupportedFormat += 1
                case Left(CodecError.UnsupportedOperandSyntax(_, _, _)) =>
                  assert(formats.contains("SOPP"), s"non-SOPP syntax classification at ${golden.source}: $formats")
                  unsupportedFormat += 1
                case Left(error) => fail(s"${golden.source}: unclassified codec result $error\n${golden.assembly}")
              }
            case Col(Statement.InstructionPair(_, _)) => paired += 1
            case other => fail(s"${golden.source}: expected one instruction or pair, found $other")
          }
      }
    }
    assert(encoded >= 50, s"expected at least fifty exact SOPP checks, found $encoded")
    assertEquals(unsupported.toMap, ExpectedUnsupportedOracleOpcodes)
    assertEquals(encoded + unsupportedFormat + unsupported.values.sum + paired, GoldenCorpus.cases.size)
  }

  test("LLVM diagnostics never pass conservative target validation") {
    assert(GoldenCorpus.diagnostics.size >= 1000)
    GoldenCorpus.diagnostics.foreach { diagnostic =>
      Assembly.parse(diagnostic.assembly) match {
        case Left(_)        => ()
        case Right(program) =>
          Validator.validate(program, diagnostic.target) match {
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

  private def recordUnsupported(error: ParseError, counts: mutable.Map[String, Int]): Unit = {
    val RawUnknown = "unknown AMDGPU opcode '([^']+)'".r
    val opcode     = error.message match {
      case RawUnknown(raw) => stripEncodingSuffixes(raw)
      case other           => fail(s"unexpected unknown-opcode diagnostic: $other")
    }
    assert(ExpectedUnsupportedOracleOpcodes.contains(opcode), s"unreviewed LLVM-only opcode: $opcode")
    counts(opcode) += 1
  }

  private def stripEncodingSuffixes(opcode: String): String =
    EncodingSuffixes.collectFirst { case suffix if opcode.endsWith(suffix) => opcode.dropRight(suffix.length) } match {
      case Some(value) => stripEncodingSuffixes(value)
      case None        => opcode
    }

  // Exact, bounded LLVM-oracle gaps absent from the pinned ACO/AMD production metadata.
  private val ExpectedUnsupportedOracleOpcodes = Map(
    "tensor_save"                  -> 3,
    "tensor_stop"                  -> 2,
    "v_illegal"                    -> 2,
    "v_mfma_ld_scale_b32"          -> 24,
    "v_pk_add_f64"                 -> 12,
    "v_pk_add_nc_u64"              -> 12,
    "v_pk_fma_f64"                 -> 15,
    "v_pk_lshl_add_u64"            -> 13,
    "v_pk_max_num_f64"             -> 12,
    "v_pk_min_num_f64"             -> 12,
    "v_pk_mul_f64"                 -> 12,
    "v_pk_sub_nc_u64"              -> 12,
    "v_wmma_f64_16x16x4_f64"       -> 9,
    "v_wmma_ld_scale16_paired_b64" -> 15,
    "v_wmma_ld_scale_paired_b32"   -> 15
  )

  private val EncodingSuffixes = Col("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
}
