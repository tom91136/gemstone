package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object AMDGPUBackend {
  val format: ProgramFormat = ProgramFormat.HSACodeObject
}

class AMDGPUBackend(workgroups: Workgroups = Workgroups.Sequential) extends Backend {
  override val format: ProgramFormat = AMDGPUBackend.format

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    CodeObject.decode(image).left.map(error => Fault.InvalidRequest(error.message))
      .map(LoadedProgram.load(_, workgroups))
}
