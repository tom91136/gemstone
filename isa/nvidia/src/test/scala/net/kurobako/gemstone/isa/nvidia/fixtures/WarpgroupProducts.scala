package net.kurobako.gemstone.isa.nvidia.fixtures

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.nvidia.*

/** Warpgroup products, `wgmma.mma_async` on one warpgroup, as hand-written PTX kernels with their inputs and a CPU
  * product. The operands are placed as the PTX ISA lays them out (9.7.16.5.1): registers by CuTe's SM90 GMMA fragment
  * layouts and shared memory by the canonical layouts and swizzles of the matrix descriptor, both evaluated here from
  * their shapes and strides. The values are small integers, so every product and sum is exact. Each kernel copies a
  * shared memory image in, loads each lane's A fragment, accumulator and metadata words, multiplies and stores D.
  */
object WarpgroupProducts {
  val SharedBytes = 65536

  private val KeptColumns = Vec(Vec(0, 1), Vec(0, 2), Vec(0, 3), Vec(1, 2), Vec(1, 3), Vec(2, 3))

  /** One product: A in registers or at a descriptor with its swizzle, B's swizzle, MN-major operands, negations, the
    * scale-d operand (`1`, `0`, or a predicate `p` or `!p`), a sparse A and `.satfinite`.
    */
  case class Case(
      n: Int,
      d: Type,
      a: Type,
      b: Type,
      registers: Boolean = false,
      swizzleA: Int = 0,
      swizzleB: Int = 0,
      transposeA: Boolean = false,
      transposeB: Boolean = false,
      negateA: Boolean = false,
      negateB: Boolean = false,
      scale: String = "p",
      sparse: Boolean = false,
      saturate: Boolean = false,
      selector: Int = 0,
      overflow: Boolean = false
  ) {
    val k: Int       = a.depth * (if (sparse) 2 else 1)
    val packed: Int  = if (sparse) k / 2 else k
    def name: String =
      s"m64n${n}k$k ${d.name}.${a.name}.${b.name} A ${if (registers) "registers" else s"swizzle $swizzleA"}" +
        s" B swizzle $swizzleB${if (transposeA) " transA" else ""}${if (transposeB) " transB" else ""}" +
        s"${if (negateA) " -A" else ""}${if (negateB) " -B" else ""} scale-d $scale" +
        s"${if (sparse) s" sparse sel $selector" else ""}${if (saturate) " satfinite" else ""}"
  }

  def a(test: Case)(row: Int, column: Int): Long = test.a.value(row * 7 + column * 3 + 1, 7)
  def b(test: Case)(row: Int, column: Int): Long = test.b.value(row * 5 + column * 3 + 2, 5)
  def c(test: Case)(row: Int, column: Int): Long =
    if (test.overflow) Int.MaxValue.toLong - (row + column) % 3 else test.d.value(row + 2 * column, 9)

  /** The two (or, for 32-bit elements, one) columns of each chunk of a sparse A's row that hold its elements. */
  private def kept(test: Case, row: Int, chunk: Int): Vec[Int] =
    if (test.a.bits == 32) Vec((row + chunk) % 2)
    else KeptColumns((row * 3 + chunk)       % 6)

  private def chunkWidth(test: Case): Int = if (test.a.bits == 32) 2 else 4

  /** Column `column` of A's row `row` before packing: zero where a sparse A's metadata skips it. */
  private def logicalA(test: Case)(row: Int, column: Int): Long =
    if (!test.sparse) a(test)(row, column)
    else {
      val chunk = column / chunkWidth(test)
      kept(test, row, chunk).indexOf(column % chunkWidth(test)) match {
        case -1   => 0L
        case slot => a(test)(row, chunk * kept(test, row, chunk).size + slot)
      }
    }

