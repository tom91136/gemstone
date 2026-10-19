package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

// The forms are those nvcc 13.3 emits for cuda::memcpy_async, cuda::ptx and cooperative_groups on sm_90 and that
// `ptxas -arch=sm_90a` accepts (see the round-4 evidence in the report).
class BulkTensorSuite extends munit.FunSuite {
  private val nanFills   = Vec(false, true)
  private val misaligned = Vec((-3, -2), (5, 7))

  private def argument(name: String, value: Long): (NonEmpty[String], Argument) =
    (name.unsafe[NonEmpty[String]], Argument.fromLong(value))

  private def map(name: String, bytes: Array[Byte]): (NonEmpty[String], Argument) =
    (name.unsafe[NonEmpty[String]], Argument(Vec.from(bytes).unsafe[NonEmpty[Vec[Byte]]]))

  test("bulk copies, stores and reductions complete through mbarrier transactions and bulk groups") {
    val kernel = compileKernel(AsyncCases.bulkPtx)
    val memory = DeviceMemory(1284)
    val input  = Vec.tabulate(64)(index => index * 3 - 50)
    input.zipWithIndex.foreach { (value, index) =>
      assertEquals(memory.writeInt32(memory.start.offset(index * 4L), value), Right(()))
      assertEquals(memory.writeInt32(memory.start.offset(512 + index * 4L), 1000), Right(()))
    }
    assertEquals(execute(kernel, 1, 64, Map(argument("data", memory.start.raw)), memory), Right(()))
    val incremented = input.map(_ + 1)
    assertEquals(memory.ints(from = 256, count = 64), incremented)
    assertEquals(memory.ints(from = 512, count = 64), incremented.map(_ + 1000))
    assertEquals(memory.ints(from = 768, count = 64), incremented.map(math.min(_, 0)))
    assertEquals(memory.ints(from = 1024, count = 64), incremented)
    assertEquals(memory.ints(from = 1280, count = 1), Vec(1))
  }

  // A 40x20 float tensor read through boxes that leave it on the right and at the bottom.
  for {
    swizzle <- 0 to 3
    nanFill <- nanFills
  }
    test(s"tensor tile copies swizzle $swizzle with NaN fill $nanFill: boxes fill out-of-bounds elements") {
      val kernel                            = compileKernel(AsyncCases.tilePtx)
      val (w, h)                            = (40, 20)
      val width                             = if (swizzle == 0) 32 else 4 << swizzle
      val height                            = 256 / width
      val (x, y)                            = (w - width / 2, h - height / 2)
      val tensor                            = w * h * 4
      val memory                            = DeviceMemory((3 * tensor + 1024).unsafe[Int >= 0])
      val source                            = memory.start.raw
      val stored                            = source + tensor
      val reduced                           = source + 2 * tensor
      val out                               = source + 3 * tensor
      def value(column: Int, row: Int): Int = java.lang.Float.floatToRawIntBits(row * 100f + column)
      for {
        row    <- 0 until h
        column <- 0 until w
      } {
        assertEquals(memory.writeInt32(memory.start.offset((row * w + column) * 4L), value(column, row)), Right(()))
        assertEquals(
          memory.writeInt32(memory.start.offset(2L * tensor + (row * w + column) * 4L), 0x3f800000),
          Right(())
        )
      }
      def describe(base: Long) = TensorMaps.tiled(
        base,
        TensorMap.Element.F32,
        Vec(w.toLong, h.toLong),
        Vec(w * 4L),
        Vec(width, height),
        swizzle = swizzle,
        nanFill = nanFill
      )
      assertEquals(
        execute(
          kernel,
          1,
          256,
          Map(
            map("load", describe(source)),
            map("store", describe(stored)),
            map("reduce", describe(reduced)),
            argument("out", out),
            (("x": NonEmpty[String]), Argument.fromLong(x.toLong)),
            (("y": NonEmpty[String]), Argument.fromLong(y.toLong))
          ),
          memory
        ),
        Right(())
      )
      val fill = if (nanFill) 0x7fffffff else 0
      for (word <- 0 until 256) {
        val logical       = TensorMaps.unswizzle(swizzle, word * 4L) / 4
        val (column, row) = (x + (logical % width).toInt, y + (logical / width).toInt)
        val inside        = column < w && row < h
        val expected      = if (inside) value(column, row) else fill
        assertEquals(memory.ints(from = (3 * tensor) + word * 4, count = 1), Vec(expected), clues(word, column, row))
        if (inside) {
          val index = (row * w + column) * 4
          assertEquals(memory.ints(from = tensor + index, count = 1), Vec(expected))
          assertEquals(
            memory.ints(from = 2 * tensor + index, count = 1),
            Vec(java.lang.Float.floatToRawIntBits(java.lang.Float.intBitsToFloat(expected) + 1f))
          )
        }
      }
    }

