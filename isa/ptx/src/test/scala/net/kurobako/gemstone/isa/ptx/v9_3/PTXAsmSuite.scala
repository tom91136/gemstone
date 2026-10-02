package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.v9_3.asm.*

class PTXAsmSuite extends munit.FunSuite {
  test("PTX text bytes round trip whole programs and stabilize canonically") {
    val source = """// module comment
                  |.version 9.3
                  |.target sm_90
                  |.address_size 64
                  |.extern .func external(.param .b64 p);
                  |.global .align 16 .u32 values[2] = {1, 2};
                  |.visible .entry kernel(.param .u64 input) .maxntid 32, 1, 1 {
                  |  // body comment
                  |  .reg .u32 %r<2>;
                  |  ret;
                  |}
                  |""".stripMargin
    val original = Parser.parse(source).fold(error => fail(error.message), identity)
    val bytes    = Assembler.encode(original).fold(error => fail(error.toString), identity)
    val decoded  = ISA.decode(bytes).fold(error => fail(error.message), identity)

    assertEquals(decoded, original)
    assertEquals(Assembler.encode(decoded), Right(bytes))
    assertEquals(Disassembler.decode(bytes), Right(original))
  }

  test("byte decoding reports malformed UTF-8 instead of replacing it") {
    val malformed = Vec(0xc3.toByte, 0x28.toByte)
    ISA.decode(malformed) match {
      case Left(ISA.DecodeError.InvalidUTF8(offset)) => assertEquals(offset.raw, 0)
      case other                                     => fail(s"expected invalid UTF-8 diagnostic, found $other")
    }
  }
}
