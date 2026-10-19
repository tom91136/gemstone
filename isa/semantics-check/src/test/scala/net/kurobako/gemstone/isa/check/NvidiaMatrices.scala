package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.check.NvidiaElements.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Executes a lowered matrix product on a whole warp for the definition of one element of D: the element the seed
  * picks, with the row of A, the column of B, the element of C, the sparse metadata and the scale factors it reads set
  * from the inputs and every other operand element zero.
  */
object NvidiaMatrices {

  /** The code of the element at a fragment's row and column, if the product reads one there. */
  private trait ElementAt {
    def apply(at: Int, across: Int): Option[Long]
  }

  /** The matrix product among `steps`, if any. */
  def product(steps: Vec[VMIR.Instruction]): Option[Operation.Multiply] = steps.collectFirst {
    case VMIR.Instruction.Tensor(multiply: Operation.Multiply) => multiply
  }

  def run(
      prologue: Vec[VMIR.Instruction],
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      multiply: Operation.Multiply,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      seed: Long,
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val named               = parameters.map(_._1).zip(inputs).toMap
    def input(name: String) = named.getOrElse(name, 0L)
    val d                   = multiply.d
    // a stepped HMMA.884 reads C in only some registers and zeros the rest, so only those elements are defined
    val shared                   = multiply.addend.diff(multiply.addend.distinct).toSet
    val (partition, row, column) =
      if (shared.isEmpty)
        (
          (seed                         % d.partitions).toInt,
          (seed / d.partitions          % d.rows).toInt,
          (seed / d.partitions / d.rows % d.columns).toInt
        )
      else {
        val c          = multiply.c
        val candidates = WarpLane.values.toVec.flatMap(lane =>
          (0 until c.registers.raw * c.element.packed.raw).toVec.collect {
            case index if !shared.contains(multiply.addend(index / c.element.packed)) =>
              val (at, across) = c.coordinate(lane, index.unsafe[Int >= 0])
              (c.partition(lane).raw, at.raw, across.raw)
          }
        ).distinct
        candidates((seed % candidates.size).toInt)
      }
    val combined = steps.collect { case VMIR.Instruction.MoveCombine(destination, sources, width) =>
      destination -> (sources, width)
    }.toMap
    val settings                                            = mutable.LinkedHashMap.empty[(Int, VMIR.Register), Long]
    val conflicts                                           = mutable.Set.empty[(Int, VMIR.Register)]
    def set(lane: Int, value: VMIR.Value, bits: Long): Unit = value match {
      case VMIR.Value.Register(slot) =>
        combined.get(slot) match {
          case Some((sources, width)) =>
            sources.zipWithIndex.foreach((part, index) =>
              set(lane, part, Bits.truncate(bits >>> (index * width), width.widen[Int in 1 ~ 64]))
            )
          case None =>
            val previous = settings.getOrElseUpdate((lane, slot), bits)
            if (previous != bits) { val _ = conflicts += ((lane, slot)) }
        }
      case _ => ()
    }
    def orInto(lane: Int, slot: VMIR.Register, bits: Long): Unit =
      settings((lane, slot)) = settings.getOrElse((lane, slot), 0L) | bits
    def fragment(fragment: Fragment, values: Vec[VMIR.Value])(element: ElementAt): Unit =
      WarpLane.values.foreach { lane =>
        values.zipWithIndex.foreach { (value, register) =>
          val bits = (0 until fragment.element.packed).foldLeft(0L) { (bits, part) =>
            val index        = (register * fragment.element.packed + part).unsafe[Int >= 0]
            val (at, across) = fragment.coordinate(lane, index)
            val code         =
              if (fragment.partition(lane).raw != partition) None else element(at, across)
            bits | (code.fold(0L)(fragment.element.container(_)) << (part * fragment.element.bits))
          }
          set(lane, value, bits)
        }
      }
    val single                              = multiply.bits.nonEmpty
    def bit(name: String, index: Int): Long = (input(s"$name${index / 32}") >>> (index % 32)) & 1L
    fragment(multiply.a, multiply.left)((at, across) =>
      Option.when(at == row)(if (single) bit("a", across) else input(s"a$across"))
    )
    fragment(multiply.b, multiply.right)((at, across) =>
      Option.when(across == column)(if (single) bit("b", at) else input(s"b$at"))
    )
    fragment(multiply.c, multiply.addend)((at, across) => Option.when(at == row && across == column)(input("c")))
    multiply.sparsity.foreach { sparsity =>
      val element = multiply.a.element
      val packed  = packedPerChunk(element).raw
      val select  = sparsity.selector match {
        case VMIR.Value.Immediate(value) => value.toInt & 3
        case _                           => 0
      }
      (0 until multiply.b.rows / chunkWidth(element)).foreach { chunk =>
        val place  = metadataNibble(element, depth = multiply.b.rows, row = row, chunk = chunk, selector = select)
        val fields = (chunk * packed until (chunk + 1) * packed).map(selectionField(element, _)).distinct
        val nibble = fields.zipWithIndex.foldLeft(0L)((nibble, field) =>
          nibble | ((input(s"e${field._1}") & 3L) << (2 * field._2))
        )
        orInto(place.lane, register(sparsity.metadata), nibble << place.offset)
      }
    }
    multiply.scaling.foreach { scaling =>
      (0 until scaling.vector).foreach { block =>
        val a    = 4 * (row % 8) + row / 8
        val b    = 4 * column
        val byte = if (scaling.vector == 4) block else block
        orInto(a, register(scaling.aData), (input(s"sa$block") & 0xffL) << (8 * byte))
        orInto(b, register(scaling.bData), (input(s"sb$block") & 0xffL) << (8 * byte))
      }
    }
    if (conflicts.nonEmpty) Left("the product's fragments share registers")
    else execute(
      prologue,
      steps,
      registerCount,
      predicateCount,
      settings.toMap,
      multiply,
      row,
      column,
      partition,
      results,
      memory
    )
  }

