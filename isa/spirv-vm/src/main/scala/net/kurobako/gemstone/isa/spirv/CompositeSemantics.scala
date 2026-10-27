package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V composite semantics, as requests to a [[SPIRVMachine]]. A composite is its scalar leaves in lane order, so
  * every operation moves leaves from operands to the result.
  */
private object CompositeSemantics {

  inline def extract[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      offset: Int,
      count: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.extract(source, offset, count, target, typeId)(machine.advance)

  inline def construct[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      parts: Array[Int],
      leaves: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.construct(parts, leaves, target, typeId)(machine.advance)

  inline def conditionalCopy[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pairs: Array[Int],
      target: Int,
      typeId: Int
  ): F[Unit] = machine.conditionalCopy(pairs, target, typeId)(machine.advance)

  inline def insert[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      composite: Int,
      offset: Int,
      count: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.insert(source, composite, offset, count, target, typeId)(machine.advance)

  inline def shuffle[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      left: Int,
      right: Int,
      selection: Array[Long],
      target: Int,
      typeId: Int
  ): F[Unit] = machine.shuffle(left, right, selection, target, typeId)(machine.advance)

  inline def extractDynamic[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      vector: Int,
      index: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.extractDynamic(vector, index, target, typeId)(machine.advance)

  inline def insertDynamic[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      vector: Int,
      component: Int,
      index: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.insertDynamic(vector, component, index, target, typeId)(machine.advance)

  inline def copy[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(source: Int, target: Int, typeId: Int): F[Unit] =
    machine.copyValue(source, target, typeId)(machine.advance)

  private inline def piece[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      have: Int,
      from: Int,
      leaf: Int
  ): V =
    if (leaf < have) algebra.truncate(machine.leaf(source, leaf), (from * 8).unsafe[Int in 1 ~ 64])
    else algebra.literal(0L)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def reinterpreted[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      have: Int,
      from: Int,
      to: Int,
      lane: Int
  ): V =
    if (from == to) piece(algebra, machine)(source, have, from, lane)
    else if (from > to) {
      val ratio = from / to
      val shift = 8L * to * (lane % ratio)
      val moved = if (shift == 0L) piece(algebra, machine)(source, have, from, lane / ratio)
      else algebra.shiftRightLogical(piece(algebra, machine)(source, have, from, lane / ratio), algebra.literal(shift))
      algebra.truncate(moved, (to * 8).unsafe[Int in 1 ~ 64])
    } else {
      val ratio  = to / from
      var packed = piece(algebra, machine)(source, have, from, lane * ratio)
      var part   = 1
      while (part < ratio) {
        packed = algebra.or(
          packed,
          algebra.shiftLeft(
            piece(algebra, machine)(source, have, from, lane * ratio + part),
            algebra.literal(8L * from * part)
          )
        )
        part += 1
      }
      packed
    }

  /** Lane `i` of `count` of the array `target` takes the bytes `to` wide at offset `i * to` in the bytes of the array
    * `source`, whose `have` lanes are `from` bytes wide and which read as zero past their end.
    */
  inline def bitCast[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      have: Int,
      from: Int in 1 ~ 8,
      target: Int,
      count: Int,
      to: Int in 1 ~ 8,
      typeId: Int
  ): F[Unit] = machine.eachLeaf(Array(source), count, target, typeId)(lane =>
    reinterpreted(algebra, machine)(source, have, from, to, lane)
  )(machine.advance)

  inline def subArray[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      index: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.subArray(source, index, target, typeId)(machine.advance)

  inline def zero[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      typeId: Int,
      opcode: String
  ): F[Unit] = machine.zero(target, typeId, opcode)(machine.advance)

  /** The forms the explorer shows: extraction and insertion of the `count` leaves from leaf `offset`, and the `count`
    * leaves a replicated value fills.
    */
  def render(instruction: Instruction, machine: SPIRVMachineRender, offset: Int, count: Int): Option[Term.Steps] =
    instruction match {
      case Instruction.CompositeExtract(resultType, dest, composite, _) =>
        Some(extract(machine)(id(composite), offset, count, id(dest), id(resultType)))
      case Instruction.CompositeConstruct(resultType, dest, constituents) =>
        Some(construct(machine)(constituents.map(id(_)).toArray, 0, id(dest), id(resultType)))
      case Instruction.CompositeConstructReplicateEXT(resultType, dest, value) =>
        Some(construct(machine)(Array.fill(count)(id(value)), count, id(dest), id(resultType)))
      case Instruction.ConditionalCopyObjectINTEL(resultType, dest, pairs) =>
        Some(conditionalCopy(machine)(pairs.map(id(_)).toArray, id(dest), id(resultType)))
      case Instruction.CompositeInsert(resultType, dest, source, composite, _) =>
        Some(insert(machine)(id(source), id(composite), offset, count, id(dest), id(resultType)))
      case Instruction.VectorShuffle(resultType, dest, left, right, components) =>
        Some(shuffle(machine)(id(left), id(right), components.map(unsigned).toArray, id(dest), id(resultType)))
      case Instruction.VectorExtractDynamic(resultType, dest, vector, index) =>
        Some(extractDynamic(machine)(id(vector), id(index), id(dest), id(resultType)))
      case Instruction.VectorInsertDynamic(resultType, dest, vector, component, index) =>
        Some(insertDynamic(machine)(
          vector = id(vector),
          component = id(component),
          index = id(index),
          target = id(dest),
          typeId = id(resultType)
        ))
      case Instruction.CopyObject(resultType, dest, source) => Some(copy(machine)(id(source), id(dest), id(resultType)))
      case Instruction.CopyLogical(resultType, dest, source) =>
        Some(copy(machine)(id(source), id(dest), id(resultType)))
      case Instruction.ExpectKHR(resultType, dest, value, _) =>
        Some(copy(machine)(id(value), id(dest), id(resultType)))
      case Instruction.Undef(resultType, dest)     => Some(zero(machine)(id(dest), id(resultType), "OpUndef"))
      case Instruction.PoisonKHR(resultType, dest) => Some(zero(machine)(id(dest), id(resultType), "OpPoisonKHR"))
      case Instruction.BitCastArrayQCOM(resultType, dest, source) =>
        Some(bitCast(Render, machine)(
          source = id(source),
          have = machine.leafCount(id(source)),
          from = machine.leafBytes(id(source)),
          target = id(dest),
          count = machine.leafCount(id(dest)),
          to = machine.leafBytes(id(dest)),
          typeId = id(resultType)
        ))
      case Instruction.ExtractSubArrayQCOM(resultType, dest, source, index) =>
        Some(subArray(machine)(id(source), id(index), id(dest), id(resultType)))
      case _ => None
    }
}
