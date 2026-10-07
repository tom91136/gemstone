package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object KernelArguments {
  enum Resolved {
    case Bytes(bytes: NonEmpty[Vec[Byte]])
    case Local(size: Long > 0)
  }

  def resolve(task: KernelTask, addresses: AddressSpace): Either[Fault, Vec[Resolved]] =
    resolve(task, (region, offset) => addresses.address(region, offset))

  /** Each argument as little-endian bytes: memory arguments become eight-byte addresses and argument pointer
    * relocations are written over their source bytes. `address` resolves every allocation offset, so an ISA can
    * redirect bound program globals.
    */
  def resolve(
      task: KernelTask,
      address: (AllocationId, Long >= 0) => Either[Fault, GuestAddress]
  ): Either[Fault, Vec[Resolved]] =
    task.arguments.zipWithIndex.foldLeft[Either[Fault, Vec[Resolved]]](Right(Vec.empty)) {
      case (result, (argument, index)) =>
        result.flatMap { resolved =>
          val relocations = task.pointerRelocations.filter {
            case KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(source), _, _, _) =>
              source.raw == index
            case _ => false
          }
          val bytes = argument match {
            case KernelTask.Argument.Scalar(bytes)             => Right(Vec.toArray(bytes))
            case KernelTask.Argument.Memory(region, offset, _) =>
              address(region, offset).map(target => LittleEndian.toArray(target.raw, 8))
            case KernelTask.Argument.Local(_) =>
              Either.cond(
                relocations.isEmpty,
                Array.emptyByteArray,
                Fault.InvalidRequest(s"pointer relocation targets local kernel argument $index")
              )
            case KernelTask.Argument.Descriptor =>
              Left(Fault.InvalidRequest(s"kernel argument $index has no descriptor binding"))
          }
          bytes.flatMap(relocate(index, _, relocations, address)).map { relocated =>
            resolved :+
              (argument match {
                case KernelTask.Argument.Local(size) => Resolved.Local(size)
                case _ => Resolved.Bytes(Vec.unsafeWrapArray(relocated).unsafe[NonEmpty[Vec[Byte]]])
              })
          }
        }
    }

  private def relocate(
      index: Int,
      bytes: Array[Byte],
      relocations: Vec[KernelTask.PointerRelocation],
      address: (AllocationId, Long >= 0) => Either[Fault, GuestAddress]
  ): Either[Fault, Array[Byte]] =
    relocations.foldLeft[Either[Fault, Array[Byte]]](Right(bytes)) { (result, relocation) =>
      for {
        relocated <- result
        _         <- Either.cond(
          relocation.sourceOffset.raw <= relocated.length - 8L,
          (),
          Fault.InvalidRequest(s"pointer relocation exceeds kernel argument $index")
        )
        target <- address(relocation.target, relocation.targetOffset)
      } yield {
        LittleEndian.write(target.raw, relocated, relocation.sourceOffset.raw.toInt, 8)
        relocated
      }
    }
}
