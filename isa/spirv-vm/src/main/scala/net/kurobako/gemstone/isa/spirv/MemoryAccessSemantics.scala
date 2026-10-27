package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V memory semantics, as requests to a [[SPIRVMachine]]. A pointer operand names the address it holds, and the
  * machine reads the pointer's bounds, storage class and layout from it.
  */
private object MemoryAccessSemantics {

  inline def scalarLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.loadScalar(pointer, size, target, typeId)(machine.advance)

  inline def addressLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      target: Int,
      typeId: Int,
      instruction: Instruction
  ): F[Unit] = machine.loadAddress(pointer, target, typeId, instruction)(machine.advance)

  inline def valueLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      target: Int,
      typeId: Int,
      size: Int >= 0
  ): F[Unit] = machine.loadValue(pointer, target, typeId, size)(machine.advance)

  inline def handleLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      target: Int,
      typeId: Int,
      pointee: Int
  ): F[Unit] = machine.loadHandle(pointer, target, typeId, pointee)(machine.advance)

  inline def sampledLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      target: Int,
      typeId: Int,
      imageType: Int
  ): F[Unit] = machine.loadSampled(pointer, target, typeId, imageType)(machine.advance)

  inline def scalarStore[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      source: Int,
      size: Int in 1 ~ 8,
      instruction: Instruction
  ): F[Unit] = machine.storeScalar(pointer, source, size, instruction)(machine.advance)

  inline def valueStore[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      source: Int,
      instruction: Instruction
  ): F[Unit] = machine.storeValue(pointer, source, instruction)(machine.advance)

  inline def copy[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(target: Int, origin: Int): F[Unit] =
    machine.copyMemory(target, origin)(machine.advance)

  inline def copySized[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      origin: Int,
      size: Int
  ): F[Unit] =
    machine.copyMemorySized(target, origin, size)(machine.advance)

  inline def groupAsyncCopy[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  ): F[Unit] =
    machine.groupAsyncCopy(target, typeId, scope, destination, source, count, stride, element)(machine.advance)

  inline def waitEvents[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      scope: Int,
      count: Int,
      events: Int
  ): F[Unit] =
    machine.waitEvents(scope, count, events)(machine.advance)

  inline def atomicLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int,
      scope: Int,
      semantics: Int
  ): F[Unit] = machine.atomicLoad(pointer, size, target, typeId, scope, semantics)(machine.advance)

  inline def atomicStore[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      source: Int,
      size: Int in 1 ~ 8,
      scope: Int,
      semantics: Int
  ): F[Unit] = machine.atomicStore(pointer, source, size, scope, semantics)(machine.advance)

  inline def flagTestAndSet[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      target: Int,
      typeId: Int,
      scope: Int,
      semantics: Int
  ): F[Unit] = machine.flagTestAndSet(pointer, target, typeId, scope, semantics)(machine.advance)

  inline def flagClear[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      scope: Int,
      semantics: Int
  ): F[Unit] =
    machine.flagClear(pointer, scope, semantics)(machine.advance)

  inline def convertHandle[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.convertHandle(source, target, typeId)(machine.advance)

  inline def rawAccessChain[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.rawAccessChain(base, stride, element, offset, pointeeType, target, typeId)(machine.advance)

  inline def predicatedLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      predicate: Int,
      pointer: Int,
      default: Int,
      target: Int,
      typeId: Int,
      size: Int >= 0
  ): F[Unit] = machine.predicatedLoad(predicate, pointer, default, target, typeId, size)(machine.advance)

  inline def predicatedStore[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      predicate: Int,
      pointer: Int,
      source: Int
  ): F[Unit] = machine.predicatedStore(predicate, pointer, source)(machine.advance)

  inline def maskedGather[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      pointers: Int,
      mask: Int,
      fill: Int,
      target: Int,
      typeId: Int,
      width: Int in 1 ~ 8
  ): F[Unit] = machine.maskedGather(pointers, mask, fill, target, typeId, width)(machine.advance)

  inline def maskedScatter[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      data: Int,
      pointers: Int,
      mask: Int,
      width: Int in 1 ~ 8
  ): F[Unit] = machine.maskedScatter(data, pointers, mask, width)(machine.advance)

  private inline def elementOffset[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      lane: Int,
      width: Int in 1 ~ 8
  ): V = algebra.multiply(
    algebra.add(algebra.multiply(algebra.literal(lane.toLong), machine.subgroupSize), machine.subgroupLocal),
    algebra.literal(width.raw.toLong)
  )

  inline def blockLoad[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      width: Int in 1 ~ 8,
      lanes: Int,
      target: Int,
      typeId: Int
  ): F[Unit] =
    machine.loadLeaves(pointer, lanes, width, target, typeId)(lane => elementOffset(algebra, machine)(lane, width))(
      machine.advance
    )

  inline def blockStore[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      source: Int,
      width: Int in 1 ~ 8,
      lanes: Int
  ): F[Unit] =
    machine.storeLeaves(pointer, source, lanes, width)(lane => elementOffset(algebra, machine)(lane, width))(
      machine.advance
    )

  inline def prefetch[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(pointer: Int): F[Unit] =
    machine.prefetch(pointer)(machine.advance)

  inline def convertSampledHandle[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.convertSampledHandle(source, target, typeId)(machine.advance)

  /** The forms the explorer shows: scalar accesses of `bytes` bytes, copies and atomic accesses of `bytes` bytes. */
  def render(instruction: Instruction, machine: SPIRVMachineRender, bytes: Int): Option[Term.Steps] =
    instruction match {
      case Instruction.Load(resultType, dest, pointer, _) =>
        Some(scalarLoad(machine)(id(pointer), bytes.unsafe[Int in 1 ~ 8], id(dest), id(resultType)))
      case Instruction.Store(pointer, source, _) =>
        Some(scalarStore(machine)(id(pointer), id(source), bytes.unsafe[Int in 1 ~ 8], instruction))
      case Instruction.CopyMemory(target, source, _, _)            => Some(copy(machine)(id(target), id(source)))
      case Instruction.CopyMemorySized(target, source, size, _, _) =>
        Some(copySized(machine)(id(target), id(source), id(size)))
      case Instruction.UntypedPrefetchKHR(pointerType = pointer)               => Some(prefetch(machine)(id(pointer)))
      case Instruction.SubgroupBlockPrefetchINTEL(pointer, _, _)               => Some(prefetch(machine)(id(pointer)))
      case Instruction.Subgroup2DBlockPrefetchINTEL(srcBasePointer = pointer)  => Some(prefetch(machine)(id(pointer)))
      case Instruction.AtomicLoad(resultType, dest, pointer, scope, semantics) =>
        Some(atomicLoad(machine)(
          pointer = id(pointer),
          size = bytes.unsafe[Int in 1 ~ 8],
          target = id(dest),
          typeId = id(resultType),
          scope = id(scope),
          semantics = id(semantics)
        ))
      case Instruction.AtomicStore(pointer, scope, semantics, value) =>
        Some(atomicStore(machine)(id(pointer), id(value), bytes.unsafe[Int in 1 ~ 8], id(scope), id(semantics)))
      case Instruction.AtomicFlagTestAndSet(resultType, dest, pointer, scope, semantics) =>
        Some(flagTestAndSet(machine)(
          pointer = id(pointer),
          target = id(dest),
          typeId = id(resultType),
          scope = id(scope),
          semantics = id(semantics)
        ))
      case Instruction.AtomicFlagClear(pointer, scope, semantics) =>
        Some(flagClear(machine)(id(pointer), id(scope), id(semantics)))
      case Instruction.GroupAsyncCopy(t, d, scope, destination, source, count, stride, _) =>
        Some(groupAsyncCopy(machine)(
          target = id(d),
          typeId = id(t),
          scope = id(scope),
          destination = id(destination),
          source = id(source),
          count = id(count),
          stride = id(stride),
          element = -1
        ))
      case Instruction.UntypedGroupAsyncCopyKHR(
            resultType = t,
            dest = d,
            execution = scope,
            destination = destination,
            src = source,
            elementNumBytes = element,
            numElements = count,
            stride = stride
          ) =>
        Some(groupAsyncCopy(machine)(
          target = id(d),
          typeId = id(t),
          scope = id(scope),
          destination = id(destination),
          source = id(source),
          count = id(count),
          stride = id(stride),
          element = id(element)
        ))
      case Instruction.GroupWaitEvents(scope, count, events) =>
        Some(waitEvents(machine)(id(scope), id(count), id(events)))
      case _ => renderVendor(instruction, machine, bytes)
    }

  // the vendor forms render in their own method to keep each method under the JIT's size limit
  private def renderVendor(instruction: Instruction, machine: SPIRVMachineRender, bytes: Int): Option[Term.Steps] =
    instruction match {
      case Instruction.ConvertHandleToImageINTEL(t, d, handle) => Some(convertHandle(machine)(id(handle), id(d), id(t)))
      case Instruction.ConvertHandleToSamplerINTEL(t, d, handle) =>
        Some(convertHandle(machine)(id(handle), id(d), id(t)))
      case Instruction.RawAccessChainNV(t, d, base, stride, element, offset, _) =>
        Some(rawAccessChain(machine)(
          base = id(base),
          stride = id(stride),
          element = id(element),
          offset = id(offset),
          pointeeType = id(t),
          target = id(d),
          typeId = id(t)
        ))
      case Instruction.PredicatedLoadINTEL(t, d, pointer, predicate, default, _) =>
        Some(predicatedLoad(machine)(
          predicate = id(predicate),
          pointer = id(pointer),
          default = id(default),
          target = id(d),
          typeId = id(t),
          size = bytes.unsafe[Int >= 0]
        ))
      case Instruction.PredicatedStoreINTEL(pointer, source, predicate, _) =>
        Some(predicatedStore(machine)(id(predicate), id(pointer), id(source)))
      case Instruction.MaskedGatherINTEL(t, d, pointers, _, mask, fill) =>
        Some(maskedGather(machine)(
          pointers = id(pointers),
          mask = id(mask),
          fill = id(fill),
          target = id(d),
          typeId = id(t),
          width = bytes.unsafe[Int in 1 ~ 8]
        ))
      case Instruction.MaskedScatterINTEL(input, pointers, _, mask) =>
        Some(maskedScatter(machine)(id(input), id(pointers), id(mask), bytes.unsafe[Int in 1 ~ 8]))
      case Instruction.ConvertHandleToSampledImageINTEL(t, d, handle) =>
        Some(convertSampledHandle(machine)(id(handle), id(d), id(t)))
      case Instruction.SubgroupBlockReadINTEL(t, d, pointer) =>
        Some(blockLoad(Render, machine)(
          pointer = id(pointer),
          width = bytes.unsafe[Int in 1 ~ 8],
          lanes = machine.leafCount(id(d)),
          target = id(d),
          typeId = id(t)
        ))
      case Instruction.SubgroupBlockWriteINTEL(pointer, data) =>
        Some(blockStore(Render, machine)(
          pointer = id(pointer),
          source = id(data),
          width = bytes.unsafe[Int in 1 ~ 8],
          lanes = machine.leafCount(id(data))
        ))
      case _ => None
    }
}
