package net.kurobako.gemstone.isa.nvidia.fixtures

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import scala.util.Random

/** tcgen05 kernels, their inputs and their expected results, shared by the PTX suites and the SASS suites that run
  * ptxas's output for them. Every kernel assembles with CUDA 13.3 `ptxas -arch=sm_100a` (`sm_103a` for tcgen05.ld.red);
  * expected tensor memory layouts follow the PTX ISA 9.4 figures (section 9.7.18).
  */
object TensorMemoryCases {

  val movementPtx: String =
    """.version 8.7
      |.target sm_100a
      |.address_size 64
      |.visible .entry movement(.param .u64 out) {
      | .shared .align 16 .b32 slot;
      | .reg .u64 %out, %offset, %at;
      | .reg .u32 %t, %warp, %taddr, %base, %fill, %v<8>, %r<8>, %p<2>;
      | .reg .pred %first;
      | ld.param.u64 %out, [out];
      | mov.u32 %t, %tid.x;
      | shr.u32 %warp, %t, 5;
      | setp.eq.u32 %first, %warp, 0;
      | @%first tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [slot], 64;
      | @%first tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;
      | tcgen05.fence::before_thread_sync;
      | bar.sync 0;
      | tcgen05.fence::after_thread_sync;
      | ld.shared.b32 %taddr, [slot];
      | shl.b32 %base, %warp, 21;
      | add.u32 %base, %taddr, %base;
      | or.b32 %fill, %t, 0x7700;
      | tcgen05.st.sync.aligned.32x32b.x8.b32 [%base], {%fill, %fill, %fill, %fill, %fill, %fill, %fill, %fill};
      | mad.lo.u32 %v0, %t, 16, 0;
      | add.u32 %v1, %v0, 1;
      | add.u32 %v2, %v0, 2;
      | add.u32 %v3, %v0, 3;
      | tcgen05.st.sync.aligned.16x256b.x1.b32 [%base], {%v0, %v1, %v2, %v3};
      | add.u32 %v4, %v0, 0x10000;
      | add.u32 %v5, %v0, 0x10001;
      | tcgen05.st.sync.aligned.16x128b.x1.b32 [%base+4], {%v4, %v5};
      | add.u32 %v6, %v0, 0x20000;
      | tcgen05.st.sync.aligned.16x64b.x1.b32 [%base+0x100006], {%v6};
      | add.u32 %v7, %v0, 0x30000;
      | tcgen05.st.sync.aligned.16x32bx2.x1.b32 [%base+0x100000], 7, {%v7};
      | tcgen05.wait::st.sync.aligned;
      | tcgen05.ld.sync.aligned.32x32b.x8.b32 {%r0, %r1, %r2, %r3, %r4, %r5, %r6, %r7}, [%base];
      | tcgen05.ld.sync.aligned.16x32bx2.x1.pack::16b.b32 {%p0}, [%base+0x100000], 3;
      | tcgen05.st.sync.aligned.16x64b.x1.unpack::16b.b32 [%base+0x100004], {%v0};
      | tcgen05.ld.sync.aligned.16x128b.x1.b32 {%p1, %v7}, [%base+0x100004];
      | tcgen05.wait::ld.sync.aligned;
      | mul.wide.u32 %offset, %t, 40;
      | add.u64 %at, %out, %offset;
      | st.global.v2.u32 [%at], {%r0, %r1};
      | st.global.v2.u32 [%at+8], {%r2, %r3};
      | st.global.v2.u32 [%at+16], {%r4, %r5};
      | st.global.v2.u32 [%at+24], {%r6, %r7};
      | st.global.v2.u32 [%at+32], {%p0, %p1};
      | tcgen05.fence::before_thread_sync;
      | bar.sync 0;
      | @%first tcgen05.dealloc.cta_group::1.sync.aligned.b32 %taddr, 64;
      | ret;
      |}
      |""".stripMargin

  /** The ten words each of 128 threads writes: its lane's eight columns, then the packed and 16x128b loads. */
  lazy val movementExpected: Vec[Vec[Long]] = {
    // the CTA's tensor memory by (lane, column), as each figure places a thread's registers
    val columns                                                        = 16
    val cells                                                          = Array.ofDim[Long](128 * columns)
    def cell(lane: Int, column: Int): Int                              = lane * columns + column
    def low(lane: Int, column: Int): Long                              = cells(cell(lane, column)) & 0xffffL
    inline def threads(inline function: (Int, Int, Int) => Unit): Unit =
      (0 until 128).foreach(thread => function(thread, 32 * (thread / 32), thread % 32))
    threads((thread, quarter, lane) =>
      (0 until 8).foreach(column => cells(cell(quarter + lane, column)) = thread | 0x7700L)
    )
    // 16x256b: row r of the first eight holds threads 4r to 4r+3, two registers each; rows 8 on hold r2 and r3
    threads((thread, quarter, lane) =>
      (0 until 4).foreach(register =>
        cells(cell(quarter + lane / 4 + 8 * (register / 2), 2 * (lane % 4) + register % 2)) = thread * 16L + register
      )
    )
    // 16x128b from column 4: row r holds threads 4r to 4r+3 in r0, and row r+8 the same threads' r1
    threads((thread, quarter, lane) =>
      (0 until 2).foreach(register =>
        cells(cell(quarter + lane / 4 + 8 * register, 4 + lane % 4)) = thread * 16L + 0x10000L + register
      )
    )
    // 16x64b from lane 16 and column 6: row r holds threads 4r and 4r+2, row r+8 threads 4r+1 and 4r+3
    threads((thread, quarter, lane) =>
      cells(cell(quarter + 16 + lane / 4 + 8 * (lane % 2), 6 + lane / 2 % 2)) = thread * 16L + 0x20000L
    )
    // 16x32bx2 from lane 16: threads 0 to 15 at the address, 16 to 31 at the address plus the split offset 7
    threads((thread, quarter, lane) =>
      cells(cell(quarter + 16 + lane % 16, if (lane < 16) 0 else 7)) = thread * 16L + 0x30000L
    )
    val loaded = Vec.tabulate(128) { thread =>
      val quarter = 32 * (thread / 32)
      val lane    = thread % 32
      val packed  = if (lane < 16) 0 else 3
      Vec.tabulate(8)(column => cells(cell(quarter + lane, column))) :+
        (low(quarter + 16 + lane % 16, packed) | (low(quarter + 16 + lane % 16, packed + 1) << 16))
    }
    // 16x64b.unpack::16b from column 4: each register's halves go to adjacent columns
    threads { (thread, quarter, lane) =>
      val row    = quarter + 16 + lane / 4 + 8 * (lane % 2)
      val column = 4 + 2 * (lane / 2                   % 2)
      cells(cell(row, column)) = (thread * 16L) & 0xffffL
      cells(cell(row, column + 1)) = (thread * 16L) >>> 16
    }
    Vec.tabulate(128)(thread =>
      loaded(thread) :+ cells(cell(32 * (thread / 32) + 16 + thread % 32 / 4, 4 + thread % 4))
    )
  }

