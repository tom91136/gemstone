package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import scala.collection.immutable.ArraySeq

/** Content identity of a module image. The CRC32 is computed once, so large images are not rescanned by lookups, and
  * equality still compares every byte.
  */
case class ImageKey(bytes: Vec[Byte]) derives CanEqual {
  private val hash = bytes match {
    case image: ArraySeq.ofByte => PlatformChecksum.crc32(image.unsafeArray)
    case _                      => PlatformChecksum.crc32(Vec.toArray(bytes))
  }

  override def hashCode(): Int = hash
}
