package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import VMIR.Matrix.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Warp-level tensor operations. Sources are read before any staged writes commit. WMMA has an intentionally
  * unspecified fragment layout: use a stable cyclic distribution, with replicated elements where the specified register
  * capacity exceeds the matrix size. MMA and ldmatrix use the specified lane/register layouts, so compiler-generated
  * fragment operations work.
  */
object VMTensor {
  private val halfwords = Vec.range[0, 2]

  private def invalid(detail: NonEmpty[String]): Fault = Fault.Backend(detail)
  private def memoryIndex(
      fragment: Fragment,
      lane: WarpLane,
      index: Int >= 0,
      stride: Long,
      column: Boolean
  ): Long = {
    val (row, col) = fragment.coordinate(lane, index)
    if (column) col.toLong.raw * stride + row.raw else row.toLong.raw * stride + col.raw
  }
  private def shared(space: MemorySpace, address: Long): Boolean =
    space == MemorySpace.Shared || (space == MemorySpace.Generic && VMAddressSpace.isShared(address))

  private def readMemory(
      context: VMContext,
      space: MemorySpace,
      address: Long,
      bits: Int in (8, 16, 32, 64)
  ): Either[Fault, Long] =
    if (shared(space, address)) Right(context.workgroup.readShared(address, bits))
    else context.memory.readValue(GuestAddress(address), bits.toScalarByteWidth)

  private def writeMemory(
      effects: VMWriteBuffer,
      lane: WorkgroupLane,
      space: MemorySpace,
      address: Long,
      bits: Int in (8, 16, 32, 64),
      value: Long
  ): Unit =
    if (shared(space, address)) effects.stageShared(address, bits, value)
    else effects.stage(VMWrite.Memory(lane, GuestAddress(address), bits, value, false))

  private def uniform(
      context: VMContext,
      base: WorkgroupLane.WarpBase,
      value: Value
  ): Either[Fault, Long] = {
    val values = WarpLane.values.map(lane => context.read(WorkgroupLane.inWarp(base, lane), value))
    Either.cond(
      values.distinct.size == 1,
      values.head,
      invalid("WMMA address and stride must be uniform within each warp")
    )
  }

