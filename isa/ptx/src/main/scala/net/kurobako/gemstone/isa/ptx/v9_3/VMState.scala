package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VirtualMachine.*

import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth

import scala.collection.mutable

/** Mutable storage for one executing PTX block. */
private[v9_3] final class VMState(val laneCount: Int, initialLive: LaneMask) {
  val registers: Array[VMRegisterFile]                                  = Array.fill(laneCount)(VMRegisterFile())
  val functionRegisters: Array[mutable.Map[RegisterName, FunctionName]] =
    Array.fill(laneCount)(mutable.Map.empty[RegisterName, FunctionName])
  val predicates: Array[VMPredicateFile]                  = Array.fill(laneCount)(VMPredicateFile())
  val parameters: Array[mutable.Map[ParameterName, Long]] =
    Array.fill(laneCount)(mutable.Map.empty[ParameterName, Long])
  val functionParameters: Array[mutable.Map[ParameterName, FunctionName]] =
    Array.fill(laneCount)(mutable.Map.empty[ParameterName, FunctionName])
  val parameterBytes: Array[mutable.Map[ParameterName, mutable.Map[Int, Byte]]] =
    Array.fill(laneCount)(mutable.Map.empty[ParameterName, mutable.Map[Int, Byte]])
  val carry: Array[Boolean]                             = Array.fill(laneCount)(false)
  val parameterBuffers: mutable.Map[Long, Array[Byte]]  = mutable.Map.empty
  val parameterAddresses: mutable.Map[SymbolName, Long] = mutable.Map.empty
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var nextParameterBuffer                   = 0x700000000L
  private val shared: mutable.Map[Long, Byte]       = mutable.Map.empty
  private val local: Array[mutable.Map[Long, Byte]] = Array.fill(laneCount)(mutable.Map.empty[Long, Byte])
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  var live: LaneMask = initialLive

  def readShared(address: Long, width: BitWidth): Long =
    LittleEndian.decode(byteCount(width.asByteAlignedBitWidth))(index =>
      shared.getOrElse(address + index.toLong, 0.toByte)
    )

  def writeShared(address: Long, width: BitWidth, value: Long): Unit =
    LittleEndian.write(value, byteCount(width.asByteAlignedBitWidth)) { (index, byte) =>
      shared(address + index.toLong) = byte
    }

  def readLocal(lane: Int, address: Long, width: BitWidth): Long =
    LittleEndian.decode(byteCount(width.asByteAlignedBitWidth))(index =>
      local(lane).getOrElse(address + index.toLong, 0.toByte)
    )

  def writeLocal(lane: Int, address: Long, width: BitWidth, value: Long): Unit =
    LittleEndian.write(value, byteCount(width.asByteAlignedBitWidth)) { (index, byte) =>
      local(lane)(address + index.toLong) = byte
    }

  def writeParameterBytes(
      lane: Int,
      name: ParameterName,
      offset: Int,
      width: BitWidth,
      value: Long
  ): Unit = {
    val bytes = parameterBytes(lane).getOrElseUpdate(name, mutable.Map.empty)
    LittleEndian.write(value, byteCount(width.asByteAlignedBitWidth)) { (index, byte) =>
      bytes(offset + index) = byte
    }
  }

  def readParameterBytes(lane: Int, name: ParameterName, offset: Int, size: ScalarByteWidth): Long =
    LittleEndian.decode(size) { index =>
      parameterBytes(lane).get(name).flatMap(_.get(offset + index)).getOrElse(0.toByte)
    }

  def allocateParameterBuffer(size: Int): Long = {
    val base = nextParameterBuffer
    parameterBuffers(base) = Array.ofDim[Byte](Math.max(size, 1))
    nextParameterBuffer = Math.addExact(base, Math.max(size, 1).toLong + 0x100L)
    base
  }

  def bindParameterBuffer(name: ParameterName, bytes: Array[Byte]): Unit = {
    val base = allocateParameterBuffer(bytes.length)
    Array.copy(bytes, 0, parameterBuffers(base), 0, bytes.length)
    parameterAddresses(SymbolName(name.value)) = base
  }

  def importParameterBuffers(buffers: Map[Long, Array[Byte]]): Unit =
    buffers.foreach { case (address, bytes) =>
      parameterBuffers(address) = bytes
      nextParameterBuffer = Math.max(
        nextParameterBuffer,
        Math.addExact(address, Math.max(bytes.length, 1).toLong + 0x100L)
      )
    }

  def parameterBufferRange(address: Long, size: Int): Option[(Array[Byte], Int)] =
    parameterBuffers.iterator.collectFirst {
      case (base, bytes)
          if address >= base && address - base >= 0L && address - base <= bytes.length.toLong - size.toLong =>
        bytes -> (address - base).toInt
    }

  def readParameterBuffer(address: Long, size: ScalarByteWidth): Option[Long] =
    parameterBufferRange(address, size.value).map { case (bytes, offset) =>
      LittleEndian.decode(bytes, offset, size)
    }

  def readParameterBufferBytes(address: Long, size: Int): Option[Array[Byte]] =
    parameterBufferRange(address, size).map { case (bytes, offset) =>
      java.util.Arrays.copyOfRange(bytes, offset, offset + size)
    }
}
