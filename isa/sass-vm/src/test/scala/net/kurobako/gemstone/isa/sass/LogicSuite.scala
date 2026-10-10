package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class LogicSuite extends munit.FunSuite {
  private def operand(text: String): Operand = asm.OperandParser.parse(text).value

  private val patterns = Vec(0, 1, 2, 3, 31, 32, 0x80000000, -1, 0x55555555, 0xaaaaaaaa)

  private def logic(sm: Int, function: String, listing: String, zero: Boolean = false): Unit = {
    val kernel                                                           = load(sm, Fixture.listing(listing), function)
    val instructions                                                     = kernel.function.instructions
    def has(mnemonic: String, modifiers: Instruction.Modifier*): Boolean =
      instructions.exists(i => i.mnemonic.raw == mnemonic && i.modifiers == Vec.from(modifiers))
    if (function == "logic_probe")
      assert(instructions.exists(i =>
        i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.LUT) && i.operands.size == 5
      ))
    else {
      assert(has("LOP", Instruction.Modifier.AND, Instruction.Modifier.NZ) &&
        has("LOP", Instruction.Modifier.OR, Instruction.Modifier.NZ))
      if (zero) assert(instructions.exists(i =>
        i.mnemonic.raw == "LOP" && i.modifiers == Vec(Instruction.Modifier.AND, Instruction.Modifier.Z) &&
          i.operands(1) != operand("RZ")
      ))
    }
    for {
      a <- patterns
      b <- patterns
    } {
      val c         = a ^ 0x12345678
      val arguments = Vec(Arg.zeros(24), Arg.u32(a), Arg.u32(b)) ++
        (if (function == "logic_probe") Vec(Arg.u32(c)) else Vec.empty[Arg])
      val expected =
        if (function == "logic_probe")
          Vec(
            a & b,
            if ((a & b) != 0) 17 else 29,
            if ((a & b) == 0) 37 else 41,
            a | b,
            if ((a | b) != 0) 43 else 47,
            (a & b) | (~a & c)
          )
        else Vec(
          if ((a & 3) != 0) a else 0,
          if ((a & 31) == 0) b else 0,
          if (zero) a & 31 else 0,
          if ((a | b) != 0) b else 0,
          0,
          0
        )
      assertEquals(
        kernel.launch(1)(arguments*).words(0).map(_.toInt),
        expected,
        s"sm_$sm $function zero=$zero a=$a b=$b"
      )
    }
  }

  for (sm <- Vec(50, 60)) {
    test(s"sm_$sm compiler LOP3 uses five operands and preserves its permuted truth table") {
      logic(sm, "logic_probe", "logicSm50")
    }
    test(s"sm_$sm compiler LOP predicates preserve independent AND and OR guards") {
      logic(sm, "predicate_probe", "logicPredicateSm50")
    }
    test(s"sm_$sm decoded LOP.Z variant preserves zero predicate and data outputs") {
      logic(sm, "predicate_probe", "logicPredicateZeroSm50Patched", zero = true)
    }
  }

  private def lop3Predicate(sm: Int): Kernel =
    load(sm, Fixture.listing(s"lop3PredicateSm$sm${if (sm == 70) "Patched" else ""}"), "probe")

  private def predicateLop(kernel: Kernel): DecodedInstruction = kernel.function.instructions.find(i =>
    i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.LUT) && i.operands.head == operand("P0")
  ).get

  for (sm <- Vec(70, 75, 80, 86, 89)) test(s"sm_$sm LOP3 predicate OR/AND preserves data and aliased input") {
    val rows = for {
      a <- Vec(0L, 1L, 0x80000000L, 0xffffffffL)
      b <- Vec(0L, 1L, 0x80000000L, 0xffffffffL)
      q <- Vec(0L, 0xffffffffL)
    } yield Vec(a, b, 0x12345678L, q)
    val kernel = lop3Predicate(sm)
    assert(kernel.function.instructions.exists(i =>
      i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.LUT, Instruction.Modifier.PAND)
    ))
    assertEquals(predicateLop(kernel).operands.last, operand("!P0"))
    val expected = rows.flatMap { row =>
      val (result, q) = (row(0) & row(1), row(3) != 0)
      val p           = result != 0
      Vec(result, if (p || q) 1L else 0L, if (p && q) 1L else 0L, if (p || !q) 1L else 0L)
    }
    assertEquals(
      kernel.launch(rows.size)(Arg.zeros(rows.size * 16), Arg.Buffer(words(rows.flatten))).words(0),
      expected
    )
  }

  test("LOP3 dynamic predicate input remains bounded to verified architectures and operand forms") {
    val kernel                                                    = lop3Predicate(70)
    val lop                                                       = predicateLop(kernel)
    def rejected(sm: Int, opcode: String, input: String): Boolean = {
      val parts   = Vec.from(opcode.split('.'))
      val changed = lop.rewrite(
        mnemonic = parts.head.refine[NonEmpty[String]].value,
        modifiers = parts.tail.map(Instruction.Modifier(_)),
        operands = lop.operands.updated(lop.operands.size - 1, operand(input)),
        guard = lop.guard
      )
      kernel.copy(sm = sm, functions = Vec(kernel.function.copy(instructions = Vec(changed)))).lower(8, 8).isLeft
    }
    assert(rejected(35, "LOP3.LUT", "P0"))
    assert(rejected(70, "ULOP3.LUT", "UP0"))
    val invalid = Instruction.decode(
      lop.mnemonic.raw,
      lop.modifiers,
      lop.operands.updated(lop.operands.size - 1, operand("R0")),
      lop.guard
    )
    assert(invalid.left.exists(_.startsWith("invalid operands for LOP3:")))
  }

  for (sm <- Vec(50, 60))
    test(s"legacy LOP3.NZ produces the result predicate on sm_$sm") {
      check(
        sm,
        "S2R R0, SR_TID.X; MOV R5, 0; LOP3.NZ P0, RZ, R0, R5, RZ, 0xf8; SEL R6, RZ, 1, !P0; LDC.64 R2, c[0x0][0x160]; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2], R6; EXIT",
        Vec(0, 1, 1, 1)
      )
    }

  test("UPLOP3 writes a uniform predicate from its LUT") {
    check(
      120,
      "UPLOP3.LUT UP0, UPT, UPT, UPT, UPT, 0x80, 0x8; @UP0 MOV R6, 7; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R6; EXIT",
      Vec(7)
    )
  }

  for {
    (start, count, expected) <- Vec((0, 0, 0), (0, 32, -1), (4, 5, 0x1f0), (31, 2, Int.MinValue), (32, 1, 0))
    immediate                <- Vec(false, true)
  }
    test(s"UBMSK start=$start count=$count immediate=$immediate preserves an aliased width source") {
      check(
        120,
        s"S2R R0, SR_TID.X; UMOV UR4, $start; UMOV UR5, $count; UBMSK UR5, UR4, ${
            if (immediate) count.toString else "UR5"
          }; MOV R4, UR5; " +
          "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.fill(32)(expected)
      )
    }

  test("compiler-produced sm_35 BFE.BREV, BFE and BFI match bit-field edge semantics") {
    def extract(value: Int, position: Int, width: Int, signed: Boolean): Int =
      if (width == 0) 0
      else if (position >= 32) { if (signed && value < 0) -1 else 0 }
      else {
        val actual = math.min(width, 32 - position)
        val mask   = if (actual == 32) -1 else (1 << actual) - 1
        val bits   = (value >>> position) & mask
        if (signed && actual < 32 && (bits & (1 << (actual - 1))) != 0) bits | ~mask else bits
      }
    def insert(base: Int, value: Int, position: Int, width: Int): Int = {
      val actual = if (position >= 32) 0 else math.min(width, 32 - position)
      if (actual == 0) base
      else {
        val mask = if (actual == 32) -1 else ((1 << actual) - 1) << position
        (base & ~mask) | ((value << position) & mask)
      }
    }
    val kernel = load(35, Fixture.listing("bitfieldSm35"), "bitfield_probe")
    Vec(
      (0xe1234567, 0x89abcdef, 0x35, 0, 32),
      (0xe1234567, 0x89abcdef, 0x35, 1, 1),
      (0xe1234567, 0x89abcdef, 0x35, 29, 8),
      (0xe1234567, 0x89abcdef, 0x35, 31, 8),
      (0xe1234567, 0x89abcdef, 0x35, 8, 0),
      (0xe1234567, 0x89abcdef, 0x35, 32, 1),
      (0xe1234567, 0x89abcdef, 0x35, 255, 8),
      (0x61234567, 0x89abcdef, 0x35, 32, 8)
    ).foreach { (x, base, inserted, position, width) =>
      val run =
        kernel.launch(1)(Arg.zeros(16), Arg.u32(x), Arg.u32(base), Arg.u32(inserted), Arg.u32(position), Arg.u32(width))
      assertEquals(
        run.words(0).map(_.toInt),
        Vec(
          extract(x, position, width, signed = false),
          extract(x, position, width, signed = true),
          insert(base, inserted, position, width),
          Integer.reverse(x)
        )
      )
    }
  }

  private val sgxt    = load(70, Fixture.listing("sgxtSm70"), "sgxt_probe")
  private val sgxtRaw = load(70, Fixture.listing("sgxtRawWidthSm70Patched"), "sgxt_probe")

  private def extract(value: Int, rawWidth: Int, signed: Boolean, raw: Boolean): Int = {
    val width = if (raw) { if (Integer.compareUnsigned(rawWidth, 32) >= 0) 32 else rawWidth }
    else rawWidth & 0xff
    if (width == 0) 0
    else if (width >= 32) value
    else {
      val mask = (1 << width) - 1
      val bits = value & mask
      if (signed && (bits & (1 << (width - 1))) != 0) bits | ~mask else bits
    }
  }

  private def checkSgxt(kernel: Kernel, raw: Boolean, immediate: Option[Int] = None): Unit = {
    val widened = immediate.fold(kernel) { width =>
      kernel.map { i =>
        val immediateForm = (i.operation match { case _: Instruction.Sgxt => true; case _ => false }) &&
          ((i.modifiers == Vec(Instruction.Modifier.U32) && i.operands(2) == operand("0x2")) ||
            (i.modifiers.isEmpty && i.operands(2) == operand("0x3")))
        if (immediateForm)
          i.rewrite(operands = i.operands.updated(2, Operand.Bits(BigInt(Integer.toUnsignedLong(width)), 0)))
        else i
      }
    }
    Vec(
      (0x81234567, 0),
      (0xffffffff, 1),
      (0x7fffffff, 2),
      (0x80000000, 7),
      (0x87654321, 8),
      (0xffffffff, 15),
      (0x80000001, 16),
      (0xdeadbeef, 31),
      (0x80000000, 32),
      (0x87654321, 33),
      (0xffffffff, 63),
      (0x12345678, 64),
      (0xfedcba98, 255),
      (0x87654321, 256),
      (0x80000001, 257),
      (0x12345678, 0x80000000),
      (0xffffffff, -1)
    ).foreach { (value, width) =>
      assertEquals(
        widened.launch(1)(Arg.zeros(20), Arg.u32(value), Arg.u32(width)).words(0).map(_.toInt),
        Vec(
          extract(value, width, signed = false, raw),
          extract(value, width, signed = false, raw),
          extract(value, width, signed = true, raw),
          extract(value, immediate.getOrElse(2), signed = false, raw = true),
          extract(value, immediate.getOrElse(3), signed = true, raw = true)
        )
      )
    }
  }

  test("compiler-produced sm_70 SGXT forms preserve PTX BFE descriptor behaviour") {
    checkSgxt(sgxt, raw = false)
  }

  test("raw sm_70 SGXT widths clamp at 32 bits, including large register values") {
    checkSgxt(sgxtRaw, raw = true)
  }

  test("SGXT immediate widths use the measured 9-bit encoding and clamp at 32") {
    checkSgxt(sgxt, raw = false, immediate = Some(256))
  }

  test("SGXT rejects forms outside its measured operands and architectures") {
    val original =
      sgxt.function.instructions.find(i => i.mnemonic.raw == "SGXT" && i.modifiers == Vec(Instruction.Modifier.U32)).get
    Vec(
      original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), false, false)))),
      original.rewrite(modifiers = Vec(Instruction.Modifier.U16)),
      original.rewrite(operands = original.operands.updated(1, operand("R4.H0"))),
      original.rewrite(operands = original.operands.updated(2, operand("-1"))),
      original.rewrite(operands = original.operands.updated(2, operand("0x200")))
    ).foreach(invalid => assert(sgxt.map(i => if (i == original) invalid else i).lower(8, 4, 4).isLeft))
    assert(sgxt.copy(sm = 89).lower(8, 4, 4).isLeft)
  }
}
