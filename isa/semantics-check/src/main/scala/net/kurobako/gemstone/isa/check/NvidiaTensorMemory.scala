package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.check.NvidiaElements.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.TensorMemory.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Tcgen05.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Executes a lowered tcgen05.mma on one warp for the definition of one element of D: the element the seed picks among
  * those warp 0's lanes hold, with its row of A and column of B in shared or tensor memory as K-major matrices, C, the
  * sparse metadata and the scale factors in tensor memory set from the inputs, and everything else zero.
  */
object NvidiaTensorMemory {
  private def located(place: Placement, row: Int, column: Int): (cta: Int, lane: Int, column: Int) =
    (place.cta(row), place.lane(row, column), place.column(column))

  // where the harness puts each operand: A then B in shared memory, A, the metadata and scales in tensor memory
  private val SharedA                       = 0L
  private val SharedB                       = 16384L
  private val TensorA                       = 256
  private val Metadata                      = 384
  private val ScaleA                        = 448
  private val ScaleB                        = 456
  private def descriptor(start: Long): Long =
    ((start >>> 4) & 0x3fffL) | ((128L >>> 4) << 16) | ((1024L >>> 4) << 32) | (1L << 46)

  def product(steps: Vec[VMIR.Instruction]): Option[Multiply] = steps.collectFirst {
    case VMIR.Instruction.TensorMemoryMultiply(multiply) => multiply
  }

  def run(
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      multiply: Multiply,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      seed: Long,
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val named               = parameters.map(_._1).zip(inputs).toMap
    def input(name: String) = named.getOrElse(name, 0L)
    val enable              = input("enableInputD")
    (for {
      bits <- multiply.descriptor match {
        case VMIR.Value.Immediate(value) => Right(value)
        case other                       => Left(s"the instruction descriptor $other is not an immediate")
      }
      product <- TensorMemory.product(multiply, bits).left.map(_.raw)
      place      = TensorMemory.placement(multiply, product)
      candidates = Vec.tabulate(product.m * product.n)(index => (index / product.n, index % product.n))
        .filter((row, column) => located(place, row, column).cta == 0 && located(place, row, column).lane < 32)
      _ <- Either.cond(candidates.nonEmpty, (), "no element of D is in warp 0's lanes")
    } yield {
      val (row, column) = candidates(java.lang.Long.remainderUnsigned(seed, candidates.size.toLong).toInt)
      val at            = located(place, row, column)
      val element       = product.multiply
      val shared        = mutable.LinkedHashMap.empty[Long, Long]
      val cells         = mutable.LinkedHashMap.empty[(Int, Int), Long]
      def cell(lane: Int, column: Int, value: Long, shift: Int): Unit =
        cells((lane, column)) = cells.getOrElse((lane, column), 0L) | (value << shift)
      def orInto(address: Long, bits: Long): Unit = shared(address) = shared.getOrElse(address, 0L) | bits
      def put(layout: SharedDescriptor, base: Long, index: Int, k: Int, element: Element, code: Long): Unit = {
        val bits   = TensorMemory.container(element)
        val chunk  = layout.major(index, k / (128 / bits)) - layout.start + base
        val offset = (k % (128 / bits)) * bits
        val value  = element.container(code)
        (0 until math.max(bits / 8, 1)).foreach { byte =>
          orInto(chunk + offset / 8 + byte, ((value >>> (8 * byte)) << (if (bits == 4) offset % 8 else 0)) & 0xffL)
        }
      }
      val terms = if (product.sparse) product.k / 2 else product.k
      val a     = SharedDescriptor.decode(descriptor(SharedA)).toOption.get
      val b     = SharedDescriptor.decode(descriptor(SharedB)).toOption.get
      (0 until terms).foreach { index =>
        val code = input(s"a$index")
        if (multiply.aInTensorMemory) {
          val bits = TensorMemory.container(element.a.element)
          cell(
            at.lane,
            TensorA + index * bits / 32,
            element.a.element.container(code),
            index * bits % 32
          )
        } else put(a, SharedA, row, index, element.a.element, code)
      }
      (0 until product.k).foreach(index => put(b, SharedB, column, index, element.b.element, input(s"b$index")))
      cell(at.lane, at.column, Bits.truncate(input("c"), element.c.element.bits.widen), 0)
      if (product.sparse) {
        val packed = packedPerChunk(element.a.element).raw
        val wide   = element.a.element.bits >= 16
        (0 until terms / packed).foreach { chunk =>
          val fields = (chunk * packed until (chunk + 1) * packed).map(selectionField(element.a.element, _)).distinct
          val nibble = fields.zipWithIndex.foldLeft(0L)((nibble, field) =>
            nibble | ((input(s"e${field._1}") & 3L) << (2 * field._2))
          )
          val local = at.lane % 32
          if (wide)
            cell(
              (local / 16) * 16 + (chunk / 4) * 8 + local % 8,
              Metadata + product.selector,
              nibble,
              4 * ((local % 16 / 8) * 4 + chunk % 4)
            )
          else cell(local, Metadata + chunk / 8, nibble, 4 * (chunk % 8))
        }
      }
      element.scaling.foreach { scaling =>
        (0 until scaling.vector).foreach { block =>
          cell(row    % 32, ScaleA + row / 32, input(s"sa$block") & 0xffL, 8 * ((product.scaleIds.a + block)    % 4))
          cell(column % 32, ScaleB + column / 32, input(s"sb$block") & 0xffL, 8 * ((product.scaleIds.b + block) % 4))
        }
      }
      (shared.toVec, cells.toVec, at)
    }).flatMap((shared, cells, at) =>
      execute(steps, registerCount, predicateCount, multiply, shared, cells, at, enable, memory)
        .map(value => results.map((_, tpe) => tpe.represent(value)))
    )
  }

