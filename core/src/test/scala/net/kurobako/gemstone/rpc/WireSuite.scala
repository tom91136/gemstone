package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

class WireSuite extends munit.FunSuite {
  // Fixed bytes and boundary values below are independent expectations for the wire/bit representation.
  // Do not derive these from the encoder or the constants whose values these cases verify.

  private case class Pair(number: Int, flag: Boolean) derives Codec

  private val limits = Limits(16, 1024, 256)

  private def bytes(values: Int*): Array[Byte] = values.map(_.toByte).toArray

  private def decode[A: Codec](values: Int*): Either[String, A] = Codec[A].decode(bytes(values*), limits)

  test("signed integer widths match fixed MessagePack bytes at every transition") {
    val cases = Seq(
      (0L, bytes(0)),
      (127L, bytes(0x7f)),
      (128L, bytes(0xd1, 0, 0x80)),
      (-1L, bytes(0xff)),
      (-32L, bytes(0xe0)),
      (-33L, bytes(0xd0, 0xdf)),
      (-128L, bytes(0xd0, 0x80)),
      (-129L, bytes(0xd1, 0xff, 0x7f)),
      (32767L, bytes(0xd1, 0x7f, 0xff)),
      (32768L, bytes(0xd2, 0, 0, 0x80, 0)),
      (-32768L, bytes(0xd1, 0x80, 0)),
      (-32769L, bytes(0xd2, 0xff, 0xff, 0x7f, 0xff)),
      (Int.MaxValue.toLong, bytes(0xd2, 0x7f, 0xff, 0xff, 0xff)),
      (Int.MinValue.toLong, bytes(0xd2, 0x80, 0, 0, 0)),
      (2147483648L, bytes(0xd3, 0, 0, 0, 0, 0x80, 0, 0, 0)),
      (-2147483649L, bytes(0xd3, 0xff, 0xff, 0xff, 0xff, 0x7f, 0xff, 0xff, 0xff)),
      (Long.MaxValue, bytes(0xd3, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff)),
      (Long.MinValue, bytes(0xd3, 0x80, 0, 0, 0, 0, 0, 0, 0))
    )
    cases.foreach { (value, encoded) =>
      assertEquals(Codec[Long].encode(value, limits).map(_.toSeq), Right(encoded.toSeq), clues(value))
      assertEquals(Codec[Long].decode(encoded, limits), Right(value), clues(value))
      encoded.indices.foreach { length =>
        assert(Codec[Long].decode(encoded.take(length), limits).isLeft, clues(value, length))
      }
      (1 until encoded.length).foreach { maximum =>
        val bounded = limits.copy(maximumBytes = maximum.unsafe[Int > 0])
        assert(Codec[Long].encode(value, bounded).isLeft, clues(value, maximum))
      }
    }
  }

  test("unsigned integer encodings are accepted only when representable by the destination") {
    assertEquals(decode[Long](0xcc, 0xff), Right(255L))
    assertEquals(decode[Long](0xcd, 0xff, 0xff), Right(65535L))
    assertEquals(decode[Long](0xce, 0xff, 0xff, 0xff, 0xff), Right(4294967295L))
    assertEquals(decode[Long](0xcf, 0, 0, 0, 0, 0, 0, 0, 0), Right(0L))
    assertEquals(decode[Long](0xcf, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff), Right(Long.MaxValue))
    assert(decode[Long](0xcf, 0x80, 0, 0, 0, 0, 0, 0, 0).isLeft)
    assert(decode[Int](0xce, 0x80, 0, 0, 0).isLeft)
    assert(decode[Int](0xd3, 0xff, 0xff, 0xff, 0xff, 0x7f, 0xff, 0xff, 0xff).isLeft)
    assertEquals(decode[Int](0xce, 0x7f, 0xff, 0xff, 0xff), Right(Int.MaxValue))
    assertEquals(decode[Int](0xd2, 0x80, 0, 0, 0), Right(Int.MinValue))
  }

  test("byte decoding rejects narrowing overflow") {
    assertEquals(decode[Byte](0xd0, 0x80), Right(Byte.MinValue))
    assertEquals(decode[Byte](0x7f), Right(Byte.MaxValue))
    assert(decode[Byte](0xcc, 0x80).isLeft)
    assert(decode[Byte](0xd1, 0xff, 0x7f).isLeft)
    assert(decode[Byte](0xcd, 1, 0).isLeft)
  }

