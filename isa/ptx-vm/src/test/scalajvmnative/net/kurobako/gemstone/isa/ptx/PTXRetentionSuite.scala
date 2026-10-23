package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.either.*

class PTXRetentionSuite extends munit.FunSuite {
  import PTXCacheSuite.*

  test("a released program stays parsed for the next load of equal content") {
    val calls   = AtomicInteger()
    val backend = PTXBackend(decode = { bytes =>
      val _ = calls.incrementAndGet()
      ISA.decode(bytes)
    })
    backend.load(image).value.close()
    backend.load(Vec.from(source.getBytes(StandardCharsets.UTF_8))).value.close()
    assertEquals(calls.get(), 1)
    backend.load(Vec.from(s"$source\n// other".getBytes(StandardCharsets.UTF_8))).value.close()
    assertEquals(calls.get(), 2)
  }

  test("a failed decode is retried rather than retained") {
    val calls   = AtomicInteger()
    val backend = PTXBackend(decode = { bytes =>
      if (calls.incrementAndGet() == 1) Left(ISA.DecodeError.InvalidUTF8(0)) else ISA.decode(bytes)
    })
    assert(backend.load(image).isLeft)
    backend.load(image).value.close()
    backend.load(image).value.close()
    assertEquals(calls.get(), 2)
  }
}
