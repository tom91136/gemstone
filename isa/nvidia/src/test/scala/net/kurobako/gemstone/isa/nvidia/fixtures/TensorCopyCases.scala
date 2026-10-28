package net.kurobako.gemstone.isa.nvidia.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*

/** Tensor copies sm_100 adds, their tensor maps and their expected results, shared by the PTX suites and the SASS
  * suites that run ptxas's output for them: `.tile::gather4` and `.tile::scatter4`, `.im2col::w` and `.im2col::w::128`
  * with wHalo and wOffset, `.cta_group::2` completing on the even CTA's mbarrier, and the packed sub-byte element
  * types. Every kernel assembles with CUDA 13.3 `ptxas -arch=sm_100a`; expected layouts follow the PTX ISA 9.4 tensor
  * sections (5.5) and their figures.
  */
object TensorCopyCases {

  /** A tensor of `rows` rows of 8 32-bit words, `100 * row + column` each. */
  def rows(rows: Int): Vec[Long] = Vec.tabulate(rows * 8)(index => 100L * (index / 8) + index % 8)

  /** gather4 reads columns 2 to 5 of rows 5, 2, 9 and 17 of a 16-row tensor into four shared rows, then scatter4 writes
    * them to columns 4 to 7 of rows 1, 3, 4 and 20 of another.
    */
  val gatherPtx: String =
    """.version 9.0
      |.target sm_100a
      |.address_size 64
      |.visible .entry gather(.param .align 64 .b8 load[128], .param .align 64 .b8 store[128], .param .u64 out) {
      | .shared .align 128 .b8 box[64];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %offset, %token;
      | .reg .u32 %thread, %addr, %r, %c, %r0, %r1, %r2, %r3;
      | .reg .pred %p, %leader;
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | bar.sync 0;
      | @!%leader bra WAIT;
      | mov.b64 %map, load;
      | cvta.param.u64 %map, %map;
      | mov.u32 %c, 2;
      | mov.u32 %r0, 5;
      | mov.u32 %r1, 2;
      | mov.u32 %r2, 9;
      | mov.u32 %r3, 17;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile::gather4.mbarrier::complete_tx::bytes [box], [%map, {%c, %r0, %r1, %r2, %r3}], [bar];
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 64;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %offset, %thread, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %r;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra DONE;
      | mov.b64 %map, store;
      | cvta.param.u64 %map, %map;
      | mov.u32 %c, 4;
      | mov.u32 %r0, 1;
      | mov.u32 %r1, 3;
      | mov.u32 %r2, 4;
      | mov.u32 %r3, 20;
      | cp.async.bulk.tensor.2d.global.shared::cta.tile::scatter4.bulk_group [%map, {%c, %r0, %r1, %r2, %r3}], [box];
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group.read 0;
      |DONE:
      | ret;
      |}
      |""".stripMargin

  /** A 32-bit map over `rows` rows of 8 words with 4-column, 1-row boxes. */
  def gatherMap(address: Long, rows: Int): Array[Byte] =
    TensorMaps.tiled(address, TensorMap.Element.U32, Vec(8L, rows.toLong), Vec(32L), Vec(4, 1))

  /** The 16 words of the gathered box and the 20 rows of the scattered tensor. */
  lazy val gather: (box: Vec[Long], scattered: Vec[Long]) = {
    val source = rows(16)
    val box    =
      Vec(5, 2, 9, 17).flatMap(row => Vec.tabulate(4)(column => if (row < 16) source(row * 8 + 2 + column) else 0L))
    val placed = Map(1 -> 0, 3 -> 1, 4 -> 2)
    (
      box = box,
      scattered = Vec.tabulate(20 * 8) { index =>
        val (row, column) = (index / 8, index % 8)
        placed.get(row).filter(_ => column >= 4).fold(0L)(slot => box(slot * 4 + column - 4))
      }
    )
  }