  test("booleans, nil, and unit consume exactly their own representation") {
    assertEquals(decode[Boolean](0xc2), Right(false))
    assertEquals(decode[Boolean](0xc3), Right(true))
    assertEquals(decode[Option[Int]](0xc0), Right(None))
    assertEquals(decode[Option[Int]](0), Right(Some(0)))
    assertEquals(decode[Unit](0x90), Right(()))
    assert(decode[Boolean](0).isLeft)
    assert(decode[Option[Int]]().isLeft)
    assert(decode[Option[Int]](0xc0, 0).isLeft)
    assert(decode[Unit](0x91, 0).isLeft)
    assert(decode[Unit](0xc0).isLeft)
    assert(decode[Long](0xc3).isLeft)
    assert(decode[String](0xc4, 0).isLeft)
    assert(decode[Vec[Byte]](0xa0).isLeft)
    assert(decode[Vec[Int]](0xa0).isLeft)
  }

  test("string, binary, and array length encodings check truncation before accessing payload") {
    val strings = Seq(bytes(0xa1, 0x78), bytes(0xd9, 1, 0x78), bytes(0xda, 0, 1, 0x78), bytes(0xdb, 0, 0, 0, 1, 0x78))
    val binary  = Seq(bytes(0xc4, 1, 0xff), bytes(0xc5, 0, 1, 0xff), bytes(0xc6, 0, 0, 0, 1, 0xff))
    val arrays  = Seq(bytes(0x91, 7), bytes(0xdc, 0, 1, 7), bytes(0xdd, 0, 0, 0, 1, 7))
    strings.foreach { encoded =>
      assertEquals(Codec[String].decode(encoded, limits), Right("x"))
      encoded.indices.foreach(length => assert(Codec[String].decode(encoded.take(length), limits).isLeft))
    }
    binary.foreach { encoded =>
      assertEquals(Codec[Vec[Byte]].decode(encoded, limits), Right(Vec[Byte](-1)))
      encoded.indices.foreach(length => assert(Codec[Vec[Byte]].decode(encoded.take(length), limits).isLeft))
    }
    arrays.foreach { encoded =>
      assertEquals(Codec[Vec[Int]].decode(encoded, limits), Right(Vec(7)))
      encoded.indices.foreach(length => assert(Codec[Vec[Int]].decode(encoded.take(length), limits).isLeft))
    }
    assert(decode[String](0xdb, 0xff, 0xff, 0xff, 0xff).isLeft)
    assert(decode[Vec[Byte]](0xc6, 0xff, 0xff, 0xff, 0xff).isLeft)
    assert(decode[Vec[Int]](0xdd, 0xff, 0xff, 0xff, 0xff).isLeft)
    assertEquals(decode[String](0xdb, 0, 0, 0, 0), Right(""))
    assertEquals(decode[Vec[Byte]](0xc6, 0, 0, 0, 0), Right(Vec.empty[Byte]))
    assertEquals(decode[Vec[Int]](0xdd, 0, 0, 0, 0), Right(Vec.empty[Int]))
  }

  test("text and element limits allow equality and reject one additional byte or element") {
    val small = Limits(2, 16, 2)
    assertEquals(Codec[String].decode(bytes(0xa2, 0xc3, 0xa9), small), Right("é"))
    assert(Codec[String].decode(bytes(0xa3, 0xc3, 0xa9, 0x78), small).isLeft)
    assertEquals(Codec[String].encode("é", small).map(_.toSeq), Right(bytes(0xa2, 0xc3, 0xa9).toSeq))
    assert(Codec[String].encode("éx", small).isLeft)
    assertEquals(Codec[Vec[Int]].decode(bytes(0x92, 1, 2), small), Right(Vec(1, 2)))
    assert(Codec[Vec[Int]].decode(bytes(0x93, 1, 2, 3), small).isLeft)
    assertEquals(Codec[Vec[Int]].encode(Vec(1, 2), small).map(_.toSeq), Right(bytes(0x92, 1, 2).toSeq))
    assert(Codec[Vec[Int]].encode(Vec(1, 2, 3), small).isLeft)
  }

  test("total byte limits include headers and apply to codec and protocol decoding") {
    val small = Limits(16, 3, 256)
    assertEquals(Codec[String].decode(bytes(0xa2, 0x61, 0x62), small), Right("ab"))
    assertEquals(Codec[String].encode("ab", small).map(_.toSeq), Right(bytes(0xa2, 0x61, 0x62).toSeq))
    assert(Codec[String].encode("abc", small).isLeft)
    assert(Codec[String].decode(bytes(0xa3, 0x61, 0x62, 0x63), small).isLeft)
    assertEquals(Codec[Vec[Byte]].decode(bytes(0xc4, 1, 7), small), Right(Vec(7.toByte)))
    assertEquals(Codec[Vec[Byte]].encode(Vec(7.toByte), small).map(_.toSeq), Right(bytes(0xc4, 1, 7).toSeq))
    assert(Codec[Vec[Byte]].encode(Vec[Byte](7, 8), small).isLeft)
    assert(Codec[Vec[Byte]].decode(bytes(0xc4, 2, 7, 8), small).isLeft)
    assert(Codec[Vec[Int]].decode(bytes(0x93, 1, 2, 3), small).isLeft)

    val protocol      = Protocol[ExecutionRPC.Operation]
    val operation     = ExecutionRPC.Operation.LoadProgram("x", Vec.empty)
    val (id, encoded) = protocol.encodePayload(operation, limits).fold(fail(_), identity)
    val exact         = limits.copy(maximumBytes = encoded.length.unsafe[Int > 0])
    assertEquals(protocol.decodePayload(id, encoded, exact).map(_.value), Right(operation))
    assert(protocol.decodePayload(id, encoded, small).isLeft)
    val (pingId, _) = protocol.encodePayload(ExecutionRPC.Operation.Ping, limits).fold(fail(_), identity)
    assertEquals(protocol.decodePayload(pingId, bytes(0x90), limits).map(_.value), Right(ExecutionRPC.Operation.Ping))
    assert(protocol.decodePayload(pingId, bytes(0x91), limits).isLeft)
  }

