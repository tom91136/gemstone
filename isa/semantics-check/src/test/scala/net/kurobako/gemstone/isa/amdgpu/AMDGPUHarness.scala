package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.isa.check.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.collection.mutable

private[isa] object AMDGPUHarness {

  /** The one active lane of the wave. */
  inline val Lane = 4

  /** What lane `index` holds of a vector register [[Lane]] holds as `value`. */
  def held(value: Long, index: Int): Long =
    if (index == Lane) Bits.low32(value).toLong else Bits.low32(value * 0x9e3779b1L + index * 0x7f4a7c15L).toLong

  /** The byte global memory holds at `address` until it is written. */
  def initial(address: Long): Byte = ((address * 0x9e3779b97f4a7c15L) >>> 56).toByte

  /** The workgroup's LDS allocation, in bytes. */
  inline val LdsBytes = 0x10010

  /** The bytes of a lane's private segment, past which execution faults. */
  inline val PrivateSegment = 4096

  /** `offset` into a lane's private segment where `size` bytes from it fit, as execution checks it. */
  private def privateOffset(offset: Long, size: Int): Long = {
    require(offset >= 0L && offset <= PrivateSegment - size, s"private access at offset $offset exceeds the segment")
    offset
  }

  /** Little-endian loads and stores on the bytes and byte address an access of `size` bytes locates. */
  trait Located[A] extends Memory[A] {
    protected def locate(address: A, size: Int): (SeededMemory, Long)

    private def read(address: A, size: Int): Long = {
      val (bytes, at) = locate(address, size)
      bytes.load(at, size)
    }

    private def write(address: A, value: Long, size: Int): Unit = {
      val (bytes, at) = locate(address, size)
      bytes.store(at, value, size)
    }

    private def sized(bytes: UInt[32]): Int = {
      require(bytes.raw >= 1L && bytes.raw <= 8L, s"${bytes.raw} bytes is no single access")
      bytes.raw.toInt
    }

    def load8(address: A): UInt[8]                                = fromRaw[UInt[8]](read(address, 1))
    def load16(address: A): UInt[16]                              = fromRaw[UInt[16]](read(address, 2))
    def load32(address: A): UInt[32]                              = fromRaw[UInt[32]](read(address, 4))
    def load64(address: A): UInt[64]                              = fromRaw[UInt[64]](read(address, 8))
    def store8(address: A, value: UInt[8]): Unit                  = write(address, value.raw, 1)
    def store16(address: A, value: UInt[16]): Unit                = write(address, value.raw, 2)
    def store32(address: A, value: UInt[32]): Unit                = write(address, value.raw, 4)
    def store64(address: A, value: UInt[64]): Unit                = write(address, value.raw, 8)
    def load(address: A, bytes: UInt[32]): UInt[64]               = fromRaw[UInt[64]](read(address, sized(bytes)))
    def store(address: A, bytes: UInt[32], value: UInt[64]): Unit = write(address, value.raw, sized(bytes))
  }

  class Global(bytes: SeededMemory) extends GlobalMemory with Located[UInt[64]] {
    protected def locate(address: UInt[64], size: Int): (SeededMemory, Long) = (bytes, address.raw)
  }

  class Shared(bytes: SeededMemory) extends LocalMemory with Located[UInt[32]] {
    protected def locate(address: UInt[32], size: Int): (SeededMemory, Long) = (bytes, address.raw)
  }

  class Scratch(bytes: SeededMemory) extends PrivateMemory with Located[UInt[64]] {
    protected def locate(address: UInt[64], size: Int): (SeededMemory, Long) = (bytes, privateOffset(address.raw, size))
  }

  /** Flat addresses routed by aperture as execution routes them. */
  class Flat(global: SeededMemory, shared: SeededMemory, scratch: SeededMemory) extends FlatMemory
      with Located[UInt[64]] {
    protected def locate(address: UInt[64], size: Int): (SeededMemory, Long) =
      if (VMAddressSpace.isLocal(address.raw))
        (shared, Execute.ldsOffset(VMAddressSpace.offset(address.raw)))
      else if (VMAddressSpace.isPrivate(address.raw)) (scratch, privateOffset(VMAddressSpace.offset(address.raw), size))
      else (global, address.raw)
  }

  /** Linear images as execution addresses them; a descriptor execution cannot read is outside a definition's domain.
    */
  object Linear extends Images {
    private def resource(isa: ISA, words: UInt[32]*): ImageResource =
      ImageResource.decode(
        isa,
        Array.from(words.map(_.raw))
      ).fold(message => throw IllegalArgumentException(message), identity)

    def texel(
        isa: ISA,
        t0: UInt[32],
        t1: UInt[32],
        t2: UInt[32],
        t3: UInt[32],
        t4: UInt[32],
        t5: UInt[32],
        t6: UInt[32],
        t7: UInt[32],
        c0: UInt[32],
        c1: UInt[32],
        c2: UInt[32],
        c3: UInt[32],
        c4: UInt[32],
        dimension: Option[ImageDimension],
        mip: Bool
    ): UInt[64] =
      fromRaw[UInt[64]](
        ImageResource.locate(
          resource(isa, t0, t1, t2, t3, t4, t5, t6, t7),
          dimension,
          mip,
          c0 = c0.raw,
          c1 = c1.raw,
          c2 = c2.raw,
          c3 = c3.raw,
          c4 = c4.raw
        )
      )

    def information(
        isa: ISA,
        t0: UInt[32],
        t1: UInt[32],
        t2: UInt[32],
        t3: UInt[32],
        t4: UInt[32],
        t5: UInt[32],
        t6: UInt[32],
        t7: UInt[32],
        level: UInt[32],
        component: Int
    ): UInt[32] = fromRaw[UInt[32]](
      resource(isa, t0, t1, t2, t3, t4, t5, t6, t7).information(level.raw.toInt, component)
    )
  }

  /** The dword VGPR `index` holds in lane `lane` until written: its own index as [[held]] spreads it, and VGPR 0's
    * beyond the last.
    */
  def background(index: Long, lane: Int): Long = held(if (index >= 256L) 0L else index, lane)

  /** A complete wave in which only [[Lane]] is active and every VGPR holds [[background]] until written; `vectors`
    * takes the VGPRs [[Lane]] writes to values other than that.
    */
  class SingleLane(vectors: mutable.Map[Int, Long]) extends Wave {
    private def current(index: UInt[32]): Long = {
      val at = if (index.raw >= 256L) 0 else index.raw.toInt
      vectors.getOrElse(at, background(at, Lane))
    }

    def lane(): UInt[32]                                 = fromRaw[UInt[32]](Lane.toLong)
    def exec(): UInt[64]                                 = fromRaw[UInt[64]](1L << Lane)
    def ballot(predicate: Bool): UInt[64]                = fromRaw[UInt[64]](if (predicate) 1L << Lane else 0L)
    def read(value: UInt[32], index: UInt[32]): UInt[32] = fromRaw[UInt[32]](held(value.raw, index.raw.toInt))
    def permute(value: UInt[32], destination: UInt[32], existing: UInt[32]): UInt[32] =
      if (destination.raw == Lane.toLong) value else existing
    def vgpr(index: UInt[32]): UInt[32]                   = fromRaw[UInt[32]](current(index))
    def vgprAt(index: UInt[32], lane: UInt[32]): UInt[32] =
      if (lane.raw == Lane.toLong) vgpr(index)
      else fromRaw[UInt[32]](background(index.raw, lane.raw.toInt))
    def writeVgpr(index: UInt[32], value: UInt[32]): Unit =
      if (index.raw < 256L)
        if (value.raw == background(index.raw, Lane)) { val _ = vectors.remove(index.raw.toInt) }
        else vectors(index.raw.toInt) = value.raw
  }
}

