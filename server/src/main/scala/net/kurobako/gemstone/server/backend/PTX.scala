package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3.{Program as _, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object PTX {
  val encoding: String = "ptx-text"
}

class PTX(workgroups: Workgroups = Workgroups.Sequential) extends RuntimeBackend {
  override val encoding: String = PTX.encoding

  override def load(
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] =
    for {
      program <- Parser.decode(String(Vec.toArray(image), StandardCharsets.UTF_8))
        .left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded <- LoadedProgram.load(program).left.map(error => ExecutionFailure.InvalidRequest(error.message))
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
        case _ => Left(ExecutionFailure.Unsupported("PTX execution requires address-space capable memory"))
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
      memory match {
        case addresses: AddressSpace =>
          loaded.global(name).left.map(_ =>
            ExecutionFailure.NotFound(s"PTX global '${name}' is not present")
          )
            .flatMap(_ =>
              loaded.bindGlobal(name, region, offset, regionSize, addresses)
                .left.map(ExecutionFailure.fromFault(_))
            )
        case _ => Left(ExecutionFailure.Unsupported("PTX global binding requires address-space capable memory"))
      }
    }

    override def writeGlobal(
        name: NonEmpty[String],
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = open {
      Left(ExecutionFailure.Unsupported("PTX globals are not writable"))
    }
  }
}
