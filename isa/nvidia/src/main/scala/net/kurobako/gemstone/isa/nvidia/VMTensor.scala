package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*

/** Warp-level tensor operations. Sources are read before any staged writes commit. WMMA has an intentionally
  * unspecified fragment layout: use a stable cyclic distribution, with replicated elements where the specified register
  * capacity exceeds the matrix size. MMA and ldmatrix use the specified lane/register layouts, so compiler-generated
  * fragment operations work.
  */
object VMTensor {
  private val halfwords = Vec.range[0, 2]

  enum Element(val bits: Int in (8, 16, 32, 64), val packed: Int > 0) {
    case F16  extends Element(16, 2)
    case BF16 extends Element(16, 2)
    case TF32 extends Element(32, 1)
    case F32  extends Element(32, 1)
    case F64  extends Element(64, 1)
    case S8   extends Element(8, 4)
    case U8   extends Element(8, 4)
    case S32  extends Element(32, 1)

    def decode(value: Long): Double = this match {
      case F16        => IEEE754.binary16ToFloat(Binary16.low(value)).toDouble
      case BF16       => java.lang.Float.intBitsToFloat((value.toInt & 0xffff) << 16).toDouble
      case TF32 | F32 => java.lang.Float.intBitsToFloat(value.toInt).toDouble
      case F64        => java.lang.Double.longBitsToDouble(value)
      case S8         => value.toByte.toDouble
      case U8         => (value & 255L).toDouble
      case S32        => value.toInt.toDouble
    }
    def encode(value: Double): Long = this match {
      case F16        => IEEE754.doubleToBinary16(value).raw
      case F32 | TF32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
      case F64        => java.lang.Double.doubleToRawLongBits(value)
      case BF16       => (java.lang.Float.floatToRawIntBits(value.toFloat) >>> 16).toLong
      case S8 | U8    => value.toLong & 255L
      case S32        => value.toLong & 0xffffffffL
    }
  }
  case class Shape(m: Int > 0, n: Int > 0, k: Int > 0)
  enum Space { case Generic, Global, Shared }
  enum Role  { case A, B, C                 }
  case class Fragment(
      shape: Shape,
      role: Role,
      element: Element,
      wmma: Boolean,
      columnMajor: Boolean = false
  ) {
    def rows: Int > 0                  = if (role == Role.B) shape.k else shape.m
    def columns: Int > 0               = if (role == Role.A) shape.k else shape.n
    private def mma884F16: Boolean     = !wmma && shape == Shape(8, 8, 4) && element != Element.F64
    def partitions: Int                = if (mma884F16) 4 else 1
    def partition(lane: WarpLane): Int = if (mma884F16) (lane.raw % 16) / 4 else 0
    def registers: Int                 = if (mma884F16) {
      role match {
        case Role.A | Role.B                  => 2
        case Role.C if element == Element.F32 => 8
        case Role.C                           => 4
      }
    } else if (wmma && element == Element.F16 && role != Role.C) 8
    else rows.raw * columns.raw / (32 * element.packed.raw)

    // Return the logical matrix element held by a lane's unpacked fragment element.
    def coordinate(lane: WarpLane, index: Int >= 0): (Int, Int) =
      if (mma884F16) {
        val thread = lane.raw % 4
        val high   = if (lane.raw < 16) 0 else 4
        role match {
          case Role.A if columnMajor            => ((index.raw % 4) + high, thread)
          case Role.A                           => (thread + high, index.raw)
          case Role.B if columnMajor            => (index.raw, thread + high)
          case Role.B                           => (thread, index.raw + high)
          case Role.C if element == Element.F32 =>
            ((lane.raw & 1) + (index.raw & 2) + high, (index.raw & 4) + (lane.raw & 2) + (index.raw & 1))
          case Role.C => (thread + high, index.raw)
        }
      } else if (wmma) {
        val linear = (index.raw * 32 + lane.raw) % (rows.raw * columns.raw)
        (linear / columns.raw, linear % columns.raw)
      } else {
        val group  = lane.raw / 4
        val thread = lane.raw % 4
        role match {
          case Role.C => (group + (index.raw / 2) * 8, thread * 2 + index.raw % 2)
          case Role.A =>
            val chunk = if (element.bits.raw < 32) element.packed.raw else 1
            (
              group + ((index.raw / chunk) % 2) * 8,
              thread * chunk + index.raw   % chunk + (index.raw / (2 * chunk)) * (4 * chunk)
            )
          case Role.B =>
            val chunk = if (element.bits.raw < 32) element.packed.raw else 1
            (thread * chunk + index.raw % chunk + (index.raw / chunk) * (4 * chunk), group)
        }
      }
  }
  enum Operation {
    case Load(
        fragment: Fragment,
        destinations: Vec[NonEmpty[String]],
        address: Value,
        stride: Value,
        columnMajor: Boolean,
        space: Space
    )
    case Store(
        fragment: Fragment,
        sources: Vec[Value],
        address: Value,
        stride: Value,
        columnMajor: Boolean,
        space: Space
    )
    case Multiply(
        a: Fragment,
        b: Fragment,
        c: Fragment,
        d: Fragment,
        destinations: Vec[NonEmpty[String]],
        left: Vec[Value],
        right: Vec[Value],
        addend: Vec[Value],
        saturate: Boolean
    )
    case LoadMatrix(destinations: Vec[NonEmpty[String]], address: Value, transpose: Boolean, space: Space)
    case StoreMatrix(sources: Vec[Value], address: Value, transpose: Boolean, space: Space)
  }