  /** A one-CTA kernel that copies a shared memory image of `bytes` bytes, fills its 128 lanes of `columns` tensor
    * memory columns from `cells`, runs `lines` from thread 0 and commits them to an mbarrier, then writes the columns
    * back to `out`: thread t moves lane t. The descriptors in `adesc` and `bdesc` gain the shared address of the image
    * and of its second half; `extra`, its low half `%mask` and `%xd`, `extra` plus the image's shared address, are free
    * for the lines to use.
    */
  def productPtx(lines: String*): String =
    s""".version 8.7
       |.target sm_100a
       |.address_size 64
       |.visible .entry mma(.param .u64 out, .param .u64 image, .param .u64 cells, .param .u64 adesc, .param .u64 bdesc,
       | .param .u32 idesc, .param .u32 bytes, .param .u32 columns, .param .u32 enable, .param .u64 extra)
       |{
       | .shared .align 1024 .b8 tiles[32768];
       | .shared .align 8 .b64 bar;
       | .shared .align 16 .b32 slot;
       | .reg .u64 %out, %image, %cells, %at, %offset, %smem64;
       | .reg .b64 %ad, %bd, %extra, %xd;
       | .reg .u32 %t, %warp, %base, %i, %v, %columns, %bytes, %enable, %smem, %row, %col, %cell;
       | .reg .b32 %idesc, %taddr, %mask;
       | .reg .b32 %t256, %t260, %t264, %t384;
       | .reg .pred %first, %leader, %loop, %done, %acc;
       | ld.param.u64 %out, [out];
       | ld.param.u64 %image, [image];
       | ld.param.u64 %cells, [cells];
       | ld.param.b64 %ad, [adesc];
       | ld.param.b64 %bd, [bdesc];
       | ld.param.b64 %extra, [extra];
       | ld.param.b32 %idesc, [idesc];
       | ld.param.u32 %bytes, [bytes];
       | ld.param.u32 %columns, [columns];
       | ld.param.u32 %enable, [enable];
       | cvt.u32.u64 %mask, %extra;
       | setp.ne.u32 %acc, %enable, 0;
       | mov.u32 %t, %tid.x;
       | shr.u32 %warp, %t, 5;
       | setp.eq.u32 %first, %warp, 0;
       | setp.eq.u32 %leader, %t, 0;
       | mov.u32 %smem, tiles;
       | shr.u32 %v, %smem, 4;
       | and.b32 %v, %v, 0x3fff;
       | cvt.u64.u32 %smem64, %v;
       | add.u64 %ad, %ad, %smem64;
       | add.u64 %bd, %bd, %smem64;
       | add.u64 %bd, %bd, 1024;
       | add.u64 %xd, %extra, %smem64;
       | mul.lo.u32 %i, %t, 4;
       |COPY:
       | setp.lt.u32 %loop, %i, %bytes;
       | @!%loop bra COPIED;
       | cvt.u64.u32 %offset, %i;
       | add.u64 %at, %image, %offset;
       | ld.global.u32 %v, [%at];
       | add.u32 %row, %smem, %i;
       | st.shared.u32 [%row], %v;
       | add.u32 %i, %i, 512;
       | bra COPY;
       |COPIED:
       | @%first tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [slot], 512;
       | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
       | fence.proxy.async.shared::cta;
       | fence.mbarrier_init.release.cluster;
       | tcgen05.fence::before_thread_sync;
       | bar.sync 0;
       | tcgen05.fence::after_thread_sync;
       | ld.shared.b32 %taddr, [slot];
       | add.u32 %t256, %taddr, 256;
       | add.u32 %t260, %taddr, 260;
       | add.u32 %t264, %taddr, 264;
       | add.u32 %t384, %taddr, 384;
       | shl.b32 %base, %warp, 21;
       | add.u32 %base, %taddr, %base;
       | mov.u32 %col, 0;
       |FILL:
       | setp.lt.u32 %loop, %col, %columns;
       | @!%loop bra FILLED;
       | mad.lo.u32 %cell, %t, %columns, %col;
       | mul.wide.u32 %offset, %cell, 4;
       | add.u64 %at, %cells, %offset;
       | ld.global.u32 %v, [%at];
       | add.u32 %row, %base, %col;
       | tcgen05.st.sync.aligned.32x32b.x1.b32 [%row], {%v};
       | add.u32 %col, %col, 1;
       | bra FILL;
       |FILLED:
       | tcgen05.wait::st.sync.aligned;
       | tcgen05.fence::before_thread_sync;
       | bar.sync 0;
       | tcgen05.fence::after_thread_sync;
       |${lines.map(line => s" @%leader $line").mkString("\n")}
       | @%leader tcgen05.commit.cta_group::1.mbarrier::arrive::one.shared::cluster.b64 [bar];
       |WAIT:
       | mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], 0;
       | @!%done bra WAIT;
       | tcgen05.fence::after_thread_sync;
       | mov.u32 %col, 0;
       |READ:
       | setp.lt.u32 %loop, %col, %columns;
       | @!%loop bra DONE;
       | add.u32 %row, %base, %col;
       | tcgen05.ld.sync.aligned.32x32b.x1.b32 {%v}, [%row];
       | tcgen05.wait::ld.sync.aligned;
       | mad.lo.u32 %cell, %t, %columns, %col;
       | mul.wide.u32 %offset, %cell, 4;
       | add.u64 %at, %out, %offset;
       | st.global.u32 [%at], %v;
       | add.u32 %col, %col, 1;
       | bra READ;
       |DONE:
       | tcgen05.fence::before_thread_sync;
       | bar.sync 0;
       | @%first tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;
       | @%first tcgen05.dealloc.cta_group::1.sync.aligned.b32 %taddr, 512;
       | ret;
       |}
       |""".stripMargin