  /** The metadata nibble of chunk `chunk` of row `row`: each kept column's index within the chunk, two bits apiece. */
  private def nibble(test: Case, row: Int, chunk: Int): Long =
    if (test.a.bits == 32) if (kept(test, row, chunk).head == 0) 0x4L else 0xeL
    else kept(test, row, chunk).zipWithIndex.foldLeft(0L)((bits, at) => bits | (at._1.toLong << (2 * at._2)))

  /** Each lane's metadata word, as CuTe's SM90 sparse GMMA `ELayout`s place the nibbles within each warp's rows. */
  private def metadata(test: Case): Vec[Long] = {
    val words = Array.fill(128)(0L)
    for {
      row   <- 0 until 64
      chunk <- 0 until test.k / chunkWidth(test)
    } {
      val bytes = test.a.bits == 8
      val lane  =
        if (bytes) 4 * (row % 8) + row % 16 / 8 + 2 * (chunk / 8) else 4 * (row % 8) + 2 * test.selector + chunk / 4
      val bit = if (bytes) 4 * (chunk % 8) else 16 * (row % 16 / 8) + 4 * (chunk % 4)
      words(row / 16 * 32 + lane) |= nibble(test, row, chunk) << bit
    }
    Vec.from(words)
  }

  def expected(test: Case)(row: Int, column: Int): Long = {
    val signA  = if (test.negateA) -1L else 1L
    val signB  = if (test.negateB) -1L else 1L
    val addend = test.scale match {
      case "0"  => 0L
      case "!p" => 0L
      case _    => c(test)(row, column)
    }
    val sum =
      if (test.a == Type.B1)
        (0 until test.k).count(index => logicalA(test)(row, index) == 1L && b(test)(index, column) == 1L).toLong
      else (0 until test.k).map(index => logicalA(test)(row, index) * b(test)(index, column)).sum * signA * signB
    if (test.saturate) math.min(Int.MaxValue.toLong, addend + sum)
    else if (test.d == Type.S32) (addend + sum).toInt.toLong
    else addend + sum
  }

  /** The canonical layout (9.7.16.5.1.2.1.3) of an `mn` by `k` matrix of `bits`-wide elements, in elements from its
    * start before the swizzle, with the leading and stride byte offsets chosen to pack its core matrices or swizzle
    * atoms one after another.
    */
  private def canonical(bits: Int, mn: Int, k: Int, swizzle: Int, transposed: Boolean)
      : (layout: Layout, leading: Long, stride: Long) = {
    val t                           = 128 / bits
    val width                       = math.max(swizzle / 16, 1)
    def elements(bytes: Long): Long = bytes * 8 / bits
    if (!transposed) {
      val chunks = k / t
      if (swizzle == 0) {
        val (leading, stride) = (128L, 128L * chunks)
        (
          Layout(Vec(8, mn / 8) -> Vec(t, chunks), Vec(t.toLong, elements(stride)) -> Vec(1L, elements(leading))),
          leading,
          stride
        )
      } else {
        val stride = 128L * width
        (
          Layout(Vec(8, mn / 8) -> Vec(t, chunks), Vec((width * t).toLong, elements(stride)) -> Vec(1L, t.toLong)),
          16L,
          stride
        )
      }
    } else if (swizzle == 0) {
      val groups            = math.max(mn / t, 1)
      val (stride, leading) = (128L, 128L * groups)
      (
        Layout(
          Vec(t, 1, groups)                   -> Vec(8, k / 8),
          Vec(1L, t.toLong, elements(stride)) -> Vec(t.toLong, elements(leading))
        ),
        leading,
        stride
      )
    } else {
      val atoms   = math.max(mn / (t * width), 1)
      val leading = 128L * width
      val stride  = leading * atoms
      (
        Layout(
          Vec(t, width, atoms)                 -> Vec(8, k / 8),
          Vec(1L, t.toLong, elements(leading)) -> Vec((width * t).toLong, elements(stride))
        ),
        leading,
        stride
      )
    }
  }

