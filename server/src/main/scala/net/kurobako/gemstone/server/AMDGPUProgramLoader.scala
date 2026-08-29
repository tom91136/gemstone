package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.DispatchInterpreter

import cats.syntax.all.*

private final class AMDGPULoadedProgram(kernels: Map[String, DispatchInterpreter.PreparedKernel])
    extends LoadedProgram {
  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    kernels.get(dispatch.kernelName).toRight(
      ExecutionFailure.NotFound(s"AMDGPU kernel '${dispatch.kernelName}' is not present")
    ).flatMap(DispatchInterpreter.execute(_, dispatch, memory, control))
}

object AMDGPUCodeObjectProgramLoader extends ProgramLoader {
  override val format: ProgramFormat = ProgramFormat.AMDGPUCodeObject

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
    CodeObject.decode(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
      .flatMap(_.iterator.foldLeft[Either[ExecutionFailure, Map[String, DispatchInterpreter.PreparedKernel]]](
        Right(Map.empty)
      ) { case (result, (name, kernel)) =>
        result.flatMap(kernels => DispatchInterpreter.prepare(kernel).map(prepared => kernels.updated(name, prepared)))
      })
      .map(AMDGPULoadedProgram.apply)
}

object AMDGPURawKernelProgramLoader extends ProgramLoader {
  override val format: ProgramFormat = ProgramFormat.AMDGPURawKernel

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
    CodeObject.decodeRaw(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
      .flatMap(DispatchInterpreter.prepare)
      .map(kernel => AMDGPULoadedProgram(Map(kernel.kernel.name -> kernel)))
}