  /** The shared memory image, 16 KiB of A then 16 KiB of B, and the tensor memory, by lane and column, of a product. */
  class Setup(val columns: Int) {
    private val bytes: Array[Byte]                        = Array.ofDim[Byte](32768)
    private val words: Array[Long]                        = Array.ofDim[Long](128 * columns)
    def image: Vec[Byte]                                  = Vec.from(bytes)
    def cells: Vec[Long]                                  = Vec.from(words)
    def cell(lane: Int, column: Int): Long                = words(lane * columns + column)
    def update(lane: Int, column: Int, value: Long): Unit = words(lane * columns + column) = value & 0xffffffffL
    def put(offset: Int, value: Long, count: Int): Unit   =
      LittleEndian.write(value, bytes, offset, count.unsafe[Int in 0 ~ 8])
    def merge(offset: Int, value: Long, shift: Int, width: Int): Unit =
      bytes(offset) =
        ((bytes(offset) & ~(((1 << width) - 1) << shift)) | ((value.toInt & ((1 << width) - 1)) << shift)).toByte
  }

  /** A run of [[productPtx]] with `lines`: its inputs and the tensor memory it should read back. */
  case class Product(
      name: String,
      lines: Vec[String],
      setup: Setup,
      adesc: Long,
      bdesc: Long,
      idesc: Long,
      enable: Boolean,
      extra: Long,
      expected: Vec[Long]
  ) {
    def ptx: String = productPtx(lines*)
  }

  /** Byte offsets of the canonical layouts: K-major rows of eight 16-byte chunks swizzled across 128 bytes as TMA's
    * 128-byte swizzle stores a box, or core matrices of eight 16-byte rows without swizzling.
    */
  def swizzled128(row: Int, byte: Int): Int =
    MatrixDescriptor.swizzled((row % 8) * 128L + (row / 8) * 1024L + byte, 128).toInt
  // callers name the offsets
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def interleaved(row: Int, byte: Int, leading: Int, stride: Int): Int =
    (row % 8) * 16 + (row / 8) * stride + (byte / 16) * leading + byte % 16

  /** The instruction descriptor fields of Table 51. */
  // callers name the fields
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def instruction(d: Int, a: Int, b: Int, m: Int, n: Int, others: Long = 0L): Long =
    (d.toLong << 4) | (a.toLong << 7) | (b.toLong << 10) | ((n.toLong >>> 3) << 17) | ((m.toLong >>> 4) << 24) | others

  def single(value: Double): Long = java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
  def half(value: Double): Long   = IEEE754.fromDouble(value, 16)
  def e2m1(value: Double): Long   = Minifloat.E2M1.fromDouble(value)

