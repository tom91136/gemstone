package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

/** The instructions of a program image, in the order the machine addresses them, so a recorded step can show the
  * instruction it ran. The image is the bytes the device loaded, so a listing describes exactly the program that ran.
  */
object ProgramListing {

  /** A program's bytes as the device loaded them, the language that reads them, and the target that decodes them. */
  case class Image(language: Language, bytes: Vec[Byte], target: Option[String])

  /** The instructions of `image` when its language reads it, or why they cannot be listed. */
  def of(image: Image): Either[String, Vec[ProgramLine]] =
    image.language match {
      case Language.PTX    => PTXListing.lines(image.bytes)
      case Language.SASS   => SASSListing.lines(image.bytes)
      case Language.AMDGPU => AMDGPUListing.lines(image.bytes, image.target)
      case Language.SPIRV  => SPIRVListing.lines(image.bytes)
    }
}