  test("collection headers preserve their length across MessagePack width transitions") {
    val large   = Limits(65536, 131072, 65536)
    val strings = Seq(
      0     -> bytes(0xa0),
      31    -> bytes(0xbf),
      32    -> bytes(0xd9, 32),
      255   -> bytes(0xd9, 0xff),
      256   -> bytes(0xda, 1, 0),
      65535 -> bytes(0xda, 0xff, 0xff),
      65536 -> bytes(0xdb, 0, 1, 0, 0)
    )
    strings.foreach { (size, header) =>
      val encoded = header ++ Array.fill[Byte](size)(0x78)
      val actual  = Codec[String].encode("x" * size, large).fold(fail(_), identity)
      assert(actual.sameElements(encoded), clues(size))
      assert(Codec[String].decode(encoded, large).fold(fail(_), identity) == "x" * size, clues(size))
    }
    val binary = Seq(
      0     -> bytes(0xc4, 0),
      255   -> bytes(0xc4, 0xff),
      256   -> bytes(0xc5, 1, 0),
      65535 -> bytes(0xc5, 0xff, 0xff),
      65536 -> bytes(0xc6, 0, 1, 0, 0)
    )
    binary.foreach { (size, header) =>
      val value   = Vec.fill(size)(7.toByte)
      val encoded = header ++ Array.fill[Byte](size)(7)
      val actual  = Codec[Vec[Byte]].encode(value, large).fold(fail(_), identity)
      assert(actual.sameElements(encoded), clues(size))
      assert(Codec[Vec[Byte]].decode(encoded, large).fold(fail(_), identity) == value, clues(size))
    }
    val arrays = Seq(
      0     -> bytes(0x90),
      15    -> bytes(0x9f),
      16    -> bytes(0xdc, 0, 16),
      65535 -> bytes(0xdc, 0xff, 0xff),
      65536 -> bytes(0xdd, 0, 1, 0, 0)
    )
    arrays.foreach { (size, header) =>
      val value   = Vec.fill(size)(7)
      val encoded = header ++ Array.fill[Byte](size)(7)
      val actual  = Codec[Vec[Int]].encode(value, large).fold(fail(_), identity)
      assert(actual.sameElements(encoded), clues(size))
      assert(Codec[Vec[Int]].decode(encoded, large).fold(fail(_), identity) == value, clues(size))
    }
  }

  test("derived products and sums reject wrong arity, unknown tags, and invalid ordinals") {
    import net.kurobako.gemstone.rpc.ExecutionProtocol.given
    assertEquals(decode[Pair](0x92, 7, 0xc3), Right(Pair(7, true)))
    assert(decode[Pair](0x91, 7, 0xc3).isLeft)
    assert(decode[Pair](0x93, 7, 0xc3).isLeft)
    import net.kurobako.gemstone.isa.*
    assertEquals(decode[KernelMetadata.ParameterKind](0), Right(KernelMetadata.ParameterKind.Scalar))
    assertEquals(decode[KernelMetadata.ParameterKind](2), Right(KernelMetadata.ParameterKind.Local))
    assert(decode[KernelMetadata.ParameterKind](3).isLeft)
    assert(decode[KernelMetadata.ParameterKind](4).isLeft)
    assert(decode[KernelMetadata.ParameterKind](0xff).isLeft)
    val failed = bytes(0x92, 0xa6, 0x46, 0x61, 0x69, 0x6c, 0x65, 0x64, 0x91, 0xa1, 0x78)
    assertEquals(Codec[ExecutionFailure].decode(failed, limits), Right(ExecutionFailure.Failed("x")))
    assert(Codec[ExecutionFailure].decode(bytes(0x91) ++ failed.drop(1), limits).isLeft)
    assert(decode[ExecutionFailure](0x92, 0xa1, 0x3f, 0x91, 0xa1, 0x78).isLeft)
  }
}
