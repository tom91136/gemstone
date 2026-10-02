package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class BinarySASSDisassemblerSuite extends munit.FunSuite {
  private lazy val database = NVDisasm.forTarget("SM89").get
  private lazy val text     = CopyAddFixtures.copyAdd
  private lazy val code     = SASSOracleWords.read(text).flatMap(_._2).toArray
  private def candidate(address: Int, name: String): FieldDecoder.Candidate =
    FieldDecoder.candidates("SM89", code.slice(address, address + 16)).fold(fail(_), identity)
      .find(_.name == name).getOrElse(fail(s"missing $name"))

  test("generated binary instruction decoding matches pinned NVIDIA output") {
    val actual = SASSOracleWords.read(text).map { (address, bytes) =>
      InstructionDecoder.decodeTyped(address, bytes, target = "SM89").fold(fail(_), identity)
    }
    val expected = ListingDecoder.parse(
      text,
      "copy_add",
      89
    ).fold(fail(_), identity).head.instructions
    val low64 = (BigInt(1) << 64) - 1
    assertEquals(
      actual.map(instruction =>
        (instruction.address, instruction.operation, instruction.encodingWord.map(_ & low64))
      ),
      expected.map(instruction => (instruction.address, instruction.operation, instruction.encodingWord))
    )
  }

  test("typed FORMAT decoding agrees with the text frontend for the compiler-produced kernel") {
    val expected = ListingDecoder.parse(
      text,
      "copy_add",
      89
    ).fold(fail(_), identity).head.instructions
    expected.foreach { textInstruction =>
      val bytes  = code.slice(textInstruction.address.raw, textInstruction.address.raw + 16)
      val actual = InstructionDecoder.decodeTyped(textInstruction.address.raw, bytes, "SM89").fold(fail(_), identity)
      assertEquals(actual.address, textInstruction.address)
      assertEquals(actual.mnemonic, textInstruction.mnemonic, textInstruction.toString)
      assertEquals(actual.modifiers, textInstruction.modifiers, textInstruction.toString)
      assertEquals(actual.operands, textInstruction.operands, textInstruction.toString)
      assertEquals(actual.guard, textInstruction.guard, textInstruction.toString)
    }
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

  test("negated true guard remains an explicit false predicate") {
    val instruction = ListingDecoder.parse("kernel:\n/*0000*/ @!PT NOP;", "kernel")
      .fold(fail(_), identity).head.instructions.head
    assertEquals(instruction.guard.map(_.predicate.negated), Some(true))
  }

  test("binary memory decoding supports general operands beyond the former adapter subset") {
    val form = NVDisasm.forTarget("SM89").get.forms.find(_.name == "ldg_memdesc__Ra64").get
    Vec("Ra_URb" -> 6, "Ra_offset" -> 4, "input_reg_sz_64_dist" -> 0, "sz" -> 5, "Pu" -> 0).foreach {
      case (binding, value) =>
        val field = form.fields.find(_.expression match {
          case NVDisasm.Expression.Reference(name, None, _) => name == binding
          case _                                            => false
        }).getOrElse(fail(s"missing binary field $binding"))
        val original = BigInt(1, code.slice(0x50, 0x60).reverse)
        val mutated  = field.positions.zipWithIndex.foldLeft(original) { case (word, (physical, logical)) =>
          if (BigInt(value).testBit(logical)) word.setBit(physical) else word.clearBit(physical)
        }
        val bytes = Array.tabulate[Byte](16)(index => ((mutated >> (index * 8)) & 255).toByte)
        assertEquals(
          InstructionDecoder.decodeTyped(0x50, bytes).isRight,
          binding != "input_reg_sz_64_dist",
          binding
        )
    }
  }

  test("omitting a default predicate never shifts a later predicate destination") {
    val text   = ListingDecoder.parse(CopyAddFixtures.copyAdd, "copy_add", 89).fold(fail(_), identity)
    val typed  = InstructionDecoder.decodeTyped(0x70, code.slice(0x70, 0x80), "SM89").fold(fail(_), identity)
    val oracle = text.head.instructions.find(_.address.raw == 0x70).get
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
    val database = NVDisasm.forTarget("SM89").get
    def encode(name: String, opcodeName: String, values: Map[String, BigInt]): Array[Byte] = {
      val form = database.forms.find(_.name == name).getOrElse(fail(s"missing $name schema"))
      def field(binding: String): NVDisasm.Assignment = form.fields.find(_.expression match {
        case NVDisasm.Expression.Reference(name, None, _) => name == binding
        case _                                            => false
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
    val r2p = InstructionDecoder.decodeTyped(
      0x200,
      encode("r2p__RRR", "R2P", Map("Ra" -> 7)),
      "SM89"
    ).fold(fail(_), identity)
    assertEquals(r2p.mnemonic.raw, "R2P")
    assertEquals(r2p.operands.headOption, Some(Operand.Symbol("PR")))
    assert(!r2p.modifiers.exists(_.name == "PR"))

    val p2r = InstructionDecoder.decodeTyped(
      0x200,
      encode("p2r__RsIR_RIR", "P2R", Map("Rd" -> 55, "Ra" -> 255, "Sb" -> 120)),
      "SM89"
    ).fold(fail(_), identity)
    assertEquals(p2r.mnemonic.raw, "P2R")
    assertEquals(
      p2r.operands,
      Vec(Operand.Register(RegisterBank.R, 55), Operand.Register(RegisterBank.R, 255), Operand.Bits(120, 0))
    )
  }

}
