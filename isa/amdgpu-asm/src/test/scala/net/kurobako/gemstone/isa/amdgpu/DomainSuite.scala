package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.domain.*

class DomainSuite extends munit.FunSuite {
  private def target(isa: ISA): Target = Target.all.find(_.isa == isa).get

  private def check[V <: DomainValue](domain: Domain[V], isa: ISA): Unit = {
    val table = domain.table(isa)
    table.slots.foreach {
      case Slot.Immediate(value) =>
        val code = table.encode(Slot.Immediate(value)).getOrElse(fail(s"${domain.id} $isa: $value"))
        assertEquals(table.decodeImmediate(code.toLong), Some(value), s"${domain.id} $isa")
      case slot =>
        val code = table.encode(slot).getOrElse(fail(s"${domain.id} $isa: $slot has no code"))
        assertEquals(table.decode(code.toLong), Some(slot), s"${domain.id} $isa")
    }
    domain.values(isa).foreach(value =>
      value.slot match {
        case Slot.Immediate(_) => ()
        case _                 => assertEquals(domain.decode(isa, domain.encode(isa, value).get.toLong), Some(value))
      }
    )
  }

  test("every value of every domain survives its codes on every ISA family") {
    ISA.values.foreach(isa => OperandDomains.all.foreach(domain => check(domain, isa)))
  }

  private def text(slot: Slot): Option[String] = slot match {
    case Slot.Register(file, index) if file == RegisterFile.Scalar        => Some(s"s$index")
    case Slot.Register(file, index) if file == RegisterFile.TrapTemporary => Some(s"ttmp$index")
    case Slot.Named("null" | "src_lds_direct")                            => None
    case Slot.Named(name)                                                 => Some(name)
    case Slot.Integer(value)                                              => Some(value.toString)
    case Slot.Floating(value) if value != 0.15915494                      => Some(value.toString)
    case _                                                                => None
  }

  test("every scalar source value of the first SOP1 move assembles and disassembles to itself on each ISA family") {
    ISA.values.foreach { isa =>
      val mode   = TargetMode(target(isa), 64)
      val values = Ssrc.table(isa).slots.flatMap(text(_))
      assert(values.size > 100, isa.toString)
      val bytes = values.flatMap { value =>
        Parser.parse(s"s_mov_b32 s0, $value").toOption.flatMap(_.statements.headOption).flatMap { statement =>
          Binary.encodeStatement(statement, mode).toOption.map(value -> _)
        }
      }
      assert(bytes.size >= values.size - 40, s"$isa assembled ${bytes.size} of ${values.size}")
      bytes.foreach { (value, encoded) =>
        val decoded = Binary.decodeStatementAt(encoded, 0, mode).fold(error => fail(s"$isa $value: $error"), identity)
        assertEquals(Binary.encodeStatement(decoded.statement, mode), Right(encoded), s"$isa $value")
      }
    }
  }

  private def assemble(isa: ISA, line: String): Either[String, Vec[Byte]] =
    Parser.parse(line).left.map(_.toString).flatMap(_.statements.headOption.toRight(line)).flatMap(
      Binary.encodeStatement(_, TargetMode(target(isa), 64)).left.map(_.toString)
    )

  private def hex(values: Int*): Vec[Byte] = Vec.from(values.map(_.toByte))

  test("tba and tma take codes 108 to 111 before GFX9 and print as llvm-mc does") {
    val vectors = Vector(
      (ISA.GFX6, "s_mov_b32 tba_lo, s1", hex(0x01, 0x03, 0xec, 0xbe)),
      (ISA.GFX7, "s_mov_b32 tma_hi, s1", hex(0x01, 0x03, 0xef, 0xbe)),
      (ISA.GFX8, "s_mov_b64 tba, s[2:3]", hex(0x02, 0x01, 0xec, 0xbe)),
      (ISA.GFX8, "s_mov_b64 tma, s[2:3]", hex(0x02, 0x01, 0xee, 0xbe)),
      (ISA.GFX8, "s_mov_b32 ttmp11, s1", hex(0x01, 0x00, 0xfb, 0xbe))
    )
    vectors.foreach { (isa, line, bytes) =>
      assertEquals(assemble(isa, line), Right(bytes), line)
      val decoded = Binary.decodeStatementAt(bytes, 0, TargetMode(target(isa), 64)).toOption.get
      assertEquals(Printer.render(Program(Vec(decoded.statement))).trim, line)
    }
    assert(assemble(ISA.GFX9, "s_mov_b32 tba_lo, s1").isLeft)
  }

