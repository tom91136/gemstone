package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

class StringSuite extends munit.FunSuite {
  test("primitive integer literals retain signed bounds and Java radix prefixes") {
    Vec(
      "0"             -> 0,
      "-0"            -> 0,
      "+17"           -> 17,
      "017"           -> 15,
      "0x7fffffff"    -> Int.MaxValue,
      "-0X80000000"   -> Int.MinValue,
      "-#80000000"    -> Int.MinValue,
      "-020000000000" -> Int.MinValue,
      "2147483647"    -> Int.MaxValue,
      "-2147483648"   -> Int.MinValue
    ).foreach { (text, expected) =>
      assertEquals(parseIntLiteral(text), Some(expected))
    }
    Vec(
      "",
      "+",
      "-",
      "0x",
      "#",
      "0x-1",
      "-0x+1",
      "08",
      " 1",
      "1 ",
      "--1",
      "++1",
      "2147483648",
      "-2147483649",
      "0x80000000",
      "037777777777"
    ).foreach { text =>
      assertEquals(parseIntLiteral(text), None, text)
    }
  }

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

  test("shared UTF-8 conversion is strict, keeps NUL, and reports byte offsets") {
    val text  = "nul\u0000 and astral 😀"
    val bytes = UTF8.encode(text).fold(error => fail(error.message), identity)
    assertEquals(UTF8.decode(bytes), Right(text))

    val invalid = Array[Byte](65, 0xe2.toByte, 0x82.toByte)
    UTF8.decode(invalid, 0, invalid.length) match {
      case Left(error)  => assertEquals(error.offset.raw, 1)
      case Right(value) => fail(s"accepted malformed UTF-8: $value")
    }

    assertEquals(UTF8.decode(Array[Byte](0, 0xc3.toByte, 0x28.toByte), 1, 2).left.map(_.offset.raw), Left(1))
    assertEquals(UTF8.encode("é\ud800").left.map(_.offset.raw), Left(2))

    UTF8.encode("bad\ud800") match {
      case Left(error)  => assertEquals(error.offset.raw, 3)
      case Right(value) => fail(s"accepted malformed UTF-16: $value")
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
  test("case conversion preserves nonemptiness when Unicode changes the length") {
    val upperInput: NonEmpty[String] = "Straße"
    val lowerInput: NonEmpty[String] = "İ"
    val upper: NonEmpty[String]      = upperInput.toUpperCaseRoot
    val lower: NonEmpty[String]      = lowerInput.toLowerCaseRoot
    assertEquals(upper, "STRASSE")
    assertEquals(lower, "i\u0307")
  }

}