  private def parameters(
      context: VMContext,
      base: WorkgroupLane.WarpBase,
      address: Value,
      stride: Value,
      fragment: Fragment,
      column: Boolean
  ): Either[Fault, (Long, Long)] =
    for {
      pointer <- uniform(context, base, address)
      leading <- uniform(context, base, stride)
      alignment = fragment.registers * fragment.element.bits.toMemoryValueByteWidth.widen[Int >= 0].max(4)
      _ <- Either.cond(
        pointer % alignment.raw == 0 && (leading * fragment.element.bits.raw / 8) % alignment.raw == 0,
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
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] = {
    val warps = WorkgroupLane.warpBases.filter(base =>
      base.raw < context.workgroup.laneCount.raw && context.active.warpBits(base.raw) != 0
    )
    if (
      warps.exists(base =>
        base.raw + 32 > context.workgroup.laneCount.raw || context.active.warpBits(base.raw) != 0xffffffffL
      )
    )
      Left(invalid("tensor .sync.aligned instruction requires all 32 lanes of each participating warp"))
    else warps.toVec.traverse_(base => executeWarp(operation, base, context, effects)).map(_ => Step.Advance)
  }

  private def executeWarp(
      operation: Operation,
      base: WorkgroupLane.WarpBase,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Unit] = operation match {
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
            }.map(parts =>
              effects.stageRegister(WorkgroupLane.inWarp(base, lane), destination, parts.foldLeft(0L)(_ | _))
            )
          }
        }
      }
    case Operation.Store(fragment, sources, address, stride, column, space) =>
      parameters(context, base, address, stride, fragment, column).map { (pointer, leading) =>
        val sourceRegisters = sources.zip(Vec.indices(sources.nonNegativeSize))
        val parts           = Vec.indices(fragment.element.packed)
        WarpLane.values.foreach { lane =>
          sourceRegisters.foreach { (source, register) =>
            val value = context.read(WorkgroupLane.inWarp(base, lane), source)
            parts.foreach { part =>
              val offset = memoryIndex(fragment, lane, register * fragment.element.packed + part, leading, column)
              writeMemory(
                effects,
                WorkgroupLane.inWarp(base, lane),
                space,
                pointer + offset * (fragment.element.bits.raw / 8),
                fragment.element.bits,
                value >>> (part.raw * fragment.element.bits.raw)
              )
            }
          }
        }
      }
    case Operation.Multiply(a, b, c, d, destinations, left, right, addend, saturate) =>
      val av = matrix(a, left, base, context)
      val bv = matrix(b, right, base, context)
      val cv = matrix(c, addend, base, context)
      Right {
        val destinationRegisters = destinations.zip(Vec.indices(destinations.nonNegativeSize))
        val parts                = Vec.indices(d.element.packed)
        val area                 = d.rows.raw * d.columns.raw
        val result               = Array.tabulate(d.partitions.raw * area) { index =>
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
              val resultIndex = d.partition(lane).raw * area + row.raw * d.columns.raw + col.raw
              bits | (d.element.encode(result(resultIndex)) << (part.raw * d.element.bits.raw))
            }
            effects.stageRegister(WorkgroupLane.inWarp(base, lane), destination, bits)
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
              bits | (context.workgroup.readShared(addresses(matrix.raw * 8 + row) + col * 2L, 16) << (part.raw * 16))
            }
            effects.stageRegister(WorkgroupLane.inWarp(base, lane), destination, bits)
          }
        }
      }
    case Operation.StoreMatrix(sources, address, transpose, space) =>
      matrixAddresses(context, base, address, sources.nonNegativeSize, space).map { addresses =>
        val sourceMatrices = sources.zip(Vec.indices(sources.nonNegativeSize))
        WarpLane.values.foreach { lane =>
          sourceMatrices.foreach { (source, matrix) =>
            val bits = context.read(WorkgroupLane.inWarp(base, lane), source)
            halfwords.foreach { part =>
              val row = if (transpose) lane.raw % 4 * 2 + part.raw else lane.raw / 4
              val col = if (transpose) lane.raw / 4 else lane.raw % 4 * 2 + part.raw
              effects.stageShared(addresses(matrix.raw * 8 + row) + col * 2L, 16, bits >>> (part.raw * 16))
            }
          }
        }
      }
  }

  private def matrixAddresses(
      context: VMContext,
      base: WorkgroupLane.WarpBase,
      address: Value,
      count: Int >= 0,
      space: MemorySpace
  ): Either[Fault, Vec[Long]] = Vec.indices(count * 8).traverse { lane =>
    val pointer = context.read(WorkgroupLane.fromIndex(base.raw + lane.raw), address)
    Either.cond(
      pointer % 16 == 0 && shared(space, pointer),
      pointer,
      invalid("matrix row addresses must be 16-byte aligned shared addresses")
    )
  }

  private def matrix(
      fragment: Fragment,
      values: Vec[Value],
      base: WorkgroupLane.WarpBase,
      context: VMContext
  ): Array[Double] = {
    val area           = fragment.rows * fragment.columns
    val result         = Array.ofDim[Double]((area * fragment.partitions).raw)
    val valueRegisters = values.zip(Vec.indices(values.nonNegativeSize))
    val parts          = Vec.indices(fragment.element.packed)
    WarpLane.values.foreach { lane =>
      valueRegisters.foreach { (value, register) =>
        val bits = context.read(WorkgroupLane.inWarp(base, lane), value)
        parts.foreach { part =>
          val (row, col)  = fragment.coordinate(lane, register * fragment.element.packed + part)
          val resultIndex = fragment.partition(lane) * area + row * fragment.columns + col
          result(resultIndex.raw) = fragment.element.decode(bits >>> (part.raw * fragment.element.bits.raw))
        }
      }
    }
    result
  }
}
