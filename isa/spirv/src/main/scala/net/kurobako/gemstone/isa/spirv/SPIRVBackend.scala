package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

class SPIRVBackend(
    environment: Validation.Environment,
    override val encoding: ProgramEncoding = ProgramEncoding.SPIRVOpenCLCompute,
    workgroups: Workgroups = Workgroups.Sequential
) extends Backend {
  private val decoder = SharedDecoder()

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    decoder.decode(image).left.map(error => Fault.InvalidRequest(error.message))
      .flatMap(VMProgram.load(_, environment, workgroups))
}
