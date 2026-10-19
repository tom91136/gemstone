package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import scala.collection.immutable.ArraySeq

/** Immutable image key. Hashing uses the bulk byte implementation; equality still compares the entire image. */
private case class CodeObjectKey(image: Vec[Byte]) {
  private val hash = image match {
    case bytes: ArraySeq.ofByte => PlatformChecksum.crc32(bytes.unsafeArray)
    case _                      => PlatformChecksum.crc32(Vec.toArray(image))
  }

  override def hashCode(): Int = hash
}
