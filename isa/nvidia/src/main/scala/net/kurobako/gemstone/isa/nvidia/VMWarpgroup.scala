package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Warpgroup matrix products on the VM. The VM completes each product when it issues, which the asynchronous ones
  * allow: their results and sources are undefined to every access until a `wgmma.wait_group` that covers them. Sources
  * are read before any write is staged.
  */
private object VMWarpgroup {
  private def invalid(detail: NonEmpty[String]): Fault = Fault.Backend(detail)

  /** The first lanes of the warpgroups with an active lane, each of which must have all 128 lanes active. */
  def participating(context: VMContext): Either[Fault, Vec[Int]] = {
    val count  = context.workgroup.laneCount
    val groups = Vec.tabulate((count + Warpgroup.Lanes - 1) / Warpgroup.Lanes)(_ * Warpgroup.Lanes).filter(first =>
      (first until math.min(first + Warpgroup.Lanes, count) by 32).exists(context.active.warpBits(_) != 0L)
    )
    Either.cond(
      groups.forall(first =>
        first + Warpgroup.Lanes <= count &&
          (first until first + Warpgroup.Lanes by 32).forall(context.active.warpBits(_) == 0xffffffffL)
      ),
      groups,
      invalid("wgmma requires all 128 lanes of each participating warpgroup")
    )
  }

  private def lane(first: Int, index: Int): WorkgroupLane = WorkgroupLane.fromIndex(first + index)

  /** The value every lane of the warpgroup holds in `parts`, low word first, which must be the same in each. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def uniform(context: VMContext, first: Int, parts: Vec[Value], what: String): Either[Fault, Long] = {
    def at(index: Int): Long =
      if (parts.size == 1) context.read(lane(first, index), parts(0))
      else {
        var bits = 0L
        var part = 0
        while (part < parts.size) {
          bits |= (context.read(lane(first, index), parts(part)) & 0xffffffffL) << (32 * part)
          part += 1
        }
        bits
      }
    val value = at(0)
    Either.cond(
      (1 until Warpgroup.Lanes).forall(at(_) == value),
      value,
      invalid(s"wgmma $what must be the same in every lane of the warpgroup")
    )
  }

  /** A matrix's encodings, signed ones sign-extended. */
  private def code(element: Element, bits: Long): Long = element match {
    case Element.S8 | Element.S32 => Bits.signExtend(element.code(bits), element.bits.widen)
    case _                        => element.code(bits)
  }

  /** The multiplicand a shared matrix is: A has rows M and columns K, B has rows K and columns N. */
  private enum Side derives CanEqual { case A, B }

  /** The `rows` by `columns` matrix in shared memory at `descriptor`, row `mn` and column `k` of the canonical layout,
    * or transposed for B, whose rows are K.
    */
  private def shared(
      context: VMContext,
      descriptor: Long,
      element: Element,
      rows: Int,
      columns: Int,
      transposed: Boolean,
      side: Side
  ): Array[Long] = {
    val layout = Warpgroup.Descriptor(descriptor)
    val bits   = element.bits.raw
    Array.tabulate(rows * columns) { index =>
      val (row, column) = (index / columns, index % columns)
      val (mn, k)       = if (side == Side.B) (column, row) else (row, column)
      val (offset, bit) = layout.locate(bits = bits, mn = mn, k = k, transposed = transposed)
      val address       = VMAddressSpace.SharedBase + offset
      val raw           = bits match {
        case 32 => context.workgroup.readShared(address, 32)
        case 16 => context.workgroup.readShared(address, 16)
        case 8  => context.workgroup.readShared(address, 8)
        case _  => (context.workgroup.readShared(address, 8) >>> bit) & 1L
      }
      code(element, raw)
    }
  }

