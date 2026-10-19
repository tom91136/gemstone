package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.elf.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

// ptxas 13.3 output for the PTX the PTX VM suites run: SYNCS mbarriers, UBLKCP/UBLKRED bulk copies, UTMALDG/UTMASTG/
// UTMAREDG tensor copies, STAS/REDAS and cluster shared memory through SR_SWINHI and the c0 cluster fields.
class AsyncSuite extends munit.FunSuite {
  private enum Parameter {
    case Buffer(region: AllocationId)
    case Bytes(bytes: Vec[Byte])
  }

  private val Swizzles = Vec(0, 3)

  /** Runs `kernel` from its listing with ptxas's parameter offsets and, for cluster kernels, its cluster shape. */
  private def run(
      listing: String,
      kernel: String,
      sm: Int,
      offsets: Vec[Int],
      memory: Harness.Memory,
      parameters: Vec[Parameter],
      blocks: Int,
      threads: Int,
      cluster: Int = 1
  )(using munit.Location): Either[Fault, Unit] = {
    val function = Listing.function(Fixture.listing(listing), kernel, sm).value
    val base     = parameterBase(sm)
    val sizes    = parameters.map {
      case Parameter.Buffer(_)    => 8
      case Parameter.Bytes(bytes) => bytes.size
    }
    val size  = offsets.zip(sizes).map(_ + _).max
    val cubin = Cubin.Module(
      sm.unsafe[Int >= 35],
      Vec(
        Cubin.Section(0, s".text.$kernel", 1, 0L, 0, 0, 255 << 24, Vec.empty),
        Cubin.Section(
          1,
          s".nv.constant0.$kernel",
          1,
          2L,
          (base + size).unsafe[Int >= 0],
          0,
          0,
          Vec.fill(base + size)(0)
        ),
        // ptxas's bank 4 of driver helper addresses, which sm_120 copies into other cluster CTAs call through.
        Cubin.Section(2, ".nv.constant4", 1, 2L, 8, 0, 0, Vec.fill(8)(0)),
        Cubin.Section(3, ".rela.nv.constant4", SectionType.RelocationWithAddend, 0L, 24, 0, 2, Vec.fill(24)(0))
      ),
      Vec(Cubin.Kernel(
        kernel.unsafe[NonEmpty[String]],
        0,
        base.unsafe[Int >= 0],
        size.unsafe[Int >= 0],
        offsets.zip(sizes).zipWithIndex.map { case ((offset, width), ordinal) =>
          Cubin.Parameter(ordinal.unsafe[Int >= 0], offset.unsafe[Int >= 0], width.unsafe[Int > 0])
        },
        requiredCluster = LaunchGeometry.Dimensions(cluster.unsafe[Int > 0], 1, 1)
      )),
      Vec.empty
    )
    for {
      program <-
        VMCompiler.compile(cubin, cubin.kernels.head, Vec(function)).left.map(error => Fault.Unsupported(error))
      task <- KernelTask.validated(
        kernel.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions(threads.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions(blocks.unsafe[Int > 0], 1, 1),
        parameters.map {
          case Parameter.Buffer(region) => KernelTask.Argument.Memory(region, 0L)
          case Parameter.Bytes(bytes)   => KernelTask.Argument.Scalar(bytes.unsafe[NonEmpty[Vec[Byte]]])
        }
      ).left.map(Fault.InvalidRequest(_))
      _ <- memory.run(program, task)
    } yield ()
  }

  private def scalar(value: Long, size: Int): Parameter =
    Parameter.Bytes(LittleEndian.toVec(value, size.unsafe[Int in 0 ~ 8]))

  private def address(memory: Harness.Memory, region: AllocationId): Long = memory.address(region, 0L).value.raw

  private def words(memory: Harness.Memory, region: AllocationId, from: Int, count: Int): Vec[Int] =
    Vec.tabulate(count)(index => memory.getInt(region, (from + index * 4).unsafe[Int >= 0]))

  for (sm <- Vec(90, 100, 120))
    test(s"sm_$sm bulk copies, stores and reductions complete through SYNCS transactions") {
      val memory = Harness.Memory()
      val input  = Vec.tabulate(64)(index => index * 3 - 50)
      val data   = memory.allocate(Vec.fill(1284)(0.toByte))
      input.zipWithIndex.foreach { (value, index) =>
        memory.putInt(data, (index * 4).unsafe[Int >= 0], value)
        memory.putInt(data, (512 + index * 4).unsafe[Int >= 0], 1000)
      }
      assertEquals(run(s"bulkSm${sm}a", "bulk", sm, Vec(0), memory, Vec(Parameter.Buffer(data)), 1, 64), Right(()))
      val incremented = input.map(_ + 1)
      assertEquals(words(memory, data, from = 256, count = 64), incremented)
      assertEquals(words(memory, data, from = 512, count = 64), incremented.map(_ + 1000))
      assertEquals(words(memory, data, from = 768, count = 64), incremented.map(math.min(_, 0)))
      assertEquals(words(memory, data, from = 1024, count = 64), incremented)
      assertEquals(words(memory, data, from = 1280, count = 1), Vec(1))
    }

  for {
    sm      <- Vec(90, 100, 120)
    swizzle <- Swizzles
  }
    test(s"sm_$sm tensor tile copies with swizzle $swizzle fill, store and reduce boxes") {
      val memory                            = Harness.Memory()
      val (w, h)                            = (40, 20)
      val width                             = if (swizzle == 0) 32 else 4 << swizzle
      val height                            = 256 / width
      val (x, y)                            = (w - width / 2, h - height / 2)
      def value(column: Int, row: Int): Int = java.lang.Float.floatToRawIntBits(row * 100f + column)
      val source                            = memory.allocate(Vec.from((0 until w * h).flatMap(index =>
        LittleEndian.toVec(value(index % w, index / w).toLong & 0xffffffffL, 4)
      )))
      val stored  = memory.allocate(Vec.fill(w * h * 4)(0.toByte))
      val reduced = memory.allocate(Vec.from((0 until w * h).flatMap(_ => LittleEndian.toVec(0x3f800000L, 4))))
      val out     = memory.allocate(Vec.fill(1024)(0.toByte))
      def describe(region: AllocationId) = Parameter.Bytes(Vec.unsafeWrapArray(TensorMaps.tiled(
        address(memory, region),
        TensorMap.Element.F32,
        Vec(w.toLong, h.toLong),
        Vec(w * 4L),
        Vec(width, height),
        swizzle = swizzle
      )))
      val offsets = if (sm >= 100) Vec(0, 128, 256, 384, 392, 396) else Vec(48, 176, 304, 432, 440, 444)
      assertEquals(
        run(
          s"tileSm${sm}a",
          "tile",
          sm,
          offsets,
          memory,
          Vec(
            describe(source),
            describe(stored),
            describe(reduced),
            Parameter.Buffer(out),
            scalar(x.toLong, 4),
            scalar(y.toLong, 4)
          ),
          1,
          256
        ),
        Right(())
      )
      for (word <- 0 until 256) {
        val logical       = TensorMaps.unswizzle(swizzle, word * 4L) / 4
        val (column, row) = (x + (logical % width).toInt, y + (logical / width).toInt)
        val inside        = column < w && row < h
        assertEquals(
          words(memory, out, from = word * 4, count = 1),
          Vec(if (inside) value(column, row) else 0),
          clues(word)
        )
        if (inside) {
          val index = (row * w + column) * 4
          assertEquals(words(memory, stored, from = index, count = 1), Vec(value(column, row)))
          assertEquals(
            words(memory, reduced, from = index, count = 1),
            Vec(java.lang.Float.floatToRawIntBits(java.lang.Float.intBitsToFloat(value(column, row)) + 1f))
          )
        }
      }
    }

  for (sm <- Vec(90, 100, 120))
    test(s"sm_$sm im2col tensor loads unpack their offsets from the uniform word") {
      val memory                     = Harness.Memory()
      val (channels, pixels, images) = (8, 10, 2)
      val tensor                     = memory.allocate(Vec.from((0 until channels * pixels * images).flatMap { index =>
        val (n, w, c) = (index / (channels * pixels), (index / channels) % pixels, index % channels)
        LittleEndian.toVec((n * 1000 + w * 10 + c).toLong, 4)
      }))
      val out        = memory.allocate(Vec.fill(512)(0.toByte))
      val descriptor = TensorMaps.im2col(
        address(memory, tensor),
        TensorMap.Element.S32,
        Vec(channels.toLong, pixels.toLong, images.toLong),
        Vec(channels * 4L, channels * pixels * 4L),
        Vec(-1),
        Vec(-1),
        channels,
        16
      )
      val offsets = if (sm >= 100) Vec(0, 128, 136) else Vec(48, 176, 184)
      assertEquals(
        run(
          s"columnsSm${sm}a",
          "columns",
          sm,
          offsets,
          memory,
          Vec(Parameter.Bytes(Vec.unsafeWrapArray(descriptor)), Parameter.Buffer(out), scalar(1L, 2)),
          1,
          128
        ),
        Right(())
      )
      for {
        pixel <- 0 until 16
        c     <- 0 until channels
      } {
        val n        = if (pixel < 10) 0 else 1
        val w        = if (pixel < 10) pixel else pixel - 10
        val expected = if (w >= 0 && w < pixels) n * 1000 + w * 10 + c else 0
        assertEquals(words(memory, out, from = (pixel * channels + c) * 4, count = 1), Vec(expected))
      }
    }

  // ptxas lowers tensormap.replace to shared-memory bit-field updates, so the map's layout must be the hardware's.
  for (sm <- Vec(90, 100))
    test(s"sm_$sm tensormap.replace as ptxas lowers it edits the fields tensor copies read") {
      val memory  = Harness.Memory()
      val global  = memory.allocate(Vec.fill(128)(0.toByte))
      val base    = memory.allocate(Vec.from((0 until 16).flatMap(index => LittleEndian.toVec(index + 7L, 4))))
      val out     = memory.allocate(Vec.fill(64)(0.toByte))
      val map     = TensorMaps.tiled(0x100000L, TensorMap.Element.U32, Vec(64L), Vec.empty, Vec(4))
      val offsets = if (sm >= 100) Vec(0, 128, 136, 144) else Vec(48, 176, 184, 192)
      assertEquals(
        run(
          s"replaceSm${sm}a",
          "replace",
          sm,
          offsets,
          memory,
          Vec(
            Parameter.Bytes(Vec.unsafeWrapArray(map)),
            scalar(address(memory, global), 8),
            scalar(address(memory, base), 8),
            scalar(address(memory, out), 8)
          ),
          1,
          32
        ),
        Right(())
      )
      assertEquals(
        words(memory, out, from = 0, count = 16),
        Vec.tabulate(16)(index => if (index < 12) index + 7 else 0)
      )
    }

  for (sm <- Vec(90, 100, 120))
    test(s"sm_$sm cluster CTAs exchange shared memory through the SR_SWINHI window") {
      val memory = Harness.Memory()
      val out    = memory.allocate(Vec.fill(4 * 32 * 48)(0.toByte))
      assertEquals(
        run(s"exchangeSm${sm}a", "exchange", sm, Vec(0), memory, Vec(Parameter.Buffer(out)), 4, 32, cluster = 2),
        Right(())
      )
      for {
        block <- 0 until 4
        lane  <- 0 until 32
      } {
        val peer = block ^ 1
        assertEquals(
          words(memory, out, from = (block * 32 + lane) * 48, count = 12),
          Vec(
            peer * 1000 + lane,
            peer * 1000 + lane,
            peer * 1000 + lane,
            block * 1000 + lane + 1,
            peer  % 2,
            block % 2,
            2,
            block / 2,
            2,
            2,
            1,
            block % 2
          ),
          clues(block, lane)
        )
      }
    }

  for (sm <- Vec(90, 100))
    test(s"sm_$sm STAS, REDAS, multicast copies and remote arrivals complete on peer mbarriers") {
      val memory = Harness.Memory()
      val out    = memory.allocate(Vec.from((0 until 16).flatMap(index => LittleEndian.toVec(index + 100L, 4))) ++
        Vec.fill(128)(0.toByte))
      assertEquals(
        run(s"asyncSm${sm}a", "async", sm, Vec(0), memory, Vec(Parameter.Buffer(out)), 4, 64, cluster = 2),
        Right(())
      )
      for (block <- 0 until 4)
        assertEquals(
          words(memory, out, from = 64 + block * 32, count = 4),
          Vec((block ^ 1) % 2 + 10, 5, 102, 103),
          clues(block)
        )
    }

  test("sm_100 mbarrier drops, transactions, copy arrivals, UMEMSETS fills and LDGMC loads") {
    val memory = Harness.Memory()
    val out    = memory.allocate(Vec.fill(76)(0.toByte))
    memory.putInt(out, 64.unsafe[Int >= 0], 7)
    memory.putInt(out, 72.unsafe[Int >= 0], 10)
    assertEquals(run("formsSm100a", "forms", 100, Vec(0), memory, Vec(Parameter.Buffer(out)), 1, 1), Right(()))
    assertEquals(words(memory, out, from = 0, count = 9), Vec(3, 2, 2, 0, 0, 0, 1, 1, 0))
    assertEquals(words(memory, out, from = 64, count = 3), Vec(7, 7, 15))
  }

  test("UGETNEXTWORKID.BROADCAST writes every CTA's response and completes each CTA's mbarrier") {
    val memory = Harness.Memory()
    val out    = memory.allocate(Vec.fill(2 * 32 * 4)(0.toByte))
    assertEquals(run("cancelSm100a", "cancel", 100, Vec(0), memory, Vec(Parameter.Buffer(out)), 2, 32, 2), Right(()))
    assertEquals(words(memory, out, from = 0, count = 64), Vec.fill(64)(2))
  }

  for (sm <- Vec(90, 100))
    test(s"sm_$sm UTMALDG.MULTICAST fills every CTA the mask names") {
      val memory          = Harness.Memory()
      val (width, height) = (16, 8)
      val tensor          =
        memory.allocate(Vec.from((0 until width * height).flatMap(index => LittleEndian.toVec(index + 1000L, 4))))
      val out = memory.allocate(Vec.fill(4 * 64 * 4)(0.toByte))
      val map = TensorMaps.tiled(
        address(memory, tensor),
        TensorMap.Element.U32,
        Vec(width.toLong, height.toLong),
        Vec(width * 4L),
        Vec(16, 4)
      )
      val offsets = if (sm >= 100) Vec(0, 128) else Vec(48, 176)
      assertEquals(
        run(
          s"multicastSm${sm}a",
          "multicast",
          sm,
          offsets,
          memory,
          Vec(Parameter.Bytes(Vec.unsafeWrapArray(map)), Parameter.Buffer(out)),
          4,
          64,
          cluster = 2
        ),
        Right(())
      )
      val expected = Vec.tabulate(64) { index =>
        val (column, row) = (4 + index % 16, 1 + index / 16)
        if (column < width) row * width + column + 1000 else 0
      }
      for (block <- 0 until 4) assertEquals(words(memory, out, from = block * 256, count = 64), expected, clues(block))
    }
}
