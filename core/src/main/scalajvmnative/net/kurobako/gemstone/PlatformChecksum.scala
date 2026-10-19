package net.kurobako.gemstone

import java.util.zip.CRC32

private object PlatformChecksum {
  def crc32(bytes: Array[Byte]): Int = {
    val checksum = CRC32()
    checksum.update(bytes)
    checksum.getValue.toInt
  }
}
