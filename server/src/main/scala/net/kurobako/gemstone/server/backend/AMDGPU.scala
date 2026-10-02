package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object AMDGPU {
  val encoding: String = "hsa-code-object"
}

class AMDGPU(workgroups: Workgroups = Workgroups.Sequential) extends RuntimeBackend {
  override val encoding: String = AMDGPU.encoding

  override def load(
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] =
    for {
      program <- CodeObject.decode(image).left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded  <- LoadedProgram.load(program).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    } yield Program(loaded)

  private class Program(loaded: LoadedProgram)
      extends RuntimeBackend.Program {
    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[ExecutionFailure, Unit] = open {
      memory match {
        case addresses: AddressSpace =>
          loaded.execute(task, addresses, control, workgroups).left.map(ExecutionFailure.fromFault(_))
        case _ => Left(ExecutionFailure.Unsupported("execution requires address-space capable memory"))
      }
    }

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
      loaded.bindGlobal(name, region, offset, regionSize, memory).left.map(ExecutionFailure.fromFault(_))
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