  // GH200 (sm_90, driver 580.178.04, CUDA 12.6) k_tensor2d: a 2D float tile whose inner coordinate is not a multiple of
  // 16 bytes ends the launch with cudaErrorIllegalInstruction, in every swizzle mode and for both fills. The probe
  // issues load, store and reduce in turn, so the trapping instruction is one of the writes or the load.
  for {
    swizzle <- 0 to 3
    nanFill <- nanFills
    (x, y)  <- misaligned
  }
    test(
      s"sm_90 hardware: tensor tile at x=$x raises an illegal instruction in swizzle $swizzle with NaN fill $nanFill"
    ) {
      val kernel               = compileKernel(AsyncCases.tilePtx)
      val (w, h)               = (40, 20)
      val width                = if (swizzle == 0) 32 else 4 << swizzle
      val height               = 256 / width
      val tensor               = w * h * 4
      val memory               = DeviceMemory((3 * tensor + 1024).unsafe[Int >= 0])
      val source               = memory.start.raw
      def describe(base: Long) = TensorMaps.tiled(
        base,
        TensorMap.Element.F32,
        Vec(w.toLong, h.toLong),
        Vec(w * 4L),
        Vec(width, height),
        swizzle = swizzle,
        nanFill = nanFill
      )
      val result = execute(
        kernel,
        1,
        256,
        Map(
          map("load", describe(source)),
          map("store", describe(source + tensor)),
          map("reduce", describe(source + 2 * tensor)),
          argument("out", source + 3 * tensor),
          (("x": NonEmpty[String]), Argument.fromLong(x.toLong)),
          (("y": NonEmpty[String]), Argument.fromLong(y.toLong))
        ),
        memory
      )
      result match {
        case Left(Fault.IllegalInstruction(_)) => ()
        case other                             => fail(s"expected an illegal instruction fault, got $other")
      }
    }

  // GH200 k_bulk_smem: the shared-to-shared bulk copy of a launch without a cluster ends in cudaErrorIllegalInstruction.
  test("sm_90 hardware: shared-to-shared cp.async.bulk outside a cluster raises an illegal instruction") {
    val kernel = compileKernel(""".version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry bulk(.param .u64 data) {
      | .shared .align 128 .b8 a[256];
      | .shared .align 128 .b8 b[256];
      | .shared .align 8 .b64 bar;
      | .reg .u32 %thread;
      | .reg .u64 %token;
      | .reg .pred %p, %leader;
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | fence.mbarrier_init.release.cluster;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra COPIED;
      | mbarrier.expect_tx.relaxed.cta.shared::cta.b64 [bar], 256;
      | cp.async.bulk.shared::cluster.shared::cta.mbarrier::complete_tx::bytes [b], [a], 256, [bar];
      | mbarrier.arrive.shared::cta.b64 %token, [bar];
      |COPIED:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra COPIED;
      | ret;
      |}
      |""".stripMargin)
    val memory = DeviceMemory(256)
    execute(kernel, 1, 64, Map(argument("data", memory.start.raw)), memory) match {
      case Left(Fault.IllegalInstruction(_)) => ()
      case other                             => fail(s"expected an illegal instruction fault, got $other")
    }
  }

