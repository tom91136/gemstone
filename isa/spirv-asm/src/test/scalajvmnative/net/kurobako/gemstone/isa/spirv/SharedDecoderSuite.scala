package net.kurobako.gemstone.isa.spirv

import assembly.*
import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import scala.annotation.tailrec
import scala.collection.mutable
import scala.sys.runtime
import scala.util.Using

class SharedDecoderSuite extends Suite {
  private def module(value: Int): Module = spv"""
    %uint = OpTypeInt 32 0
    %spec = OpSpecConstant %uint $value
  """

  private def bytes(value: Int): Vec[Byte] = ISA.encode(module(value)).value

  @tailrec
  private def collected(reclaimed: => Boolean, attempts: Int = 30): Boolean =
    if (attempts == 0) false
    else {
      runtime.gc()
      Thread.sleep(20)
      if (reclaimed) true else collected(reclaimed, attempts - 1)
    }

  test("specialized modules share unchanged instruction graphs and keep distinct constant values") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).value
    val second  = decoder.decode(bytes(10)).value
    val third   = decoder.decode(bytes(5)).value
    assertEquals(first, module(5))
    assertEquals(second, module(10))
    assert(first.instructions(0) eq second.instructions(0))
    assert(first.instructions(1) ne second.instructions(1))
    assert(first.instructions(1) eq third.instructions(1))
  }

  test("sharing preserves module headers and byte order") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).value
    val changed = module(5).copy(header = module(5).header.copy(bound = Word(64)), endianness = Endianness.Big)
    val second  = decoder.decode(ISA.encode(changed).value).value
    assertEquals(second, changed)
    assert(first.instructions(0) eq second.instructions(0))
  }

  test("malformed modules retain ordinary decoder errors and do not poison subsequent loads") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).value
    val unknown = Binary.encode(RawModule(
      first.header,
      Vec(RawInstruction(SPIRVOpcodeNumber(65535), Vec.empty)),
      first.endianness
    )).value
    Seq(Vec.empty[Byte], bytes(5).dropRight(1), unknown).foreach { invalid =>
      assert(ISA.decode(invalid).isLeft)
      assertEquals(decoder.decode(invalid), ISA.decode(invalid))
    }
    val next = decoder.decode(bytes(5)).value
    assert(first.instructions(0) eq next.instructions(0))
  }

  test("concurrent loads share immutable instructions without mixing specialization values") {
    val decoder = SharedDecoder()
    val anchor  = decoder.decode(bytes(0)).value
    Using.resource(threads.Pool(4)) { pool =>
      val futures = mutable.ArrayBuffer.empty[Future[Module]]
      for (value <- 1 to 32)
        futures += pool.executor.submit((() => decoder.decode(bytes(value)).value): Callable[Module])
      futures.zipWithIndex.foreach { (future, index) =>
        val decoded = future.get(10, TimeUnit.SECONDS)
        assertEquals(decoded, module(index + 1))
        assert(anchor.instructions(0) eq decoded.instructions(0))
      }
    }
  }

  test("released instructions can be reclaimed while the decoder remains live") {
    val decoder = SharedDecoder()
    val result  = AtomicReference[WeakReference[Instruction]]()
    // A terminated thread leaves no conservative native stack roots for the module.
    val worker = Thread { () =>
      val decoded = decoder.decode(bytes(5)).value
      result.set(WeakReference(decoded.instructions.head))
    }
    worker.start()
    worker.join()
    val released = collected(Option(result.get().get()).isEmpty)
    // Keep the decoder live through collection, without keeping a module live.
    assert(decoder.decode(bytes(10)).isRight)
    assert(released, "decoder retained a released instruction graph")
  }

  test("hash collisions preserve distinct constants and live entries during reclamation") {
    def encoded(low: Int, high: Int): Vec[Byte] = {
      val bits = (high.toLong << 32) | (low & 0xffffffffL)
      ISA.encode(spv"""
        %ulong = OpTypeInt 64 0
        %spec = OpSpecConstant %ulong 0x${bits.toHexString}
      """).value
    }
    val firstBytes  = encoded(low = 1418299984, high = -1284610799)
    val secondBytes = encoded(low = -1012570844, high = 172645704)
    val decoder     = SharedDecoder()
    val first       = decoder.decode(firstBytes).value.instructions(1)
    val result      = AtomicReference[(WeakReference[Instruction], Int, Boolean)]()
    val worker      = Thread { () =>
      val second = decoder.decode(secondBytes).value.instructions(1)
      result.set((WeakReference(second), second.hashCode(), first != second && (first ne second)))
    }
    worker.start()
    worker.join()
    val (reference, hash, distinct) = result.get()
    assertEquals(first.hashCode(), hash)
    assert(distinct, "hash collision merged distinct instructions")
    assert(collected(Option(reference.get()).isEmpty), "a live colliding entry retained its released sibling")
    assert(first eq decoder.decode(firstBytes).value.instructions(1))
    assert(first != decoder.decode(secondBytes).value.instructions(1))
  }
}