  /** The operands of one warpgroup's product, gathered from its lanes and shared memory, positioned at an element of D
    * by [[at]].
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Operands(
      operation: Operation.WarpgroupMultiply,
      left: Array[Long],
      right: Array[Long],
      addend: Array[Long],
      metadata: Array[Long],
      selector: Int,
      enabled: Long
  ) extends WarpgroupOperands[Long] {
    private val product = operation.product
    private val depth   = product.a.columns.raw
    private val columns = product.d.columns.raw
    private val slots   = Array.ofDim[Long](TensorSemantics.Slots)
    private var row     = 0
    private var column  = 0

    def at(index: Int): Unit = {
      row = index / columns
      column = index % columns
    }

    def a(index: Int): Long =
      if (product.bits.isDefined) MatrixOperands.segment(left, row * depth + index * 32, 1)
      else left(row * depth + index)
    def b(index: Int): Long =
      if (product.bits.isDefined) MatrixOperands.segment(right, index * 32 * columns + column, columns)
      else right(index * columns + column)
    def c: Long     = addend(row * columns + column)
    def scale: Long = enabled

    def selection(index: Int): Long = if (product.sparsity.isEmpty) 0L
    else {
      val element = product.a.element
      val chunk   = index / packedPerChunk(element)
      val slot    = index % packedPerChunk(element)
      val place   =
        metadataNibble(element, depth = product.b.rows, row = row % 16, chunk = chunk, selector = selector)
      val nibble = (metadata(row / 16 * 32 + place.lane) >>> place.offset) & 0xfL
      element.bits.raw match {
        case 32 => nibble & 3L
        case _  => (nibble >>> (2 * slot)) & 3L
      }
    }

    def scaleA(block: Int): Long = 0L
    def scaleB(block: Int): Long = 0L

    def bind(name: String, value: Long): Long            = value
    def hold(slot: Int, name: String, value: Long): Long = {
      slots(slot) = value
      value
    }
    def held(slot: Int): Long = slots(slot)
  }

  /** The operands of the warpgroup from `first` on, or why they cannot be read. */
  private def operands(operation: Operation.WarpgroupMultiply, first: Int, context: VMContext)
      : Either[Fault, Operands] = {
    val product = operation.product
    val rows    = Warpgroup.Rows
    val depth   = product.a.columns.raw
    val k       = product.b.rows.raw
    val n       = product.d.columns.raw
    for {
      b <- uniform(context, first, operation.b, "B descriptor")
      a <- if (operation.a.isEmpty) Right(None) else uniform(context, first, operation.a, "A descriptor").map(Some(_))
      scale <- uniform(context, first, Vec(operation.scale), "scale-d predicate")
    } yield {
      val left = a.fold {
        val values  = Array.ofDim[Long](rows * depth)
        val element = product.a.element
        (0 until Warpgroup.Lanes).foreach { index =>
          product.left.zipWithIndex.foreach { (register, slot) =>
            val bits = context.read(lane(first, index), register)
            (0 until element.packed).foreach { part =>
              val (row, column) = Warpgroup.multiplicand(element, index, slot * element.packed + part)
              values(row * depth + column) = code(element, bits >>> (part * element.bits))
            }
          }
        }
        values
      }(descriptor => shared(context, descriptor, product.a.element, rows, depth, operation.transposeA, Side.A))
      val right  = shared(context, b, product.b.element, k, n, operation.transposeB, Side.B)
      val addend = Array.ofDim[Long](rows * n)
      val d      = product.d.element
      (0 until Warpgroup.Lanes).foreach { index =>
        product.addend.zipWithIndex.foreach { (register, slot) =>
          val bits = context.read(lane(first, index), register)
          (0 until d.packed).foreach { part =>
            val (row, column) = Warpgroup.accumulator(index, slot * d.packed + part)
            addend(row * n + column) = code(d, bits >>> (part * d.bits))
          }
        }
      }
      val metadata = product.sparsity.fold(Array.emptyLongArray)(sparsity =>
        Array.tabulate(Warpgroup.Lanes)(index => context.read(lane(first, index), sparsity.metadata))
      )
      val selector = product.sparsity.fold(0)(sparsity => context.read(lane(first, 0), sparsity.selector).toInt & 3)
      Operands(operation, left, right, addend, metadata, selector, if (scale != 0L) 1L else 0L)
    }
  }

  /** `wgmma.mma_async` on one warpgroup: each element of D is `element` of its operands. */
  inline def multiply(operation: Operation.WarpgroupMultiply, first: Int, context: VMContext)(
      inline element: WarpgroupOperands[Long] => Long
  ): Either[Fault, Unit] = operands(operation, first, context).map { operands =>
    val results = Array.tabulate(Warpgroup.Rows * operation.product.d.columns) { index =>
      operands.at(index)
      element(operands)
    }
    store(operation, first, context, results)
  }

  /** Writes D's fragment to every lane of the warpgroup. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def store(operation: Operation.WarpgroupMultiply, first: Int, context: VMContext, results: Array[Long])
      : Unit = {
    val d = operation.product.d
    val n = d.columns.raw
    (0 until Warpgroup.Lanes).foreach { index =>
      operation.product.destinations.zipWithIndex.foreach { (register, slot) =>
        var bits = 0L
        (0 until d.element.packed).foreach { part =>
          val (row, column) = Warpgroup.accumulator(index, slot * d.element.packed + part)
          bits |= Bits.truncate(results(row * n + column), d.element.bits.widen) << (part * d.element.bits)
        }
        context.effects.stageRegister(lane(first, index), register, bits)
      }
    }
  }

  /** Every participating warpgroup's product. */
  inline def execute(operation: Operation.WarpgroupMultiply, context: VMContext)(
      inline element: WarpgroupOperands[Long] => Long
  ): Either[Fault, Unit] =
    participating(context).flatMap(_.traverse_(first => multiply(operation, first, context)(element)))
}