  private def invalid(detail: NonEmpty[String]): VMFault = VMFault.InvalidState(detail)
  private def memoryIndex(
      fragment: Fragment,
      lane: WarpLane,
      index: Int >= 0,
      stride: Long,
      column: Boolean
  ): Long = {
    val (row, col) = fragment.coordinate(lane, index)
    if (column) col.toLong * stride + row else row.toLong * stride + col
  }
  private def shared(space: Space, address: Long): Boolean =
    space == Space.Shared || (space == Space.Generic && VMAddressSpace.isShared(address))

  private def readMemory(
      context: VMInstructionContext,
      space: Space,
      address: Long,
      bits: Int in (8, 16, 32, 64)
  ): Either[VMFault, Long] =
    if (shared(space, address))
      Right(context.state.readShared(address, bits))
    else context.memory.readLittleEndian(GuestAddress(address), (bits.raw / 8).unsafe[Int where Between[1, 8]])
      .left.map(VMFault.fromMemory(_))

  private def writeMemory(
      effects: VMBlockState.WriteBuffer,
      lane: Int,
      space: Space,
      address: Long,
      bits: Int in (8, 16, 32, 64),
      value: Long
  ): Unit =
    if (shared(space, address))
      effects.stage(VMWrite.Shared(lane, address, bits, value))
    else effects.stage(VMWrite.Memory(
      lane,
      GuestAddress(address),
      bits,
      value
    ))

  private def uniform(context: VMInstructionContext, base: Int, value: Value): Either[VMFault, Long] =
    WarpLane.values.traverse(i => context.read(base + i.raw, value)).flatMap { values =>
      if (values.distinct.size == 1) Right(values.head)
      else Left(invalid("WMMA address and stride must be uniform within each warp"))
    }

  private def parameters(
      context: VMInstructionContext,
      base: Int,
      address: Value,
      stride: Value,
      fragment: Fragment,
      column: Boolean
  ): Either[VMFault, (Long, Long)] =
    for {
      pointer <- uniform(context, base, address)
      leading <- uniform(context, base, stride)
      alignment = fragment.registers * math.max(4, fragment.element.bits.raw / 8)
      _ <- Either.cond(
        pointer % alignment == 0 && (leading * fragment.element.bits.raw / 8) % alignment == 0,
        (),
        invalid(s"WMMA address and stride must be aligned to $alignment bytes")
      )
      _ <- Either.cond(
        leading >= (if (column) fragment.rows.raw else fragment.columns.raw),
        (),
        invalid("WMMA stride is smaller than the matrix leading dimension")
      )
    } yield (pointer, leading)