/** The machine a rendered definition's lane runs on: [[AMDGPUHarness.Lane]] alone active in a complete wave whose other
  * lanes hold what [[AMDGPUHarness.held]] derives from this lane's registers; global memory holding
  * [[AMDGPUHarness.initial]] until written; and LDS and private memory holding zeros.
  */
private[isa] class AMDGPUHarness {
  import AMDGPUHarness.*

  val globalBytes: SeededMemory  = SeededMemory(initial(_))
  val sharedBytes: SeededMemory  = SeededMemory(_ => 0, LdsBytes, 0xffffffffL)
  val privateBytes: SeededMemory = SeededMemory(_ => 0)

  private val vectorWrites: mutable.Map[Int, Long] = mutable.Map.empty

  given wave: Wave             = SingleLane(vectorWrites)
  given global: GlobalMemory   = Global(globalBytes)
  given lds: LocalMemory       = Shared(sharedBytes)
  given scratch: PrivateMemory = Scratch(privateBytes)
  given flat: FlatMemory       = Flat(globalBytes, sharedBytes, privateBytes)
  given images: Images         = Linear

  /** Records the VGPR `index` as written to `value`, as the reference finds it. */
  def writeVector(index: Int, value: Long): Unit = vectorWrites(index) = value

  /** The bytes written to global memory and to LDS, and the VGPRs [[AMDGPUHarness.Lane]] writes to other than their
    * [[AMDGPUHarness.background]].
    */
  def writes: (global: Map[Long, Byte], shared: Map[Long, Byte], vectors: Map[Int, Long]) =
    (globalBytes.writes, sharedBytes.writes, vectorWrites.toMap)
}
