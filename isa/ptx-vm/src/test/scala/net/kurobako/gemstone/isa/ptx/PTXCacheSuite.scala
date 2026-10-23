package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Using

object PTXCacheSuite {
  val source: String =
    """|.version 8.0
       |.target sm_80
       |.address_size 64
       |.global .align 4 .u32 shared_value;
       |.visible .entry read_global(.param .u64 output) {
       |  .reg .b64 %rd;
       |  .reg .b32 %r;
       |  ld.param.u64 %rd, [output];
       |  ld.global.u32 %r, [shared_value];
       |  st.global.u32 [%rd], %r;
       |  ret;
       |}
       |""".stripMargin
  val image: Vec[Byte] = Vec.from(source.getBytes(StandardCharsets.UTF_8))
}

class PTXCacheSuite extends munit.FunSuite {
  import PTXCacheSuite.*

  test("images with colliding CRC32 hashes retain independent cache entries and lifetimes") {
    val firstImage  = Vec.from(bytes.hex("58fbb7a9b2680392"))
    val secondImage = Vec.from(bytes.hex("69176c4aa471a321"))
    val calls       = AtomicInteger()
    val backend     = PTXBackend(
      decode = { bytes =>
        val _      = calls.incrementAndGet()
        val kernel = if (bytes == firstImage) "first" else "second"
        ISA.decode(Vec.from(s".version 8.0\n.target sm_80\n.address_size 64\n.entry $kernel() { ret; }"
          .getBytes(StandardCharsets.UTF_8)))
      },
      retainReleased = false
    )
    Using.Manager { use =>
      val first  = backend.load(firstImage).value
      val second = use(backend.load(secondImage).value)
      try {
        assertEquals(first.symbols.value.kernels.map(_.name.raw), Vec("first"))
        assertEquals(second.symbols.value.kernels.map(_.name.raw), Vec("second"))
      } finally first.close()
      val retained = use(backend.load(Vec.from(Vec.toArray(secondImage))).value)
      assertEquals(retained.symbols, second.symbols)
      assertEquals(calls.get(), 2)
    }.get
    backend.load(firstImage).value.close()
    assertEquals(calls.get(), 3)
  }

  test("decode errors and thrown failures release the cache reservation and allow retry") {
    val calls       = AtomicInteger()
    val interrupted = InterruptedException("decode interrupted")
    val backend     = PTXBackend(
      decode = { bytes =>
        calls.incrementAndGet() match {
          case 1 => Left(ISA.DecodeError.InvalidUTF8(0))
          case 2 => throw interrupted
          case _ => ISA.decode(bytes)
        }
      },
      retainReleased = false
    )
    assert(backend.load(image).isLeft)
    val observed = try { val _ = backend.load(image); None }
    catch { case error: InterruptedException => Some(error) }
    assert(observed.contains(interrupted))
    backend.load(image).value.close()
    backend.load(image).value.close()
    assertEquals(calls.get(), 4)
  }
}