  private def mode(swizzle: Int): Warpgroup.Swizzle = swizzle match {
    case 0   => Warpgroup.Swizzle.Interleave
    case 128 => Warpgroup.Swizzle.B128
    case 64  => Warpgroup.Swizzle.B64
    case _   => Warpgroup.Swizzle.B32
  }

  /** Writes a matrix's elements into the shared memory image at `start`, and returns its descriptor less the start of
    * the image and the end of the bytes it spans.
    */
  private def place(
      image: Array[Byte],
      start: Int,
      tpe: Type,
      mn: Int,
      k: Int,
      swizzle: Int,
      transposed: Boolean
  )(element: (Int, Int) => Long): (descriptor: Long, end: Int) = {
    val (layout, leading, stride) = canonical(tpe.bits, mn, k, swizzle, transposed)
    val cells                     = for {
      row    <- 0 until mn
      column <- 0 until k
    } yield {
      val bit     = layout(row, column) * tpe.bits
      val address = (start + MatrixDescriptor.swizzled(bit / 8, swizzle)).toInt
      (bit, address, tpe.encode(element(row, column)))
    }
    cells.foreach { (bit, address, encoded) =>
      if (tpe.bits == 1) image(address) = (image(address) | (encoded << (bit % 8))).toByte
      else (0 until tpe.bits / 8).foreach(byte => image(address + byte) = (encoded >>> (8 * byte)).toByte)
    }
    val ends = cells.map((_, address, _) => address + math.max(tpe.bits / 8, 1))
    (
      Descriptors.warpgroup(start = start.toLong, leading = leading, stride = stride, swizzle = mode(swizzle)),
      ends.max
    )
  }

  /** The kernel: copy the shared memory image in, load each lane's A fragment, D and metadata, multiply, store D. */
  def kernel(test: Case): String = {
    val registers = 64 * test.n / 128 / (if (test.d.bits == 16) 2 else 1)
    val shape     = s"m64n${test.n}k${test.k}"
    val qualifier = s"${if (test.sparse) ".sp" else ""}.sync.aligned.$shape${if (test.saturate) ".satfinite" else ""}"
    val types     = s".${test.d.name}.${test.a.name}.${test.b.name}${if (test.a == Type.B1) ".and.popc" else ""}"
    val d         = (0 until registers).map(index => s"%d$index").mkString("{", ", ", "}")
    val a         = if (test.registers) "{%a0, %a1, %a2, %a3}" else "%da"
    val sparse    = if (test.sparse) s", %e, ${test.selector}" else ""
    val scale     = test.scale match {
      case "p" | "!p" => "%p"
      case other      => other
    }
    val immediates =
      if (test.a.integer) ""
      else {
        val signs = s", ${if (test.negateA) -1 else 1}, ${if (test.negateB) -1 else 1}"
        if (!test.a.halves) signs
        else if (test.registers) s"$signs, ${if (test.transposeB) 1 else 0}"
        else s"$signs, ${if (test.transposeA) 1 else 0}, ${if (test.transposeB) 1 else 0}"
      }
    val loads = (0 until 4 + registers + 1).map { index =>
      val target = if (index < 4) s"%a$index" else if (index < 4 + registers) s"%d${index - 4}" else "%e"
      s"ld.global.b32 $target, [%r+${index * 4}];"
    }.mkString("\n  ")
    val stores = (0 until registers).map(index => s"st.global.b32 [%o+${index * 4}], %d$index;").mkString("\n  ")
    val words  = 4 + registers + 1
    s""".version 8.7
.target sm_90a
.address_size 64
.visible .entry wgmma(.param .u64 image, .param .u32 size, .param .u64 descA, .param .u64 descB, .param .u64 lanes,
  .param .u64 out, .param .u32 keep)
{
  .reg .b32 %i, %n, %v, %s, %t, %q, %e, %a<4>, %d<$registers>;
  .reg .b64 %g, %w, %da, %db, %start, %r, %o;
  .reg .pred %p, %done;
  .shared .align 1024 .b8 tile[$SharedBytes];
  ld.param.u64 %g, [image];
  ld.param.u32 %n, [size];
  mov.u32 %i, %tid.x;
COPY:
  setp.ge.u32 %done, %i, %n;
  @%done bra COPIED;
  cvt.u64.u32 %w, %i;
  add.u64 %w, %g, %w;
  ld.global.u8 %v, [%w];
  mov.u32 %s, tile;
  add.u32 %s, %s, %i;
  st.shared.u8 [%s], %v;
  add.u32 %i, %i, 128;
  bra COPY;
COPIED:
  bar.sync 0;
  mov.u32 %s, tile;
  cvt.u64.u32 %start, %s;
  and.b64 %start, %start, 0x3ffff;
  shr.u64 %start, %start, 4;
  ld.param.u64 %da, [descA];
  add.u64 %da, %da, %start;
  ld.param.u64 %db, [descB];
  add.u64 %db, %db, %start;
  mov.u32 %t, %tid.x;
  mul.wide.u32 %w, %t, ${words * 4};
  ld.param.u64 %r, [lanes];
  add.u64 %r, %r, %w;
  $loads
  ld.param.u32 %q, [keep];
  setp.ne.u32 %p, %q, 0;
  fence.proxy.async;
  wgmma.fence.sync.aligned;
  wgmma.mma_async$qualifier$types $d, $a, %db$sparse, $scale$immediates;
  wgmma.commit_group.sync.aligned;
  wgmma.wait_group.sync.aligned 0;
  mul.wide.u32 %w, %t, ${registers * 4};
  ld.param.u64 %o, [out];
  add.u64 %o, %o, %w;
  $stores
  ret;
}
"""
  }

