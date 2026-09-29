package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Mutable storage for one executing PTX block. */
private class VMBlockState(
    val laneCount: Int,
    initialLive: VMLaneMask,
    val shared: mutable.Map[Long, Byte] = mutable.Map.empty,
    val registerChunkCount: Int = 96
) {
  val registers: Array[VMRegisterFile] = Array.fill(laneCount)(VMRegisterFile(registerChunkCount))
  val functionRegisters: Array[mutable.Map[NonEmpty[String], NonEmpty[String]]] =
    Array.fill(laneCount)(mutable.Map.empty[NonEmpty[String], NonEmpty[String]])
  val predicates: Array[VMPredicateFile]                     = Array.fill(laneCount)(VMPredicateFile())
  val parameters: Array[mutable.Map[NonEmpty[String], Long]] =
    Array.fill(laneCount)(mutable.Map.empty[NonEmpty[String], Long])
  val functionParameters: Array[mutable.Map[NonEmpty[String], NonEmpty[String]]] =
    Array.fill(laneCount)(mutable.Map.empty[NonEmpty[String], NonEmpty[String]])
  val parameterBytes: Array[mutable.Map[NonEmpty[String], mutable.Map[Int, Byte]]] =
    Array.fill(laneCount)(mutable.Map.empty[NonEmpty[String], mutable.Map[Int, Byte]])
  val carry: Array[Boolean]                                   = Array.fill(laneCount)(false)
  val effects: VMWriteBuffer                                  = VMWriteBuffer()
  val parameterBuffers: mutable.Map[Long, Array[Byte]]        = mutable.Map.empty
  val parameterAddresses: mutable.Map[NonEmpty[String], Long] = mutable.Map.empty
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var nextParameterBuffer                   = 0x700000000L
  private val local: Array[mutable.Map[Long, Byte]] = Array.fill(laneCount)(mutable.Map.empty[Long, Byte])
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  var live: VMLaneMask = initialLive

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def reset(nextLive: VMLaneMask): Unit = {
    var lane = 0
    while (lane < laneCount) {
      registers(lane).clear()
      functionRegisters(lane).clear()
      predicates(lane).clear()
      parameters(lane).clear()
      functionParameters(lane).clear()
      parameterBytes(lane).clear()
      carry(lane) = false
      local(lane).clear()
      lane += 1
    }
    parameterBuffers.clear()
    parameterAddresses.clear()
    shared.clear()
    effects.clear()
    nextParameterBuffer = 0x700000000L
    live = nextLive
  }

  def readShared(address: Long, width: Int where (Between[8, 64] & MultipleOf[8])): Long =
    LittleEndian.decode(byteCount(width))(index =>
      shared.getOrElse(address + index.toLong, 0.toByte)
    )

  def writeShared(address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) =>
      shared(address + index.toLong) = byte
    }

  def readLocal(lane: Int, address: Long, width: Int where (Between[8, 64] & MultipleOf[8])): Long =
    LittleEndian.decode(byteCount(width))(index =>
      local(lane).getOrElse(address + index.toLong, 0.toByte)
    )

  def writeLocal(lane: Int, address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) =>
      local(lane)(address + index.toLong) = byte
    }

  def writeParameterBytes(
      lane: Int,
      name: NonEmpty[String],
      offset: Int,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  ): Unit = {
    val bytes = parameterBytes(lane).getOrElseUpdate(name, mutable.Map.empty)
    LittleEndian.write(value, byteCount(width)) { (index, byte) =>
      bytes(offset + index) = byte
    }
  }

  def readParameterBytes(lane: Int, name: NonEmpty[String], offset: Int, size: Int where Between[0, 8]): Long =
    LittleEndian.decode(size) { index =>
      parameterBytes(lane).get(name).flatMap(_.get(offset + index)).getOrElse(0.toByte)
    }

  def allocateParameterBuffer(size: Int): Long = {
    val base = nextParameterBuffer
    parameterBuffers(base) = Array.ofDim[Byte](Math.max(size, 1))
    nextParameterBuffer = Math.addExact(base, Math.max(size, 1).toLong + 0x100L)
    base
  }

  def bindParameterBuffer(name: NonEmpty[String], bytes: Array[Byte]): Unit = {
    val base = allocateParameterBuffer(bytes.length)
    Array.copy(bytes, 0, parameterBuffers(base), 0, bytes.length)
    parameterAddresses(name) = base
  }

  def importParameterBuffers(buffers: Map[Long, Array[Byte]]): Unit =
    buffers.foreach { case (address, bytes) =>
      parameterBuffers(address) = bytes
      nextParameterBuffer = Math.max(
        nextParameterBuffer,
        Math.addExact(address, Math.max(bytes.length, 1).toLong + 0x100L)
      )
    }

  // This hot lookup avoids the intermediate view and collectFirst closure for each PTX memory access.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoIterator.no-iterator"))
  def parameterBufferRange(address: Long, size: Int): Option[(Array[Byte], Int)] = {
    val buffers                            = parameterBuffers.iterator
    var result: Option[(Array[Byte], Int)] = None
    while (buffers.hasNext && result.isEmpty) {
      val (base, bytes) = buffers.next()
      val offset        = address - base
      if (address >= base && offset >= 0L && offset <= bytes.length.toLong - size.toLong)
        result = Some(bytes -> offset.toInt)
    }
    result
  }

  def readParameterBuffer(address: Long, size: Int where Between[0, 8]): Option[Long] =
    parameterBufferRange(address, size.raw).map { case (bytes, offset) =>
      LittleEndian.decode(bytes, offset, size)
    }

  def readParameterBufferBytes(address: Long, size: Int): Option[Array[Byte]] =
    parameterBufferRange(address, size).map { case (bytes, offset) =>
      java.util.Arrays.copyOfRange(bytes, offset, offset + size)
    }
}