  private def execute(
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      multiply: Multiply,
      shared: Vec[(Long, Long)],
      cells: Vec[((Int, Int), Long)],
      at: (cta: Int, lane: Int, column: Int),
      enable: Long,
      memory: AddressSpace
  ): Either[String, Long] = {
    val scratch                                                   = VMIR.Register(registerCount)
    val selector                                                  = VMIR.Predicate(predicateCount)
    def immediate(value: Long): VMIR.Value                        = VMIR.Value.Immediate(value)
    def set(value: VMIR.Value, bits: Long): Vec[VMIR.Instruction] = value match {
      case VMIR.Value.Register(slot)  => Vec(VMIR.Instruction.Move(slot, immediate(bits), 64))
      case VMIR.Value.Predicate(slot) => Vec(VMIR.Instruction.MovePredicate(slot, immediate(bits)))
      case _                          => Vec.empty
    }
    val word     = Movement(DataShape.Lanes32Bits32, registers = 1, split = 0, packed = false)
    val operands =
      set(multiply.d, 0L) ++
        set(multiply.a, if (multiply.aInTensorMemory) TensorA.toLong else descriptor(SharedA)) ++
        set(multiply.b, descriptor(SharedB)) ++ set(multiply.enable, enable & 1L) ++
        multiply.disabled.flatMap(set(_, 0L)) ++ multiply.metadata.toVec.flatMap(set(_, Metadata.toLong)) ++
        multiply.scales.toVec.flatMap(scales => set(scales.a, ScaleA.toLong) ++ set(scales.b, ScaleB.toLong)) ++
        multiply.zeroColumns.toVec.flatMap(set(_, 0L))
    val stores = shared.map((offset, bits) =>
      VMIR.Instruction.Store(
        immediate(VMAddressSpace.SharedBase + offset),
        immediate(bits),
        8,
        VMIR.MemoryAccess.Generic(false)
      )
    )
    val tensor = cells.groupBy(_._1._2).toVec.sortBy(_._1).flatMap { (target, entries) =>
      VMIR.PredicatedInstruction(VMIR.Instruction.Move(scratch, immediate(0L), 64), None) +:
        entries.flatMap { case ((lane, _), bits) =>
          NvidiaHarness.only(
            selector,
            VMIR.Value.LaneId,
            lane,
            Vec(VMIR.Instruction.Move(scratch, immediate(bits & 0xffffffffL), 64))
          )
        } :+ VMIR.PredicatedInstruction(
          VMIR.Instruction.TensorMemoryStore(word, Vec(VMIR.Value.Register(scratch)), immediate(target.toLong)),
          None
        )
    }
    // tcgen05.mma issues once per thread, so one lane issues it
    val leader = VMIR.Instruction.SetPredicate(
      selector,
      VMIR.Comparison.Equal,
      VMIR.NumericFormat.Unsigned(32),
      VMIR.Value.LaneId,
      immediate(0L)
    )
    val body = (operands ++ stores :+ leader).map(VMIR.PredicatedInstruction(_, None)) ++
      steps.map(VMIR.PredicatedInstruction(_, Some(VMIR.ExecutionPredicate(selector, negated = false)))) ++
      Vec(
        VMIR.Instruction.TensorMemoryLoad(word, Vec(scratch), immediate(at.column.toLong), None),
        VMIR.Instruction.Return
      ).map(VMIR.PredicatedInstruction(_, None))
    val instructions = tensor ++ body
    NvidiaHarness.run(
      instructions,
      (registerCount.raw + 1).unsafe[Int >= 0],
      (predicateCount.raw + 1).unsafe[Int >= 0],
      32,
      memory
    )(
      _.register(at.lane.unsafe[Int >= 0], scratch.unwrap)
    )
  }
}
