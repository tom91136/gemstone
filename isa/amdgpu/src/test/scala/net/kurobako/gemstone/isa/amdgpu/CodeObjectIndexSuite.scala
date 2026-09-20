package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Bits, ByteRange, ScalarByteWidth}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import scala.util.Using

final class CodeObjectIndexSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("a real ELF index validates descriptors and materializes one shared kernel lazily") {
    val image   = minimalCodeObject()
    val indexed = CodeObject.index(Vec.unsafeFromArray(image)).toOption.get
    assertEquals(indexed.target, Target("gfx900"))
    assertEquals(indexed.kernelNames, Set("kernel"))
    assertEquals(
      indexed.global(GlobalName("device_global")).map { (address, size) =>
        val rawAddress: Long = address.value
        val rawSize: Int     = size
        rawAddress -> rawSize
      },
      Some(0x3000L -> 16)
    )
    assertEquals(indexed.writeGlobal(GlobalName("device_global"), 4L, Vec[Byte](9, 8, 7, 6)), Right(()))

    // The index owns its image: mutating the caller's input after indexing must
    // not affect later lazy kernel materialization.
    java.util.Arrays.fill(image, 0.toByte)
    final class Pool extends AutoCloseable {
      val executor               = Executors.newFixedThreadPool(4)
      override def close(): Unit = {
        val _ = executor.shutdownNow()
      }
    }
    Using.resource(Pool()) { pool =>
      val pending = Vec.fill(16)(pool.executor.submit(() => indexed.kernel("kernel").toOption.get))
      val kernels = pending.map(_.get())
      val kernel  = kernels.head
      assert(kernels.forall(_ eq kernel), "concurrent materialization returned distinct kernels")
      assertEquals(kernel.name, "kernel")
      assertEquals(kernel.target, Target("gfx900"))
      assertEquals(kernel.kernargSize, 24)
      assertEquals(kernel.groupSegmentSize, 16)
      assertEquals(kernel.privateSegmentSize, 8)
      assertEquals(kernel.computePgmRsrc2, 0x80)
      assertEquals(kernel.kernelCodeProperties, 0x0a)
      assertEquals(kernel.code, LittleEndian.toVec(0xbf810000L, ScalarByteWidth(4)))
      assertEquals(kernel.readOnlyAddress, 0x2000L)
      assert(kernel.readOnly.length > 64)
      assertEquals(
        kernel.globalMemory.find(_._1 == 0x3000L).map(_._2.drop(4).take(4).toSeq),
        Some(Seq[Byte](9, 8, 7, 6))
      )
    }
  }

  test("identical ELF indexes isolate mutable globals across cached views") {
    val first  = CodeObject.index(Vec.unsafeFromArray(minimalCodeObject())).toOption.get
    val second = CodeObject.index(Vec.unsafeFromArray(minimalCodeObject())).toOption.get
    assertEquals(first.writeGlobal(GlobalName("device_global"), 4L, Vec[Byte](9, 8, 7, 6)), Right(()))
    assertEquals(
      second.kernel("kernel").toOption.get.globalMemory.find(_._1 == 0x3000L).map { case (_, bytes) =>
        bytes.drop(4).take(4).toSeq
      },
      Some(Seq[Byte](0, 0, 0, 0))
    )
  }

  test("linked code objects infer zero-sized kernel symbols from the next function") {
    assertEquals(CodeObject.inferredFunctionSize(0x1000L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x180))
    assertEquals(CodeObject.inferredFunctionSize(0x1300L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x100))
    assertEquals(CodeObject.inferredFunctionSize(0x1400L, 0x1400L, Seq.empty), None)
  }

  test("inferred linked functions discard alignment padding after their final end-program") {
    val code = Array[Byte](
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x00,
      0x00
    )
    assertEquals(
      CodeObject.trimTrailingKernelPadding(code).toSeq,
      code.take(4).toSeq
    )
  }

}
