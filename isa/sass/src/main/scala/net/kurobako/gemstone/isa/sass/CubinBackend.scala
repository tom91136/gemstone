package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object CubinBackend {
  val encoding: ProgramEncoding = ProgramEncoding.CUDACubin
}

class CubinBackend(workgroups: Workgroups = Workgroups.Sequential) extends Backend {
  override val encoding: ProgramEncoding = CubinBackend.encoding

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    Cubin.decode(image).left.map(Fault.InvalidRequest(_))
      .map(LoadedProgram(_, Map.empty, workgroups))
}
