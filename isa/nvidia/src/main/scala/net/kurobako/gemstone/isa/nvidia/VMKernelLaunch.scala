package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*

/** Shared launch argument preparation and invocation for NVIDIA instruction sets. */
private[isa] object VMKernelLaunch {
  def execute(
      kernel: Kernel,
      launch: KernelLaunch
  ): Either[Fault, Unit] = {
    val KernelLaunch(task, context, workgroups) = launch
    val parameters                              = kernel.entry.parameters
    for {
      _ <- Either.cond(
        task.argumentBuffer.isEmpty,
        (),
        Fault.Unsupported(s"${kernel.isaName} launches do not support prepacked argument buffers")
      )
      _ <- Either.cond(
        task.arguments.size == parameters.size,
        (),
        Fault.InvalidRequest(
          s"${kernel.isaName} kernel ${kernel.name} expects ${parameters.size} arguments, found ${task.arguments.size}"
        )
      )
      _        <- parameters.zip(task.arguments).traverse_(validateArgument(kernel.isaName, _, _))
      resolved <- KernelArguments.resolve(task, context.memory)
      arguments = parameters.zip(resolved).collect { case (parameter, KernelArguments.Resolved.Bytes(bytes)) =>
        parameter.name -> Argument(bytes)
      }.toMap
      _ <-
        VMInterpreter.execute(
          kernel,
          task.geometry,
          arguments,
          context.memory,
          context.control,
          workgroups,
          task.groupSegmentBytes,
          task.cluster
        )
    } yield ()
  }

  private def validateArgument(
      isaName: NonEmpty[String],
      parameter: KernelParameter,
      argument: KernelTask.Argument
  ): Either[Fault, Unit] = argument match {
    case KernelTask.Argument.Scalar(bytes) =>
      Either.cond(
        bytes.size == parameter.widthBytes.raw,
        (),
        Fault.InvalidRequest(
          s"$isaName argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${bytes.size}"
        )
      )
    case KernelTask.Argument.Memory(_, _, _) =>
      Either.cond(
        parameter.widthBytes == 8,
        (),
        Fault.InvalidRequest(s"memory argument ${parameter.name} requires an eight-byte $isaName parameter")
      )
    case KernelTask.Argument.Local(size) =>
      Left(Fault.Unsupported(s"$isaName local argument ${parameter.name} ($size bytes) is unsupported"))
    case KernelTask.Argument.Descriptor =>
      Left(Fault.InvalidRequest(s"$isaName kernels have no descriptor arguments"))
  }
}
