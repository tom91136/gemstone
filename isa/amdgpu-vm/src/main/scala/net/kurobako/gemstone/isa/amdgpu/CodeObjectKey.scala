package net.kurobako.gemstone.isa.amdgpu

import java.util.zip.CRC32
import net.kurobako.gemstone.collection.*
import scala.collection.immutable.ArraySeq

/** Immutable image key. Hashing uses the bulk byte implementation; equality still compares the entire image. */
private case class CodeObjectKey(image: Vec[Byte]) {
  private val hash = {
    val checksum = CRC32()
    image match {
      case bytes: ArraySeq.ofByte => checksum.update(bytes.unsafeArray)
      case _                      => checksum.update(Vec.toArray(image))
    }
    checksum.getValue.toInt
  }

  override def hashCode(): Int = hash
}