  private val floating = Vec(
    (Type.F32, Type.F16, Type.F16),
    (Type.F16, Type.F16, Type.F16),
    (Type.F32, Type.BF16, Type.BF16),
    (Type.F32, Type.TF32, Type.TF32),
    (Type.F32, Type.E4M3, Type.E5M2),
    (Type.F16, Type.E5M2, Type.E4M3)
  )

  val cases: Vec[Case] =
    floating.flatMap((d, a, b) =>
      Vec(
        Case(8, d, a, b),
        Case(24, d, a, b, registers = true, scale = "1", negateA = true),
        Case(64, d, a, b, swizzleA = 128, swizzleB = 128, negateB = true, scale = "!p"),
        Case(16, d, a, b, swizzleA = 64, swizzleB = 32, scale = "0"),
        Case(256, d, a, b, swizzleA = 32, swizzleB = 64, negateA = true, negateB = true)
      ) ++
        (if (a.halves)
           Vec(
             Case(32, d, a, b, transposeA = true, transposeB = true),
             Case(64, d, a, b, swizzleA = 128, swizzleB = 64, transposeA = true, transposeB = true),
             Case(8, d, a, b, swizzleA = 32, swizzleB = 128, transposeB = true, registers = true),
             Case(128, d, a, b, swizzleA = 64, swizzleB = 32, transposeA = true)
           )
         else Vec.empty[Case]) ++
        Vec(
          Case(8, d, a, b, sparse = true),
          Case(32, d, a, b, sparse = true, registers = true, swizzleB = 128, selector = if (a.bits == 8) 0 else 1),
          Case(16, d, a, b, sparse = true, swizzleA = 64, swizzleB = 64, scale = "!p")
        )
    ) ++ Vec(
      Case(8, Type.S32, Type.S8, Type.S8),
      Case(48, Type.S32, Type.U8, Type.S8, registers = true, swizzleB = 128),
      Case(32, Type.S32, Type.S8, Type.U8, swizzleA = 64, swizzleB = 32, scale = "0"),
      Case(256, Type.S32, Type.U8, Type.U8, swizzleA = 128, swizzleB = 128),
      Case(16, Type.S32, Type.U8, Type.U8, overflow = true),
      Case(16, Type.S32, Type.U8, Type.U8, overflow = true, saturate = true),
      Case(24, Type.S32, Type.S8, Type.S8, sparse = true, registers = true),
      Case(16, Type.S32, Type.U8, Type.S8, sparse = true, swizzleA = 128, swizzleB = 64, saturate = true),
      Case(8, Type.S32, Type.B1, Type.B1),
      Case(64, Type.S32, Type.B1, Type.B1, registers = true, swizzleB = 128),
      Case(16, Type.S32, Type.B1, Type.B1, swizzleA = 32, swizzleB = 32, scale = "!p")
    )

