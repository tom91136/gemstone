package net.kurobako.gemstone.isa.spirv.v1_6

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.{ByteKey, LRUCache, Vec}
import net.kurobako.gemstone.execution.*

import cats.syntax.all.*

object SPIRVProgramDecoder extends ProgramDecoder {
  override val format: ProgramFormat = ProgramFormat.SPIRVBinary

  private val prepared = LRUCache[ByteKey, Either[ExecutionFailure, Interpreter.PreparedProgram]](8)

  override def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance] =
    (if (image.length > 8 * 1024 * 1024) prepare(image) else prepared.get(ByteKey(image))(prepare(image)))
      .map(SPIRVProgramInstance(_))

  private def prepare(image: Vec[Byte]): Either[ExecutionFailure, Interpreter.PreparedProgram] =
    ISA.decode(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
      .flatMap { module =>
        val entryPointModels = module.instructions.collect {
          case Instruction.EntryPoint(executionModel, _, _, _) => executionModel
        }
        entryPointModels
          .traverse(_.singleWord.leftMap(detail =>
            ExecutionFailure.InvalidRequest(s"invalid OpEntryPoint execution model: $detail")
          ))
          .flatMap(_ =>
            Interpreter.prepare(
              module,
              if (entryPointModels.isEmpty) Validation.Environment.OpenCLLibrary
              else Validation.Environment.OpenCLCompute
            )
          )
      }

  private final class SPIRVProgramInstance(program: Interpreter.PreparedProgram) extends ProgramInstance {
    override val catalog: Either[ExecutionFailure, ProgramCatalog] =
      Interpreter.kernelNames(program).traverse { kernelName =>
        LinkageName.either(kernelName.value).leftMap(_ =>
          ExecutionFailure.InvalidRequest(
            s"invalid SPIR-V linkage name '${kernelName.value}'"
          )
        ).map(linkageName => ProgramKernel(kernelName, linkageName))
      }.map(kernels => ProgramCatalog(kernels, Vec.empty))

    override def kernel(name: KernelName): Either[ExecutionFailure, KernelMetadata] =
      Interpreter.kernelMetadata(program, name)

    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] = Interpreter.run(program, dispatch, memory, control)
  }
}
