package net.kurobako.gemstone

class MessagePackSuite extends munit.FunSuite {
  // MessagePack marker bytes are independent fixtures, deliberately not produced by a writer.
  private def reader(values: Int*): msgpack.Reader = {
    val bytes = values.map(_.toByte).toArray
    msgpack.Reader(bytes = bytes, start = 0, size = bytes.length, maximumElements = 4)
  }

  test("skipping unknown metadata handles nested arrays, maps, floats, binary and extensions") {
    val input = reader(0x94, 0x81, 0xa1, 'x', 0xca, 0, 0, 0, 0, 0xc4, 2, 1, 2, 0xd4, 0, 42, 0xc7, 2, 0, 3, 4, 7)
    assertEquals(input.skip(4), Right(()))
    assertEquals(input.readUnsignedInt(), Right(7))
    assertEquals(input.remaining, 0)
  }

  test("skipping cannot exceed its input slice, nesting or container bounds") {
    assert(reader(0xc6, 0x7f, 0xff, 0xff, 0xff).skip(4).isLeft)
    assert(reader(0x95).skip(4).isLeft)
    assert(reader(0x91, 0x91, 0).skip(1).isLeft)
    assert(reader(0xc1).skip(4).isLeft)
    val bytes = Array[Byte](0, 0xd9.toByte, 2, 65, 66)
    val input = msgpack.Reader(bytes = bytes, start = 1, size = 3)
    assert(input.readString().isLeft)
  }

  test("metadata enforces UTF-8 while permissive text decoding remains explicit") {
    assert(reader(0xa1, 0xff).readString(strict = true).isLeft)
    assertEquals(reader(0xa1, 0xff).readString(), Right("\ufffd"))
    assertEquals(reader(0xa2, 0xc3, 0xa9).readString(strict = true), Right("é"))
  }

  test("unsigned metadata fields reject signed encodings and integers outside Int") {
    assert(reader(0xd1, 0, 1).readUnsignedInt().isLeft)
    assert(reader(0xce, 0x80, 0, 0, 0).readUnsignedInt().isLeft)
    assert(reader(0xcf, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff).readUnsignedInt().isLeft)
    assertEquals(reader(0xce, 0x7f, 0xff, 0xff, 0xff).readUnsignedInt(), Right(Int.MaxValue))
  }
}
