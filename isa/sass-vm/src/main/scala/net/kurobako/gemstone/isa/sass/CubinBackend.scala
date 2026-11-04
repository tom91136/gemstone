package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object CubinBackend {
  val format: ProgramFormat = ProgramFormat.CUDACubin
}

class CubinBackend(
    workgroups: Workgroups = Workgroups.Sequential,
    text: Instruction => Option[String]
) extends Backend {
  override val format: ProgramFormat = CubinBackend.format

  private val images = ImageCache[Fault, LoadedProgram.Image]()

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    images.acquire(ImageKey(image))(Cubin.decode(image).left.map(Fault.InvalidRequest(_)).map(LoadedProgram.Image(_)))
      .map(lease => LoadedProgram(lease.value, Map.empty, workgroups, text, () => lease.release()))
}
