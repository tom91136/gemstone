package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class DisassemblerSuite extends munit.FunSuite {
  private lazy val database = decoder(89)
  private lazy val text     = Fixture.listing("copyAddSm89")
  private lazy val listing  = Listing.parse(text, "copy_add", 89).value
  private lazy val code     = Listing.words(text, "copy_add", 89).value.flatMap(_._2).toArray
  private def candidate(address: Int, name: String): FieldDecoder.Candidate =
    FieldDecoder.candidates(decoder(89), code.slice(address, address + 16)).fold(fail(_), identity)
      .find(_.name == name).getOrElse(fail(s"missing $name"))

  test("generated binary instruction decoding matches pinned NVIDIA output") {
    val actual = Listing.words(text, "copy_add", 89).value.map { (address, bytes) =>
      InstructionDecoder.decode(address, bytes, target(89)).value
    }
    assertEquals(
      actual.map(instruction => (instruction.address, instruction.operation, instruction.encodingWord)),
      listing.map(instruction => (instruction.address, instruction.operation, instruction.encodingWord))
    )
  }

  test("unknown forms and out-of-range register groups are rejected") {
    val mov = candidate(0, "mov__RC")
    assert(FieldDecoder.withBindings(
      database,
      mov,
      Map("unknown" -> BigInt(0))
    ).isLeft)
    val load = candidate(0x50, "ldg_memdesc__Ra64")
    assert(FieldDecoder.withBindings(
      database,
      load,
      Map("Ra" -> BigInt(253))
    ).isLeft)
  }

  test("binary memory decoding supports general operands beyond the former adapter subset") {
    val form = decoder(89).forms.find(_.name == "ldg_memdesc__Ra64").get
    Vec("Ra_URb" -> 6, "Ra_offset" -> 4, "input_reg_sz_64_dist" -> 0, "sz" -> 5, "Pu" -> 0).foreach {
      case (binding, value) =>
        val field = form.fields.find(_.expression match {
          case Expression.Reference(name, None, _) => name == binding
          case _                                   => false
        }).getOrElse(fail(s"missing binary field $binding"))
        val original = BigInt(1, code.slice(0x50, 0x60).reverse)
        val mutated  = field.positions.zipWithIndex.foldLeft(original) { case (word, (physical, logical)) =>
          if (BigInt(value).testBit(logical)) word.setBit(physical) else word.clearBit(physical)
        }
        val bytes = Array.tabulate[Byte](16)(index => ((mutated >> (index * 8)) & 255).toByte)
        assertEquals(
          InstructionDecoder.decode(0x50, bytes, target(89)).isRight,
          binding != "input_reg_sz_64_dist",
          binding
        )
    }
  }

  test("omitting a default predicate never shifts a later predicate destination") {
    val typed  = InstructionDecoder.decode(0x70, code.slice(0x70, 0x80), target(89)).fold(fail(_), identity)
    val oracle = listing.find(_.address == 0x70).get
    assertEquals(typed.operands, oracle.operands)
    assertEquals(typed.guard, oracle.guard)
    assertEquals(Printer.print(typed.operation), Printer.print(oracle.operation))
  }

  test("memory spelling refinement must agree with encoded relations") {
    val load    = candidate(0x50, "ldg_memdesc__Ra64")
    val refined =
      FieldDecoder.withBindings(
        database,
        load,
        Map("sco" -> BigInt(0), "private" -> BigInt(0))
      ).fold(fail(_), identity)
    assertEquals(refined.form, load.form)
    assertEquals(refined.bindings("sco"), BigInt(0))
    assert(!refined.issues.exists(_.message.contains("TABLES_mem_1")))
    assert(FieldDecoder.withBindings(
      database,
      load,
      Map("sco" -> BigInt(999))
    ).isLeft)
    assert(FieldDecoder.withBindings(
      database,
      load,
      Map("unknown" -> BigInt(0))
    ).isLeft)
  }
  test("literal predicate bank is an operand for R2P and omitted from P2R operands") {
    val database                                                                           = decoder(89)
    def encode(name: String, opcodeName: String, values: Map[String, BigInt]): Array[Byte] = {
      val form = database.forms.find(_.name == name).getOrElse(fail(s"missing $name schema"))
      def field(binding: String): NVDisasm.Assignment = form.fields.find(_.expression match {
        case Expression.Reference(reference, None, _) => reference == binding
        case _                                        => false
      }).getOrElse(fail(s"missing $binding field in $name"))
      def insertField(word: BigInt, assignment: NVDisasm.Assignment, value: BigInt): BigInt =
        assignment.positions.zipWithIndex.foldLeft(word) { case (result, (position, logical)) =>
          if (value.testBit(logical)) result.setBit(position) else result.clearBit(position)
        }
      val opcode  = field("Opcode")
      val initial = insertField(BigInt(0), opcode, form.opcodes.find(_.name == opcodeName).get.value)
      val word    = values.foldLeft(initial) { case (result, (binding, value)) =>
        insertField(result, field(binding), value)
      }
      Array.tabulate[Byte](16)(index => ((word >> (index * 8)) & 255).toByte)
    }
    val r2p = InstructionDecoder.decode(
      0x200,
      encode("r2p__RRR", "R2P", Map("Ra" -> 7)),
      target(89)
    ).fold(fail(_), identity)
    assertEquals(r2p.mnemonic.raw, "R2P")
    assertEquals(r2p.operands.headOption, Some(Operand.Symbol("PR")))
    assert(!r2p.modifiers.exists(_.name == "PR"))

    val p2r = InstructionDecoder.decode(
      0x200,
      encode("p2r__RsIR_RIR", "P2R", Map("Rd" -> 55, "Ra" -> 255, "Sb" -> 120)),
      target(89)
    ).fold(fail(_), identity)
    assertEquals(p2r.mnemonic.raw, "P2R")
    assertEquals(
      p2r.operands,
      Vec(Operand.Register(RegisterBank.R, 55), Operand.Register(RegisterBank.R, 255), Operand.Bits(120, 0))
    )
  }

  private def checkFixture(name: String, kernel: String): Unit = {
    val text     = Fixture.listing(name)
    val expected = Listing.parse(text, kernel, 89).value
    val actual   = Listing.words(text, kernel, 89).value.map { (address, bytes) =>
      InstructionDecoder.decode(address, bytes, target(89))
    }
    assertEquals(actual.size, expected.size, s"$kernel oracle instruction count")
    val unsupported = actual.zipWithIndex.collect { case (Left(error), index) => s"0x${index.toHexString}: $error" }
    val rendered    = actual.zip(expected).zipWithIndex.collect { case ((Right(instruction), oracle), index) =>
      index -> (instruction, oracle)
    }
    assert(rendered.nonEmpty, s"no $kernel instructions rendered; ${unsupported.take(3).mkString("; ")}")
    rendered.foreach { case (index, (instruction, oracle)) =>
      val clue = s"$kernel instruction 0x${(index * 16).toHexString}"
      assert(equivalentOpcode(oracle.operation, instruction.operation), clue)
      // Register-reuse scheduling hints do not change the operand value.
      assertEquals(
        instruction.operands.map(OperandQueries.without(_, OperandSelector.reuse)),
        oracle.operands.map(OperandQueries.without(_, OperandSelector.reuse)),
        clue
      )
      assertEquals(instruction.guard, oracle.guard, clue)
    }
    assert(unsupported.isEmpty)
  }

  test("generated FORMAT rendering matches the scalar SGEMM text oracle words") {
    checkFixture("scalarSgemmSm89", "scalar_sgemm")
  }

  test("generated FORMAT rendering matches the SAXPY text oracle words") {
    checkFixture("saxpyO0Sm89", "saxpy")
  }

  private def equivalentOpcode(expected: Instruction, actual: Instruction): Boolean = {
    val expectedModifiers                           = expected.modifiers
    val actualModifiers                             = actual.modifiers
    def isZero(value: Operand): Boolean             = value == Operand.Register(RegisterBank.R, 255)
    def positivePowerOfTwo(value: Operand): Boolean = {
      val integer = value match {
        case Operand.Integer(number) => Some(number)
        case Operand.Bits(number, _) => Some(number)
        case _                       => None
      }
      integer.exists(number => number > 0 && (number & (number - 1)) == 0)
    }
    def isOne(value: Operand): Boolean = value match {
      case Operand.Integer(number) => number == 1
      case Operand.Bits(number, _) => number == 1
      case _                       => false
    }
    val imadMov = expected.mnemonic.raw == "IMAD" && expectedModifiers.headOption.contains(Instruction.Modifier.MOV) &&
      actual.mnemonic.raw == "IMAD" && expectedModifiers.filterNot(_ == Instruction.Modifier.MOV) == actualModifiers &&
      actual.operands.lift(1).exists(isZero(_)) && actual.operands.lift(2).exists(isZero(_))
    val imadShl = expected.mnemonic.raw == "IMAD" &&
      expectedModifiers.take(2) == Vec(Instruction.Modifier.SHL, Instruction.Modifier.U32) &&
      actual.mnemonic.raw == "IMAD" && actualModifiers == Vec(Instruction.Modifier.U32) &&
      actual.operands.lift(3).exists(isZero(_)) && actual.operands.lift(2).exists(positivePowerOfTwo(_))
    val imadAdd = expected.mnemonic.raw == "IMAD" && expectedModifiers.headOption.contains(Instruction.Modifier.IADD) &&
      actual.mnemonic.raw == "IMAD" && expectedModifiers.filterNot(_ == Instruction.Modifier.IADD) == actualModifiers &&
      actual.operands.lift(2).exists(isOne(_))
    (expected.mnemonic == actual.mnemonic && expected.modifiers == actual.modifiers) || imadMov || imadShl || imadAdd
  }
}
