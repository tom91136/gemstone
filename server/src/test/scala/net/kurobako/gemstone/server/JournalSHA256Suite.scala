package net.kurobako.gemstone.server
import java.nio.charset.StandardCharsets

final class JournalSHA256Suite extends munit.FunSuite {
  // Published SHA-256 examples: FIPS180-4 / NIST SHAVS, including padding into a second block.
  private val vectors = Seq(
    ""    -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    "abc" -> "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
    "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq" ->
      "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
    ("a" * 1000000) -> "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0"
  )
  test("standard SHA256 vectors agree for whole, byte-wise and chunked input") {
    for {
      (text, expected) <- vectors
      chunk            <- Seq(1, 7, 63, 64, 65, 8192)
    } {
      val bytes  = text.getBytes(StandardCharsets.US_ASCII)
      val digest = JournalSHA256()
      var offset = 0
      while (offset < bytes.length) {
        val size = math.min(chunk, bytes.length - offset); digest.update(bytes, offset, size); offset += size
      }
      assertEquals(RPCJournal.hex(digest.digest()), expected)
    }
    val digest = JournalSHA256(); "abc".foreach(c => digest.updateByte(c.toInt))
    assertEquals(RPCJournal.hex(digest.digest()), vectors(1)._2)
  }
  test("invalid ranges do not consume input and finalized digest cannot be reused") {
    val digest = JournalSHA256()
    val _      = intercept[IllegalArgumentException](digest.update(Array[Byte](1), 1, 1))
    assertEquals(RPCJournal.hex(digest.digest()), vectors.head._2)
    val _ = intercept[IllegalArgumentException](digest.updateByte(0))
  }
}
