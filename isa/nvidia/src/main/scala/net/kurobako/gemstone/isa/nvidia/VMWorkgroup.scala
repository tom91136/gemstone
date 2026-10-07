package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Memory and synchronisation state of one workgroup, shared by every frame executing within it. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
class VMWorkgroup(val laneCount: Int in 1 ~ 1024) {
  val shared: mutable.LongMap[Byte]           = mutable.LongMap.empty
  val mbarriers: mutable.LongMap[MBarrier]    = mutable.LongMap.empty
  val invalidatedMBarriers: mutable.Set[Long] = mutable.Set.empty
  private val local                           = Array.ofDim[mutable.LongMap[Byte] | Null](laneCount.raw)
  private val convergence                     = mutable.LongMap.empty[Long]
  private var nextConvergenceToken            = 0
  private val buffers                         = java.util.TreeMap[Long, Array[Byte]]()
  private var nextBuffer                      = VMAddressSpace.ParameterBase

  def readShared(address: Long, width: ScalarBitWidth): Long = {
    val base = VMAddressSpace.sharedAddress(address)
    LittleEndian.decode(width.toScalarByteWidth)(index => shared.getOrElse(base + index.toLong, 0.toByte))
  }

  def writeShared(address: Long, width: ScalarBitWidth, value: Long): Unit = {
    val base = VMAddressSpace.sharedAddress(address)
    val size = width.toScalarByteWidth
    if (mbarriers.nonEmpty || invalidatedMBarriers.nonEmpty) {
      // A partial or same-value guest write replaces cached opaque state as well.
      val first = base & ~7L
      val last  = (base + size.raw - 1L) & ~7L
      val _     = mbarriers.remove(first)
      val _     = invalidatedMBarriers.remove(first)
      if (last != first) {
        val _ = mbarriers.remove(last)
        val _ = invalidatedMBarriers.remove(last)
      }
    }
    LittleEndian.write(value, size)((index, byte) => shared(base + index.toLong) = byte)
  }

  private def localBytes(lane: WorkgroupLane): mutable.LongMap[Byte] = local(lane.raw) match {
    case null =>
      val allocated = mutable.LongMap.empty[Byte]
      local(lane.raw) = allocated
      allocated
    case existing => existing
  }

  def readLocal(lane: WorkgroupLane, address: Long, width: ScalarBitWidth): Long = {
    val bytes = localBytes(lane)
    LittleEndian.decode(width.toScalarByteWidth)(index => bytes.getOrElse(address + index.toLong, 0.toByte))
  }

  def writeLocal(lane: WorkgroupLane, address: Long, width: ScalarBitWidth, value: Long): Unit = {
    val bytes = localBytes(lane)
    LittleEndian.write(value, width.toScalarByteWidth)((index, byte) => bytes(address + index.toLong) = byte)
  }

  /** Buffers occupy their own aperture; the first one allocated after a reset is the kernel parameter block. */
  def allocateBuffer(bytes: Array[Byte]): Long = {
    val base = nextBuffer
    val _    = buffers.put(base, if (bytes.isEmpty) Array.ofDim[Byte](1) else bytes)
    nextBuffer = Math.addExact(base, math.max(bytes.length, 1).toLong + 0x100L)
    base
  }

  def buffer(address: Long, size: Int >= 0): Option[(Array[Byte], Int)] =
    Option(buffers.floorEntry(address)).flatMap { entry =>
      Option.when(ByteRange.contains(entry.getValue.length, address - entry.getKey, size.raw.toLong))(
        entry.getValue -> (address - entry.getKey).toInt
      )
    }

  def bufferBytes(address: Long, size: Int >= 0): Option[Array[Byte]] =
    buffer(address, size).map((bytes, offset) => java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw))

  private def convergenceKey(warp: WorkgroupLane.WarpBase, token: Long): Long =
    (warp.raw.toLong << 32) | (token & 0xffffffffL)

  def createConvergence(warp: WorkgroupLane.WarpBase, mask: Long): Long = {
    nextConvergenceToken = Math.incrementExact(nextConvergenceToken)
    convergence(convergenceKey(warp, nextConvergenceToken.toLong)) = mask
    nextConvergenceToken.toLong
  }

  def convergenceMask(warp: WorkgroupLane.WarpBase, token: Long): Long =
    convergence.getOrElse(convergenceKey(warp, token), 0L)

  def leaveConvergence(warp: WorkgroupLane.WarpBase, token: Long, lane: WarpLane): Unit = {
    val key = convergenceKey(warp, token)
    convergence(key) = convergence.getOrElse(key, 0L) & ~(1L << lane.raw)
  }

  def completeConvergence(warp: WorkgroupLane.WarpBase, token: Long): Unit = {
    val _ = convergence.remove(convergenceKey(warp, token))
  }

  def reset(): Unit = {
    shared.clear()
    mbarriers.clear()
    invalidatedMBarriers.clear()
    local.foreach {
      case null  => ()
      case bytes => bytes.clear()
    }
    convergence.clear()
    nextConvergenceToken = 0
    buffers.clear()
    nextBuffer = VMAddressSpace.ParameterBase
  }
}