  test("im2col tensor loads walk pixels across the bounding box and into the next image") {
    val kernel = compileKernel(AsyncCases.columnsPtx)
    // NWC with 8 channels, 10 pixels and 2 images; the box spans W positions -1..8.
    val (channels, pixels, images) = (8, 10, 2)
    for (offset <- Vec(0, 1)) {
      val memory = DeviceMemory((channels * pixels * images * 4 + 512).unsafe[Int >= 0])
      for {
        n <- 0 until images
        w <- 0 until pixels
        c <- 0 until channels
      }
        assertEquals(
          memory.writeInt32(memory.start.offset(((n * pixels + w) * channels + c) * 4L), n * 1000 + w * 10 + c),
          Right(())
        )
      val out        = memory.start.raw + channels * pixels * images * 4
      val descriptor = TensorMaps.im2col(
        memory.start.raw,
        TensorMap.Element.S32,
        Vec(channels.toLong, pixels.toLong, images.toLong),
        Vec(channels * 4L, channels * pixels * 4L),
        Vec(-1),
        Vec(-1),
        channels,
        16
      )
      assertEquals(
        execute(
          kernel,
          1,
          128,
          Map(
            map("map", descriptor),
            argument("out", out),
            (("offset": NonEmpty[String]), Argument.fromLong(offset.toLong))
          ),
          memory
        ),
        Right(())
      )
      for {
        pixel <- 0 until 16
        c     <- 0 until channels
      } {
        val n        = if (pixel < 10) 0 else 1
        val position = if (pixel < 10) pixel - 1 else pixel - 11
        val w        = position + offset
        val expected = if (w >= 0 && w < pixels) n * 1000 + w * 10 + c else 0
        assertEquals(
          memory.ints(from = channels * pixels * images * 4 + (pixel * channels + c) * 4, count = 1),
          Vec(expected)
        )
      }
    }
  }

  test("tensormap.replace edits a staged map that a fenced copy publishes for tensor loads") {
    val kernel = compileKernel(AsyncCases.replacePtx)
    val memory = DeviceMemory(128 + 64 + 64)
    val global = memory.start.raw
    val base   = global + 128
    val out    = base + 64
    for (index <- 0 until 16)
      assertEquals(memory.writeInt32(memory.start.offset(128 + index * 4L), index + 7), Right(()))
    val original = TensorMaps.tiled(0x100000L, TensorMap.Element.U32, Vec(64L), Vec.empty, Vec(4))
    assertEquals(
      execute(
        kernel,
        1,
        32,
        Map(map("map", original), argument("global", global), argument("base", base), argument("out", out)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.ints(from = 192, count = 16), Vec.tabulate(16)(index => if (index < 12) index + 7 else 0))
    val published = memory.readBytes(memory.start, 128).toOption.get
    val decoded   = TensorMap.decode(Vec.toArray(published)).toOption.get
    assertEquals((decoded.address, decoded.box, decoded.dimensions), (base, Vec(16), Vec(12L)))
  }

  test("mbarrier drops, transactions, noComplete arrivals, bulk fills and single-device multimem") {
    val kernel = compileKernel(AsyncCases.formsPtx(noComplete = true))
    val memory = DeviceMemory(76)
    assertEquals(memory.writeInt32(memory.start.offset(64), 7), Right(()))
    assertEquals(memory.writeInt32(memory.start.offset(72), 10), Right(()))
    assertEquals(execute(kernel, 1, 1, Map(argument("out", memory.start.raw)), memory), Right(()))
    assertEquals(memory.ints(from = 0, count = 9), Vec(3, 2, 2, 0, 0, 0, 1, 1, 0))
    assertEquals(memory.ints(from = 64, count = 3), Vec(7, 7, 15))
  }
}
