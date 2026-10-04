package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

/** Shared launch argument preparation and invocation for NVIDIA instruction sets. */
object VMKernelLaunch {
  def execute(
      kernel: Kernel,
      task: KernelTask,
      addresses: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups
  ): Either[Fault, Unit] =
    for {
      _ <- Either.cond(
        task.argumentBuffer.isEmpty,
        (),
        Fault.Unsupported(s"${kernel.isaName} launches do not support prepacked argument buffers")
      )
      _ <- Either.cond(
        task.arguments.size == kernel.parameters.size,
        (),
        Fault.InvalidTask(
          s"${kernel.isaName} kernel ${kernel.name} expects ${kernel.parameters.size} arguments, found ${task.arguments.size}"
        )
      )
      arguments <- prepareArguments(kernel, task.arguments, task.pointerRelocations, addresses)
      _         <- VMInterpreter.execute(
        kernel.copy(dynamicSharedBytes = task.groupSegmentBytes),
        task.geometry,
        arguments,
        DynamicAddressMemory(VMMemory(addresses)),
        control,
        workgroups,
        Map.empty,
        None
      ).left.map(_.toFault)
    } yield ()

  private def prepareArguments(
      kernel: Kernel,
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation],
      addresses: AddressSpace
  ): Either[Fault, Map[NonEmpty[String], Argument]] =
    kernel.parameters.zip(arguments).zipWithIndex.foldLeft[
      Either[Fault, Map[NonEmpty[String], Argument]]
    ](Right(Map.empty)) { case (result, ((parameter, argument), index)) =>
      result.flatMap { values =>
        argument match {
          case KernelTask.Argument.Scalar(bytes) =>
            for {
              _ <- Either.cond(
                bytes.size == parameter.widthBytes.raw,
                (),
                Fault.InvalidTask(
                  s"${kernel.isaName} argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${bytes.size}"
                )
              )
              nonEmpty  <- bytes.refine[NonEmpty[Vec[Byte]]].left.map(Fault.InvalidTask(_))
              relocated <- relocateArgument(kernel.isaName, index, nonEmpty, relocations, addresses)
            } yield values.updated(parameter.name, Argument(relocated))
          case KernelTask.Argument.Memory(region, offset, _) =>
            for {
              _ <- Either.cond(
                parameter.widthBytes.raw == 8,
                (),
                Fault.InvalidTask(
                  s"memory argument ${parameter.name} requires an eight-byte ${kernel.isaName} parameter"
                )
              )
              address <- addresses.address(region, offset)
            } yield values.updated(parameter.name, Argument.fromLong(address.raw))
          case KernelTask.Argument.Local(size) =>
            Left(Fault.Unsupported(
              s"${kernel.isaName} local argument ${parameter.name} ($size bytes) is unsupported"
            ))
        }
      }
    }

  private def relocateArgument(
      isaName: NonEmpty[String],
      index: Int,
      bytes: NonEmpty[Vec[Byte]],
      relocations: Vec[KernelTask.PointerRelocation],
      addresses: AddressSpace
  ): Either[Fault, NonEmpty[Vec[Byte]]] = {
    val relocated = Vec.toArray(bytes)
    relocations.foldLeft[Either[Fault, Unit]](Right(())) {
      case (
            result,
            KernelTask.PointerRelocation(
              KernelTask.RelocationSource.Argument(source),
              sourceOffset,
              target,
              targetOffset
            )
          ) if source.raw == index =>
        result.flatMap { _ =>
          Either.cond(
            sourceOffset.raw <= relocated.length && 8 <= relocated.length - sourceOffset.raw,
            (),
            Fault.InvalidTask(s"pointer relocation exceeds its ${isaName.raw} argument")
          ).flatMap(_ =>
            addresses.address(target, targetOffset).map(address =>
              LittleEndian.write(address.raw, relocated, sourceOffset.raw.toInt, 8)
            )
          )
        }
      case (result, _) => result
    }.map(_ => Vec.from(relocated).unsafe[NonEmpty[Vec[Byte]]])
  }
}
