package net.kurobako.gemstone.isa.nvidia

import TensorMemory.*
import VMIR.Tcgen05.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The cells of tensor memory that tcgen05 instructions address, as the primitives their semantics calls and the
  * functions rendered semantics calls in their place. A move code names a [[Movement]] with the register and the 16-bit
  * half of it a cell holds, an address holds a lane in its high half and a column in its low half, and a thread is a
  * lane of the warp that moves.
  */
private[isa] object TensorMemoryLayout {
  private val Packed = 8L
  private val Half   = 16L

  /** What a fault shows of a value. */
  enum Text derives CanEqual {
    case Hexadecimal, Descriptor
  }

  def code(movement: Movement, register: Int, half: Int): Long =
    movement.shape.ordinal.toLong | (if (movement.packed) Packed else 0L) | (if (half == 0) 0L else Half) |
      (movement.threads.ordinal.toLong << 5) | (register.toLong << 8) | (movement.split.toLong << 32)

  private def shape(code: Long): DataShape = DataShape.fromOrdinal((code & 7L).toInt)

  private def register(code: Long): Int = ((code >>> 8) & 0xffffffL).toInt

  private def from(code: Long, address: Long, thread: Int): Long =
    if (shape(code) == DataShape.Lanes16Bits32x2 && thread >= 16) address + (code >> 32) else address

  private def flag(holds: Boolean): Long = if (holds) 1L else 0L

  private def includedOf(code: Long, thread: Long): Long =
    flag(Threads.fromOrdinal(((code >>> 5) & 3L).toInt).includes(thread.toInt))

  private def cellLaneOf(code: Long, address: Long, thread: Long): Long =
    TensorMemory.lane(from(code, address, thread.toInt)) + shape(code).lane(thread.toInt, register(code))

  private def cellColumnOf(code: Long, address: Long, thread: Long): Long = {
    val logical = shape(code).column(thread.toInt, register(code))
    val half    = if ((code & Half) == 0L) 0 else 1
    TensorMemory.column(from(code, address, thread.toInt)) +
      (if ((code & Packed) == 0L) logical else 2 * logical + half)
  }

  private def laneOf(address: Long): Long = TensorMemory.lane(address)

  private def columnOf(address: Long): Long = TensorMemory.column(address)

  /** The granules an allocation of `count` columns takes, or 0 where it is not a power of two from 32 to 512. */
  private def grantedOf(count: Long): Long =
    if (count < Granule || count > Columns || java.lang.Long.bitCount(count) != 1) 0L else count / Granule

  private def claimableOf(granules: Long): Long =
    flag(granules >= 1 && granules <= Granules && java.lang.Long.bitCount(granules) == 1)

  /** The granules a deallocation of `columns` columns at `address` frees, or 0 where it is not a whole run at lane 0.
    */
  private def freedOf(address: Long, columns: Long): Long =
    if (TensorMemory.lane(address) != 0) 0L
    else TensorMemory.granules(TensorMemory.column(address), columns).getOrElse(0L)

  def combination(maximum: Boolean, kind: ReductionKind): Long = {
    val bits = kind match {
      case ReductionKind.Float(absolute, propagate) => 2L | (if (absolute) 8L else 0L) | (if (propagate) 16L else 0L)
      case ReductionKind.Signed                     => 1L
      case ReductionKind.Unsigned                   => 0L
    }
    bits | (if (maximum) 4L else 0L)
  }

  private def combinedOf(mode: Long, left: Long, right: Long): Long = {
    val maximum = (mode & 4L) != 0L
    (mode & 3L) match {
      case 2L =>
        VMArithmetic.extremum(left, right, 32, maximum, (mode & 16L) != 0L, (mode & 8L) != 0L, xorSign = false)
      case 1L =>
        val first  = Bits.signExtend(left, 32)
        val second = Bits.signExtend(right, 32)
        (if (maximum) math.max(first, second) else math.min(first, second)) & 0xffffffffL
      case _ => if (maximum) math.max(left, right) else math.min(left, right)
    }
  }

  private def decodableOf(descriptor: Long): Long = flag(SharedDescriptor.decode(descriptor).isRight)

  private def majorOf(descriptor: Long, row: Long, part: Long): Long =
    SharedDescriptor.decode(descriptor).fold(_ => 0L, _.major(row.toInt, part.toInt))

  private def replicatedOf(replication: Long, row: Long, index: Long): Long =
    Replication.fromOrdinal(replication.toInt).lanes(row.toInt)(index.toInt).toLong

  def text(text: Text, value: Long): String = text match {
    case Text.Hexadecimal => java.lang.Long.toHexString(value)
    case Text.Descriptor  => SharedDescriptor.decode(value).fold(message => message, _ => "")
  }

  val AddressLane: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.addressLane", laneOf(_))
  val AddressColumn: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.addressColumn", columnOf(_))
  val Granted: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.granted", grantedOf(_))
  val Claimable: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.claimable", claimableOf(_))
  val Freed: Primitive[Primitive.Binary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.freed", freedOf(_, _))
  val Included: Primitive[Primitive.Binary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.included", includedOf(_, _))
  val CellLane: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.cellLane", cellLaneOf(_, _, _))
  val CellColumn: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.cellColumn", cellColumnOf(_, _, _))
  val Combined: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.combined", combinedOf(_, _, _))
  val Decodable: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.decodable", decodableOf(_))
  val Major: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.major", majorOf(_, _, _))
  val Replicated: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorMemoryLayout.replicated", replicatedOf(_, _, _))

  def addressLane(address: UInt[64]): UInt[64] = fromRaw[UInt[64]](laneOf(toRaw(address)))

  def addressColumn(address: UInt[64]): UInt[64] = fromRaw[UInt[64]](columnOf(toRaw(address)))

  def granted(count: UInt[64]): UInt[64] = fromRaw[UInt[64]](grantedOf(toRaw(count)))

  def claimable(granules: UInt[64]): UInt[64] = fromRaw[UInt[64]](claimableOf(toRaw(granules)))

  def freed(address: UInt[64], columns: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](freedOf(toRaw(address), toRaw(columns)))

  def included(code: UInt[64], thread: UInt[64]): UInt[64] = fromRaw[UInt[64]](includedOf(toRaw(code), toRaw(thread)))

  def cellLane(code: UInt[64], address: UInt[64], thread: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](cellLaneOf(toRaw(code), toRaw(address), toRaw(thread)))

  def cellColumn(code: UInt[64], address: UInt[64], thread: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](cellColumnOf(toRaw(code), toRaw(address), toRaw(thread)))

  def combined(mode: UInt[64], left: UInt[64], right: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](combinedOf(toRaw(mode), toRaw(left), toRaw(right)))

  def decodable(descriptor: UInt[64]): UInt[64] = fromRaw[UInt[64]](decodableOf(toRaw(descriptor)))

  def major(descriptor: UInt[64], row: UInt[64], part: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](majorOf(toRaw(descriptor), toRaw(row), toRaw(part)))

  def replicated(replication: UInt[64], row: UInt[64], index: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](replicatedOf(toRaw(replication), toRaw(row), toRaw(index)))
}