  private def register(value: VMIR.Value): VMIR.Register = value match {
    case VMIR.Value.Register(slot) => slot
    case other                     => throw IllegalArgumentException(s"$other is not a register")
  }

  private def execute(
      prologue: Vec[VMIR.Instruction],
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      settings: Map[(Int, VMIR.Register), Long],
      multiply: Operation.Multiply,
      row: Int,
      column: Int,
      partition: Int,
      results: Vec[(String, Type)],
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val selector     = VMIR.Predicate(predicateCount)
    val setup        = NvidiaHarness.laneSetup(selector, VMIR.Value.LaneId, 32, settings)
    val body         = (prologue ++ steps :+ VMIR.Instruction.Return).map(VMIR.PredicatedInstruction(_, None))
    val instructions = setup ++ body
    val d            = multiply.d
    val (lane, at)   = WarpLane.values.view.flatMap(lane =>
      (0 until d.registers.raw * d.element.packed.raw).view.map(index => (lane, index))
    ).find { (lane, index) =>
      val (r, c) = d.coordinate(lane, index.unsafe[Int >= 0])
      d.partition(lane).raw == partition && r.raw == row && c.raw == column
    }.getOrElse((0.unsafe[WarpLane], 0))
    val register = at / d.element.packed
    val part     = at % d.element.packed
    val pairs    = d.element.bits == 64 && multiply.destinations.size == 2 * d.registers
    NvidiaHarness.run(instructions, registerCount, (predicateCount.raw + 1).unsafe[Int >= 0], 32, memory) { view =>
      def read(slot: VMIR.Register): Long = view.register(lane.widen[Int >= 0], slot.unwrap)
      val bits                            =
        if (pairs)
          (read(multiply.destinations(2 * register)) & 0xffffffffL) |
            (read(multiply.destinations(2 * register + 1)) << 32)
        else read(multiply.destinations(register))
      val element = bits >>> (part * d.element.bits)
      if (d.element.bits == 64) element else Bits.truncate(element, d.element.bits.widen)
    }.map(value => results.map((_, tpe) => tpe.represent(value)))
  }
}
