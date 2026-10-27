package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V pointer semantics, as requests to a [[SPIRVMachine]]. */
private object PointerAccessSemantics {

  inline def accessChain[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      base: Int,
      element: Int,
      indexes: Array[Int],
      chain: AccessChain,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.accessChain(base, element, indexes, chain, target, typeId)(machine.advance)

  private def chained(
      machine: SPIRVMachineRender,
      chain: AccessChain,
      resultType: Operand,
      dest: Operand,
      base: Operand,
      element: Int,
      indexes: Vec[Operand]
  ): Option[Term.Steps] =
    Some(accessChain(machine)(id(base), element, indexes.map(id(_)).toArray, chain, id(dest), id(resultType)))

  /** The forms the explorer shows: access chains over the layout `chain` names. */
  def renderChain(instruction: Instruction, machine: SPIRVMachineRender, chain: AccessChain): Option[Term.Steps] =
    instruction match {
      case Instruction.AccessChain(resultType, dest, base, indexes) =>
        chained(machine, chain, resultType, dest, base, -1, indexes)
      case Instruction.InBoundsAccessChain(resultType, dest, base, indexes) =>
        chained(machine, chain, resultType, dest, base, -1, indexes)
      case Instruction.PtrAccessChain(resultType, dest, base, element, indexes) =>
        chained(machine, chain, resultType, dest, base, id(element), indexes)
      case Instruction.InBoundsPtrAccessChain(resultType, dest, base, element, indexes) =>
        chained(machine, chain, resultType, dest, base, id(element), indexes)
      case _ => None
    }

  def renderUntypedChain(
      instruction: Instruction,
      machine: SPIRVMachineRender,
      chain: AccessChain
  ): Option[Term.Steps] =
    instruction match {
      case Instruction.UntypedAccessChainKHR(resultType, dest, _, base, indexes) =>
        chained(machine, chain, resultType, dest, base, -1, indexes)
      case Instruction.UntypedInBoundsAccessChainKHR(resultType, dest, _, base, indexes) =>
        chained(machine, chain, resultType, dest, base, -1, indexes)
      case Instruction.UntypedPtrAccessChainKHR(resultType, dest, _, base, element, indexes) =>
        chained(machine, chain, resultType, dest, base, id(element), indexes)
      case Instruction.UntypedInBoundsPtrAccessChainKHR(resultType, dest, _, base, element, indexes) =>
        chained(machine, chain, resultType, dest, base, id(element), indexes)
      case _ => None
    }

  inline def pointerToInteger[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      mask: Long,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.pointerToInteger(pointer, mask, target, typeId)(machine.advance)

  inline def pointersToIntegers[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      mask: Long,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.pointersToIntegers(source, mask, target, typeId)(machine.advance)

  inline def integerToPointer[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.integerToPointer(source, pointee, storageClass, function, target, typeId)(machine.advance)

  inline def integersToPointers[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      lanes: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.integersToPointers(source, lanes, target, typeId)(machine.advance)

  inline def castToGeneric[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      cast: PointerCast,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.castToGeneric(source, cast, target, typeId)(machine.advance)

  inline def castFromGeneric[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      cast: PointerCast,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.castFromGeneric(source, cast, target, typeId)(machine.advance)

  inline def arrayLength[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      structure: Int,
      member: Int,
      declared: Option[Int],
      target: Int,
      typeId: Int
  ): F[Unit] = machine.arrayLength(structure, member, declared, target, typeId)(machine.advance)

  /** The forms the explorer shows: conversions to and from integers, casts and array lengths. */
  def render(instruction: Instruction, machine: SPIRVMachineRender, cast: PointerCast): Option[Term.Steps] =
    instruction match {
      case Instruction.ConvertPtrToU(resultType, dest, pointer) =>
        Some(pointerToInteger(machine)(id(pointer), -1L, id(dest), id(resultType)))
      case Instruction.ConvertUToPtr(resultType, dest, source) =>
        Some(integerToPointer(machine)(
          source = id(source),
          pointee = cast.pointee,
          storageClass = cast.storageClass,
          function = false,
          target = id(dest),
          typeId = id(resultType)
        ))
      case Instruction.PtrCastToGeneric(resultType, dest, source) =>
        Some(castToGeneric(machine)(id(source), cast, id(dest), id(resultType)))
      case Instruction.GenericCastToPtr(resultType, dest, source) =>
        Some(castFromGeneric(machine)(id(source), cast, id(dest), id(resultType)))
      case Instruction.GenericCastToPtrExplicit(resultType, dest, source, _) =>
        Some(castFromGeneric(machine)(id(source), cast, id(dest), id(resultType)))
      case Instruction.ArrayLength(resultType, dest, structure, member) =>
        Some(arrayLength(machine)(id(structure), unsigned(member).toInt, None, id(dest), id(resultType)))
      case Instruction.UntypedArrayLengthKHR(resultType, dest, structure, pointer, member) =>
        Some(arrayLength(machine)(id(pointer), unsigned(member).toInt, Some(id(structure)), id(dest), id(resultType)))
      case _ => None
    }
}
