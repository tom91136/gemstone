package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import VMIR.Matrix.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** NVIDIA control flow, asynchronous copy and warp matrix semantics, as requests to a [[NvidiaMachine]]. */
private[isa] object MachineSemantics {

  extension [V, L, D, C, F[_]](inline machine: NvidiaMachine[V, L, D, C, F]) {
    inline def convergence(token: Register, action: ConvergenceAction): F[Step] = action match {
      case ConvergenceAction.Setup       => machine.setupConvergence(token)(machine.advance)
      case ConvergenceAction.Synchronise => machine.synchroniseConvergence(token)
      case ConvergenceAction.Break       => machine.leaveConvergence(token)(machine.advance)
    }
  }

  inline def barrierSync[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      id: Value,
      count: Option[Value]
  ): F[Step] = workgroupBarrier(algebra, machine)(id, count, blocking = true, None)

  // arrivals always name a thread count, which syncs and reductions may omit
  @SuppressWarnings(Array("scalafix:ApiConsistency.inconsistent-parameter"))
  inline def barrierArrive[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      id: Value,
      count: Value
  ): F[Step] = workgroupBarrier(algebra, machine)(id, Some(count), blocking = false, None)

  inline def barrierReduce[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      result: BarrierResult,
      id: Value,
      predicate: Predicate,
      count: Option[Value],
      negated: Boolean
  ): F[Step] =
    workgroupBarrier(algebra, machine)(id, count, blocking = true, Some(BarrierReduction(result, predicate, negated)))

  private inline def workgroupBarrier[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      id: Value,
      count: Option[Value],
      inline blocking: Boolean,
      inline reduction: Option[BarrierReduction]
  ): F[Step] = (id, count) match {
    case (Value.Immediate(barrier), None) => uniformArrival(algebra, machine)(barrier, 0L, blocking, reduction)
    case (Value.Immediate(barrier), Some(Value.Immediate(expected))) =>
      uniformArrival(algebra, machine)(barrier, expected, blocking, reduction)
    case _ =>
      machine.parkEach(
        lane => machine.read(lane, id),
        lane => count.fold(algebra.literal(0L))(machine.read(lane, _)),
        blocking,
        reduction
      )
  }

  private inline def uniformArrival[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      barrier: Long,
      expected: Long,
      inline blocking: Boolean,
      inline reduction: Option[BarrierReduction]
  ): F[Step] = inline reduction match {
    case None          => machine.park(algebra.literal(barrier), algebra.literal(expected), blocking)
    case Some(reduced) => machine.parkReduce(algebra.literal(barrier), algebra.literal(expected), reduced)
  }

  private inline def unconverged: Fault =
    Fault.Backend("tensor .sync.aligned instruction requires all 32 lanes of each participating warp")

  /** `movmatrix`: the 8x8 matrix of 16-bit elements whose rows lanes hold, transposed. */
  inline def moveMatrix[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Register, source: Value): F[Step] =
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.gather(UnitAlgebra.Slot.First, source) { view =>
        machine.writeEach(Vec(destination)) { (lane, _) =>
          val index = machine.laneIndex(lane)
          val first = algebra.or(
            algebra.shiftLeft(algebra.and(index, algebra.literal(3L)), algebra.literal(3L)),
            algebra.shiftRightLogical(index, algebra.literal(3L))
          )
          val second           = algebra.or(first, algebra.literal(4L))
          val shift            = algebra.shiftLeft(algebra.and(index, algebra.literal(4L)), algebra.literal(2L))
          def half(from: V): V =
            algebra.and(algebra.shiftRightLogical(machine.peer(lane, view, from), shift), algebra.literal(0xffffL))
          algebra.or(half(first), algebra.shiftLeft(half(second), algebra.literal(16L)))
        }(machine.advance)
      }
    }

  /** `ldmatrix`: the rows of `tile.count` matrices, whose addresses the first lanes hold, distributed across the warp
    * as [[MatrixLayout]] places them.
    */
  inline def loadMatrix[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(tile: Tile, destinations: Vec[Register], address: Value): F[Step] = {
    val rows   = tile.count.raw * tile.addresses.raw
    val parts  = 4 / tile.shape.bytes
    val layout = algebra.literal(MatrixLayout.code(tile))
    val form   = MatrixLayout.form(tile) << 8
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.everyLane { lane =>
        val pointer = machine.read(lane, address)
        machine.require(
          algebra.or(
            algebra.lessOrEqual(algebra.literal(rows.toLong), machine.laneIndex(lane), signed = false),
            algebra.and(aligned16(algebra, pointer), inSharedRange(algebra, pointer, 16L))
          ),
          "invalid matrix row address"
        )(Fault.Misaligned("matrix row addresses must be 16-byte aligned shared addresses"))(machine.done)
      }(
        machine.gatherLoad(UnitAlgebra.Slot.First, rows, Space.Shared, address, 0L, 64) { low =>
          machine.gatherLoad(UnitAlgebra.Slot.Second, rows, Space.Shared, address, 8L, 64) { high =>
            machine.writeEach(destinations) { (lane, register) =>
              val index                 = machine.laneIndex(lane)
              def element(part: Int): V = {
                val position = algebra.literal(4L * register + part)
                val source   = machine.compute(MatrixLayout.Row, layout, index, position)
                val column   = machine.compute(MatrixLayout.Column, layout, index, position)
                machine.compute(
                  MatrixLayout.Element,
                  if (form == 0L) column else algebra.add(algebra.literal(form), column),
                  machine.peer(lane, low, source),
                  machine.peer(lane, high, source)
                )
              }
              def placed(part: Int): V =
                algebra.shiftLeft(element(part), algebra.literal(8L * tile.shape.bytes * part))
              val pair = algebra.or(element(0), placed(1))
              if (parts == 2) pair else algebra.or(pair, algebra.or(placed(2), placed(3)))
            }(machine.advance)
          }
        }
      )
    }
  }

  /** `stmatrix`: the rows of `tile.count` matrices, whose addresses the first lanes hold, written from the registers
    * `sources` the lanes hold, as [[MatrixLayout]] places them.
    */
  inline def storeMatrix[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(tile: Tile, sources: Vec[Value], address: Value, space: MemorySpace): F[Step] = {
    val rows                   = tile.count.raw * tile.addresses.raw
    val parts                  = 4 / tile.shape.bytes
    val width                  = 8 * tile.shape.bytes
    val layout                 = algebra.literal(MatrixLayout.code(tile))
    val stored: ScalarBitWidth = if (tile.shape.bytes == 2) 16 else 8
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.everyLane { lane =>
        val pointer = machine.read(lane, address)
        machine.require(
          algebra.or(
            algebra.lessOrEqual(algebra.literal(rows.toLong), machine.laneIndex(lane), signed = false),
            sharedRow(algebra, pointer, space)
          ),
          "invalid matrix row address"
        )(Fault.Misaligned("matrix row addresses must be 16-byte aligned shared addresses"))(machine.done)
      }(
        machine.gather(UnitAlgebra.Slot.First, address) { rowAddresses =>
          machine.everyLane { lane =>
            def storeFrom(index: Int): F[Unit] = {
              val register = index / parts
              val part     = index % parts
              val position = algebra.literal(4L * register + part)
              val row      = machine.compute(MatrixLayout.Row, layout, machine.laneIndex(lane), position)
              val column   = machine.compute(MatrixLayout.Column, layout, machine.laneIndex(lane), position)
              val target   = algebra.add(
                machine.peer(lane, rowAddresses, row),
                algebra.multiply(column, algebra.literal(tile.shape.bytes.toLong))
              )
              val value = machine.read(lane, sources(register))
              val field =
                if (part == 0) value else algebra.shiftRightLogical(value, algebra.literal((part * width).toLong))
              machine.storeShared(target, field, stored)(
                if (index + 1 < sources.size * parts) storeFrom(index + 1) else machine.done
              )
            }
            storeFrom(0)
          }(machine.advance)
        }
      )
    }
  }

  /** `wmma.load`: the elements of the fragment at `address`, whose rows lie `stride` elements apart, placed in
    * `destinations` as the WMMA layout deals them out.
    */
  inline def loadFragment[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      fragment: Fragment,
      destinations: Vec[Register],
      address: Value,
      stride: Value,
      columnMajor: Boolean,
      space: MemorySpace
  ): F[Step] = {
    val packed = fragment.element.packed
    val bits   = fragment.element.bits
    val width  = elementWidth(bits)
    val memory = memorySpace(space)
    fragmentAccess(algebra, machine)(fragment, address, stride, columnMajor, space) { (lane, pointer, leading) =>
      def partFrom(register: Int, part: Int, value: V): F[Unit] = {
        val bit = elementBit(algebra, machine)(fragment, lane, leading, columnMajor, register * packed + part)
        machine.load(lane, memory, algebra.add(pointer, algebra.shiftRightLogical(bit, algebra.literal(3L))), width) {
          loaded =>
            val field =
              if (bits < 8)
                algebra.and(
                  algebra.shiftRightLogical(loaded, algebra.and(bit, algebra.literal(7L))),
                  algebra.literal((1L << bits) - 1L)
                )
              else loaded
            val combined =
              if (part == 0) field
              else algebra.or(value, algebra.shiftLeft(field, algebra.literal((part * bits).toLong)))
            if (part + 1 < packed) partFrom(register, part + 1, combined)
            else machine.write(lane, destinations(register), combined)(loadFrom(register + 1))
        }
      }
      def loadFrom(register: Int): F[Unit] =
        if (register < destinations.size) partFrom(register, 0, algebra.literal(0L))
        else machine.done
      loadFrom(0)
    }
  }

  /** `wmma.store`: the elements of the fragment held in `sources`, stored to the matrix at `address`, whose rows lie
    * `stride` elements apart, as the WMMA layout deals them out.
    */
  inline def storeFragment[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      fragment: Fragment,
      sources: Vec[Value],
      address: Value,
      stride: Value,
      columnMajor: Boolean,
      space: MemorySpace
  ): F[Step] = {
    val packed = fragment.element.packed
    val bits   = fragment.element.bits
    val width  = elementWidth(bits)
    val memory = memorySpace(space)
    fragmentAccess(algebra, machine)(fragment, address, stride, columnMajor, space) { (lane, pointer, leading) =>
      def storeFrom(index: Int): F[Unit] = {
        val register = index / packed
        val part     = index % packed
        val bit      = elementBit(algebra, machine)(fragment, lane, leading, columnMajor, index)
        val value    = machine.read(lane, sources(register))
        val field    = if (part == 0) value else algebra.shiftRightLogical(value, algebra.literal((part * bits).toLong))
        machine.store(
          lane,
          memory,
          algebra.add(pointer, algebra.shiftRightLogical(bit, algebra.literal(3L))),
          field,
          width
        )(
          if (index + 1 < sources.size * packed) storeFrom(index + 1) else machine.done
        )
      }
      if (bits < 8)
        machine.require(
          algebra.lessOrEqual(algebra.literal(8L), algebra.literal(bits.toLong), signed = true),
          "wmma store width"
        )(Fault.Backend("WMMA stores whole bytes"))(machine.done)
      else storeFrom(0)
    }
  }

  /** The lanes of a WMMA access, each as `body` finds it with its unit's address and stride, once the warp agrees on
    * both and they are aligned and in range.
    */
  private inline def fragmentAccess[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(fragment: Fragment, address: Value, stride: Value, columnMajor: Boolean, space: MemorySpace)(
      inline body: (L, V, V) => F[Unit]
  ): F[Step] = {
    val bits    = fragment.element.bits
    val minimum = if (columnMajor) fragment.rows else fragment.columns
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.gather(UnitAlgebra.Slot.First, address) { pointers =>
        machine.gather(UnitAlgebra.Slot.Second, stride) { strides =>
          machine.everyLane { lane =>
            val index   = machine.laneIndex(lane)
            val pointer = machine.peer(lane, pointers, index)
            val leading = machine.peer(lane, strides, index)
            machine.require(machine.agreed(lane, pointers), "uniform wmma address")(
              Fault.Backend("WMMA address and stride must be uniform within each warp")
            ) {
              machine.require(machine.agreed(lane, strides), "uniform wmma stride")(
                Fault.Backend("WMMA address and stride must be uniform within each warp")
              ) {
                machine.require(fragmentAligned(algebra, pointer, leading, bits), "wmma alignment")(
                  Fault.Misaligned("WMMA addresses must be aligned to 32 bytes and strides to 16")
                ) {
                  machine.require(
                    algebra.lessOrEqual(algebra.literal(minimum.toLong), leading, signed = true),
                    "wmma stride"
                  )(Fault.Backend("WMMA stride is smaller than the matrix leading dimension")) {
                    machine.require(
                      fragmentWindow(algebra, machine)(fragment, space, columnMajor, pointer, leading),
                      "wmma window"
                    )(
                      Fault.Misaligned("WMMA matrix lies outside the cluster's shared windows")
                    )(body(lane, pointer, leading))
                  }
                }
              }
            }
          }(machine.advance)
        }
      }
    }
  }

  /** Whether the matrix at `pointer`, whose rows lie `leading` elements apart, lies within the shared windows of the
    * cluster if `space` is shared.
    */
  private inline def fragmentWindow[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(fragment: Fragment, space: MemorySpace, columnMajor: Boolean, pointer: V, leading: V): V = space match {
    case MemorySpace.Shared =>
      val major = if (columnMajor) fragment.columns else fragment.rows
      val minor = if (columnMajor) fragment.rows else fragment.columns
      val span  =
        algebra.add(algebra.multiply(algebra.literal(major.toLong - 1L), leading), algebra.literal(minor.toLong))
      val bits = algebra.multiply(span, algebra.literal(fragment.element.bits.toLong))
      val last = algebra.add(
        pointer,
        algebra.subtract(
          algebra.shiftRightLogical(algebra.add(bits, algebra.literal(7L)), algebra.literal(3L)),
          algebra.literal(1L)
        )
      )
      algebra.and(inSharedWindow(algebra, machine, pointer), inSharedWindow(algebra, machine, last))
    case _ => algebra.literal(1L)
  }

  private inline def inSharedWindow[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F],
      address: V
  ): V = algebra.and(
    inSharedRange(algebra, address, 1L),
    algebra.lessThan(machine.rankOf(address), machine.clusterSize, signed = true)
  )

  /** The bit at which the fragment element `element` of `lane` lies, whose matrix rows are `leading` elements apart. */
  private inline def elementBit[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(fragment: Fragment, lane: L, leading: V, columnMajor: Boolean, element: Int): V = {
    val layout   = algebra.literal(MatrixLayout.fragmentCode(fragment))
    val index    = machine.laneIndex(lane)
    val position = algebra.literal(element.toLong)
    val row      = machine.compute(MatrixLayout.FragmentRow, layout, index, position)
    val column   = machine.compute(MatrixLayout.FragmentColumn, layout, index, position)
    val offset   =
      if (columnMajor) algebra.add(algebra.multiply(column, leading), row)
      else algebra.add(algebra.multiply(row, leading), column)
    algebra.multiply(offset, algebra.literal(fragment.element.bits.toLong))
  }

  private def elementWidth(bits: Int in (1, 4, 8, 16, 32, 64)): Int in (8, 16, 32, 64) = bits match {
    case wide: (Int in (8, 16, 32, 64)) @unchecked if bits >= 8 => wide
    case _                                                      => 8
  }

  private def memorySpace(space: MemorySpace): Space = space match {
    case MemorySpace.Generic => Space.Generic
    case MemorySpace.Global  => Space.Global
    case MemorySpace.Shared  => Space.Shared
  }

  // the byte offset truncates towards zero, so its magnitude decides whether it is a multiple of 16
  private inline def fragmentAligned[V](inline algebra: Algebra[V], pointer: V, leading: V, bits: Int): V = {
    val product   = algebra.multiply(leading, algebra.literal(bits.toLong))
    val magnitude = algebra.select(
      algebra.lessThan(product, algebra.literal(0L), signed = true),
      algebra.subtract(algebra.literal(0L), product),
      product
    )
    algebra.and(
      algebra.equal(algebra.and(pointer, algebra.literal(31L)), algebra.literal(0L)),
      algebra.equal(
        algebra.and(algebra.shiftRightLogical(magnitude, algebra.literal(3L)), algebra.literal(15L)),
        algebra.literal(0L)
      )
    )
  }

  private inline def sharedRow[V](inline algebra: Algebra[V], pointer: V, space: MemorySpace): V = space match {
    case MemorySpace.Shared  => aligned16(algebra, pointer)
    case MemorySpace.Generic => algebra.and(aligned16(algebra, pointer), inSharedRange(algebra, pointer, 1L))
    case MemorySpace.Global  => algebra.literal(0L)
  }

  private inline def aligned16[V](inline algebra: Algebra[V], value: V): V =
    algebra.equal(algebra.and(value, algebra.literal(15L)), algebra.literal(0L))

  private inline def aligned8[V](inline algebra: Algebra[V], value: V): V =
    algebra.equal(algebra.and(value, algebra.literal(7L)), algebra.literal(0L))

  private inline def transactionRange[V](inline algebra: Algebra[V], value: V): V = algebra.and(
    algebra.lessOrEqual(algebra.literal(0L), value, signed = true),
    algebra.lessOrEqual(value, algebra.literal(MBarrier.MaximumCount), signed = true)
  )

  // plain vals: inline getters from VMAddressSpace exceed the inline depth of loadMatrix
  val sharedBase: Long       = VMAddressSpace.SharedBase
  val sharedLimit: Long      = VMAddressSpace.SharedLimit
  val sharedWindowBase: Long = VMAddressSpace.SharedWindowBase
  val sharedWindowSize: Long = VMAddressSpace.SharedWindowSize
  val tensorMapBytes: Long   = TensorMap.Bytes.toLong

  // unsigned, so an address below a window wraps above the window size
  private inline def inSharedRange[V](inline algebra: Algebra[V], value: V, bytes: Long): V = algebra.or(
    algebra.lessOrEqual(
      algebra.subtract(value, algebra.literal(sharedBase)),
      algebra.literal(sharedLimit - sharedBase - bytes),
      signed = false
    ),
    algebra.lessOrEqual(
      algebra.subtract(value, algebra.literal(sharedWindowBase)),
      algebra.literal(sharedWindowSize - bytes),
      signed = false
    )
  )

  /** A copy of the `sourceSize` bytes at a global source into shared memory, zero-filled to `size` bytes. */
  inline def asyncCopy[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Value, source: Value, size: Int, sourceSize: Value): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, destination)
    val origin = machine.read(lane, source)
    val count  = machine.read(lane, sourceSize)
    machine.require(
      algebra.and(
        algebra.lessOrEqual(algebra.literal(0L), count, signed = true),
        algebra.lessOrEqual(count, algebra.literal(size.toLong), signed = true)
      ),
      "cp.async source size"
    )(Fault.Backend(s"cp.async source size $count exceeds copy size $size")) {
      machine.loadBytes(lane, Space.Global, origin, count, "cp.async source") { data =>
        machine.storeSharedBytes(target, data, algebra.literal(size.toLong))(machine.done)
      }
    }
  }(machine.advance)

  /** A bulk copy into shared memory, completing its transaction bytes on the mbarrier of every destination CTA. The
    * `.ignore_oob` bytes at either end of the copy keep their contents.
    */
  inline def bulkCopy[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      copy: Instruction.AsyncBulkCopy
  ): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, copy.destination)
    val origin = machine.read(lane, copy.source)
    val bytes  = machine.read(lane, copy.size)
    val left   = copy.ignored match {
      case Some(bounds) => machine.read(lane, bounds.left)
      case None         => algebra.literal(0L)
    }
    val right = copy.ignored match {
      case Some(bounds) => machine.read(lane, bounds.right)
      case None         => algebra.literal(0L)
    }
    val inCluster =
      if (copy.clusterDestination && copy.sharedSource) machine.explicitCluster else algebra.literal(1L)
    val inRange = algebra.and(
      algebra.and(aligned16(algebra, target), aligned16(algebra, origin)),
      algebra.and(aligned16(algebra, bytes), transactionRange(algebra, bytes))
    )
    val ignoredInRange = copy.ignored match {
      case Some(_) =>
        algebra.and(
          algebra.and(
            algebra.and(
              algebra.lessOrEqual(algebra.literal(0L), left, signed = true),
              algebra.lessOrEqual(left, algebra.literal(15L), signed = true)
            ),
            algebra.and(
              algebra.lessOrEqual(algebra.literal(0L), right, signed = true),
              algebra.lessOrEqual(right, algebra.literal(15L), signed = true)
            )
          ),
          algebra.lessOrEqual(algebra.add(left, right), bytes, signed = true)
        )
      case None => algebra.literal(1L)
    }
    val kept = copy.ignored match {
      case Some(_) => algebra.subtract(algebra.subtract(bytes, left), right)
      case None    => bytes
    }
    val sourceStart = copy.ignored match {
      case Some(_) => algebra.add(origin, left)
      case None    => origin
    }
    machine.require(inCluster, "cluster launch")(
      Fault.IllegalInstruction("a bulk copy of shared::cta into shared::cluster memory needs a launch with a cluster")
    ) {
      machine.require(algebra.and(inRange, ignoredInRange), "invalid bulk copy")(
        Fault.Backend(
          s"invalid bulk copy: destination=$target, source=$origin, size=$bytes, ignored=$left+$right"
        )
      ) {
        machine.sharedWindow(lane, target, bytes, "bulk copy destination") { (owner, start) =>
          val offset = copy.ignored match {
            case Some(_) => algebra.add(start, left)
            case None    => start
          }
          machine.sharedWindow(lane, machine.read(lane, copy.barrier), algebra.literal(8L), "bulk copy mbarrier") {
            (_, word) =>
              machine.loadBytes(
                lane,
                if (copy.sharedSource) Space.Shared else Space.Generic,
                sourceStart,
                kept,
                "bulk copy source"
              ) { data =>
                machine.eachDestination(lane, owner, copy.multicast) { cta =>
                  machine.storeCluster(cta, offset, data) {
                    machine.completeTx(cta, word, bytes)(machine.done)
                  }
                }(machine.done)
              }
          }
        }
      }
    }
  }(machine.advance)

  inline def bulkStore[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      destination: Value,
      source: Value,
      size: Value,
      byteMask: Value
  ): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, destination)
    val origin = machine.read(lane, source)
    val bytes  = machine.read(lane, size)
    machine.require(
      algebra.and(algebra.and(aligned16(algebra, target), aligned16(algebra, origin)), aligned16(algebra, bytes)),
      "invalid bulk store"
    )(Fault.Backend(s"invalid bulk store: destination=$target, source=$origin, size=$bytes")) {
      machine.require(transactionRange(algebra, bytes), "invalid bulk store size")(
        Fault.Backend(s"invalid mbarrier transaction size: $bytes")
      )(machine.storeGlobalMasked(lane, target, origin, bytes, machine.read(lane, byteMask))(machine.done))
    }
  }(machine.advance)

  inline def bulkFill[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      address: Value,
      size: Value
  ): F[Step] = machine.everyLane { lane =>
    val bytes = machine.read(lane, size)
    machine.sharedWindow(lane, machine.read(lane, address), bytes, "bulk fill") { (owner, start) =>
      machine.require(
        algebra.and(
          algebra.lessOrEqual(algebra.literal(0L), bytes, signed = true),
          algebra.equal(algebra.and(bytes, algebra.literal(7L)), algebra.literal(0L))
        ),
        "invalid bulk fill size"
      )(Fault.Backend(s"invalid bulk fill size $bytes"))(machine.clearCluster(owner, start, bytes)(machine.done))
    }
  }(machine.advance)

  inline def bulkReduce[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      copy: Instruction.AsyncBulkReduce
  ): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, copy.destination)
    val origin = machine.read(lane, copy.source)
    val bytes  = machine.read(lane, copy.size)
    machine.require(
      algebra.and(
        algebra.and(aligned16(algebra, target), aligned16(algebra, origin)),
        algebra.and(aligned16(algebra, bytes), transactionRange(algebra, bytes))
      ),
      "invalid bulk reduction"
    )(Fault.Backend(s"invalid bulk reduction: destination=$target, source=$origin, size=$bytes")) {
      machine.loadBytes(lane, Space.Shared, origin, bytes, "bulk reduction source") { data =>
        copy.barrier match {
          case Some(word) =>
            machine.sharedWindow(lane, target, bytes, "bulk reduction destination") { (owner, start) =>
              machine.sharedWindow(lane, machine.read(lane, word), algebra.literal(8L), "bulk reduction mbarrier") {
                (_, offset) =>
                  machine.reduceBytes(owner, start, data, copy)(
                    machine.completeTx(owner, offset, bytes)(machine.done)
                  )
              }
            }
          case None => machine.reduceGlobal(lane, target, data, copy)(machine.done)
        }
      }
    }
  }(machine.advance)

  /** A copy of a tensor map from one generic address to another, each address checked against the cluster if shared. */
  inline def tensorMapCopy[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      destination: Value,
      source: Value
  ): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, destination)
    val origin = machine.read(lane, source)
    val size   = algebra.literal(tensorMapBytes)
    machine.require(machine.inCluster(origin, size), "tensor map source")(
      Fault.Backend(s"tensor map names a CTA outside the cluster: $origin")
    ) {
      machine.loadBytes(lane, Space.Generic, origin, size, "tensor map") { data =>
        machine.require(machine.inCluster(target, size), "tensor map destination")(
          Fault.Backend(s"tensor map names a CTA outside the cluster: $target")
        )(machine.storeBytes(lane, Space.Generic, target, data, "tensor map")(machine.done))
      }
    }
  }(machine.advance)

  /** A field of the tensor map at a generic address replaced by a value the field accepts. */
  inline def tensorMapReplace[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      address: Value,
      field: TensorMap.Field,
      index: Int,
      value: Value
  ): F[Step] = machine.everyLane { lane =>
    val target  = machine.read(lane, address)
    val operand = machine.read(lane, value)
    machine.require(
      algebra.equal(algebra.and(target, algebra.literal(63L)), algebra.literal(0L)),
      "tensor map alignment"
    )(Fault.Misaligned(s"misaligned tensor map at $target")) {
      machine.loadBytes(lane, Space.Generic, target, algebra.literal(tensorMapBytes), "tensor map") { current =>
        machine.require(accepted(algebra, machine, field, operand), "tensor map field")(
          Fault.Backend(s"invalid tensor map $field value $operand")
        ) {
          machine.replaceTensorMap(current, field, index, operand) { updated =>
            machine.storeBytes(lane, Space.Generic, target, updated, "tensor map")(machine.done)
          }
        }
      }
    }
  }(machine.advance)

  private inline def accepted[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F],
      field: TensorMap.Field,
      operand: V
  ): V = field match {
    case TensorMap.Field.ElementType =>
      algebra.notEqual(machine.compute(TensorLayout.ElementType, operand), algebra.literal(0L))
    case _ =>
      TensorMap.limit(field) match {
        case Some(limit) => algebra.lessOrEqual(operand, algebra.literal(limit), signed = true)
        case None        => algebra.literal(1L)
      }
  }

  /** A tensor copy between global memory and the shared box of a tensor map: its descriptor read and validated, then
    * the runs of box elements copied or reduced, then the transaction completed on the mbarrier of each destination.
    */
  inline def asyncTensor[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      copy: Instruction.AsyncTensor
  ): F[Step] = machine.everyLane { lane =>
    val coordinates = copy.coordinates.map(coordinate => algebra.signExtend(machine.read(lane, coordinate), 32))
    val offsets     = copy.offsets.map(offset => algebra.and(machine.read(lane, offset), algebra.literal(0xffffL)))
    machine.loadBytes(
      lane,
      Space.Generic,
      machine.read(lane, copy.tensorMap),
      algebra.literal(tensorMapBytes),
      "tensor map"
    ) { map =>
      machine.tensorTransfer(map, coordinates, offsets, copy) { transfer =>
        tensorChecked(algebra, machine)(map, transfer, coordinates, copy) {
          copy.operation match {
            case TensorMap.Operation.Prefetch => machine.done
            case TensorMap.Operation.Load     => tensorLoad(algebra, machine)(lane, transfer, copy)
            case operation                    => tensorStore(algebra, machine)(lane, transfer, copy, operation)
          }
        }
      }
    }
  }(machine.advance)

  private inline def tensorChecked[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(
      map: D,
      transfer: D,
      coordinates: Vec[V],
      copy: Instruction.AsyncTensor
  )(inline next: => F[Unit]): F[Unit] = {
    val rank   = machine.tensorField(map, TensorMapping.Field.Rank, 0)
    val mode   = machine.tensorField(map, TensorMapping.Field.Mode, 0)
    val code   = machine.tensorField(map, TensorMapping.Field.Element, 0)
    val tf32   = machine.tensorField(map, TensorMapping.Field.Tf32, 0)
    val values = algebra.primitive(code, tf32)((c: UInt[64], t: UInt[64]) => TensorLayout.elementValues(c, t))
    val bytes  = algebra.primitive(code, tf32)((c: UInt[64], t: UInt[64]) => TensorLayout.elementBytes(c, t))
    val plain  = copy.operation match {
      case TensorMap.Operation.Prefetch => algebra.literal(1L)
      case _ => TensorLayout.plain(algebra, machine.tensorField(map, TensorMapping.Field.Interleave, 0))
    }
    val reducible = copy.operation match {
      case TensorMap.Operation.Reduce(_) => algebra.equal(TensorLayout.packed(algebra, values), algebra.literal(0L))
      case _                             => algebra.literal(1L)
    }
    val storable = copy.operation match {
      case TensorMap.Operation.Store => algebra.equal(TensorLayout.unstorable(algebra, code), algebra.literal(0L))
      case _                         => algebra.literal(1L)
    }
    val dimension = machine.tensorField(map, TensorMapping.Field.Dimension, 0)
    val traversal = machine.tensorField(map, TensorMapping.Field.Traversal, 0)
    val box       = machine.tensorField(map, TensorMapping.Field.Box, 0)
    val channels  = machine.tensorField(map, TensorMapping.Field.Channels, 0)
    machine.require(TensorMap.valid(algebra, rank, mode, values), "tensor map")(
      Fault.Backend("invalid tensor map")
    ) {
      machine.require(
        TensorLayout.matches(algebra, rank, mode, coordinates.size, copy.gather, copy.im2col),
        "tensor copy rank"
      )(
        Fault.Backend(
          s"tensor copy does not match its ${machine.tensorFact(transfer, TensorLayout.Fact.Rank)}D " +
            s"${machine.tensorText(transfer, TensorLayout.Text.Mode)} tensor map"
        )
      ) {
        machine.require(plain, "tensor map interleave")(
          Fault.Backend(
            s"tensor maps with interleave ${machine.tensorFact(transfer, TensorLayout.Fact.Interleave)} are not modelled"
          )
        ) {
          machine.require(reducible, "tensor reduction form")(
            Fault.Backend(s"tensor reductions have no ${machine.tensorText(transfer, TensorLayout.Text.Element)} form")
          ) {
            machine.require(storable, "tensor store form")(
              Fault.Backend(s"tensor stores have no ${machine.tensorText(transfer, TensorLayout.Text.Element)} form")
            ) {
              machine.require(
                TensorLayout.aligned(algebra, mode, bytes, coordinates(0), copy.gather),
                "tensor tile coordinate"
              )(
                Fault.IllegalInstruction(s"tensor tile coordinate ${coordinates(0)} is not a multiple of 16 bytes")
              ) {
                machine.require(
                  TensorLayout.splits(algebra, values, mode, coordinates(0), dimension, traversal, box, channels),
                  "tensor copy units"
                )(
                  Fault.Backend(
                    s"tensor copy of ${machine.tensorText(transfer, TensorLayout.Text.Element)} elements does not split into " +
                      s"units of ${machine.tensorFact(transfer, TensorLayout.Fact.Values)}"
                  )
                )(next)
              }
            }
          }
        }
      }
    }
  }

  private inline def tensorLoad[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(lane: L, transfer: D, copy: Instruction.AsyncTensor): F[Unit] = {
    val bytes = machine.tensorFact(transfer, TensorLayout.Fact.Bytes)
    machine.sharedWindow(lane, machine.read(lane, copy.shared), bytes, "tensor copy destination") { (owner, start) =>
      copy.barrier match {
        case None =>
          machine.require(algebra.literal(0L), "tensor load mbarrier")(
            Fault.Backend("tensor load without an mbarrier")
          )(machine.done)
        case Some(word) =>
          machine.sharedWindow(lane, machine.read(lane, word), algebra.literal(8L), "tensor copy mbarrier") {
            (signalled, barrier) =>
              machine.eachRun(transfer)(run => machine.loadRun(lane, transfer, run)(machine.done)) {
                machine.eachDestination(lane, owner, copy.multicast) { cta =>
                  machine.placeBox(cta, start, transfer) {
                    if (copy.pair) {
                      val rank = machine.peerRank(cta, signalled)
                      machine.require(machine.hasCta(rank), "cta_group::2 peer")(
                        Fault.Backend(s"cta_group::2 needs CTA $rank of the pair in the cluster")
                      )(machine.completeTx(machine.ctaAt(rank), barrier, bytes)(machine.done))
                    } else machine.completeTx(cta, barrier, bytes)(machine.done)
                  }
                }(machine.done)
              }
          }
      }
    }
  }

  private inline def tensorStore[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(lane: L, transfer: D, copy: Instruction.AsyncTensor, operation: TensorMap.Operation): F[Unit] =
    machine.sharedWindow(
      lane,
      machine.read(lane, copy.shared),
      machine.tensorFact(transfer, TensorLayout.Fact.Bytes),
      "tensor copy source"
    ) { (owner, start) =>
      machine.fetchBox(owner, start, transfer) {
        operation match {
          case TensorMap.Operation.Reduce(operator) =>
            machine.require(
              algebra.equal(machine.tensorFact(transfer, TensorLayout.Fact.Unreducible), algebra.literal(0L)),
              "tensor reduction operator"
            )(
              Fault.Backend(
                s"tensor reduction $operator has no ${machine.tensorText(transfer, TensorLayout.Text.Element)} form"
              )
            )(machine.eachRun(transfer)(run => machine.reduceRun(lane, transfer, run, operator)(machine.done))(
              machine.done
            ))
          case _ => machine.eachRun(transfer)(run => machine.storeRun(lane, transfer, run)(machine.done))(machine.done)
        }
      }
    }

  inline def sharedStore[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      address: Value,
      sources: Vec[Value],
      width: ScalarBitWidth,
      barrier: Value
  ): F[Step] = {
    val unit  = width.toScalarByteWidth.toLong
    val bytes = algebra.literal(unit * sources.size)
    machine.everyLane { lane =>
      machine.sharedWindow(lane, machine.read(lane, address), bytes, "asynchronous store") { (owner, start) =>
        machine.sharedWindow(lane, machine.read(lane, barrier), algebra.literal(8L), "asynchronous store mbarrier") {
          (holder, offset) =>
            machine.require(machine.sameCta(owner, holder), "asynchronous store cta")(
              Fault.Backend("asynchronous store and its mbarrier lie in different CTAs")
            ) {
              def storeFrom(index: Int): F[Unit] =
                if (index < sources.size)
                  machine.storeWord(
                    owner,
                    if (index == 0) start else algebra.add(start, algebra.literal(index * unit)),
                    machine.read(lane, sources(index)),
                    width
                  )(storeFrom(index + 1))
                else machine.completeTx(owner, offset, bytes)(machine.done)
              storeFrom(0)
            }
        }
      }
    }(machine.advance)
  }

  inline def sharedReduce[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      operation: AtomicOperation,
      barrier: Value
  ): F[Step] = {
    val bytes = algebra.literal(width.toScalarByteWidth.toLong)
    val word  = algebra.literal(8L)
    machine.everyLane { lane =>
      machine.sharedWindow(lane, machine.read(lane, address), bytes, "asynchronous reduction") { (owner, start) =>
        machine.sharedWindow(lane, machine.read(lane, barrier), word, "asynchronous reduction mbarrier") { (_, offset) =>
          machine.reduceWord(owner, start, machine.read(lane, source), width, operation)(
            machine.completeTx(owner, offset, bytes)(machine.done)
          )
        }
      }
    }(machine.advance)
  }

  inline def clusterMap[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      register: Register,
      address: Value,
      rank: Value
  ): F[Step] = machine.everyLane { lane =>
    val raw    = machine.read(lane, address)
    val target = machine.read(lane, rank)
    machine.requireShared(raw, "mapa of a non-shared address") {
      machine.require(
        algebra.and(
          algebra.lessOrEqual(algebra.literal(0L), target, signed = true),
          algebra.lessThan(target, machine.clusterSize, signed = true)
        ),
        "mapa rank"
      )(Fault.Backend(s"mapa names rank $target outside a ${machine.clusterSize}-CTA cluster")) {
        machine.write(lane, register, machine.clusterAddress(raw, target))(machine.done)
      }
    }
  }(machine.advance)

  inline def clusterRank[V, L, D, C, F[_]](inline algebra: Algebra[V], inline machine: NvidiaMachine[V, L, D, C, F])(
      register: Register,
      address: Value
  ): F[Step] = machine.everyLane { lane =>
    val raw = machine.read(lane, address)
    machine.requireShared(raw, "getctarank of a non-shared address") {
      val rank = machine.rankOf(raw)
      machine.write(
        lane,
        register,
        algebra.select(algebra.lessThan(rank, algebra.literal(0L), signed = true), machine.ctaRank, rank)
      )(machine.done)
    }
  }(machine.advance)

  inline def clusterTryCancel[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(response: Value, barrier: Value, multicast: Boolean): F[Step] = machine.everyLane { lane =>
    val responseAddress = machine.read(lane, response)
    val barrierAddress  = machine.read(lane, barrier)
    machine.require(
      algebra.and(aligned16(algebra, responseAddress), aligned8(algebra, barrierAddress)),
      "cluster cancellation alignment"
    )(Fault.Misaligned("unaligned cluster cancellation response or barrier")) {
      def cancel(to: V, signalled: V): F[Unit] =
        machine.mbarrierTransaction(lane, signalled, algebra.literal(16L), complete = true) {
          machine.storeShared(to, algebra.literal(0L), 64) {
            machine.storeShared(algebra.add(to, algebra.literal(8L)), algebra.literal(0L), 64)(machine.done)
          }
        }
      if (multicast) machine.eachRank { rank =>
        cancel(machine.clusterAddress(responseAddress, rank), machine.clusterAddress(barrierAddress, rank))
      }
      else cancel(responseAddress, barrierAddress)
    }
  }(machine.advance)

  extension [V, L, D, C, F[_]](inline machine: NvidiaMachine[V, L, D, C, F]) {
    inline def mbarrierLoad(address: Value): F[Step] =
      machine.everyLane { lane =>
        machine.mbarrierLoad(lane, machine.read(lane, address))(machine.done)
      }(machine.advance)

    inline def mbarrierInit(
        address: Value,
        count: Value
    ): F[Step] = machine.everyLane { lane =>
      machine.mbarrierInit(lane, machine.read(lane, address), machine.read(lane, count))(machine.done)
    }(machine.advance)

    inline def mbarrierInvalidate(
        address: Value
    ): F[Step] = machine.everyLane { lane =>
      machine.mbarrierInvalidate(lane, machine.read(lane, address))(machine.done)
    }(machine.advance)

    inline def mbarrierArrive(
        destination: Option[Register],
        address: Value,
        count: Value,
        bytes: Value,
        drop: Boolean,
        complete: Boolean
    ): F[Step] = machine.everyLane { lane =>
      machine.mbarrierArrive(
        lane,
        machine.read(lane, address),
        machine.read(lane, count),
        machine.read(lane, bytes),
        drop,
        complete
      ) { token =>
        destination.fold(machine.done)(register => machine.write(lane, register, token)(machine.done))
      }
    }(machine.advance)

    inline def mbarrierTransaction(
        address: Value,
        bytes: Value,
        complete: Boolean
    ): F[Step] = machine.everyLane { lane =>
      machine.mbarrierTransaction(lane, machine.read(lane, address), machine.read(lane, bytes), complete)(machine.done)
    }(machine.advance)

    inline def mbarrierTest(
        destination: Predicate,
        address: Value,
        phase: Value,
        parity: Boolean
    ): F[Step] = machine.everyLane { lane =>
      machine.mbarrierTest(lane, machine.read(lane, address), machine.read(lane, phase), parity) { completed =>
        machine.writePredicate(lane, destination, completed)(machine.done)
      }
    }(machine.advance)
  }

  inline def mbarrierPending[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Register, token: Value): F[Step] = machine.everyLane { lane =>
    machine.write(lane, destination, pendingArrivals(algebra, machine.read(lane, token)))(machine.done)
  }(machine.advance)

  /** The arrivals a token's phase still awaited: 2^20 less the count held from bit 43. */
  private inline def pendingArrivals[V](inline algebra: Algebra[V], token: V): V = algebra.subtract(
    algebra.literal(MBarrier.MaximumCount + 1L),
    algebra.and(
      algebra.shiftRightLogical(token, algebra.literal(MBarrier.PendingShift.toLong)),
      algebra.literal(MBarrier.MaximumCount)
    )
  )

  /** Each active lane pushes `address` on its return stack, which a legacy return pops. */
  inline def returnStackPush[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(address: Long): F[Step] = machine.everyLane { lane =>
    machine.pushReturn(lane, algebra.literal(address))(machine.done)
  }(machine.advance)

  /** `alloca`: `size` bytes aligned to `alignment` allocated on the lane's stack, whose frame-local address is written
    * to `register`.
    */
  inline def stackAllocate[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(register: Register, size: Value, alignment: Long): F[Step] = machine.everyLane { lane =>
    machine.allocate(lane, machine.read(lane, size), algebra.literal(alignment)) { pointer =>
      machine.write(lane, register, pointer)(machine.done)
    }
  }(machine.advance)

  extension [V, L, D, C, F[_]](inline machine: NvidiaMachine[V, L, D, C, F]) {

    /** `stacksave`: the lane's stack pointer, as a frame-local address, written to `register`. */
    inline def stackSave(register: Register): F[Step] =
      machine.everyLane { lane =>
        machine.write(lane, register, machine.stackPointer(lane))(machine.done)
      }(machine.advance)

    /** `stackrestore`: the lane's stack pointer set to the frame-local address `source`. */
    inline def stackRestore(source: Value): F[Step] =
      machine.everyLane { lane =>
        machine.restoreStack(lane, machine.read(lane, source))(machine.done)
      }(machine.advance)
  }

  private inline def leading[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F],
      lane: L
  ): V = algebra.equal(machine.laneIndex(lane), algebra.literal(0L))

  /** The rank of the CTA at `index` of the CTAs a tensor memory instruction addresses: the issuing CTA, or of its pair
    * the even one at 0 and the odd one at 1.
    */
  private inline def ctaRankAt[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(pair: Boolean, inline index: V): V =
    if (pair) algebra.add(algebra.subtract(machine.ctaRank, algebra.and(machine.ctaRank, algebra.literal(1L))), index)
    else machine.ctaRank

  /** `next` where the CTAs of the pair the instruction addresses are resident, which a single CTA always is. */
  private inline def pairResident[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(pair: Boolean)(inline next: => F[Unit]): F[Unit] = {
    val even = ctaRankAt(algebra, machine)(pair, algebra.literal(0L))
    val odd  = ctaRankAt(algebra, machine)(pair, algebra.literal(1L))
    residentRank(algebra, machine)(pair, even) {
      residentRank(algebra, machine)(pair, odd)(next)
    }
  }

  private inline def residentRank[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(pair: Boolean, rank: V)(inline next: => F[Unit]): F[Unit] =
    machine.require(if (pair) machine.hasCta(rank) else algebra.literal(1L), "cta pair")(
      Fault.Backend(s"cta_group::2 needs CTA $rank of the pair in the cluster")
    )(next)

  /** `tcgen05.alloc`: the first lane of each warp allocates `columns` columns of tensor memory and stores the first
    * column to the shared address `destination`.
    */
  inline def tensorMemoryAllocate[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Value, columns: Value): F[Step] =
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.everyLane { lane =>
        machine.when(leading(algebra, machine, lane)) {
          val target = machine.read(lane, destination)
          val count  = machine.read(lane, columns)
          machine.withCta(machine.ctaRank) { cta =>
            machine.require(machine.tensorMemoryPermitted(cta), "tcgen05.alloc permit")(
              Fault.Backend("tcgen05.alloc after the CTA relinquished its allocation permit")
            ) {
              val granules = machine.compute(TensorMemoryLayout.Granted, count)
              machine.require(algebra.notEqual(granules, algebra.literal(0L)), "tcgen05.alloc columns")(
                Fault.Backend(s"tcgen05.alloc of $count columns, which is not a power of two from 32 to 512")
              ) {
                machine.allocateTensorMemory(cta, granules) { start =>
                  machine.require(
                    algebra.lessThan(start, algebra.literal(TensorMemory.Columns.toLong), signed = false),
                    "tcgen05.alloc space"
                  )(
                    Fault.Backend(
                      s"tcgen05.alloc of $count columns waits forever: granules 0x" +
                        s"${machine.valueText(TensorMemoryLayout.Text.Hexadecimal, machine.tensorMemoryTaken(cta))} are taken"
                    )
                  )(machine.storeShared(target, start, 32)(machine.done))
                }
              }
            }
          }
        }(machine.done)
      }(machine.advance)
    }

  /** `tcgen05.dealloc`: the first lane of each warp frees the allocated granules the `columns` columns at `address`
    * cover.
    */
  inline def tensorMemoryDeallocate[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(address: Value, columns: Value): F[Step] =
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.everyLane { lane =>
        machine.when(leading(algebra, machine, lane)) {
          val raw                = machine.read(lane, address)
          val count              = machine.read(lane, columns)
          val mask               = machine.compute(TensorMemoryLayout.Freed, raw, count)
          def unallocated: Fault = Fault.Backend(
            s"tcgen05.dealloc of $count columns at 0x" +
              s"${machine.valueText(TensorMemoryLayout.Text.Hexadecimal, raw)}, which are not allocated"
          )
          machine.require(algebra.notEqual(mask, algebra.literal(0L)), "tcgen05.dealloc range")(unallocated) {
            machine.withCta(machine.ctaRank) { cta =>
              machine.freeTensorMemory(cta, mask) { freed =>
                machine.require(freed, "tcgen05.dealloc allocated")(unallocated)(machine.done)
              }
            }
          }
        }(machine.done)
      }(machine.advance)
    }

  /** `tcgen05.mma`: on each issuing thread, every element of D of the product its instruction descriptor describes,
    * from A and B in shared or tensor memory and C in tensor memory, stored where the datapath layout puts it.
    */
  inline def tensorMemoryMultiply[V, L, D, C, F[_], G](
      inline machine: NvidiaMachine[V, L, D, C, F] { type Gathered = G }
  )(operation: Tcgen05.Multiply): F[Step] = machine.everyLane { lane =>
    machine.gatherProduct(lane, operation) { product =>
      machine.eachIndex(machine.elementCount(product)) { index =>
        machine.computeElement(product, index)(machine.storeElement(lane, product, index, _)(machine.done))
      }(machine.done)
    }
  }(machine.advance)

  /** `UTCATOMSWS.FIND_AND_SET`: the first active lane of each warp claims the aligned run of `granules` granules of
    * tensor memory that is lowest and free, and every active lane receives the first granule in `destination` and
    * whether there was a run in `claimed`; a count that is not a power of two up to the granule count claims nothing.
    */
  inline def tensorMemoryClaim[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Register, claimed: Predicate, granules: Value): F[Step] = machine.eachUnit { lane =>
    val count                      = machine.read(lane, granules)
    val valid                      = machine.compute(TensorMemoryLayout.Claimable, count)
    def finish(column: V): F[Unit] = {
      val found = algebra.lessThan(column, algebra.literal(TensorMemory.Columns.toLong), signed = false)
      machine.writeUnit(
        lane,
        destination,
        algebra.select(found, algebra.shiftRightLogical(column, algebra.literal(5L)), algebra.literal(0L))
      )(machine.writeUnitPredicate(lane, claimed, found)(machine.done))
    }
    machine.withCta(machine.ctaRank) { cta =>
      machine.when(valid)(machine.allocateTensorMemory(cta, count)(finish(_)))(
        machine.when(algebra.equal(valid, algebra.literal(0L)))(finish(algebra.literal(TensorMemory.Columns.toLong)))(
          machine.done
        )
      )
    }
  }(machine.advance)

  /** `UTCATOMSWS.AND`: the first active lane of each warp keeps only the allocated granules of `mask`. */
  inline def tensorMemoryRelease[V, L, D, C, F[_]](inline machine: NvidiaMachine[V, L, D, C, F])(mask: Value)
      : F[Step] = machine.eachUnit { lane =>
    machine.withCta(machine.ctaRank)(machine.retainTensorMemory(_, machine.read(lane, mask))(machine.done))
  }(machine.advance)

  /** The shared word of `width` at `raw` as its holder's window and offset reach it, once it is aligned. */
  private inline def lockWindow[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(lane: L, raw: V, width: ScalarBitWidth)(inline body: (C, V) => F[Unit]): F[Unit] =
    machine.sharedWindow(lane, raw, algebra.literal(width.toScalarByteWidth.toLong), "shared lock address") {
      (cta, offset) =>
        machine.require(
          algebra.equal(algebra.and(raw, algebra.literal(width.toScalarByteWidth.toLong - 1L)), algebra.literal(0L)),
          "shared lock alignment"
        )(Fault.Backend(s"invalid shared lock address: $raw"))(body(cta, offset))
    }

  private inline def lockHolder[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F],
      lane: L
  ): V = algebra.add(algebra.multiply(machine.unitIndex(lane), machine.unitSize), machine.laneIndex(lane))

  /** `LDSLK`: each lane in turn takes the lock on the shared word at `address` unless another lane holds it, then loads
    * the word into `result` and sets `destination`, which says whether it took the lock.
    */
  inline def sharedLockLoad[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Predicate, result: Register, address: Value, width: ScalarBitWidth): F[Step] =
    machine.everyLane { lane =>
      val raw = machine.read(lane, address)
      lockWindow(algebra, machine)(lane, raw, width) { (cta, offset) =>
        machine.acquireLock(cta, offset, lockHolder(algebra, machine, lane)) { free =>
          machine.when(free)(machine.load(
            lane,
            Space.Shared,
            raw,
            width
          )(machine.write(lane, result, _)(machine.done)))(
            machine.writePredicate(lane, destination, free)(machine.done)
          )
        }
      }
    }(machine.advance)

  /** `STSCUL`: each lane in turn that holds the lock on the shared word at `address` stores `source` to it and frees
    * it, and `destination` says whether it held the lock.
    */
  inline def sharedStoreUnlock[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(destination: Predicate, address: Value, source: Value, width: ScalarBitWidth): F[Step] =
    machine.everyLane { lane =>
      val raw = machine.read(lane, address)
      lockWindow(algebra, machine)(lane, raw, width) { (cta, offset) =>
        machine.releaseLock(cta, offset, lockHolder(algebra, machine, lane)) { held =>
          machine.when(held)(machine.store(lane, Space.Shared, raw, machine.read(lane, source), width)(machine.done))(
            machine.writePredicate(lane, destination, held)(machine.done)
          )
        }
      }
    }(machine.advance)

  extension [V, L, D, C, F[_]](inline machine: NvidiaMachine[V, L, D, C, F]) {

    /** `tcgen05.relinquish_alloc_permit`: the CTA allocates no more tensor memory. */
    inline def tensorMemoryRelinquish: F[Step] =
      machine.withCta(machine.ctaRank)(machine.relinquishTensorMemory(_)(machine.advance))
  }

  /** The cell of register `register` of the thread `lane` of a warp that moves `movement` at `address`: `body` over its
    * lane, its column and its last column, once the cell lies in the lanes of the warp's partition and in the tensor
    * memory.
    */
  private inline def tensorCell[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(movement: Tcgen05.Movement, register: Int, lane: L, address: V)(inline body: (V, V, V) => F[Unit]): F[Unit] = {
    val thread = machine.laneIndex(lane)
    val low    = algebra.literal(TensorMemoryLayout.code(movement, register, half = 0))
    val row    = machine.compute(TensorMemoryLayout.CellLane, low, address, thread)
    val first  = machine.compute(TensorMemoryLayout.CellColumn, low, address, thread)
    val last   =
      if (movement.packed)
        machine.compute(
          TensorMemoryLayout.CellColumn,
          algebra.literal(TensorMemoryLayout.code(movement, register, half = 1)),
          address,
          thread
        )
      else first
    val columns   = algebra.literal(TensorMemory.Columns.toLong)
    val partition = algebra.and(machine.unitIndex(lane), algebra.literal(3L))
    machine.require(
      algebra.equal(algebra.shiftRightLogical(row, algebra.literal(5L)), partition),
      "tensor memory lane"
    )(
      Fault.Backend(
        s"warp ${machine.unitIndex(lane)} cannot reach tensor memory lane $row, outside lanes " +
          s"${algebra.multiply(partition, algebra.literal(32L))} to " +
          s"${algebra.add(algebra.multiply(partition, algebra.literal(32L)), algebra.literal(31L))}"
      )
    ) {
      machine.require(algebra.lessThan(first, columns, signed = false), "tensor memory column")(
        Fault.Backend(s"tensor memory column $first is out of range")
      ) {
        machine.require(
          if (movement.packed) algebra.lessThan(last, columns, signed = false) else algebra.literal(1L),
          "tensor memory column"
        )(Fault.Backend(s"tensor memory column $last is out of range"))(body(row, first, last))
      }
    }
  }

  /** `body` over the lane's thread, if the move involves it. */
  private inline def involving[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(movement: Tcgen05.Movement, lane: L)(inline body: => F[Unit]): F[Unit] =
    machine.when(
      if (movement.threads == Tcgen05.Threads.All) algebra.literal(1L)
      else
        machine.compute(
          TensorMemoryLayout.Included,
          algebra.literal(TensorMemoryLayout.code(movement, register = 0, half = 0)),
          machine.laneIndex(lane)
        )
    )(body)(machine.done)

  /** `tcgen05.ld`: the cells of tensor memory at the first lane's `address`, loaded into the registers `destinations`
    * of each thread of the warp as `movement` places them, and reduced to the register of `reduction`, if it has one.
    */
  inline def tensorMemoryLoad[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(movement: Tcgen05.Movement, destinations: Vec[Register], address: Value, reduction: Option[Tcgen05.Reduction])
      : F[Step] = machine.require(machine.converged, "warp convergence")(unconverged) {
    machine.gather(UnitAlgebra.Slot.First, address) { addresses =>
      machine.everyLane { lane =>
        val base = machine.peer(lane, addresses, algebra.literal(0L))
        machine.withCta(machine.ctaRank) { cta =>
          involving(algebra, machine)(movement, lane) {
            def loadFrom(register: Int, combined: Option[V]): F[Unit] =
              if (register == destinations.size)
                (reduction, combined) match {
                  case (Some(reduced), Some(result)) => machine.write(lane, reduced.destination, result)(machine.done)
                  case _                             => machine.done
                }
              else
                tensorCell(algebra, machine)(movement, register, lane, base) { (row, first, last) =>
                  val low   = machine.tensorMemoryCell(cta, row, first)
                  val value =
                    if (movement.packed)
                      algebra.or(
                        algebra.and(low, algebra.literal(0xffffL)),
                        algebra.shiftLeft(
                          algebra.and(machine.tensorMemoryCell(cta, row, last), algebra.literal(0xffffL)),
                          algebra.literal(16L)
                        )
                      )
                    else low
                  val running = reduction.map { reduced =>
                    val mode = algebra.literal(TensorMemoryLayout.combination(reduced.maximum, reduced.kind))
                    combined.fold(value)(previous =>
                      machine.compute(TensorMemoryLayout.Combined, mode, previous, value)
                    )
                  }
                  machine.write(lane, destinations(register), value)(loadFrom(register + 1, running))
                }
            loadFrom(0, None)
          }
        }
      }(machine.advance)
    }
  }

  /** `tcgen05.st`: the registers `sources` of each thread of the warp stored to the cells of tensor memory at the first
    * lane's `address`, as `movement` places them.
    */
  inline def tensorMemoryStore[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(movement: Tcgen05.Movement, sources: Vec[Value], address: Value): F[Step] =
    machine.require(machine.converged, "warp convergence")(unconverged) {
      machine.gather(UnitAlgebra.Slot.First, address) { addresses =>
        machine.everyLane { lane =>
          val base = machine.peer(lane, addresses, algebra.literal(0L))
          machine.withCta(machine.ctaRank) { cta =>
            involving(algebra, machine)(movement, lane) {
              def storeFrom(register: Int): F[Unit] =
                if (register == sources.size) machine.done
                else {
                  val bits = machine.read(lane, sources(register))
                  tensorCell(algebra, machine)(movement, register, lane, base) { (row, first, last) =>
                    if (movement.packed)
                      machine.storeTensorMemory(cta, row, first, algebra.and(bits, algebra.literal(0xffffL))) {
                        machine.storeTensorMemory(
                          cta,
                          row,
                          last,
                          algebra.and(algebra.shiftRightLogical(bits, algebra.literal(16L)), algebra.literal(0xffffL))
                        )(storeFrom(register + 1))
                      }
                    else machine.storeTensorMemory(cta, row, first, bits)(storeFrom(register + 1))
                  }
                }
              storeFrom(0)
            }
          }
        }(machine.advance)
      }
    }

  /** `tcgen05.cp`: the rows of a matrix in shared memory that the descriptor `descriptor` names, copied to the tensor
    * memory at `address` of the issuing CTA and, for a pair, of its peer, each row replicated as the copy says.
    */
  inline def tensorMemoryCopy[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(copy: Tcgen05.Copy, address: Value, descriptor: Value): F[Step] = machine.everyLane { lane =>
    val target = machine.read(lane, address)
    val layout = machine.read(lane, descriptor)
    machine.require(machine.compute(TensorMemoryLayout.Decodable, layout), "tcgen05.cp descriptor")(
      Fault.Backend(machine.valueText(TensorMemoryLayout.Text.Descriptor, layout).unsafe[NonEmpty[String]])
    ) {
      pairResident(algebra, machine)(copy.pair) {
        val firstLane   = machine.compute(TensorMemoryLayout.AddressLane, target)
        val firstColumn = machine.compute(TensorMemoryLayout.AddressColumn, target)
        val replicas    = copy.replication.lanes(0).size
        val rows        = copy.shape.rows
        val parts       = copy.shape.bytes / 16
        val chunks      = algebra.literal((rows * parts).toLong)
        machine.require(
          algebra.and(
            algebra.lessThan(
              algebra.add(firstLane, algebra.literal(copy.replication.lanes(rows - 1).max.toLong)),
              algebra.literal(TensorMemory.Lanes.toLong),
              signed = true
            ),
            algebra.lessOrEqual(
              algebra.add(firstColumn, algebra.literal((copy.shape.bytes / 4).toLong)),
              algebra.literal(TensorMemory.Columns.toLong),
              signed = true
            )
          ),
          "tcgen05.cp range"
        )(
          Fault.Backend(
            s"tcgen05.cp at 0x${machine.valueText(TensorMemoryLayout.Text.Hexadecimal, target)} falls outside tensor memory"
          )
        ) {
          machine.eachIndex(algebra.literal(if (copy.pair) 2L * rows * parts else (rows * parts).toLong)) { index =>
            val within = if (copy.pair) algebra.remainder(index, chunks, signed = false) else index
            val row  = if (parts == 1) within else algebra.divide(within, algebra.literal(parts.toLong), signed = false)
            val part =
              if (parts == 1) algebra.literal(0L)
              else algebra.remainder(within, algebra.literal(parts.toLong), signed = false)
            machine.withCta(ctaRankAt(algebra, machine)(copy.pair, algebra.divide(index, chunks, signed = false))) {
              owner =>
                machine.loadChunk(owner, machine.compute(TensorMemoryLayout.Major, layout, row, part), copy.packing) {
                  data =>
                    def place(replica: Int): F[Unit] =
                      if (replica == replicas) machine.done
                      else
                        machine.storeTensorWords(
                          owner,
                          algebra.add(
                            firstLane,
                            machine.compute(
                              TensorMemoryLayout.Replicated,
                              algebra.literal(copy.replication.ordinal.toLong),
                              row,
                              algebra.literal(replica.toLong)
                            )
                          ),
                          algebra.add(firstColumn, algebra.multiply(part, algebra.literal(4L))),
                          data
                        )(place(replica + 1))
                    place(0)
                }
            }
          }(machine.done)
        }
      }
    }
  }(machine.advance)

  /** `tcgen05.shift`: the rows of the 32 lanes and eight columns at `address` of the issuing CTA and, for a pair, of
    * its peer, each moved down by one lane.
    */
  inline def tensorMemoryShift[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(address: Value, pair: Boolean): F[Step] = machine.everyLane { lane =>
    val target      = machine.read(lane, address)
    val firstLane   = machine.compute(TensorMemoryLayout.AddressLane, target)
    val firstColumn = machine.compute(TensorMemoryLayout.AddressColumn, target)
    machine.require(
      algebra.and(
        algebra.equal(algebra.and(firstLane, algebra.literal(31L)), algebra.literal(0L)),
        algebra.lessOrEqual(
          algebra.add(firstColumn, algebra.literal(8L)),
          algebra.literal(TensorMemory.Columns.toLong),
          signed = true
        )
      ),
      "tcgen05.shift address"
    )(
      Fault.Backend(
        s"tcgen05.shift at 0x${machine.valueText(TensorMemoryLayout.Text.Hexadecimal, target)} needs a lane aligned to 32"
      )
    ) {
      pairResident(algebra, machine)(pair) {
        machine.withCta(ctaRankAt(algebra, machine)(pair, algebra.literal(0L))) { even =>
          machine.shiftTensorMemory(even, firstLane, firstColumn) {
            if (pair)
              machine.withCta(ctaRankAt(algebra, machine)(pair, algebra.literal(1L))) { odd =>
                machine.shiftTensorMemory(odd, firstLane, firstColumn)(machine.done)
              }
            else machine.done
          }
        }
      }
    }
  }(machine.advance)

  /** `tcgen05.commit`: one arrival on the mbarrier at `barrier` for each lane, and, with a `mask`, on the mbarrier at
    * that address in each CTA of the cluster whose rank the mask holds.
    */
  inline def tensorMemoryCommit[V, L, D, C, F[_]](
      inline algebra: Algebra[V],
      inline machine: NvidiaMachine[V, L, D, C, F]
  )(barrier: Value, mask: Option[Value]): F[Step] = machine.everyLane { lane =>
    val at = machine.read(lane, barrier)
    mask match {
      case None =>
        machine.mbarrierArrive(lane, at, algebra.literal(1L), algebra.literal(0L), drop = false, complete = true)(_ =>
          machine.done
        )
      case Some(ranks) =>
        val held = machine.read(lane, ranks)
        machine.eachIndex(algebra.literal(16L)) { rank =>
          machine.when(
            algebra.and(algebra.shiftRightLogical(held, rank), algebra.literal(1L))
          ) {
            machine.mbarrierArrive(
              lane,
              machine.clusterAddress(at, rank),
              algebra.literal(1L),
              algebra.literal(0L),
              drop = false,
              complete = true
            )(_ => machine.done)
          }(machine.done)
        }(machine.done)
    }
  }(machine.advance)
}
