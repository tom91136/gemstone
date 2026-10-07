package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object AMDGPUBackend {
  val encoding: ProgramEncoding = ProgramEncoding.HSACodeObject
}

class AMDGPUBackend(workgroups: Workgroups = Workgroups.Sequential) extends Backend {
  override val encoding: ProgramEncoding = AMDGPUBackend.encoding

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    CodeObject.decode(image).left.map(error => Fault.InvalidRequest(error.message))
      .map(LoadedProgram.load(_, workgroups))
}
