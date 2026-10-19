package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.check.NvidiaElements.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

/** Executes a lowered warpgroup product on a whole warpgroup for the definition of one element of D: the element the
  * seed picks, with A's row, B's column, D's element, the sparse metadata and the scale-d predicate set from the
  * inputs, every other operand element zero, and A and B in shared memory, K-major without a swizzle, where descriptors
  * place them.
  */
object NvidiaWarpgroups {
  private val Lanes: Int > 0 = 128.unsafe[Int > 0]

  /** Where A and B start in shared memory. */
  private val AStart = 0L
  private val BStart = 16384L

  /** The shared memory A and B span at most. */
  private val SharedBytes = 65536

  def product(steps: Vec[VMIR.Instruction]): Option[Operation.WarpgroupMultiply] = steps.collectFirst {
    case VMIR.Instruction.Tensor(multiply: Operation.WarpgroupMultiply) => multiply
  }

  /** A K-major descriptor without a swizzle at `start` of a matrix whose rows hold `bytes` bytes each. */
  private def descriptor(start: Long, bytes: Int): Long =
    (start >>> 4) | ((128L >>> 4) << 16) | (((128L * math.max(bytes / 16, 1)) >>> 4) << 32)

  def run(
      prologue: Vec[VMIR.Instruction],
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      operation: Operation.WarpgroupMultiply,
      predicate: String => Option[VMIR.Predicate],
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      seed: Long,
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val product                                   = operation.product
    val named                                     = parameters.map(_._1).zip(inputs).toMap
    def input(name: String)                       = named.getOrElse(name, 0L)
    val n                                         = product.d.columns.raw
    val k                                         = product.b.rows.raw
    val depth                                     = product.a.columns.raw
    val row                                       = (seed                  % Warpgroup.Rows).toInt
    val column                                    = (seed / Warpgroup.Rows % n).toInt
    val single                                    = product.bits.nonEmpty
    def element(prefix: String, index: Int): Long =
      if (single) (input(s"$prefix${index / 32}") >>> (index % 32)) & 1L else input(s"$prefix$index")
    val settings                                            = mutable.LinkedHashMap.empty[(Int, VMIR.Register), Long]
    def set(lane: Int, value: VMIR.Value, bits: Long): Unit = value match {
      case VMIR.Value.Register(slot) => settings((lane, slot)) = settings.getOrElse((lane, slot), 0L) | bits
      case _                         => ()
    }
    // each shared byte the product reads, or -1 where none is set
    val shared = Array.fill(SharedBytes)(-1L)
    def place(start: Long, bytes: Int, element: Element, transposed: Boolean)(mn: Int, at: Int, value: Long): Unit = {
      val (offset, bit) = Warpgroup.Descriptor(descriptor(start, bytes)).locate(
        bits = element.bits,
        mn = mn,
        k = at,
        transposed = transposed
      )
      if (element.bits == 1) shared(offset.toInt) = math.max(shared(offset.toInt), 0L) | (value << bit)
      else (0 until element.bits / 8).foreach(byte =>
        shared(offset.toInt + byte) = (element.container(value) >>> (8 * byte)) & 0xffL
      )
    }
    val a = product.a.element
    if (product.left.nonEmpty)
      (0 until Warpgroup.Lanes).foreach { lane =>
        product.left.zipWithIndex.foreach { (register, slot) =>
          (0 until a.packed).foreach { part =>
            val (at, across) = Warpgroup.multiplicand(a, lane, slot * a.packed + part)
            if (at == row) set(lane, register, a.container(element("a", across)) << (part * a.bits))
          }
        }
      }
    else
      (0 until depth).foreach(index =>
        place(AStart, depth * a.bits.raw / 8, a, operation.transposeA)(row, index, element("a", index))
      )
    val b = product.b.element
    (0 until k).foreach(index =>
      place(BStart, k * b.bits.raw / 8, b, operation.transposeB)(column, index, element("b", index))
    )
    val d          = product.d.element
    val target     = (row, column)
    val (lane, at) = (0 until Warpgroup.Lanes).view.flatMap(lane =>
      (0 until product.destinations.size * d.packed).view.map(index => (lane, index))
    ).find((lane, index) => Warpgroup.accumulator(lane, index).toTuple == target).getOrElse((0, 0))
    set(
      lane,
      product.addend(at / d.packed),
      d.container(input("c")) << (at % d.packed * d.bits)
    )
    product.sparsity.foreach { sparsity =>
      val packed   = packedPerChunk(a).raw
      val selector = sparsity.selector match {
        case VMIR.Value.Immediate(value) => value.toInt & 3
        case _                           => 0
      }
      (0 until k / chunkWidth(a)).foreach { chunk =>
        val place  = metadataNibble(a, depth = k, row = row % 16, chunk = chunk, selector = selector)
        val fields = (chunk * packed until (chunk + 1) * packed).map(selectionField(a, _)).distinct
        val nibble = fields.zipWithIndex.foldLeft(0L)((nibble, field) =>
          nibble | ((input(s"e${field._1}") & 3L) << (2 * field._2))
        )
        set(row / 16 * 32 + place.lane, sparsity.metadata, nibble << place.offset)
      }
    }
    def descriptorOf(parts: Vec[VMIR.Value], value: Long): Unit = (0 until Warpgroup.Lanes).foreach(lane =>
      parts.zipWithIndex.foreach((part, index) =>
        set(lane, part, if (parts.size == 1) value else (value >>> (32 * index)) & 0xffffffffL)
      )
    )
    descriptorOf(operation.a, descriptor(AStart, depth * a.bits.raw / 8))
    descriptorOf(operation.b, descriptor(BStart, k * b.bits.raw / 8))
    val predicates = parameters.collect { case (name, Type.Bool) => predicate(name).map(_ -> input(name)) }.flatten
    execute(
      prologue ++ steps,
      registerCount,
      predicateCount,
      settings.toMap,
      Vec.from(shared.indices.collect { case offset if shared(offset) >= 0L => (offset.toLong, shared(offset)) }),
      predicates,
      operation,
      lane,
      at,
      results,
      memory
    )
  }