  test("null and the apertures follow the domain of each ISA family") {
    Vector(ISA.GFX8, ISA.GFX9, ISA.CDNA1, ISA.CDNA4).foreach(isa =>
      assert(assemble(isa, "s_mov_b32 s1, null").isLeft, isa.toString)
    )
    assert(assemble(ISA.GFX8, "s_mov_b32 s1, src_shared_base").isLeft)
    assert(assemble(ISA.GFX9, "s_mov_b32 s1, src_shared_base").isRight)
    assert(assemble(ISA.GFX6, "s_mov_b32 xnack_mask_lo, s1").isLeft)
    assert(assemble(ISA.GFX9, "s_mov_b32 xnack_mask_lo, s1").isRight)
    assert(assemble(ISA.RDNA1, "s_mov_b32 s1, null").isRight)
  }

  test("scalar memory offsets are signed from GFX9 and follow llvm-mc") {
    val load = "s_load_dword s1, s[2:3], "
    assertEquals(assemble(ISA.GFX9, load + "-1"), Right(hex(0x41, 0x00, 0x02, 0xc0, 0xff, 0xff, 0x1f, 0x00)))
    assertEquals(assemble(ISA.RDNA1, load + "-1"), Right(hex(0x41, 0x00, 0x00, 0xf4, 0xff, 0xff, 0x1f, 0xfa)))
    assertEquals(
      assemble(ISA.RDNA4, "s_load_b32 s1, s[2:3], -0x800000"),
      Right(hex(0x41, 0x00, 0x00, 0xf4, 0x00, 0x00, 0x80, 0xf8))
    )
    assertEquals(assemble(ISA.GFX8, load + "0xfffff"), Right(hex(0x41, 0x00, 0x02, 0xc0, 0xff, 0xff, 0x0f, 0x00)))
    Vector(
      ISA.GFX8  -> "-1",
      ISA.GFX8  -> "0x100000",
      ISA.GFX9  -> "0x100000",
      ISA.RDNA3 -> "-0x800000",
      ISA.RDNA4 -> "0x800000"
    ).foreach((isa, offset) =>
      assert(
        assemble(isa, (if (isa == ISA.RDNA4) "s_load_b32 s1, s[2:3], " else load) + offset).isLeft,
        s"$isa $offset"
      )
    )
    val decoded =
      Binary.decodeStatementAt(hex(0x41, 0x00, 0x02, 0xc0, 0xff, 0xff, 0x1f, 0x00), 0, TargetMode(target(ISA.GFX9), 64))
    assertEquals(Printer.render(Program(Vec(decoded.toOption.get.statement))).trim, load + "-1")
  }

  test("register tuples follow the alignment rules of each ISA family") {
    assert(assemble(ISA.GFX9, "s_mov_b64 s[1:2], 1").isLeft)
    assert(assemble(ISA.GFX9, "s_mov_b64 s[2:3], 1").isRight)
    assert(assemble(ISA.RDNA3, "s_load_b128 s[2:5], s[2:3], 0x0").isLeft)
    assertEquals(
      assemble(ISA.RDNA3, "s_load_b128 s[4:7], s[2:3], 0x0").isRight,
      true,
      assemble(ISA.RDNA3, "s_load_b128 s[4:7], s[2:3], 0x0").toString
    )
    assert(assemble(ISA.CDNA2, "v_add_f64 v[1:2], v[0:1], v[0:1]").isLeft)
    assert(assemble(ISA.RDNA3, "v_add_f64 v[1:2], v[0:1], v[0:1]").isRight)
  }
}
