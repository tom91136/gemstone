package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Memory and synchronisation state of one workgroup, shared by every frame executing within it. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMWorkgroup(val laneCount: Int in 1 ~ 1024) {
  private val shared                              = VMBytes()
  val mbarriers: mutable.LongMap[MBarrier]        = mutable.LongMap.empty
  val invalidatedMBarriers: mutable.LongMap[Unit] = mutable.LongMap.empty
  // the thread holding each locked shared word, by its shared offset
  val sharedLocks: mutable.LongMap[Int] = mutable.LongMap.empty
  val tensorMemory: TensorMemory.State  = TensorMemory.State()
  private val local                     = Array.fill(laneCount)(VMBytes())
  private val convergence               = mutable.LongMap.empty[Long]
  private var nextConvergenceToken      = 0
  private val buffers                   = java.util.TreeMap[Long, Array[Byte]]()
  private var nextBuffer                = VMAddressSpace.ParameterBase
  // Buffers are only added until a reset, so the last one found stays valid until then.
  private var lastBase   = 0L
  private var lastBuffer = Array.emptyByteArray

  /** This workgroup's rank in its cluster and every CTA of the cluster by rank, this one included. */
  private val alone: Array[VMWorkgroup | Null] = Array(this)
  var clusterRank: Int                         = 0
  var cluster: Array[VMWorkgroup | Null]       = alone

  /** The CTA whose shared memory holds `address`, or null when the address names a rank outside the cluster. */
  def sharedOwner(address: Long): VMWorkgroup | Null = {
    val rank = VMAddressSpace.clusterRank(address)
    if (rank < 0) this else peer(rank)
  }

  private def peer(rank: Int): VMWorkgroup | Null =
    if (rank == clusterRank) this else if (rank < cluster.length) cluster(rank) else null

  def readShared(address: Long, width: ScalarBitWidth): Long = {
    val rank = VMAddressSpace.clusterRank(address)
    if (rank < 0) shared.read(address, width.toScalarByteWidth.widen)
    else peer(rank) match {
      case null               => 0L
      case owner: VMWorkgroup => owner.shared.read(VMAddressSpace.sharedAddress(address), width.toScalarByteWidth.widen)
    }
  }

  def writeShared(address: Long, width: ScalarBitWidth, value: Long): Unit = {
    val rank = VMAddressSpace.clusterRank(address)
    if (rank < 0) writeOwnShared(address, width, value)
    else peer(rank) match {
      case null               => ()
      case owner: VMWorkgroup => owner.writeOwnShared(VMAddressSpace.sharedAddress(address), width, value)
    }
  }

  /** Reads `count` bytes of this CTA's own shared memory from the address `start` within its window. */
  def readSharedBytes(start: Long, into: Array[Byte], from: Int, count: Int): Unit =
    shared.read(start, into, from, count)

  /** Writes `count` bytes of `bytes` into this CTA's own shared memory at the address `start` within its window. */
  def writeSharedBytes(start: Long, bytes: Array[Byte], from: Int, count: Int): Unit = {
    forgetMBarriers(start, count.toLong)
    shared.write(start, bytes, from, count)
  }

  private def writeOwnShared(base: Long, width: ScalarBitWidth, value: Long): Unit = {
    val size = width.toScalarByteWidth
    forgetMBarriers(base, size.toLong)
    shared.write(base, value, size.widen)
  }

  // A partial or same-value guest write replaces cached opaque state as well.
  private def forgetMBarriers(base: Long, size: Long): Unit =
    if (size > 0 && (mbarriers.nonEmpty || invalidatedMBarriers.nonEmpty)) {
      var word = base & ~7L
      val last = (base + size - 1L) & ~7L
      while (word <= last) {
        val _ = mbarriers.subtractOne(word)
        val _ = invalidatedMBarriers.subtractOne(word)
        word += 8L
      }
    }

  def readLocal(lane: WorkgroupLane, address: Long, width: ScalarBitWidth): Long =
    local(lane.raw).read(address, width.toScalarByteWidth.widen)

  def writeLocal(lane: WorkgroupLane, address: Long, width: ScalarBitWidth, value: Long): Unit =
    local(lane.raw).write(address, value, width.toScalarByteWidth.widen)

  /** Buffers occupy their own aperture; the first one allocated after a reset is the kernel parameter block. */
  def allocateBuffer(bytes: Array[Byte]): Long = {
    val base = nextBuffer
    val _    = buffers.put(base, if (bytes.isEmpty) Array.ofDim[Byte](1) else bytes)
    nextBuffer = Math.addExact(base, math.max(bytes.length, 1).toLong + 0x100L)
    base
  }

  inline def withBuffer[A](
      address: Long,
      size: Int >= 0
  )(inline found: (Array[Byte], Int) => A)(otherwise: => A): A = {
    val offset = locateBuffer(address, size)
    if (offset >= 0) found(lastBuffer, offset) else otherwise
  }

  // The offset within lastBuffer, or -1 when no buffer holds the range.
  private def locateBuffer(address: Long, size: Int >= 0): Int =
    if (address < VMAddressSpace.ParameterBase || address >= nextBuffer) -1
    else if (ByteRange.contains(lastBuffer.length, address - lastBase, size.toLong)) (address - lastBase).toInt
    else buffers.floorEntry(address) match {
      case null  => -1
      case entry =>
        if (!ByteRange.contains(entry.getValue.length, address - entry.getKey, size.toLong)) -1
        else {
          lastBase = entry.getKey
          lastBuffer = entry.getValue
          (address - lastBase).toInt
        }
    }

  def bufferBytes(address: Long, size: Int >= 0): Option[Array[Byte]] =
    withBuffer(address, size)((bytes, offset) =>
      Option(java.util.Arrays.copyOfRange(bytes, offset, offset + size))
    )(
      None
    )

  private def convergenceKey(warp: WorkgroupLane.WarpBase, token: Long): Long =
    (warp.toLong << 32) | (token & 0xffffffffL)

  def createConvergence(warp: WorkgroupLane.WarpBase, mask: Long): Long = {
    nextConvergenceToken = Math.incrementExact(nextConvergenceToken)
    convergence(convergenceKey(warp, nextConvergenceToken.toLong)) = mask
    nextConvergenceToken.toLong
  }

  def convergenceMask(warp: WorkgroupLane.WarpBase, token: Long): Long =
    convergence.getOrElse(convergenceKey(warp, token), 0L)

  def leaveConvergence(warp: WorkgroupLane.WarpBase, token: Long, lane: WarpLane): Unit = {
    val key = convergenceKey(warp, token)
    convergence(key) = convergence.getOrElse(key, 0L) & ~(1L << lane)
  }

  def completeConvergence(warp: WorkgroupLane.WarpBase, token: Long): Unit = {
    val _ = convergence.subtractOne(convergenceKey(warp, token))
  }

  def reset(): Unit = {
    clusterRank = 0
    cluster = alone
    shared.clear()
    mbarriers.clear()
    invalidatedMBarriers.clear()
    sharedLocks.clear()
    tensorMemory.reset()
    local.foreach(_.clear())
    convergence.clear()
    nextConvergenceToken = 0
    buffers.clear()
    nextBuffer = VMAddressSpace.ParameterBase
    lastBase = 0L
    lastBuffer = Array.emptyByteArray
  }
}
