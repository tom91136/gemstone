package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.sass.asm.*

class TypedSASSPrinterNumberSuite extends munit.FunSuite {
  private def print(format: String, width: Int, bits: BigInt): String = {
    val payload = s"bits<$format>:$width(0x${bits.toString(16)})"
    Parser.parse(s"MOV R0, $payload;").flatMap(Printer.print).fold(fail(_), identity)
  }

  test("typed printer retains floating format and payload") {
    assertEquals(print("F16Imm", 16, BigInt(0x3c00)), "MOV R0, bits<F16Imm>:16(0x3c00);")
  }

  test("typed printer keeps positive and negative binary16 zero distinct") {
    assertEquals(print("F16Imm", 16, BigInt(0)), "MOV R0, bits<F16Imm>:16(0x0);")
    assertEquals(print("F16Imm", 16, BigInt(0x8000)), "MOV R0, bits<F16Imm>:16(0x8000);")
  }

  test("typed printer preserves exact NaN payload bit patterns") {
    assertEquals(print("F32Imm", 32, BigInt("ffc01234", 16)), "MOV R0, bits<F32Imm>:32(0xffc01234);")
    assertEquals(
      print("F64Imm", 64, BigInt("fff8123400000000", 16)),
      "MOV R0, bits<F64Imm>:64(0xfff8123400000000);"
    )
  }

  test("typed printer retains signed E8M7 NaN payloads") {
    assertEquals(print("E8M7Imm", 32, BigInt(0x7fc0)), "MOV R0, bits<E8M7Imm>:32(0x7fc0);")
    assertEquals(print("E8M7Imm", 32, BigInt(0xffc0)), "MOV R0, bits<E8M7Imm>:32(0xffc0);")
    assertEquals(print("E8M7Imm", 32, BigInt(0x7f81)), "MOV R0, bits<E8M7Imm>:32(0x7f81);")
    assertEquals(print("E8M7Imm", 32, BigInt(0xff81)), "MOV R0, bits<E8M7Imm>:32(0xff81);")
  }
}