  /** A CuTe layout over (row, column) of a matrix: each coordinate's modes, innermost first, with their strides; the
    * outermost mode of each extends as far as the coordinate does.
    */
  case class Layout(shape: (Vec[Int], Vec[Int]), stride: (Vec[Long], Vec[Long])) {
    private def mode(coordinate: Int, shape: Vec[Int], stride: Vec[Long]): Long =
      shape.zip(stride).zipWithIndex.foldLeft((coordinate.toLong, 0L)) { case ((rest, sum), ((size, step), index)) =>
        if (index == shape.size - 1) (0L, sum + rest * step) else (rest / size, sum + rest % size * step)
      }._2
    def apply(row: Int, column: Int): Long = mode(row, shape._1, stride._1) + mode(column, shape._2, stride._2)
  }

  /** A matrix element type: its width, the K of a dense product of it, and its encoding of the small integers here. */
  enum Type(val name: String, val bits: Int, val depth: Int, val integer: Boolean) derives CanEqual {
    case F16  extends Type(name = "f16", bits = 16, depth = 16, integer = false)
    case BF16 extends Type(name = "bf16", bits = 16, depth = 16, integer = false)
    case TF32 extends Type(name = "tf32", bits = 32, depth = 8, integer = false)
    case E4M3 extends Type(name = "e4m3", bits = 8, depth = 32, integer = false)
    case E5M2 extends Type(name = "e5m2", bits = 8, depth = 32, integer = false)
    case F32  extends Type(name = "f32", bits = 32, depth = 0, integer = false)
    case S8   extends Type(name = "s8", bits = 8, depth = 32, integer = true)
    case U8   extends Type(name = "u8", bits = 8, depth = 32, integer = true)
    case S32  extends Type(name = "s32", bits = 32, depth = 0, integer = true)
    case B1   extends Type(name = "b1", bits = 1, depth = 256, integer = true)

    def halves: Boolean = this == F16 || this == BF16

    /** A value from `seed` with `period` values: signed ones centred on zero, unsigned and single bits not. */
    def value(seed: Int, period: Int): Long = this match {
      case B1 => if (seed % 7 < 3) 1L else 0L
      case U8 => (seed % period).toLong
      case _  => (seed % period - period / 2).toLong
    }

    def encode(value: Long): Long = this match {
      case F16  => IEEE754.floatToBinary16(value.toFloat).raw
      case BF16 => Minifloat.BFloat16.fromDouble(value.toDouble)
      // the low 13 bits of a TF32 container are ignored
      case TF32 => (java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL) | 0x1555L
      case E4M3 => Minifloat.E4M3.fromDouble(value.toDouble)
      case E5M2 => Minifloat.E5M2.fromDouble(value.toDouble)
      case F32  => java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
      case B1   => value & 1L
      case _    => value & ((1L << bits) - 1L)
    }

    def decode(bits: Long): Long = this match {
      case F16 => IEEE754.toDouble(bits, 16).toLong
      case F32 => java.lang.Float.intBitsToFloat(bits.toInt).toLong
      case _   => bits.toInt.toLong
    }
  }