  private def f16Product(enable: Boolean): Product = {
    val (m, n, k)                        = (128, 64, 16)
    val setup                            = Setup(n)
    def a(row: Int, column: Int): Double = ((row * 3 + column * 5) % 7 - 3).toDouble
    def b(row: Int, column: Int): Double = ((row * 2 + column * 7) % 5 - 2).toDouble / 2
    def c(row: Int, column: Int): Double = (row - column).toDouble
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup.put(swizzled128(row, column * 2), Minifloat.BFloat16.fromDouble(a(row, column)), 2)
    for {
      row    <- 0 until n
      column <- 0 until k
    }
      setup.put(16384 + swizzled128(row, column * 2), Minifloat.BFloat16.fromDouble(b(row = column, column = row)), 2)
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(row, column) = single(c(row, column))
    Product(
      "f16",
      Vec("tcgen05.mma.cta_group::1.kind::f16 [%taddr], %ad, %bd, %idesc, %acc;"),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 1, a = 1, b = 1, m = m, n = n),
      enable,
      0L,
      Vec.tabulate(m * n) { index =>
        val (row, column) = (index / n, index % n)
        single((if (enable) c(row, column) else 0.0) + (0 until k).map(i => a(row, i) * b(i, column)).sum)
      }
    )
  }

  private def tf32Product: Product = {
    val (m, n, k)                        = (64, 32, 8)
    val setup                            = Setup(n)
    def a(row: Int, column: Int): Double = ((row + 2 * column) % 9 - 4).toDouble
    def b(row: Int, column: Int): Double = ((3 * row + column) % 5 - 2).toDouble
    def c(row: Int, column: Int): Double = 8.0 * (row % 5 - column % 3)
    // M-major without swizzling: core matrices of four M-adjacent elements by eight K rows, LBO 512 bytes along K and
    // SBO 128 bytes along M
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup.put((row % 4) * 4 + (row / 4) * 128 + (column % 8) * 16 + (column / 8) * 512, single(a(row, column)), 4)
    for {
      row    <- 0 until n
      column <- 0 until k
    }
      setup.put(
        16384 + interleaved(row, column * 4, leading = 128, stride = 256),
        single(b(row = column, column = row)),
        4
      )
    def lane(row: Int): Int = 32 * (row / 16) + row % 16
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(lane(row), column) = single(c(row, column))
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(lane(row) * n + column) = single(c(row, column) / 8 + (0 until k).map(i => a(row, i) * b(i, column)).sum)
    Product(
      "tf32",
      Vec("tcgen05.mma.cta_group::1.kind::tf32 [%taddr], %ad, %bd, %idesc, %acc, 3;"),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 512, stride = 128, mode = 0),
      Descriptors.tensorMemory(start = 0, leading = 128, stride = 256, mode = 0),
      instruction(d = 1, a = 2, b = 2, m = m, n = n, others = 1L << 15),
      true,
      0L,
      Vec.from(expected)
    )
  }

  private def f8Product: Product = {
    val (m, n, k)                        = (128, 32, 32)
    val setup                            = Setup(512)
    def a(row: Int, column: Int): Double = ((row + column * 3) % 7 - 3).toDouble
    def b(row: Int, column: Int): Double = Vec(0.0, 0.5, -1.0, 1.5, -2.0, 3.0, 4.0, -6.0)((row * 5 + column) % 8)
    def c(row: Int, column: Int): Double = ((row * 7 + column) % 11 - 5).toDouble
    // A in tensor memory from column 256: four E4M3 bytes to a word, low first
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup(row, 256 + column / 4) = setup.cell(row, 256 + column / 4) |
        (Minifloat.E4M3.fromDouble(a(row, column)) << (8 * (column % 4)))
    // B K-major without swizzling: each 16-byte chunk holds sixteen E2M1 nibbles, low first, then eight bytes of padding
    for {
      row    <- 0 until n
      column <- 0 until k
    }
      setup.merge(
        16384 + interleaved(row, column / 16 * 16 + column % 16 / 2, leading = 128, stride = 256),
        e2m1(b(row = column, column = row)),
        4 * (column % 2),
        4
      )
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(row, column) = half(c(row, column))
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m if row % 32 != 0 && row % 32 != 31
      column <- 0 until n
    }
      expected(row * 512 + column) = half(c(row, column) + (0 until k).map(i => a(row, i) * b(i, column)).sum)
    Product(
      "f8f6f4",
      Vec("tcgen05.mma.cta_group::1.kind::f8f6f4 [%taddr], [%t256], %bd, %idesc, {%mask, %mask, %mask, %mask}, %acc;"),
      setup,
      0L,
      Descriptors.tensorMemory(start = 0, leading = 128, stride = 256, mode = 0),
      instruction(d = 0, a = 0, b = 5, m = m, n = n),
      true,
      0x80000001L,
      Vec.from(expected)
    )
  }

  private def i8Product: Product = {
    val (m, n, k)                      = (128, 16, 32)
    val setup                          = Setup(n)
    def a(row: Int, column: Int): Long = ((row * 37 + column * 11) % 256 - 128).toLong
    def b(row: Int, column: Int): Long = ((row * 13 + column * 29) % 256).toLong
    def c(row: Int, column: Int): Long =
      if (row % 3 == 0) 0x7fff0000L else if (row % 3 == 1) -0x7fff0000L else row - column
    for {
      row    <- 0 until m
      column <- 0 until k
    } setup.put(swizzled128(row, column), a(row, column), 1)
    for {
      row    <- 0 until n
      column <- 0 until k
    } setup.put(16384 + swizzled128(row, column), b(row = column, column = row), 1)
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(row, column) = c(row, column)
    // as mma.sync's measured saturation does, the running sum clamps after each 16 products as well as at the end
    def clamp(sum: Long): Long = math.max(Int.MinValue.toLong, math.min(Int.MaxValue.toLong, sum))
    Product(
      "i8",
      Vec("tcgen05.mma.cta_group::1.kind::i8 [%taddr], %ad, %bd, %idesc, %acc;"),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 2, a = 1, b = 0, m = m, n = n, others = 1L << 3),
      true,
      0L,
      Vec.tabulate(m * n) { index =>
        val (row, column) = (index / n, index % n)
        val products      = (0 until k).map(i => a(row, i) * b(i, column))
        clamp(products.drop(16).sum + clamp(c(row, column) + products.take(16).sum)) & 0xffffffffL
      }
    )
  }

  private def mxProduct: Product = {
    val (m, n, k)                        = (128, 32, 32)
    val setup                            = Setup(512)
    def a(row: Int, column: Int): Double = ((row * 5 + column) % 9 - 4).toDouble / 2
    def b(row: Int, column: Int): Double = Vec(0.0, 0.5, -1.0, 1.5, -2.0, 3.0, 0.75, -0.25)((row + column * 3) % 8)
    def c(row: Int, column: Int): Double = (row                % 4 - column % 6).toDouble
    def sa(row: Int): Int                = 125 + row           % 5
    def sb(column: Int): Int             = 126 + column        % 3
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup.put(swizzled128(row, column), Minifloat.E4M3.fromDouble(a(row, column)), 1)
    // B K-major without swizzling: each 16-byte chunk holds sixteen six-bit E2M3 values, low first, then padding
    for {
      row    <- 0 until n
      column <- 0 until k
    } {
      val bit   = 6 * (column % 16)
      val chunk = 16384 + interleaved(row, column / 16 * 16, leading = 128, stride = 256)
      val code  = Minifloat.E2M3.fromDouble(b(row = column, column = row))
      setup.merge(chunk + bit / 8, code, bit % 8, math.min(6, 8 - bit % 8))
      if (bit % 8 > 2) setup.merge(chunk + bit / 8 + 1, code >>> (8 - bit % 8), 0, bit % 8 - 2)
    }
    // scale A in tensor memory from column 256: row r of each 32 at lane r, in byte 1 of column 256 + r / 32
    for (row <- 0 until m) setup(row, 256 + row / 32) = sa(row).toLong << 8
    // scale B in shared memory, one 16-byte row per column of B: tcgen05.cp replicates it to lanes r + 32w, byte 2
    for (column <- 0 until n) setup.put(16384 + 8192 + 16 * column + 2, sb(column).toLong, 1)
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(row, column) = single(c(row, column))
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(row * 512 + column) = single(
        c(row, column) +
          (0 until k).map(i =>
            a(row, i) * math.pow(2, sa(row) - 127) * b(i, column) * math.pow(2, sb(column) - 127)
          ).sum
      )
    for (lane <- 0 until 128) expected(lane * 512 + 260) = sb(lane % 32).toLong << 16
    Product(
      "mxf8f6f4",
      Vec(
        "tcgen05.cp.cta_group::1.32x128b.warpx4 [%t260], %xd;",
        "tcgen05.mma.cta_group::1.kind::mxf8f6f4.block_scale.scale_vec::1X [%taddr], %ad, %bd, %idesc, [%t256], [%t260], %acc;"
      ),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 128, stride = 256, mode = 0),
      (2L << 4) | (0L << 7) | (3L << 10) | ((n.toLong >>> 3) << 17) | (1L << 23) | ((m.toLong >>> 7) << 27) | (1L << 29),
      true,
      Descriptors.tensorMemory(start = 16384 + 8192, leading = 16, stride = 128, mode = 0),
      Vec.from(expected)
    )
  }

  private def nvf4Product: Product = {
    val (m, n, k)                           = (128, 16, 64)
    val setup                               = Setup(512)
    def a(row: Int, column: Int): Double    = Vec(0.0, 0.5, 1.0, -1.5, 2.0, -3.0, 4.0, 6.0)((row * 3 + column) % 8)
    def b(row: Int, column: Int): Double    = Vec(1.0, -0.5, 0.0, 1.5, -2.0, 3.0)((row + column * 5) % 6)
    def sa(row: Int, block: Int): Double    = Vec(0.5, 1.0, 2.0, 1.5)((row + block) % 4)
    def sb(column: Int, block: Int): Double = Vec(1.0, 0.25, 2.0)((column * 2 + block) % 3)
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup.merge(swizzled128(row, column / 2), e2m1(a(row, column)), 4 * (column % 2), 4)
    for {
      row    <- 0 until n
      column <- 0 until k
    }
      setup.merge(16384 + swizzled128(row, column / 2), e2m1(b(row = column, column = row)), 4 * (column % 2), 4)
    for {
      row   <- 0 until m
      block <- 0 until 4
    }
      setup(row, 256 + row / 32) = setup.cell(row, 256 + row / 32) |
        (Minifloat.E4M3.fromDouble(sa(row, block)) << (8 * block))
    for {
      lane  <- 0 until 128
      block <- 0 until 4
    } {
      val column = lane % 32
      if (column < n)
        setup(lane, 264) = setup.cell(lane, 264) | (Minifloat.E4M3.fromDouble(sb(column, block)) << (8 * block))
    }
    for {
      row    <- 0 until m
      column <- 0 until n
    } setup(row, column) = single(0.25 * (row % 3))
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(row * 512 + column) = single(
        0.25 * (row % 3) + (0 until k).map(i => a(row, i) * sa(row, i / 16) * b(i, column) * sb(column, i / 16)).sum
      )
    Product(
      "mxf4nvf4",
      Vec(
        "tcgen05.mma.cta_group::1.kind::mxf4nvf4.block_scale.scale_vec::4X [%taddr], %ad, %bd, %idesc, [%t256], [%t264], %acc;"
      ),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      (1L << 7) | (1L << 10) | ((n.toLong >>> 3) << 17) | ((m.toLong >>> 7) << 27),
      true,
      0L,
      Vec.from(expected)
    )
  }

  private def wsProduct: Product = {
    val (m, n, k)                        = (32, 64, 16)
    val setup                            = Setup(16)
    def a(row: Int, column: Int): Double = ((row + column)     % 5 - 2).toDouble
    def b(row: Int, column: Int): Double = ((row * 3 + column) % 7 - 3).toDouble
    for {
      row    <- 0 until m
      column <- 0 until k
    } setup.put(swizzled128(row, column * 2), half(a(row, column)), 2)
    for {
      row    <- 0 until n + 2
      column <- 0 until k
    } setup.put(16384 + swizzled128(row, column * 2), half(b(row = column, column = row)), 2)
    // Example 4 of the zero-column mask descriptor: start counts {1, 2, 1, 0}, first spans {0, 0, 1, 1} for sub-masks 3
    // to 0, skip span 2, use span 3 and a column shift of 2
    val zeroes = (1L << 24) | (2L << 16) | (1L << 8) | (1L << 33) | (1L << 32) | (1L << 39) | (2L << 40) | (3L << 48) |
      (2L << 56)
    def zeroed(column: Int): Boolean = {
      val mask  = column / 16
      val start = Vec(0, 1, 2, 1)(mask)
      // a sub-mask runs from its first span, three set bits then four clear ones, its first run cut by its start count
      val pattern = LazyList.iterate(Vec(true, true, false, false)(mask))(!_)
        .flatMap(bit => Vec.fill(if (bit) 3 else 4)(bit)).drop(start)
      pattern(column % 16)
    }
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected((32 * (column / 16) + row) * 16 + column % 16) =
        single(if (zeroed(column)) 0.0 else (0 until k).map(i => a(row, i) * b(i, column + 2)).sum)
    Product(
      "ws",
      Vec("tcgen05.mma.ws.cta_group::1.kind::f16 [%taddr], %ad, %bd, %idesc, %acc, %extra;"),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 1, a = 0, b = 0, m = m, n = n),
      false,
      zeroes,
      Vec.from(expected)
    )
  }

  private def copiesProduct: Product = {
    val setup  = Setup(512)
    val random = Random(5)
    random.nextBytes(32768).zipWithIndex.foreach((value, offset) => setup.put(offset, value.toLong, 1))
    for {
      lane   <- 0 until 128
      column <- 0 until 512
    } setup(lane, column) = random.nextInt().toLong
    val expected                                       = Array.from(setup.cells)
    def set(lane: Int, column: Int, value: Long): Unit = expected(lane * 512 + column) = value
    for {
      lane   <- 31 to 1 by -1
      column <- 384 until 392
    } set(lane, column, setup.cell(lane - 1, column))
    def row(offset: Int): Seq[Byte]      = setup.image.slice(offset, offset + 16)
    def six(bytes: Seq[Byte]): Seq[Byte] = (0 until 16).map { element =>
      val bits =
        (0 until 12).foldLeft(BigInt(0))((value, index) => value | (BigInt(bytes(index) & 0xff) << (8 * index)))
      ((bits >> (6 * element)) & 0x3f).toByte
    }
    def four(bytes: Seq[Byte]): Seq[Byte] =
      (0 until 16).map(element => (((bytes(element / 2) >> (4 * (element % 2))) & 0xf) << 2).toByte)
    def words(lane: Int, column: Int, bytes: Seq[Byte]): Unit =
      (0 until 4).foreach(word => set(lane, column + word, LittleEndian.decode(4)(index => bytes(4 * word + index))))
    for {
      r     <- 0 until 128
      chunk <- 0 until 2
    } words(r, 256 + 4 * chunk, row(swizzled128(r, 16 * chunk)))
    for {
      r    <- 0 until 64
      lane <- Vec(r, r + 64)
    } words(lane, 264, six(row(16384 + 16 * r)))
    for (r <- 0 until 128) words(r, 268, four(row(16384 + 4096 + 16 * r)))
    for {
      r    <- 0 until 64
      lane <- Vec(r % 32 + 64 * (r / 32), r % 32 + 64 * (r / 32) + 32)
    }
      words(lane, 272, row(16384 + 16 * r))
    for {
      r     <- 0 until 4
      chunk <- 0 until 2
    } words(64 + r, 276 + 4 * chunk, row(swizzled128(r, 16 * chunk)))
    Product(
      "copies",
      Vec(
        "tcgen05.shift.cta_group::1.down [%t384];",
        "tcgen05.cp.cta_group::1.128x256b [%t256], %ad;",
        "tcgen05.cp.cta_group::1.64x128b.warpx2::02_13.b8x16.b6x16_p32 [%t264], %bd;",
        "tcgen05.cp.cta_group::1.128x128b.b8x16.b4x16_p64 [%t264+4], %xd;",
        "tcgen05.cp.cta_group::1.64x128b.warpx2::01_23 [%t264+8], %bd;",
        "tcgen05.cp.cta_group::1.4x256b [%t264+0x40000c], %ad;"
      ),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 128, mode = 0),
      0L,
      true,
      Descriptors.tensorMemory(start = 16384 + 4096, leading = 16, stride = 128, mode = 0),
      Vec.from(expected)
    )
  }

  private def ashiftProduct: Product = {
    val (m, n, k)                        = (128, 16, 16)
    val setup                            = Setup(512)
    def a(row: Int, column: Int): Double = ((row + 3 * column) % 7 - 3).toDouble
    def b(row: Int, column: Int): Double = ((2 * row + column) % 5 - 2).toDouble
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      setup(row, 256 + column / 2) = setup.cell(row, 256 + column / 2) | (half(a(row, column)) << (16 * (column % 2)))
    for {
      row    <- 0 until n
      column <- 0 until k
    } setup.put(16384 + swizzled128(row, column * 2), half(b(row = column, column = row)), 2)
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(row * 512 + column) = single((0 until k).map(i => a(row, i) * b(i, column)).sum)
    for {
      row    <- 1 until m
      column <- 256 until 264
    } expected(row * 512 + column) = setup.cell(row - 1, column)
    Product(
      "ashift",
      Vec("tcgen05.mma.cta_group::1.kind::f16.ashift [%taddr], [%t256], %bd, %idesc, %acc;"),
      setup,
      0L,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 1, a = 0, b = 0, m = m, n = n),
      false,
      0L,
      Vec.from(expected)
    )
  }

  /** The 2-bit positions of the two kept elements of each four-wide chunk, as sparse metadata nibbles hold them. */
  private val pairs = Vec((0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3))

  private def sparseHalfProduct: Product = {
    val (m, n, k)                              = (128, 32, 32)
    val setup                                  = Setup(512)
    def kept(row: Int, chunk: Int): (Int, Int) = pairs((row * 5 + chunk) % 6)
    def a(row: Int, packed: Int): Double       = ((row + packed * 3) % 7 - 3).toDouble
    def b(row: Int, column: Int): Double       = ((row * 3 + column) % 5 - 2).toDouble
    for {
      row    <- 0 until m
      packed <- 0 until k / 2
    } setup.put(swizzled128(row, packed * 2), half(a(row, packed)), 2)
    for {
      row    <- 0 until n
      column <- 0 until k
    } setup.put(16384 + swizzled128(row, column * 2), half(b(row = column, column = row)), 2)
    // selector 1 reads column 257: rows r and r + 8 of each 16 share lanes, chunks 4 to 7 eight lanes on
    for {
      row   <- 0 until m
      chunk <- 0 until 8
    } {
      val local  = row                                                           % 32
      val lane   = 32 * (row / 32) + (local / 16) * 16 + (chunk / 4) * 8 + local % 8
      val nibble = (local                                                        % 16 / 8) * 4 + chunk % 4
      val (x, y) = kept(row, chunk)
      setup(lane, 257) = setup.cell(lane, 257) | (((y << 2) | x).toLong << (4 * nibble))
    }
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(row * 512 + column) = single((0 until k / 2).map { packed =>
        val (x, y) = kept(row, packed / 2)
        a(row, packed) * b(4 * (packed / 2) + (if (packed % 2 == 0) x else y), column)
      }.sum)
    Product(
      "sparseHalf",
      Vec("tcgen05.mma.sp.cta_group::1.kind::f16 [%taddr], %ad, %bd, [%t256], %idesc, %acc;"),
      setup,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 1, a = 0, b = 0, m = m, n = n, others = 1L | (1L << 2)),
      false,
      0L,
      Vec.from(expected)
    )
  }

  private def sparseByteProduct: Product = {
    val (m, n, k)                              = (128, 16, 64)
    val setup                                  = Setup(512)
    def kept(row: Int, chunk: Int): (Int, Int) = pairs((row + chunk * 7) % 6)
    def a(row: Int, packed: Int): Long         = ((row * 11 + packed * 5) % 256 - 128).toLong
    def b(row: Int, column: Int): Long         = ((row * 7 + column * 3)  % 256 - 128).toLong
    for {
      row    <- 0 until m
      packed <- 0 until k / 2
    }
      setup(row, 264 + packed / 4) =
        setup.cell(row, 264 + packed / 4) | ((a(row, packed) & 0xffL) << (8 * (packed % 4)))
    for {
      row    <- 0 until n
      column <- 0 until k
    } setup.put(16384 + swizzled128(row, column), b(row = column, column = row), 1)
    for {
      row   <- 0 until m
      chunk <- 0 until 16
    } {
      val (x, y) = kept(row, chunk)
      setup(row, 256 + chunk / 8) = setup.cell(row, 256 + chunk / 8) | (((y << 2) | x).toLong << (4 * (chunk % 8)))
    }
    val expected = Array.from(setup.cells)
    for {
      row    <- 0 until m
      column <- 0 until n
    }
      expected(row * 512 + column) = (0 until k / 2).map { packed =>
        val (x, y) = kept(row, packed / 2)
        a(row, packed) * b(4 * (packed / 2) + (if (packed % 2 == 0) x else y), column)
      }.sum & 0xffffffffL
    Product(
      "sparseByte",
      Vec("tcgen05.mma.sp.cta_group::1.kind::i8 [%taddr], [%t264], %bd, [%t256], %idesc, %acc;"),
      setup,
      0L,
      Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
      instruction(d = 2, a = 1, b = 1, m = m, n = n, others = 1L << 2),
      false,
      0L,
      Vec.from(expected)
    )
  }

  /** Every product run, one kernel per distinct name. */
  lazy val products: Vec[Product] = Vec(
    f16Product(enable = true),
    f16Product(enable = false),
    tf32Product,
    f8Product,
    i8Product,
    mxProduct,
    nvf4Product,
    wsProduct,
    copiesProduct,
    ashiftProduct,
    sparseHalfProduct,
    sparseByteProduct
  )

  val reducePtx: String =
    """.version 8.8
      |.target sm_103a
      |.address_size 64
      |.visible .entry reduce(.param .u64 out) {
      | .shared .align 16 .b32 slot;
      | .reg .u64 %out, %at, %offset;
      | .reg .u32 %t;
      | .reg .b32 %taddr, %v<4>, %r<4>, %a, %b, %c, %d;
      | ld.param.u64 %out, [out];
      | mov.u32 %t, %tid.x;
      | tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [slot], 32;
      | tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;
      | ld.shared.b32 %taddr, [slot];
      | mul.wide.u32 %offset, %t, 32;
      | add.u64 %at, %out, %offset;
      | ld.global.v4.b32 {%v0, %v1, %v2, %v3}, [%at];
      | tcgen05.st.sync.aligned.32x32b.x4.b32 [%taddr], {%v0, %v1, %v2, %v3};
      | tcgen05.wait::st.sync.aligned;
      | tcgen05.ld.red.sync.aligned.32x32b.x4.max.abs.NaN.f32 {%r0, %r1, %r2, %r3}, %a, [%taddr];
      | tcgen05.ld.red.sync.aligned.32x32b.x4.min.f32 {%r0, %r1, %r2, %r3}, %b, [%taddr];
      | tcgen05.ld.red.sync.aligned.32x32b.x4.min.s32 {%r0, %r1, %r2, %r3}, %c, [%taddr];
      | tcgen05.ld.red.sync.aligned.16x32bx2.x2.max.u32 {%r0, %r1}, %d, [%taddr], 2;
      | tcgen05.wait::ld.sync.aligned;
      | st.global.v4.b32 [%at+16], {%a, %b, %c, %d};
      | tcgen05.dealloc.cta_group::1.sync.aligned.b32 %taddr, 32;
      | ret;
      |}
      |""".stripMargin

  private def bits(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL

  /** The four words each of 32 threads stores before the reductions. */
  lazy val reduceInputs: Vec[Vec[Long]] = Vec.tabulate(32) { thread =>
    Vec.tabulate(4)(column =>
      (thread, column) match {
        case (3, 1) => 0x7fc00000L
        case (5, 2) => bits(-0.0f)
        case (5, _) => bits(0.0f)
        case (_, _) if (thread + column) % 3 == 0 => bits(-(thread * 1.5f + column))
        case _ => bits(thread * 0.25f - column * 2)
      }
    )
  }

  /** Each thread's maximum magnitude with NaN propagated, minimum, signed minimum and the unsigned maximum its half of
    * `.16x32bx2` loads.
    */
  lazy val reduceExpected: Vec[Vec[Long]] = Vec.tabulate(32) { thread =>
    def float(value: Long): Float = java.lang.Float.intBitsToFloat(value.toInt)
    val row                       = reduceInputs(thread).map(value => float(value))
    val numbers                   = row.filterNot(_.isNaN)
    // NVIDIA's canonical single-precision NaN
    val magnitude = if (row.exists(_.isNaN)) 0x7fffffffL else bits(row.map(math.abs).max)
    val minimum   = numbers.minBy(value => (value, if (bits(value) == bits(-0.0f)) 0 else 1))
    val signed    = reduceInputs(thread).map(value => Bits.signExtend(value, 32)).min & 0xffffffffL
    val split     = reduceInputs(thread % 16).slice(if (thread < 16) 0 else 2, if (thread < 16) 2 else 4).max
    Vec(magnitude, bits(minimum), signed, split)
  }

  val pairPtx: String =
    """.version 8.7
      |.target sm_100a
      |.address_size 64
      |.visible .entry pair(.param .u64 out, .param .u64 image, .param .u64 cells, .param .u64 adesc, .param .u64 bdesc,
      | .param .u32 idesc)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 1024 .b8 tiles[32768];
      | .shared .align 8 .b64 bar;
      | .shared .align 16 .b32 slot;
      | .reg .u64 %out, %image, %cells, %at, %offset, %smem64, %part;
      | .reg .b64 %ad, %bd;
      | .reg .u32 %t, %warp, %base, %i, %v, %smem, %row, %col, %cell, %rank;
      | .reg .b32 %idesc, %taddr;
      | .reg .b16 %mask;
      | .reg .pred %first, %leader, %issuer, %loop, %done, %acc;
      | ld.param.u64 %out, [out];
      | ld.param.u64 %image, [image];
      | ld.param.u64 %cells, [cells];
      | ld.param.b64 %ad, [adesc];
      | ld.param.b64 %bd, [bdesc];
      | ld.param.b32 %idesc, [idesc];
      | mov.u32 %rank, %cluster_ctarank;
      | mul.wide.u32 %part, %rank, 32768;
      | add.u64 %image, %image, %part;
      | mul.wide.u32 %part, %rank, 32768;
      | add.u64 %cells, %cells, %part;
      | add.u64 %out, %out, %part;
      | setp.eq.u32 %acc, %rank, %rank;
      | mov.u32 %t, %tid.x;
      | shr.u32 %warp, %t, 5;
      | setp.eq.u32 %first, %warp, 0;
      | setp.eq.u32 %leader, %t, 0;
      | setp.eq.u32 %issuer, %rank, 0;
      | and.pred %issuer, %issuer, %leader;
      | mov.u32 %smem, tiles;
      | shr.u32 %v, %smem, 4;
      | and.b32 %v, %v, 0x3fff;
      | cvt.u64.u32 %smem64, %v;
      | add.u64 %ad, %ad, %smem64;
      | add.u64 %bd, %bd, %smem64;
      | add.u64 %bd, %bd, 1024;
      | mul.lo.u32 %i, %t, 4;
      |COPY:
      | setp.lt.u32 %loop, %i, 32768;
      | @!%loop bra COPIED;
      | cvt.u64.u32 %offset, %i;
      | add.u64 %at, %image, %offset;
      | ld.global.u32 %v, [%at];
      | add.u32 %row, %smem, %i;
      | st.shared.u32 [%row], %v;
      | add.u32 %i, %i, 512;
      | bra COPY;
      |COPIED:
      | @%first tcgen05.alloc.cta_group::2.sync.aligned.shared::cta.b32 [slot], 64;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | fence.proxy.async.shared::cta;
      | fence.mbarrier_init.release.cluster;
      | tcgen05.fence::before_thread_sync;
      | barrier.cluster.arrive.release;
      | barrier.cluster.wait.acquire;
      | tcgen05.fence::after_thread_sync;
      | ld.shared.b32 %taddr, [slot];
      | shl.b32 %base, %warp, 21;
      | add.u32 %base, %taddr, %base;
      | mov.u32 %col, 0;
      |FILL:
      | setp.lt.u32 %loop, %col, 64;
      | @!%loop bra FILLED;
      | mad.lo.u32 %cell, %t, 64, %col;
      | mul.wide.u32 %offset, %cell, 4;
      | add.u64 %at, %cells, %offset;
      | ld.global.u32 %v, [%at];
      | add.u32 %row, %base, %col;
      | tcgen05.st.sync.aligned.32x32b.x1.b32 [%row], {%v};
      | add.u32 %col, %col, 1;
      | bra FILL;
      |FILLED:
      | tcgen05.wait::st.sync.aligned;
      | tcgen05.fence::before_thread_sync;
      | barrier.cluster.arrive.release;
      | barrier.cluster.wait.acquire;
      | tcgen05.fence::after_thread_sync;
      | mov.b16 %mask, 3;
      | @%issuer tcgen05.mma.cta_group::2.kind::f16 [%taddr], %ad, %bd, %idesc, %acc;
      | @%issuer tcgen05.commit.cta_group::2.mbarrier::arrive::one.shared::cluster.multicast::cluster.b64 [bar], %mask;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], 0;
      | @!%done bra WAIT;
      | tcgen05.fence::after_thread_sync;
      | mov.u32 %col, 0;
      |READ:
      | setp.lt.u32 %loop, %col, 64;
      | @!%loop bra DONE;
      | add.u32 %row, %base, %col;
      | tcgen05.ld.sync.aligned.32x32b.x1.b32 {%v}, [%row];
      | tcgen05.wait::ld.sync.aligned;
      | mad.lo.u32 %cell, %t, 64, %col;
      | mul.wide.u32 %offset, %cell, 4;
      | add.u64 %at, %out, %offset;
      | st.global.u32 [%at], %v;
      | add.u32 %col, %col, 1;
      | bra READ;
      |DONE:
      | tcgen05.fence::before_thread_sync;
      | barrier.cluster.arrive.release;
      | barrier.cluster.wait.acquire;
      | @%first tcgen05.relinquish_alloc_permit.cta_group::2.sync.aligned;
      | @%first tcgen05.dealloc.cta_group::2.sync.aligned.b32 %taddr, 64;
      | ret;
      |}
      |""".stripMargin

  /** The pair's shared memory images, CTA 0's then CTA 1's, its initial D, row by row, and the D it should read back: M =
    * 256 rows of A split 128 to a CTA and N = 64 columns of B split 32 to a CTA.
    */
  lazy val pair: (images: Vec[Byte], cells: Vec[Long], expected: Vec[Long], idesc: Long) = {
    val (m, n, k)                                     = (256, 64, 16)
    def a(row: Int, column: Int): Double              = ((row * 7 + column) % 9 - 4).toDouble
    def b(row: Int, column: Int): Double              = ((row + column * 3) % 5 - 2).toDouble
    def c(row: Int, column: Int): Double              = (row                % 7 - column % 4).toDouble
    val images                                        = Array.ofDim[Byte](65536)
    def put(cta: Int, offset: Int, value: Long): Unit = LittleEndian.write(value, images, cta * 32768 + offset, 2)
    for {
      row    <- 0 until m
      column <- 0 until k
    }
      put(row / 128, swizzled128(row % 128, column * 2), Minifloat.BFloat16.fromDouble(a(row, column)))
    for {
      row    <- 0 until n
      column <- 0 until k
    }
      put(
        row / 32,
        16384 + swizzled128(row % 32, column * 2),
        Minifloat.BFloat16.fromDouble(b(row = column, column = row))
      )
    (
      images = Vec.from(images),
      cells = Vec.tabulate(m * n)(index => single(c(index / n, index % n))),
      expected = Vec.tabulate(m * n) { index =>
        val (row, column) = (index / n, index % n)
        single(c(row, column) + (0 until k).map(i => a(row, i) * b(i, column)).sum)
      },
      idesc = instruction(d = 1, a = 1, b = 1, m = m, n = n)
    )
  }

  val releasePtx: String =
    """.version 9.3
      |.target sm_100a
      |.address_size 64
      |.visible .entry release(.param .u64 out) {
      | .shared .align 16 .b8 buf[32];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %out;
      | .reg .b32 %v, %buf, %bar, %r;
      | .reg .b16 %h;
      | .reg .b64 %w, %token;
      | .reg .b128 %q;
      | .reg .pred %p;
      | ld.param.u64 %out, [out];
      | mov.b32 %v, 7;
      | mov.b16 %h, 0x1234;
      | mov.b64 %w, 0x100000003;
      | mov.b128 %q, {%w, %w};
      | st.async.release.gpu.global.b32 [%out], %v;
      | st.async.release.sys.b16 [%out+4], %h;
      | st.async.mmio.release.sys.global.u64 [%out+8], %w;
      | red.async.release.gpu.global.add.u32 [%out+16], %v;
      | red.async.release.sys.add.s64 [%out+24], %w;
      | multimem.st.async.release.gpu.global.b16 [%out+32], %h;
      | multimem.red.async.release.gpu.global.add.u64 [%out+40], %w;
      | mov.u32 %buf, buf;
      | mov.u32 %bar, bar;
      | st.shared.v4.b32 [%buf], {%v, %v, %v, %v};
      | st.shared.v4.b32 [%buf+16], {%v, %v, %v, %v};
      | mbarrier.init.shared::cta.b64 [%bar], 1;
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [%bar], 16;
      | st.async.shared::cluster.mbarrier::complete_tx::bytes.b128 [%buf+16], %q, [%bar];
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [%bar], 0;
      | @!%p bra WAIT;
      | fence.proxy.async.shared::cta;
      | multimem.cp.async.bulk.global.shared::cta.bulk_group [%out+64], [%buf], 32;
      | multimem.cp.reduce.async.bulk.global.shared::cta.bulk_group.add.u32 [%out+96], [%buf], 32;
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group 0;
      | ret;
      |}
      |""".stripMargin

  /** The 128 bytes `releasePtx`'s output starts as, as 64-bit words, and what it should end as. */
  lazy val release: (initial: Vec[Long], expected: Vec[Long]) = {
    val wide = 0x100000003L
    (
      initial = Vec(0L, 0L, 1000L, -5L, 0L, 9L, 0L, 0L, 0L, 0L, 0L, 0L) ++
        Vec.tabulate(4)(index => ((101L + 2 * index) << 32) | (100L + 2 * index)),
      expected = Vec(0x123400000007L, wide, 1007L, wide - 5, 0x1234L, wide + 9, 0L, 0L) ++
        Vec(0x700000007L, 0x700000007L, wide, wide) ++ Vec(
          ((101L + 7) << 32) | (100L + 7),
          ((103L + 7) << 32) | (102L + 7),
          ((105L + 1) << 32) | (104L + 3),
          ((107L + 1) << 32) | (106L + 3)
        )
    )
  }

  /** Warp-aggregated noComplete arrivals whose tokens a thread keeps, which ptxas lowers through SYNCS.ARRIVE.TMASK and
    * RPCMOV scratch moves.
    */
  val noCompletePtx: String =
    """.version 8.8
      |.target sm_100a
      |.address_size 64
      |.visible .entry arrivals(.param .u64 out) {
      | .shared .align 8 .b64 bar;
      | .reg .b64 %token, %out, %at, %offset;
      | .reg .b32 %bar, %n, %t, %pending;
      | ld.param.u64 %out, [out];
      | mov.u32 %t, %tid.x;
      | add.u32 %n, %t, 1;
      | mov.u32 %bar, bar;
      | mbarrier.init.shared::cta.b64 [%bar], 1000;
      | bar.sync 0;
      | mbarrier.arrive.noComplete.release.cta.shared::cta.b64 %token, [%bar], %n;
      | mbarrier.pending_count.b64 %pending, %token;
      | mul.wide.u32 %offset, %t, 4;
      | add.u64 %at, %out, %offset;
      | st.global.u32 [%at], %pending;
      | ret;
      |}
      |""".stripMargin

  /** The pending counts the eight threads' tokens record: each arrives with its own count after the threads before it.
    */
  lazy val noCompleteExpected: Vec[Long] = Vec.tabulate(8)(thread => 1000L - (0 until thread).map(_ + 1L).sum)
}
