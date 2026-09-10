package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.spirv.v1_6.Environment
import net.kurobako.gemstone.isa.spirv.v1_6.Instruction
import net.kurobako.gemstone.isa.spirv.v1_6.Interpreter
import net.kurobako.gemstone.isa.spirv.v1_6.ISA

import cats.syntax.all.*

object SPIRVProgramLoader extends ProgramLoader {
  override val format: ProgramFormat = ProgramFormat.SPIRV

  private val prepared = PreparedCache[Col[Byte], Either[ExecutionFailure, Interpreter.PreparedProgram]](8)

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
    (if (image.length > 8 * 1024 * 1024) prepare(image) else prepared.get(image)(prepare(image)))
      .map(SPIRVLoadedProgram.apply)

  private def prepare(image: Col[Byte]): Either[ExecutionFailure, Interpreter.PreparedProgram] =
    ISA.decode(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
      .flatMap { module =>
        val entryPointModels = module.instructions.collect {
          case Instruction.EntryPoint(executionModel, _, _, _) => executionModel.toUnsigned
        }
        Interpreter.prepare(
          module,
          if (entryPointModels.isEmpty) Environment.OpenCLLibrary else Environment.OpenCLCompute
        )
      }

  private final class SPIRVLoadedProgram(program: Interpreter.PreparedProgram) extends LoadedProgram {
    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] = Interpreter.run(program, dispatch, memory, control)
  }
}