  /** Lane `lane`'s accumulator element `index`: CuTe's `CLayout_64xN`, ((4,8,4),(2,2,N/8)):((128,1,16),(64,8,512)) over
    * a column-major 64-row D.
    */
  def accumulator(lane: Int, index: Int): (row: Int, column: Int) = {
    val offset = lane % 4 * 128 + lane / 4 % 8 + lane / 32 * 16 + index % 2 * 64 + index / 2 % 2 * 8 + index / 4 * 512
    (offset % 64, offset / 64)
  }

  /** Each register element of a lane's A fragment and the row and column of A it holds: CuTe's `ALayout_64xK` for the
    * element width over a column-major 64-row A, its values grouped (elements per register, 2, 2).
    */
  case class ALayout(tpe: Type) {
    private val packed = 32 / tpe.bits
    val size: Int      = 4 * packed

    def position(index: Int, lane: Int): (row: Int, column: Int) = {
      val offset = lane % 4 * packed * 64 + lane / 4 % 8 + lane / 32 * 16 + index % packed * 64 +
        index / packed % 2 * 8 +
        index /
        (2 * packed) * packed * 4 * 64
      (offset % 64, offset / 64)
    }
  }

  /** A product's launch: the shared memory image and its size, the descriptors less the image's start, each lane's
    * words (A's fragment, D's registers, the metadata word) and D's registers per lane.
    */
  case class Launch(image: Vec[Byte], descA: Long, descB: Long, lanes: Vec[Long], registers: Int)

  def launch(test: Case): Launch = {
    val registers     = 64 * test.n / 128 / (if (test.d.bits == 16) 2 else 1)
    val image         = Array.ofDim[Byte](SharedBytes)
    val (descA, aEnd) =
      if (test.registers) (0L, 0)
      else place(image, 0, test.a, 64, test.packed, test.swizzleA, test.transposeA)(a(test))
    val (descB, bEnd) =
      place(image, (aEnd + 1023) / 1024 * 1024, test.b, test.n, test.k, test.swizzleB, test.transposeB)((column, row) =>
        b(test)(row, column)
      )
    val size    = (bEnd + 1023) / 1024 * 1024
    val words   = 4 + registers + 1
    val lanes   = Array.fill(128 * words)(0L)
    val sparse  = if (test.sparse) metadata(test) else Vec.fill(128)(0L)
    val aLayout = ALayout(test.a)
    for (lane <- 0 until 128) {
      (0 until aLayout.size).foreach { index =>
        val (row, column) = aLayout.position(index, lane)
        val part          = index % (32 / test.a.bits)
        val register      = index / (32 / test.a.bits)
        lanes(lane * words + register) |= test.a.encode(a(test)(row, column)) << (part * test.a.bits)
      }
      (0 until registers * (32 / test.d.bits)).foreach { index =>
        val (row, column) = accumulator(lane, index)
        val register      = index / (32 / test.d.bits)
        val part          = index % (32 / test.d.bits)
        lanes(lane * words + 4 + register) |= test.d.encode(c(test)(row, column)) << (part * test.d.bits)
      }
      lanes(lane * words + 4 + registers) = sparse(lane)
    }
    Launch(Vec.from(image.take(size)), descA, descB, Vec.from(lanes.map(_ & 0xffffffffL)), registers)
  }

  /** Each element of D the kernel stores that differs from the CPU product, from the 32-bit words it stored. */
  def mismatches(test: Case, registers: Int, stored: Vec[Long]): Vec[String] =
    (for {
      lane  <- 0 until 128
      index <- 0 until registers * (32 / test.d.bits)
    } yield {
      val (row, column)    = accumulator(lane, index)
      val word             = stored(lane * registers + index / (32 / test.d.bits))
      val bits             = (word >>> (index % (32 / test.d.bits) * test.d.bits)) & ((1L << test.d.bits) - 1L)
      val (actual, wanted) = (test.d.decode(bits), expected(test)(row, column))
      Option.when(actual != wanted)(s"D($row, $column) in lane $lane: $actual, expected $wanted")
    }).flatten.toVec
}
