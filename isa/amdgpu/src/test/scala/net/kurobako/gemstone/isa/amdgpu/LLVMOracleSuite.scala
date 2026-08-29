package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

import scala.collection.mutable

class LLVMOracleSuite extends munit.FunSuite {
  test("round-trip every LLVM MC oracle instruction covered by the pinned production metadata") {
    assertEquals(LLVMOracle.sourceFileCount, 770)
    assertEquals(LLVMOracle.sourceCaseCount, 308161)
    assertEquals(LLVMOracle.cases.size, LLVMOracle.selectedCaseCount)
    assert(LLVMOracle.cases.size >= 3000)
    assertEquals((LLVMOracle.targets ++ LLVMOracle.diagnosticTargets).distinct.size, 46)
    assertEquals(
      LLVMOracle.targets.iterator.map(_.family).toSet,
      Family.values.toSet - Family.CDNA5
    )
    var roundTripped = 0
    val unsupported  = mutable.Map.empty[String, Int].withDefaultValue(0)
    LLVMOracle.cases.foreach { golden =>
      Parser.parse(golden.assembly) match {
        case Left(error) if error.message.startsWith("unknown AMDGPU opcode") => recordUnsupported(error, unsupported)
        case Left(error)   => fail(s"${golden.source}: ${golden.target.name}: $error\n${golden.assembly}")
        case Right(parsed) =>
          val canonical = Printer.render(parsed)
          val reparsed  = Parser.parse(canonical).fold(
            error => fail(s"${golden.source}: failed to parse canonical output: $error\n$canonical"),
            identity
          )
          assertEquals(
            Printer.render(reparsed),
            canonical,
            s"${golden.source}: ${golden.target.name}: ${golden.assembly}"
          )
          roundTripped += 1
      }
    }
    assert(roundTripped >= 3000, s"expected broad LLVM oracle coverage, found $roundTripped")
    assertEquals(unsupported.keySet.toSet, ExpectedUnsupportedOracleOpcodes)
    assertEquals(roundTripped + unsupported.values.sum, LLVMOracle.cases.size)
  }

  test("every positive oracle is encoded exactly or classified as an unsupported format") {
    var encoded           = 0
    var unsupportedFormat = 0
    val unsupported       = mutable.Map.empty[String, Int].withDefaultValue(0)
    var paired            = 0
    LLVMOracle.cases.foreach { golden =>
      Parser.parse(golden.assembly) match {
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
    assertEquals(unsupported.keySet.toSet, ExpectedUnsupportedOracleOpcodes)
    assertEquals(encoded + unsupportedFormat + unsupported.values.sum + paired, LLVMOracle.cases.size)
  }

  test("LLVM diagnostics never pass conservative target validation") {
    assertEquals(LLVMOracle.sourceDiagnosticCount, 98730)
    assertEquals(LLVMOracle.diagnostics.size, LLVMOracle.selectedDiagnosticCount)
    assert(LLVMOracle.diagnostics.size >= 300)
    LLVMOracle.diagnostics.foreach { diagnostic =>
      Parser.parse(diagnostic.assembly) match {
        case Left(_)        => ()
        case Right(program) =>
          Validator.validate(program, diagnostic.target) match {
            case ValidationResult.Valid =>
              fail(
                s"${diagnostic.source}: accepted LLVM diagnostic for ${diagnostic.target.name}: " +
                  s"${diagnostic.assembly}\nexpected: ${diagnostic.message}"
              )
            case ValidationResult.Invalid(_)     => ()
            case ValidationResult.Unsupported(_) => ()
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
    counts(opcode) += 1
  }

  private def stripEncodingSuffixes(opcode: String): String =
    EncodingSuffixes.collectFirst { case suffix if opcode.endsWith(suffix) => opcode.dropRight(suffix.length) } match {
      case Some(value) => stripEncodingSuffixes(value)
      case None        => opcode
    }

  // Exact, bounded LLVM-oracle gaps absent from the pinned ACO/AMD production metadata.
  private val SpecificationGaps = Set(
    "tensor_save",
    "tensor_stop",
    "v_illegal",
    "v_mfma_ld_scale_b32",
    "v_pk_add_f64",
    "v_pk_add_nc_u64",
    "v_pk_fma_f64",
    "v_pk_lshl_add_u64",
    "v_pk_max_num_f64",
    "v_pk_min_num_f64",
    "v_pk_mul_f64",
    "v_pk_sub_nc_u64",
    "v_wmma_f64_16x16x4_f64",
    "v_wmma_ld_scale16_paired_b64",
    "v_wmma_ld_scale_paired_b32"
  )

  // LLVM retains these historical spellings, while the generated model accepts only canonical vendor mnemonics.
  private val LegacyMnemonicAliases = Set(
    "buffer_wbinvl1_sc",
    "v_accvgpr_read_b32",
    "v_accvgpr_write_b32",
    "v_addc_u32",
    "v_subb_u32",
    "v_subbrev_u32",
    "v_subrev_i32"
  )

  private val LegacyComparisonPredicates =
    Col("eq", "f", "ge", "gt", "le", "lg", "lt", "neq", "nge", "ngt", "nle", "nlg", "nlt", "o", "tru", "u")

  private val LegacyComparisonAliases = Set.from(for {
    mnemonic  <- Col("v_cmps", "v_cmpsx")
    predicate <- LegacyComparisonPredicates
    width     <- Col("f32", "f64")
  } yield s"${mnemonic}_${predicate}_$width")

  private val ExpectedUnsupportedOracleOpcodes =
    SpecificationGaps ++ LegacyMnemonicAliases ++ LegacyComparisonAliases

  private val EncodingSuffixes = Col("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
}