  private def execute(
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      settings: Map[(Int, VMIR.Register), Long],
      shared: Vec[(Long, Long)],
      predicates: Vec[(VMIR.Predicate, Long)],
      operation: Operation.WarpgroupMultiply,
      lane: Int,
      at: Int,
      results: Vec[(String, Type)],
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val selector      = VMIR.Predicate(predicateCount)
    val identity      = VMIR.Value.ThreadId(Axis.X)
    val registerSetup = NvidiaHarness.laneSetup(selector, identity, Lanes, settings)
    val sharedSetup   = NvidiaHarness.only(
      selector,
      identity,
      0,
      shared.map((offset, byte) =>
        VMIR.Instruction.Store(
          VMIR.Value.Immediate(VMAddressSpace.SharedBase + offset),
          VMIR.Value.Immediate(byte),
          8,
          VMIR.MemoryAccess.Shared
        )
      )
    )
    val predicateSetup = predicates.map((slot, value) =>
      VMIR.PredicatedInstruction(VMIR.Instruction.MovePredicate(slot, VMIR.Value.Immediate(value)), None)
    )
    val barrier      = VMIR.PredicatedInstruction(VMIR.Instruction.BarrierSync(VMIR.Value.Immediate(0L)), None)
    val body         = (steps :+ VMIR.Instruction.Return).map(VMIR.PredicatedInstruction(_, None))
    val instructions = registerSetup ++ sharedSetup ++ predicateSetup ++ Vec(barrier) ++ body
    val d            = operation.product.d.element
    NvidiaHarness.run(instructions, registerCount, (predicateCount.raw + 1).unsafe[Int >= 0], Lanes, memory) { view =>
      val bits =
        view.register(lane.unsafe[Int >= 0], operation.product.destinations(at / d.packed).unwrap) >>>
          (at % d.packed * d.bits)
      Bits.truncate(bits, d.bits.widen)
    }.map(value => results.map((_, tpe) => tpe.represent(value)))
  }
}
