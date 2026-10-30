package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Using

class CodeObjectIndexConcurrencySuite extends Suite {
  test("a real ELF index validates descriptors and materialises one shared kernel lazily") {
    val image   = minimalCodeObject()
    val indexed = ELF.index(Vec.from(image)).value
    assertEquals(indexed.target, Target("gfx900"))
    assertEquals(indexed.kernelNames.toSet[String], Set("kernel"))
    assertEquals(
      indexed.global("device_global").map { (address, size) =>
        val rawAddress: Long = address.raw
        val rawSize: Int     = size
        rawAddress -> rawSize
      },
      Some(0x3000L -> 16)
    )
    assertEquals(indexed.writeGlobal("device_global", 4L, Vec[Byte](9, 8, 7, 6)), Right(()))

    // The immutable input owns a copy of the mutable source. Changing that source
    // must not affect later lazy kernel materialisation.
    java.util.Arrays.fill(image, 0.toByte)
    Using.resource(threads.Pool(4)) { pool =>
      val pending = Vec.fill(16)(pool.executor.submit(() => indexed.kernel("kernel").value))
      val kernels = pending.map(_.get(5, TimeUnit.SECONDS))
      val kernel  = kernels.head
      assert(kernels.forall(_ eq kernel), "concurrent materialisation returned distinct kernels")
      assertEquals[String, String](kernel.name, "kernel")
      assertEquals(kernel.target, Target("gfx900"))
      assertEquals(kernel.kernargSize.raw, 24)
      assertEquals(kernel.groupSegmentSize.raw, 16)
      assertEquals(kernel.privateSegmentSize.raw, 8)
      assertEquals(kernel.computePgmRsrc2, 0x80)
      assertEquals(kernel.kernelCodeProperties, 0x0a)
      assertEquals(kernel.entrySize.raw, 4)
      assertEquals(
        kernel.text.drop((kernel.entry - kernel.textAddress).toInt).take(4),
        LittleEndian.toVec(0xbf810000L, 4)
      )
      assertEquals(kernel.readOnlyAddress, 0x2000L)
      assert(kernel.readOnly.length > 64)
      assertEquals(
        kernel.globalMemory.find(_._1 == 0x3000L).map(_._2.slice(4, 8).toSeq),
        Some(Seq[Byte](9, 8, 7, 6))
      )
    }
  }
}
