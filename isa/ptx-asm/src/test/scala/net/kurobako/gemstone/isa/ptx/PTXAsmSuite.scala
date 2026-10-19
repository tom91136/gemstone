package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.string.*

class PTXAsmSuite extends munit.FunSuite {
  test("PTX text bytes round trip whole programs and stabilise canonically") {
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
    val bytes    = UTF8.encode(Printer.render(original)).fold(error => fail(error.toString), identity)
    val decoded  = ISA.decode(bytes).fold(error => fail(error.message), identity)

    assertEquals(decoded, original)
    assertEquals(UTF8.encode(Printer.render(decoded)), Right(bytes))
  }

  test("byte decoding reports malformed UTF-8 instead of replacing it") {
    val malformed = Vec(0xc3.toByte, 0x28.toByte)
    ISA.decode(malformed) match {
      case Left(ISA.DecodeError.InvalidUTF8(offset)) => assertEquals(offset.raw, 0)
      case other                                     => fail(s"expected invalid UTF-8 diagnostic, found $other")
    }
  }
}
