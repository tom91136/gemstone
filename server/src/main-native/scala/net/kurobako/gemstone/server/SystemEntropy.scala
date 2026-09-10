package net.kurobako.gemstone.server

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*
import scala.scalanative.meta.LinktimeInfo

private[server] object SystemEntropy {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def fill(bytes: Array[Byte]): Unit = {
    if LinktimeInfo.isWindows then Zone.acquire { implicit zone =>
      val buffer = alloc[Byte](bytes.length)
      if WindowsRandom.generate(null, buffer, bytes.length.toUInt, 2.toUInt) != 0 then
        throw java.io.IOException("system random generation failed")
      var index = 0
      while index < bytes.length do { bytes(index) = buffer(index); index += 1 }
    }
    else {
      val input = java.io.FileInputStream("/dev/urandom")
      try {
        var offset = 0
        while offset < bytes.length do {
          val count = input.read(bytes, offset, bytes.length - offset)
          if count <= 0 then throw java.io.EOFException("system random source ended")
          offset += count
        }
      } finally input.close()
    }
  }
}

@extern
@link("bcrypt")
private object WindowsRandom {
  @name("BCryptGenRandom")
  def generate(algorithm: Ptr[Byte], buffer: Ptr[Byte], size: CUnsignedInt, flags: CUnsignedInt): CInt = extern
}
