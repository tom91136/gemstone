package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

class StringSuite extends munit.FunSuite {
  test("integer literals share decimal and hex signs without accepting grammar fragments") {
    assertEquals(parseBigIntLiteral("-0Xff"), Some(BigInt(-255)))
    assertEquals(parseBigIntLiteral("+0xFf"), Some(BigInt(255)))
    assertEquals(parseBigIntLiteral("+0010"), Some(BigInt(10)))
    assertEquals(parseBigIntLiteral("-0010"), Some(BigInt(-10)))
    assertEquals(parseBigIntLiteral("-0"), Some(BigInt(0)))
    assertEquals(parseBigIntLiteral("18446744073709551615"), Some((BigInt(1) << 64) - 1))
    assertEquals(parseBigIntLiteral("0xffffffffffffffffffffffffffffffff"), Some((BigInt(1) << 128) - 1))
    Seq(
      "",
      "+",
      "-",
      "0x",
      "-0X",
      "+-1",
      "-+1",
      "--1",
      "++1",
      "0x-1",
      "0x+1",
      "0xG",
      " 1",
      "1 ",
      "1u",
      "1.0",
      "1_000",
      "١",
      "１",
      "1\n"
    ).foreach(value => assertEquals(parseBigIntLiteral(value), None))
  }

  test("UTF-8 decoding preserves Unicode and rejects malformed input") {
    val text   = "kernel_λ_😀"
    val bytes  = text.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    val padded = Array[Byte](0) ++ bytes ++ Array[Byte](0)
    assertEquals(decodeUtf8(padded, 1, bytes.length), Right(text))
    assertEquals(decodeUtf8(Array.emptyByteArray, 0, 0), Right(""))
    Seq(Array(0xc0.toByte, 0xaf.toByte), Array(0xff.toByte), Array(0xe2.toByte, 0x82.toByte)).foreach { invalid =>
      assert(decodeUtf8(invalid, 0, invalid.length).isLeft)
    }
  }

  test("case conversion handles Unicode and empty strings") {
    assertEquals("Straße".toUpperCaseRoot, "STRASSE")
    assertEquals("ÉCOLE".toLowerCaseRoot, "école")
    assertEquals("".toUpperCaseRoot, "")
    assertEquals("".toLowerCaseRoot, "")
  }

  test("refined strings support equality and string extensions directly") {
    val format: NonEmpty[String] = "SOPP"
    assert(format == "SOPP")
    assert("SOPP" == format)
    assert(format != "VOP2")
    assertEquals(format.toLowerCaseRoot, "sopp")
    assertEquals(format.toUpperCaseRoot, "SOPP")
  }
}
