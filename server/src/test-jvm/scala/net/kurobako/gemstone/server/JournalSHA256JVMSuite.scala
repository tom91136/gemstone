package net.kurobako.gemstone.server
import java.security.MessageDigest

final class JournalSHA256JVMSuite extends munit.FunSuite {
  test("random and block-boundary byte streams match the JDK SHA256 implementation") {
    val random = scala.util.Random(713L)
    for {
      size  <- Seq(0, 1, 55, 56, 57, 63, 64, 65, 127, 128, 129, 1024, 4096) ++ Seq.fill(20)(random.nextInt(10000))
      chunk <- Seq(1, 7, 63, 64, 65, 1024)
    } {
      val bytes  = Array.ofDim[Byte](size); random.nextBytes(bytes)
      val digest = JournalSHA256()
      var offset = 0
      while (offset < size) {
        val count = math.min(chunk, size - offset); digest.update(bytes, offset, count); offset += count
      }
      assertEquals(digest.digest().toSeq, MessageDigest.getInstance("SHA-256").digest(bytes).toSeq)
    }
  }
}
