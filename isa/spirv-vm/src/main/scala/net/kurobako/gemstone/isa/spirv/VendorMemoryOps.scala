package net.kurobako.gemstone.isa.spirv

import Op.*
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def gather(
      memory: MemoryState,
      addresses: Array[Long],
      selected: Array[Long],
      fills: Array[Long],
      element: Int,
      width: Int in 1 ~ 8,
      storage: Int
  ): AddressSpace.ReadResult[Array[Long]] = {
    val result                                            = Array.ofDim[Long](addresses.length)
    var failure: Fault | AddressSpace.Pending.type | Null = null
    var lane                                              = 0
    while (lane < result.length && (failure eq null)) {
      if (selected(lane) == 0L) result(lane) = fills(lane % fills.length)
      else
        memory.readScalar(Pointer(addresses(lane), element, storage), width) match {
          case Left(error)  => failure = error
          case Right(value) => result(lane) = value
        }
      lane += 1
    }
    if (failure ne null) Left(failure) else Right(result)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def scatter(
      memory: MemoryState,
      data: Array[Long],
      addresses: Array[Long],
      selected: Array[Long],
      element: Int,
      width: Int in 1 ~ 8,
      storage: Int
  ): Either[Fault, Unit] = {
    var failure: Fault | Null = null
    var lane                  = 0
    while (lane < addresses.length && failure == null) {
      if (selected(lane) != 0L)
        memory.writeScalar(Pointer(addresses(lane), element, storage), data(lane), width) match {
          case Left(error) => failure = error
          case Right(())   => ()
        }
      lane += 1
    }
    if (failure != null) Left(failure) else Right(())
  }

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.MaskedGatherINTEL(t, d, pointers, _, mask, fill) =>
      vectors(site, id(t), id(pointers), "OpMaskedGatherINTEL") { (element, width, storage) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            addresses <- bits(values, id(pointers))
            selected  <- bits(values, id(mask))
            fills     <- bits(values, id(fill))
            result    <- gather(memory, addresses, selected, fills, element, width, storage)
          } yield values.update(id(d), VMValue.Bits(id(t), result))
        }
      }
    case Instruction.MaskedScatterINTEL(input, pointers, _, mask) =>
      vectors(site, site.program.valueType(id(input)), id(pointers), "OpMaskedScatterINTEL") {
        (element, width, storage) =>
          step { (execution, memory) =>
            val values = execution.values
            for {
              data      <- bits(values, id(input))
              addresses <- bits(values, id(pointers))
              selected  <- bits(values, id(mask))
              _         <- scatter(memory, data, addresses, selected, element, width, storage)
            } yield ()
          }
      }
    case Instruction.PredicatedLoadINTEL(t, d, pointer, predicate, default, _) =>
      val program = site.program
      val size    = program.typeSize(id(t))
      step { (execution, memory) =>
        val values = execution.values
        scalar(values, id(predicate)).flatMap(enabled =>
          if (enabled == 0L) transfer(values, id(default), id(d), id(t))
          else
            address(values, id(pointer)).flatMap(memory.read(_, size)).flatMap(program.decode(id(t), _))
              .map(values.update(id(d), _))
        )
      }
    case Instruction.PredicatedStoreINTEL(pointer, source, predicate, _) =>
      val program = site.program
      step { (execution, memory) =>
        val values = execution.values
        scalar(values, id(predicate)).flatMap(enabled =>
          if (enabled == 0L) Right(())
          else
            for {
              target <- address(values, id(pointer))
              value  <- values.get(id(source)).toRight(undefined(id(source)))
              bytes  <- program.encode(value)
              _      <- memory.write(target, bytes)
            } yield ()
        )
      }
    // SPV_INTEL_bindless_images: a handle is the address of the descriptor a kernel argument of the type would bind
    case Instruction.ConvertHandleToImageINTEL(t, d, handle)        => bindless(id(t), id(d), id(handle))
    case Instruction.ConvertHandleToSamplerINTEL(t, d, handle)      => bindless(id(t), id(d), id(handle))
    case Instruction.ConvertHandleToSampledImageINTEL(t, d, handle) =>
      site.program.runtimeType(id(t)) match {
        case Some(RuntimeType.SampledImage(image)) =>
          step((execution, memory) =>
            scalar(execution.values, id(handle)).flatMap(address =>
              ImageOps.sampledImage(memory, handled(address, id(t)), image)
            ).map(sampled => execution.values.update(id(d), VMValue.Address(id(t), sampled)))
          )
        case _ => Fail(Fault.InvalidRequest("OpConvertHandleToSampledImageINTEL requires a sampled image result"))
      }
    case Instruction.RawAccessChainNV(t, d, base, stride, element, offset, _) =>
      val pointee = site.program.pointerPointee(id(t))
      step { (execution, _) =>
        val values = execution.values
        for {
          start   <- address(values, id(base))
          spacing <- scalar(values, id(stride))
          index   <- scalar(values, id(element))
          bytes   <- scalar(values, id(offset))
        } yield values.update(
          id(d),
          VMValue.Address(
            id(t),
            start.copy(address = start.address + spacing * index + bytes, pointee = pointee, matrixLayout = None)
          )
        )
      }
  }

  // a handle carries no range, so the descriptor may span the largest a view has: every level, swizzle and pitch
  private val DescriptorBytes: Long = ImageDescriptor.Layout.LevelTable + 15L * ImageDescriptor.Layout.LevelBytes +
    ImageDescriptor.Layout.SwizzleBytes + ImageDescriptor.Layout.PitchBytes

  private def handled(address: Long, typeId: Int): Pointer =
    Pointer(address, typeId, StorageClass.UniformConstant, Some(address -> (address + DescriptorBytes)))

  private def bindless(resultType: Int, dest: Int, handle: Int): Op = step((execution, _) =>
    scalar(execution.values, handle).map(address =>
      execution.values.update(dest, VMValue.Address(resultType, handled(address, resultType)))
    )
  )

  /** A gather or scatter of the components of `typeId` through a vector of pointers into one storage class. */
  private inline def vectors(
      site: Site,
      typeId: Int,
      pointers: Int,
      opcode: String
  )(inline op: (Int, Int in 1 ~ 8, Int) => Op): Op = {
    val program = site.program
    (program.runtimeType(typeId), program.runtimeType(program.valueType(pointers))) match {
      case (Some(RuntimeType.VectorType(element, count)), Some(RuntimeType.VectorType(pointer, lanes)))
          if count == lanes =>
        (program.scalarMemoryWidth(element), program.runtimeType(pointer)) match {
          case (Right(width), Some(RuntimeType.Pointer(storage, _))) => op(element, width, storage)
          case _ => Fail(Fault.InvalidRequest(s"$opcode needs scalar components and a vector of pointers"))
        }
      case _ => Fail(Fault.InvalidRequest(s"$opcode needs data and pointer vectors of one lane count"))
    }
  }
}
