package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Memory and synchronisation state of one workgroup, shared by every frame executing within it. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMWorkgroup(val laneCount: Int in 1 ~ 1024) {
  private val shared                          = VMBytes()
  val mbarriers: mutable.LongMap[MBarrier]    = mutable.LongMap.empty
  val invalidatedMBarriers: mutable.Set[Long] = mutable.Set.empty
  private val local                           = Array.fill(laneCount.raw)(VMBytes())
  private val convergence                     = mutable.LongMap.empty[Long]
  private var nextConvergenceToken            = 0
  private val buffers                         = java.util.TreeMap[Long, Array[Byte]]()
  private var nextBuffer                      = VMAddressSpace.ParameterBase
  // Buffers are only added until a reset, so the last one found stays valid until then.
  private var lastBase   = 0L
  private var lastBuffer = Array.emptyByteArray

  def readShared(address: Long, width: ScalarBitWidth): Long =
    shared.read(VMAddressSpace.sharedAddress(address), width.toScalarByteWidth.widen)

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
    shared.write(base, value, size.widen)
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
  )(inline found: (Array[Byte], Int) => A)(inline otherwise: A): A = {
    val offset = locateBuffer(address, size)
    if (offset >= 0) found(lastBuffer, offset) else otherwise
  }

  // The offset within lastBuffer, or -1 when no buffer holds the range.
  private def locateBuffer(address: Long, size: Int >= 0): Int =
    if (address < VMAddressSpace.ParameterBase || address >= nextBuffer) -1
    else if (ByteRange.contains(lastBuffer.length, address - lastBase, size.raw.toLong)) (address - lastBase).toInt
    else buffers.floorEntry(address) match {
      case null  => -1
      case entry =>
        if (!ByteRange.contains(entry.getValue.length, address - entry.getKey, size.raw.toLong)) -1
        else {
          lastBase = entry.getKey
          lastBuffer = entry.getValue
          (address - lastBase).toInt
        }
    }

  def bufferBytes(address: Long, size: Int >= 0): Option[Array[Byte]] =
    withBuffer(address, size)((bytes, offset) =>
      Option(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw))
    )(
      None
    )

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
    local.foreach(_.clear())
    convergence.clear()
    nextConvergenceToken = 0
    buffers.clear()
    nextBuffer = VMAddressSpace.ParameterBase
    lastBase = 0L
    lastBuffer = Array.emptyByteArray
  }
}