  /** im2col::w and im2col::w::128 loads of an NWC tensor of 4 32-bit channels with the wHalo and wOffset the kernel
    * takes, copied out in shared order.
    */
  val widePtx: String =
    """.version 9.0
      |.target sm_100a
      |.address_size 64
      |.visible .entry wide(.param .align 64 .b8 narrow[128], .param .align 64 .b8 full[128], .param .u64 out, .param .u32 halo, .param .u32 shift) {
      | .shared .align 128 .b8 box[2432];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %offset, %token;
      | .reg .u32 %thread, %addr, %r, %zero, %w, %wide, %halo32, %shift32, %index;
      | .reg .b16 %halo, %shift;
      | .reg .pred %p, %leader, %more;
      | ld.param.u64 %out, [out];
      | ld.param.u32 %halo32, [halo];
      | ld.param.u32 %shift32, [shift];
      | cvt.u16.u32 %halo, %halo32;
      | cvt.u16.u32 %shift, %shift32;
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | bar.sync 0;
      | @!%leader bra WAIT;
      | mov.u32 %zero, 0;
      | mov.u32 %w, 7;
      | mov.u32 %wide, box;
      | add.u32 %wide, %wide, 256;
      | mov.b64 %map, narrow;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.3d.shared::cluster.global.im2col::w.mbarrier::complete_tx::bytes [box], [%map, {%zero, %w, %zero}], [bar], {%halo, %shift};
      | mov.b64 %map, full;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.3d.shared::cluster.global.im2col::w::128.mbarrier::complete_tx::bytes [%wide], [%map, {%zero, %zero, %zero}], [bar], {%halo, %shift};
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 2336;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %index, %thread;
      |COPY:
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %index, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %offset, %index, 4;
      | add.u64 %map, %out, %offset;
      | st.global.u32 [%map], %r;
      | add.u32 %index, %index, 128;
      | setp.lt.u32 %more, %index, 608;
      | @%more bra COPY;
      | ret;
      |}
      |""".stripMargin

  val Channels: Int = 4
  val Width: Int    = 10
  val Images: Int   = 16

  /** Channel `c` of pixel `w` of image `n`. */
  // callers name the coordinates
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def pixel(n: Int, w: Int, c: Int): Long = n * 1000L + w * 10 + c

  def pixels: Vec[Long] = Vec.tabulate(Images * Width * Channels)(index =>
    pixel(n = index / (Width * Channels), w = index / Channels % Width, c = index % Channels)
  )

  /** The im2col::w map of 8 pixels and the im2col::w::128 map, both with W corners 0 and 0. */
  def wideMaps(address: Long): (narrow: Array[Byte], full: Array[Byte]) = {
    def map(mode: TensorMap.Mode) = TensorMaps.im2col(
      address,
      TensorMap.Element.S32,
      Vec(Channels.toLong, Width.toLong, Images.toLong),
      Vec(Channels * 4L, Channels * Width * 4L),
      Vec(0),
      Vec(0),
      Channels,
      8,
      mode = mode
    )
    (narrow = map(TensorMap.Mode.Im2colWide), full = map(TensorMap.Mode.Im2colWide128))
  }

  /** The 608 words the kernel copies out with a wHalo of 2: 8 pixels and the halo from W = 7 of image 0, then from byte
    * 256 the 128 pixels from W = 0 and the halo after every 32, both walks starting `shift` further along W and
    * wrapping to the next image within corners moved as far.
    */
  def wide(shift: Int): Vec[Long] = {
    def walk(start: Int, index: Int): Vec[Long] = {
      val (image, w) = ((start + index) / Width, shift + (start + index) % Width)
      Vec.tabulate(Channels)(c => if (w < Width && image < Images) pixel(n = image, w = w, c = c) else 0L)
    }
    val narrow = Vec.tabulate(8 + 2)(walk(7, _)).flatten
    val main   = Vec.tabulate(128)(walk(0, _)).flatten
    val halos  = Vec.tabulate(4 * 2)(slot => walk(0, 32 * (slot % 4 + 1) + slot / 4)).flatten
    narrow ++ Vec.fill(64 - narrow.size)(0L) ++ main ++ halos
  }

  /** Each CTA of a pair loads its half of an 8-row box with `.cta_group::2`, completing on CTA 0's mbarrier, which CTA
    * 0 waits on before the cluster barrier lets both copy their halves out.
    */
  val pairPtx: String =
    """.version 9.0
      |.target sm_100a
      |.address_size 64
      |.visible .entry pair(.param .align 64 .b8 load[128], .param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 128 .b8 box[128];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %offset, %token;
      | .reg .u32 %thread, %rank, %addr, %r, %zero, %row, %even, %index;
      | .reg .pred %p, %leader, %first;
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | mov.u32 %rank, %cluster_ctarank;
      | setp.eq.u32 %leader, %thread, 0;
      | setp.eq.u32 %first, %rank, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | fence.mbarrier_init.release.cluster;
      | barrier.cluster.arrive.release.aligned;
      | barrier.cluster.wait.acquire.aligned;
      | @!%leader bra WAIT;
      | mov.u32 %even, bar;
      | mapa.shared::cluster.u32 %even, %even, 0;
      | mov.b64 %map, load;
      | cvta.param.u64 %map, %map;
      | mov.u32 %zero, 0;
      | shl.b32 %row, %rank, 2;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes.cta_group::2 [box], [%map, {%zero, %row}], [%even];
      | @%first mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 256;
      |WAIT:
      | @!%first bra SYNC;
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      |SYNC:
      | barrier.cluster.arrive.release.aligned;
      | barrier.cluster.wait.acquire.aligned;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mad.lo.u32 %index, %rank, 32, %thread;
      | mul.wide.u32 %offset, %index, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %r;
      | ret;
      |}
      |""".stripMargin

