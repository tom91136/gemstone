package net.kurobako.gemstone

/** Scala.js has no `java.util.zip`; this is the same reflected CRC-32 with polynomial 0xedb88320. */
private object PlatformChecksum {
  private val Table = Array.tabulate(256) { index =>
    (0 until 8).foldLeft(index)((crc, _) => if ((crc & 1) != 0) (crc >>> 1) ^ 0xedb88320 else crc >>> 1)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def crc32(bytes: Array[Byte]): Int = {
    var crc   = -1
    var index = 0
    while (index < bytes.length) {
      crc = Table((crc ^ bytes(index)) & 0xff) ^ (crc >>> 8)
      index += 1
    }
    ~crc
  }
}
