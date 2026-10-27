package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Vendor memory accesses: SPV_INTEL_masked_gather_scatter over vectors of pointers, whose lanes hold addresses,
  * SPV_INTEL_predicated_io, SPV_NV_raw_access_chains, whose robustness operands only permit what the VM already does,
  * and SPV_INTEL_bindless_images' handles.
  */
private object VendorMemoryOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.MaskedGatherINTEL(t, d, pointers, _, mask, fill) =>
      vectors(site, id(t), id(pointers), "OpMaskedGatherINTEL")(width =>
        MaskedGather(pointers = id(pointers), mask = id(mask), fill = id(fill), target = id(d), typeId = id(t), width)
      )
    case Instruction.MaskedScatterINTEL(input, pointers, _, mask) =>
      vectors(site, site.program.valueType(id(input)), id(pointers), "OpMaskedScatterINTEL")(width =>
        MaskedScatter(id(input), id(pointers), id(mask), width)
      )
    case Instruction.PredicatedLoadINTEL(t, d, pointer, predicate, default, _) =>
      PredicatedLoad(
        predicate = id(predicate),
        pointer = id(pointer),
        default = id(default),
        target = id(d),
        typeId = id(t),
        size = site.program.typeSize(id(t))
      )
    case Instruction.PredicatedStoreINTEL(pointer, source, predicate, _) =>
      PredicatedStore(id(predicate), id(pointer), id(source))
    // SPV_INTEL_bindless_images: a handle is the address of the descriptor a kernel argument of the type would bind
    case Instruction.ConvertHandleToImageINTEL(t, d, handle)        => ConvertHandle(id(handle), id(d), id(t))
    case Instruction.ConvertHandleToSamplerINTEL(t, d, handle)      => ConvertHandle(id(handle), id(d), id(t))
    case Instruction.ConvertHandleToSampledImageINTEL(t, d, handle) =>
      site.program.runtimeType(id(t)) match {
        case Some(RuntimeType.SampledImage(_)) => ConvertSampledHandle(id(handle), id(d), id(t))
        case _ => Fail(Fault.InvalidRequest("OpConvertHandleToSampledImageINTEL requires a sampled image result"))
      }
    case Instruction.RawAccessChainNV(t, d, base, stride, element, offset, _) =>
      RawAccessChain(
        base = id(base),
        stride = id(stride),
        element = id(element),
        offset = id(offset),
        pointeeType = site.program.pointerPointee(id(t)),
        target = id(d),
        typeId = id(t)
      )
  }

  private class MaskedGather(pointers: Int, mask: Int, fill: Int, target: Int, typeId: Int, width: Int in 1 ~ 8)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.maskedGather(InvocationMachine(execution))(pointers, mask, fill, target, typeId, width)
  }

  private class MaskedScatter(data: Int, pointers: Int, mask: Int, width: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.maskedScatter(InvocationMachine(execution))(data, pointers, mask, width)
  }

  private class PredicatedLoad(predicate: Int, pointer: Int, default: Int, target: Int, typeId: Int, size: Int >= 0)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.predicatedLoad(InvocationMachine(execution))(
        predicate,
        pointer,
        default,
        target,
        typeId,
        size
      )
  }

  private class PredicatedStore(predicate: Int, pointer: Int, source: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.predicatedStore(InvocationMachine(execution))(predicate, pointer, source)
  }

  private class ConvertHandle(source: Int, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.convertHandle(InvocationMachine(execution))(source, target, typeId)
  }

  private class ConvertSampledHandle(source: Int, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.convertSampledHandle(InvocationMachine(execution))(source, target, typeId)
  }

  private class RawAccessChain(
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.rawAccessChain(InvocationMachine(execution))(
        base,
        stride,
        element,
        offset,
        pointeeType,
        target,
        typeId
      )
  }

  /** A gather or scatter of the components of `typeId` through a vector of pointers into one storage class. */
  private inline def vectors(
      site: Site,
      typeId: Int,
      pointers: Int,
      opcode: String
  )(inline op: (Int in 1 ~ 8) => Op): Op = {
    val program = site.program
    (program.runtimeType(typeId), program.runtimeType(program.valueType(pointers))) match {
      case (Some(RuntimeType.VectorType(element, count)), Some(RuntimeType.VectorType(pointer, lanes)))
          if count == lanes =>
        (program.scalarMemoryWidth(element), program.runtimeType(pointer)) match {
          case (Right(width), Some(RuntimeType.Pointer(_, _))) => op(width)
          case _ => Fail(Fault.InvalidRequest(s"$opcode needs scalar components and a vector of pointers"))
        }
      case _ => Fail(Fault.InvalidRequest(s"$opcode needs data and pointer vectors of one lane count"))
    }
  }
}