  /** A 32-bit map over 8 rows of 8 words with 8-column, 4-row boxes. */
  def pairMap(address: Long): Array[Byte] =
    TensorMaps.tiled(address, TensorMap.Element.U32, Vec(8L, 8L), Vec(32L), Vec(8, 4))

  /** Loads of each packed type into shared memory, copied out, and a `.b6p2x16` store of 128 bytes of 6-bit values. */
  val packedPtx: String =
    """.version 9.0
      |.target sm_100a
      |.address_size 64
      |.visible .entry packed(.param .align 64 .b8 four[128], .param .align 64 .b8 padded[128], .param .align 64 .b8 six[128], .param .align 64 .b8 spread[128], .param .u64 out) {
      | .shared .align 128 .b8 box[704];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %offset, %token, %at;
      | .reg .u32 %thread, %addr, %r, %zero, %index, %b;
      | .reg .b16 %byte;
      | .reg .pred %p, %leader, %more;
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | bar.sync 0;
      | @!%leader bra WAIT;
      | mov.u32 %zero, 0;
      | mov.u32 %addr, box;
      | mov.b64 %map, four;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes [%addr], [%map, {%zero, %zero}], [bar];
      | add.u32 %addr, %addr, 64;
      | mov.b64 %map, padded;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes [%addr], [%map, {%zero, %zero}], [bar];
      | add.u32 %addr, %addr, 256;
      | mov.b64 %map, six;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes [%addr], [%map, {%zero, %zero}], [bar];
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 576;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %index, %thread;
      |COPY:
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %index, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %offset, %index, 4;
      | add.u64 %at, %out, %offset;
      | st.global.u32 [%at], %r;
      | add.u32 %index, %index, 32;
      | setp.lt.u32 %more, %index, 144;
      | @%more bra COPY;
      | mov.u32 %index, %thread;
      |FILL:
      | mul.lo.u32 %b, %index, 7;
      | and.b32 %b, %b, 63;
      | cvt.u16.u32 %byte, %b;
      | mov.u32 %addr, box;
      | add.u32 %addr, %addr, 576;
      | add.u32 %addr, %addr, %index;
      | st.shared.u8 [%addr], %byte;
      | add.u32 %index, %index, 32;
      | setp.lt.u32 %more, %index, 128;
      | @%more bra FILL;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra DONE;
      | mov.u32 %addr, box;
      | add.u32 %addr, %addr, 576;
      | mov.b64 %map, spread;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.1d.global.shared::cta.tile.bulk_group [%map, {%zero}], [%addr];
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group.read 0;
      |DONE:
      | ret;
      |}
      |""".stripMargin

  /** Byte `index` of the packed source tensors. */
  def source(index: Int): Int = (index * 37 + 11) & 0xff

  /** The `.b4x16` map over 2 rows of 64 values, the `.b4x16_p64` and `.b6x16_p32` maps over 2 rows of 128, each read
    * whole, and the `.b6p2x16` map over one row of 128, over the tensors at `addresses` in that order.
    */
  def packedMaps(addresses: Vec[Long])
      : (four: Array[Byte], padded: Array[Byte], six: Array[Byte], spread: Array[Byte]) =
    (
      four = TensorMaps.tiled(addresses(0), TensorMap.Element.B4x16, Vec(64L, 2L), Vec(32L), Vec(64, 2)),
      padded = TensorMaps.tiled(addresses(1), TensorMap.Element.B4x16P64, Vec(128L, 2L), Vec(64L), Vec(128, 2)),
      six = TensorMaps.tiled(addresses(2), TensorMap.Element.B6x16, Vec(128L, 2L), Vec(96L), Vec(128, 2)),
      spread = TensorMaps.tiled(addresses(3), TensorMap.Element.B6x16, Vec(128L), Vec.empty, Vec(128))
    )

  /** The 576 bytes of the three loads in shared order, None for the padding the PTX ISA leaves uninitialised, and the
    * 96 bytes the `.b6p2x16` store packs, each value's 6 bits from the least significant.
    */
  lazy val packed: (loaded: Vec[Option[Int]], stored: Vec[Int]) = {
    val four   = Vec.tabulate(64)(index => Some(source(index)))
    val padded = Vec.tabulate(256)(index => Option.when(index % 16 < 8)(source(index / 16 * 8 + index % 16)))
    val six    = Vec.tabulate(256)(index => Option.when(index % 16 < 12)(source(index / 16 * 12 + index % 16)))
    val values = Vec.tabulate(128)(index => (index * 7) & 63)
    val stored = Vec.tabulate(96)(byte =>
      (0 until 8).foldLeft(0)((bits, bit) =>
        bits | (((values((byte * 8 + bit) / 6) >> ((byte * 8 + bit) % 6)) & 1) << bit)
      )
    )
    (loaded = four ++ padded ++ six, stored = stored)
  }
}