  def execute(
      operation: Operation,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = {
    val warps = (0 until context.state.laneCount by 32).filter(base => context.active.warpBits(base) != 0)
    if (warps.exists(base => base + 32 > context.state.laneCount || context.active.warpBits(base) != 0xffffffffL))
      Left(invalid("tensor .sync.aligned instruction requires all 32 lanes of each participating warp"))
    else warps.toVec.traverse_(base => executeWarp(operation, base, context, effects)).map(_ => Step.Advance)
  }

  private def executeWarp(
      operation: Operation,
      base: Int,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Unit] = operation match {
    case Operation.Load(fragment, destinations, address, stride, column, space) =>
      parameters(context, base, address, stride, fragment, column).flatMap { (pointer, leading) =>
        val registerDestinations = destinations.zip(Vec.indices(destinations.nonNegativeSize))
        val parts                = Vec.indices(fragment.element.packed)
        WarpLane.values.traverse_ { lane =>
          registerDestinations.traverse_ { (destination, register) =>
            parts.traverse { part =>
              val offset = memoryIndex(fragment, lane, register * fragment.element.packed + part, leading, column)
              readMemory(context, space, pointer + offset * (fragment.element.bits.raw / 8), fragment.element.bits)
                .map(_ << (part.raw * fragment.element.bits.raw))
            }.map(parts => effects.stageRegister(base + lane.raw, destination, parts.foldLeft(0L)(_ | _)))
          }
        }
      }
    case Operation.Store(fragment, sources, address, stride, column, space) =>
      parameters(context, base, address, stride, fragment, column).flatMap { (pointer, leading) =>
        val sourceRegisters = sources.zip(Vec.indices(sources.nonNegativeSize))
        val parts           = Vec.indices(fragment.element.packed)
        WarpLane.values.traverse_ { lane =>
          sourceRegisters.traverse_ { (source, register) =>
            context.read(base + lane.raw, source).map { value =>
              parts.foreach { part =>
                val offset = memoryIndex(fragment, lane, register * fragment.element.packed + part, leading, column)
                writeMemory(
                  effects,
                  base + lane.raw,
                  space,
                  pointer + offset * (fragment.element.bits.raw / 8),
                  fragment.element.bits,
                  value >>> (part.raw * fragment.element.bits.raw)
                )
              }
            }
          }
        }
      }
    case Operation.Multiply(a, b, c, d, destinations, left, right, addend, saturate) =>
      for {
        av <- matrix(a, left, base, context)
        bv <- matrix(b, right, base, context)
        cv <- matrix(c, addend, base, context)
      } yield {
        val destinationRegisters = destinations.zip(Vec.indices(destinations.nonNegativeSize))
        val parts                = Vec.indices(d.element.packed)
        val area                 = d.rows.raw * d.columns.raw
        val result               = Array.tabulate(d.partitions * area) { index =>
          val partition = index / area
          val local     = index % area
          val row       = local / d.columns.raw
          val col       = local % d.columns.raw
          val sum       = (0 until a.columns.raw).foldLeft(cv(index)) { (acc, k) =>
            val next = Math.fma(
              av(partition * a.rows.raw * a.columns.raw + row * a.columns.raw + k),
              bv(partition * b.rows.raw * b.columns.raw + k * b.columns.raw + col),
              acc
            )
            if (d.element == Element.F64 || d.element == Element.S32) next else next.toFloat.toDouble
          }
          if (d.element == Element.S32 && saturate)
            math.max(Int.MinValue.toDouble, math.min(Int.MaxValue.toDouble, sum))
          else if (d.element == Element.S32) sum.toLong.toInt.toDouble
          else sum
        }
        WarpLane.values.foreach { lane =>
          destinationRegisters.foreach { (destination, register) =>
            val bits = parts.foldLeft(0L) { (bits, part) =>
              val index       = register * d.element.packed + part
              val (row, col)  = d.coordinate(lane, index)
              val resultIndex = d.partition(lane) * area + row * d.columns.raw + col
              bits | (d.element.encode(result(resultIndex)) << (part.raw * d.element.bits.raw))
            }
            effects.stageRegister(base + lane.raw, destination, bits)
          }
        }
      }
    case Operation.LoadMatrix(destinations, address, transpose, space) =>
      matrixAddresses(context, base, address, destinations.nonNegativeSize, space).map { addresses =>
        val destinationMatrices = destinations.zip(Vec.indices(destinations.nonNegativeSize))
        WarpLane.values.foreach { lane =>
          destinationMatrices.foreach { (destination, matrix) =>
            val bits = halfwords.foldLeft(0L) { (bits, part) =>
              val row = if (transpose) lane.raw % 4 * 2 + part.raw else lane.raw / 4
              val col = if (transpose) lane.raw / 4 else lane.raw % 4 * 2 + part.raw
              bits | (context.state.readShared(addresses(matrix.raw * 8 + row) + col * 2L, 16) << (part.raw * 16))
            }
            effects.stageRegister(base + lane.raw, destination, bits)
          }
        }
      }
    case Operation.StoreMatrix(sources, address, transpose, space) =>
      matrixAddresses(context, base, address, sources.nonNegativeSize, space).flatMap { addresses =>
        val sourceMatrices = sources.zip(Vec.indices(sources.nonNegativeSize))
        WarpLane.values.traverse_ { lane =>
          sourceMatrices.traverse_ { (source, matrix) =>
            context.read(base + lane.raw, source).map { bits =>
              halfwords.foreach { part =>
                val row = if (transpose) lane.raw % 4 * 2 + part.raw else lane.raw / 4
                val col = if (transpose) lane.raw / 4 else lane.raw % 4 * 2 + part.raw
                effects.stage(VMWrite.Shared(
                  base + lane.raw,
                  addresses(matrix.raw * 8 + row) + col * 2L,
                  16,
                  bits >>> (part.raw * 16)
                ))
              }
            }
          }
        }
      }
  }

  private def matrixAddresses(
      context: VMInstructionContext,
      base: Int,
      address: Value,
      count: Int >= 0,
      space: Space
  ): Either[VMFault, Vec[Long]] = Vec.indices(count * 8).traverse { lane =>
    context.read(base + lane.raw, address).flatMap { pointer =>
      Either.cond(
        pointer % 16 == 0 && shared(space, pointer),
        pointer,
        invalid("matrix row addresses must be 16-byte aligned shared addresses")
      )
    }
  }

  private def matrix(
      fragment: Fragment,
      values: Vec[Value],
      base: Int,
      context: VMInstructionContext
  ): Either[VMFault, Array[Double]] = {
    val area           = fragment.rows.raw * fragment.columns.raw
    val result         = Array.ofDim[Double](fragment.partitions * area)
    val valueRegisters = values.zip(Vec.indices(values.nonNegativeSize))
    val parts          = Vec.indices(fragment.element.packed)
    WarpLane.values.traverse_ { lane =>
      valueRegisters.traverse_ { (value, register) =>
        context.read(base + lane.raw, value).map { bits =>
          parts.foreach { part =>
            val (row, col)  = fragment.coordinate(lane, register * fragment.element.packed + part)
            val resultIndex = fragment.partition(lane) * area + row * fragment.columns.raw + col
            result(resultIndex) = fragment.element.decode(bits >>> (part.raw * fragment.element.bits.raw))
          }
        }
      }
    }.map(_ => result)
  }
}
