package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

/** Direct bridge from decoded SASS to the shared NVIDIA execution engine. */
object LoadedProgram {
  class Compiled private[LoadedProgram] (private[LoadedProgram] val kernel: Kernel)

  def compile(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      globalSymbols: Map[NonEmpty[String], GuestAddress] = Map.empty
  ): Either[Fault, Compiled] =
    VMCompiler.compile(module, kernel, functions, globalSymbols)
      .leftMap(detail => Fault.Unsupported(detail.unsafe[NonEmpty[String]]))
      .map(Compiled(_))

  def execute(
      compiled: Compiled,
      task: KernelTask,
      addresses: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups
  ): Either[Fault, Unit] =
    VMKernelLaunch.execute(compiled.kernel, task, addresses, control, workgroups)
}
