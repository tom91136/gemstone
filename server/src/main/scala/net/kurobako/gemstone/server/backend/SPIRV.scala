package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object SPIRV extends RuntimeBackend.Specialization {
  override def describe(image: Vec[Byte]): Either[ExecutionFailure, Vec[SpecializationInfo]] =
    Specialization.describe(image).left.map(ExecutionFailure.InvalidRequest(_)).map(
      _.map(constant => SpecializationInfo(constant.specId, constant.byteSize))
    )

  override def specialize(
      image: Vec[Byte],
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, Vec[Byte]] =
    Specialization.specialize(image, constants.map(constant => constant.specId -> constant.value).toMap)
      .left.map(ExecutionFailure.InvalidRequest(_))
}

class SPIRV(
    environment: Validation.Environment,
    override val encoding: String = "spirv-binary",
    workgroups: Workgroups = Workgroups.Sequential
) extends RuntimeBackend {
  private val decoder = SharedDecoder()
  override def load(
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] =
    for {
      module <- decoder.decode(image).left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded <-
        LoadedProgram.load(module, environment).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    } yield Program(loaded)

  private class Program(loaded: LoadedProgram)
      extends RuntimeBackend.Program {
    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[ExecutionFailure, Unit] = open {
      loaded.execute(task, memory, control, workgroups, handler).left.map(ExecutionFailure.fromFault(_))
    }

    override def bindFunctionPointerNamespace(id: Long): Either[ExecutionFailure, Unit] =
      open(loaded.bindFunctionPointerNamespace(id).left.map(ExecutionFailure.fromFault(_)))

    override def symbols: Either[ExecutionFailure, ProgramSymbols] =
      open(loaded.symbols.left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
      open(loaded.kernel(name).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def global(name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage] = open {
      loaded.global(name).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    }

    override def bindGlobal(
        name: NonEmpty[String],
        region: AllocationId,
        offset: Long >= 0,
        regionSize: Long > 0,
        memory: Memory
    ): Either[ExecutionFailure, Unit] = open {
      loaded.bindGlobal(name, region, offset, regionSize, memory)
        .left.map(ExecutionFailure.fromFault(_))
    }

    override def writeGlobal(
        name: NonEmpty[String],
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = open {
      loaded.writeGlobal(name, offset, bytes).left.map(ExecutionFailure.fromFault(_))
    }
  }
}
