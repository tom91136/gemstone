package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*

/** The backends available to a device, one per format. */
class BackendRegistry(backends: Seq[Backend]) {
  private val byFormat = backends.map(backend => backend.format -> backend).toMap
  require(byFormat.size == backends.size, "each format needs exactly one backend")

  def formats: Set[ProgramFormat] = byFormat.keySet

  def backend(format: ProgramFormat): Either[Fault, Backend] =
    byFormat.get(format).toRight(Fault.Unsupported(s"no backend loads ${format.raw} images"))

  def load(format: ProgramFormat, image: Vec[Byte]): Either[Fault, Backend.Program] =
    backend(format).flatMap(_.load(image))
}
