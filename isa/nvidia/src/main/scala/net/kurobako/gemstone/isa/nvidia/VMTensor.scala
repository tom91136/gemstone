package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import VMIR.Matrix.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
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
private object VMTensor {
  private def invalid(detail: NonEmpty[String]): Fault = Fault.Backend(detail)
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

  /** Element `offset` of a matrix at `pointer` whose elements are `bits` wide, packed low first within bytes. */
  private def readElement(
      context: VMContext,
      space: MemorySpace,
      pointer: Long,
      offset: Long,
      bits: Int in (1, 4, 8, 16, 32, 64)
  ): Either[Fault, Long] = bits match {
    case wide: (Int in (8, 16, 32, 64)) @unchecked if bits >= 8 =>
      readMemory(context, space, pointer + offset * (bits.raw / 8), wide)
    case _ =>
      val bit = offset * bits
      readMemory(context, space, pointer + bit / 8, 8).map(byte => (byte >>> (bit % 8)) & ((1L << bits) - 1L))
  }

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
      _       <- Either.cond(
        pointer % 32 == 0 && (leading * fragment.element.bits / 8) % 16 == 0,
        (),
        Fault.Misaligned("WMMA addresses must be aligned to 32 bytes and strides to 16")
      )
      _ <- Either.cond(
        leading >= (if (column) fragment.rows else fragment.columns),
        (),
        invalid("WMMA stride is smaller than the matrix leading dimension")
      )
    } yield (pointer, leading)

  def execute(
      operation: Operation,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    participating(context).flatMap(_.traverse_(base => executeWarp(operation, base, context, effects)))
      .map(_ => Step.Advance)

  /** The warps with an active lane, each of which must have all 32 lanes active. */
  def participating(context: VMContext): Either[Fault, Vec[WorkgroupLane.WarpBase]] = {
    val warps = WorkgroupLane.warpBases.filter(base =>
      base < context.workgroup.laneCount && context.active.warpBits(base) != 0
    )
    Either.cond(
      !warps.exists(base =>
        base + 32 > context.workgroup.laneCount || context.active.warpBits(base) != 0xffffffffL
      ),
      warps.toVec,
      invalid("tensor .sync.aligned instruction requires all 32 lanes of each participating warp")
    )
  }

  /** The operands of one warp's matrix product, gathered from its lanes, positioned at an element of D by [[at]]. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Operands(operation: Operation.Multiply, base: WorkgroupLane.WarpBase, context: VMContext)
      extends MatrixOperands[Long] {
    private val left                             = matrix(operation.a, operation.left, base, context)
    private val right                            = matrix(operation.b, operation.right, base, context)
    private val addend                           = matrix(operation.c, operation.addend, base, context)
    private val aArea                            = operation.a.rows.raw * operation.a.columns.raw
    private val bArea                            = operation.b.rows.raw * operation.b.columns.raw
    private val columns                          = operation.d.columns.raw
    private val area                             = operation.d.rows.raw * columns
    private val slots                            = Array.ofDim[Long](TensorSemantics.Slots)
    private def lanes(value: Value): Array[Long] =
      Array.tabulate(32)(lane => context.read(WorkgroupLane.fromIndex(base + lane), value))
    private val metadata = operation.sparsity.fold(Array.emptyLongArray)(sparsity => lanes(sparsity.metadata))
    private val selector =
      operation.sparsity.fold(0)(sparsity =>
        context.read(WorkgroupLane.fromIndex(base), sparsity.selector).toInt & 3
      )
    private val scales = operation.scaling.fold(Array.emptyLongArray)(scaling =>
      lanes(scaling.aData) ++ lanes(scaling.bData) ++ Array(
        context.read(WorkgroupLane.fromIndex(base), scaling.aByte),
        context.read(WorkgroupLane.fromIndex(base), scaling.aThread),
        context.read(WorkgroupLane.fromIndex(base), scaling.bByte),
        context.read(WorkgroupLane.fromIndex(base), scaling.bThread)
      )
    )
    private var partition = 0
    private var row       = 0
    private var column    = 0

    def at(index: Int): Unit = {
      partition = index / area
      row = index    % area / columns
      column = index % columns
    }

    def a(index: Int): Long =
      if (operation.bits.isDefined)
        MatrixOperands.segment(left, partition * aArea + row * operation.a.columns + index * 32, 1)
      else left(partition * aArea + row * operation.a.columns + index)
    def b(index: Int): Long =
      if (operation.bits.isDefined)
        MatrixOperands.segment(right, partition * bArea + index * 32 * columns + column, columns)
      else right(partition * bArea + index * columns + column)
    def c: Long = addend(partition * area + row * columns + column)

    def selection(index: Int): Long = if (operation.sparsity.isEmpty) 0L
    else {
      val element = operation.a.element
      val chunk   = index / packedPerChunk(element)
      val slot    = index % packedPerChunk(element)
      val place   = metadataNibble(element, depth = operation.b.rows, row = row, chunk = chunk, selector = selector)
      val nibble  = (metadata(place.lane) >>> place.offset) & 0xfL
      MatrixOperands.sparseSelection(element, nibble, slot)
    }

    private def byte(value: Long, index: Long): Long                  = (value >>> (8 * (index & 3L))) & 0xffL
    private def offset(vector: Int, selected: Long, block: Int): Long = if (vector == 4) block else selected + block

    // 0 without block scaling
    private val vector = operation.scaling.fold(0)(_.vector.raw)

    def scaleA(block: Int): Long =
      if (vector == 0) 0L
      else byte(scales(4 * (row % 8) + 2 * (scales(65) & 1L).toInt + row / 8), offset(vector, scales(64), block))
    def scaleB(block: Int): Long =
      if (vector == 0) 0L
      else byte(scales(32 + 4 * column + (scales(67) & 3L).toInt), offset(vector, scales(66), block))

    def bind(name: String, value: Long): Long            = value
    def hold(slot: Int, name: String, value: Long): Long = {
      slots(slot) = value
      value
    }
    def held(slot: Int): Long = slots(slot)
  }

  /** `mma` on one warp: each element of D is `element` of its operands. Sources are read before any write is staged. */
  inline def multiply(operation: Operation.Multiply, base: WorkgroupLane.WarpBase, context: VMContext)(
      inline element: MatrixOperands[Long] => Long
  ): Unit = {
    val d        = operation.d
    val area     = d.rows.raw * d.columns.raw
    val operands = Operands(operation, base, context)
    val results  = Array.tabulate(area * d.partitions) { index =>
      operands.at(index)
      element(operands)
    }
    store(operation, base, context, results)
  }

  /** Writes D's fragment; binary64 elements go to register pairs when there are two destinations per element. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def store(
      operation: Operation.Multiply,
      base: WorkgroupLane.WarpBase,
      context: VMContext,
      results: Array[Long]
  )
      : Unit = {
    val d     = operation.d
    val area  = d.rows.raw * d.columns.raw
    val parts = Vec.indices(d.element.packed)
    val pairs = d.element.bits == 64 && operation.destinations.size == 2 * d.registers
    WarpLane.values.foreach { lane =>
      val target = WorkgroupLane.inWarp(base, lane)
      Vec.indices(d.registers).foreach { register =>
        var bits = 0L
        parts.foreach { part =>
          val (row, col) = d.coordinate(lane, register * d.element.packed + part)
          val element    = results(d.partition(lane).raw * area + row.raw * d.columns.raw + col.raw)
          bits |= Bits.truncate(element, d.element.bits.widen) << (part.raw * d.element.bits)
        }
        if (pairs) {
          context.effects.stageRegister(target, operation.destinations(2 * register), bits & 0xffffffffL)
          context.effects.stageRegister(target, operation.destinations(2 * register + 1), bits >>> 32)
        } else context.effects.stageRegister(target, operation.destinations(register), bits)
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def orAll(values: Vec[Long]): Long = {
    var result = 0L
    var index  = 0
    while (index < values.length) {
      result |= values(index)
      index += 1
    }
    result
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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
              val offset =
                memoryIndex(fragment, lane, register * fragment.element.packed + part, leading, column)
              readElement(context, space, pointer, offset, fragment.element.bits)
                .map(_ << (part.raw * fragment.element.bits))
            }.map(parts =>
              effects.stageRegister(WorkgroupLane.inWarp(base, lane), destination, orAll(parts))
            )
          }
        }
      }
    case Operation.Store(fragment, sources, address, stride, column, space) =>
      parameters(context, base, address, stride, fragment, column).flatMap { (pointer, leading) =>
        fragment.element.bits match {
          case bits: (Int in (8, 16, 32, 64)) @unchecked if fragment.element.bits >= 8 =>
            val sourceRegisters = sources.zip(Vec.indices(sources.nonNegativeSize))
            val parts           = Vec.indices(fragment.element.packed)
            Right(WarpLane.values.foreach { lane =>
              sourceRegisters.foreach { (source, register) =>
                val value = context.read(WorkgroupLane.inWarp(base, lane), source)
                parts.foreach { part =>
                  val offset =
                    memoryIndex(fragment, lane, register * fragment.element.packed + part, leading, column)
                  writeMemory(
                    effects,
                    WorkgroupLane.inWarp(base, lane),
                    space,
                    pointer + offset * (bits.raw / 8),
                    bits,
                    value >>> (part.raw * bits)
                  )
                }
              }
            })
          case _ => Left(invalid("WMMA stores whole bytes"))
        }
      }
    case Operation.Multiply(d = d) => Left(invalid(s"mma into ${d.element} is not supported"))
    case Operation.LoadMatrix(tile, destinations, address, space) =>
      rowAddresses(context, base, address, tile.count.raw * tile.addresses, space).map { addresses =>
        val rows = Array.tabulate(addresses.size)(index =>
          tile.packing.unpack(
            Array.tabulate(16)(byte => context.workgroup.readShared(addresses(index) + byte, 8).toByte)
          )
        )
        val parts = 4 / tile.shape.bytes
        WarpLane.values.foreach { lane =>
          destinations.zipWithIndex.foreach { (destination, register) =>
            var bits = 0L
            (0 until parts).foreach { part =>
              val (matrix, row, column) = tile.coordinate(lane, register, part)
              val (stored, offset)      = tile.stored(row, column)
              val memory                = rows(matrix * tile.addresses + stored)
              val element               =
                if (tile.shape.bytes == 2) (memory(offset * 2) & 0xffL) | ((memory(offset * 2 + 1) & 0xffL) << 8)
                else memory(offset) & 0xffL
              bits |= element << (part * 8 * tile.shape.bytes)
            }
            effects.stageRegister(WorkgroupLane.inWarp(base, lane), destination, bits)
          }
        }
      }
    case Operation.StoreMatrix(tile, sources, address, space) =>
      rowAddresses(context, base, address, tile.count.raw * tile.addresses, space).map { addresses =>
        val parts = 4 / tile.shape.bytes
        val width = 8 * tile.shape.bytes
        WarpLane.values.foreach { lane =>
          sources.zipWithIndex.foreach { (source, register) =>
            val bits = context.read(WorkgroupLane.inWarp(base, lane), source)
            (0 until parts).foreach { part =>
              val (matrix, row, column) = tile.coordinate(lane, register, part)
              val (stored, offset)      = tile.stored(row, column)
              effects.stageShared(
                addresses(matrix * tile.addresses + stored) + offset * tile.shape.bytes,
                if (width == 16) 16 else 8,
                bits >>> (part * width)
              )
            }
          }
        }
      }
    case widened: Operation.Widen                       => Right(widen(widened, base, context, effects))
    case Operation.WarpgroupMultiply(product = product) =>
      Left(invalid(s"wgmma into ${product.d.element} from ${product.a.element} is not supported"))
    case Operation.Transpose(destination, source) =>
      val values = Vec.tabulate(32)(lane => context.read(WorkgroupLane.fromIndex(base + lane), source))
      def element(row: Int, column: Int): Long = (values(4 * row + column / 2) >>> (16 * (column % 2))) & 0xffffL
      Right(WarpLane.values.foreach { lane =>
        val row = 2 * (lane.raw % 4)
        effects.stageRegister(
          WorkgroupLane.inWarp(base, lane),
          destination,
          element(row, lane.raw / 4) | (element(row + 1, lane.raw / 4) << 16)
        )
      })
  }

  private def widen(
      operation: Operation.Widen,
      base: WorkgroupLane.WarpBase,
      context: VMContext,
      effects: VMWriteBuffer
  )
      : Unit = {
    val values = Vec.tabulate(32)(lane => context.read(WorkgroupLane.fromIndex(base + lane), operation.source))
    def element(row: Int, column: Int): Long = (values(4 * row + column / 8) >>> (4 * (column % 8))) & 0xfL
    WarpLane.values.foreach { lane =>
      def bytes(first: Int): Long = LittleEndian.decode(4)(part => element(lane.raw / 4, first + part).toByte)
      val target                  = WorkgroupLane.inWarp(base, lane)
      effects.stageRegister(target, operation.low, bytes(lane.raw % 4 * 4))
      effects.stageRegister(target, operation.high, bytes(16 + lane.raw % 4 * 4))
    }
  }

  private def rowAddresses(
      context: VMContext,
      base: WorkgroupLane.WarpBase,
      address: Value,
      count: Int,
      space: MemorySpace
  ): Either[Fault, Vec[Long]] = Vec.tabulate(count)(identity).traverse { lane =>
    val pointer = context.read(WorkgroupLane.fromIndex(base + lane), address)
    Either.cond(
      pointer % 16 == 0 && shared(space, pointer),
      pointer,
      Fault.Misaligned("matrix row addresses must be 16-byte aligned shared addresses")
    )
  }

  /** A fragment's elements as their encodings, signed ones sign-extended, in row-major order within each partition. */
  def matrix(
      fragment: Fragment,
      values: Vec[Value],
      base: WorkgroupLane.WarpBase,
      context: VMContext
  ): Array[Long] = {
    val area           = fragment.rows * fragment.columns
    val result         = Array.ofDim[Long](area * fragment.partitions)
    val valueRegisters = values.zip(Vec.indices(values.nonNegativeSize))
    val parts          = Vec.indices(fragment.element.packed)
    val element        = fragment.element
    val signed         = element match {
      case Element.S8 | Element.S4 | Element.S32 => true
      case _                                     => false
    }
    WarpLane.values.foreach { lane =>
      valueRegisters.foreach { (value, register) =>
        val bits = context.read(WorkgroupLane.inWarp(base, lane), value)
        parts.foreach { part =>
          val (row, col)  = fragment.coordinate(lane, register * element.packed + part)
          val resultIndex = fragment.partition(lane) * area + row * fragment.columns + col
          val code        = element.code(bits >>> (part.raw * element.bits))
          result(resultIndex) = if (signed) Bits.signExtend(code, element.bits.widen) else code
        }
      }
    }
    result
  }
}
