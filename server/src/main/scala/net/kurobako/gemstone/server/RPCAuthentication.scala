package net.kurobako.gemstone.server

/** Fixed-size RPC credentials. Entropy comes from the host platform, never a PRNG fallback. */
private[gemstone] object RPCAuthentication {
  def token(): String = {
    val bytes = Array.ofDim[Byte](32)
    SystemEntropy.fill(bytes)
    java.util.HexFormat.of().formatHex(bytes)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def matches(left: Array[Byte], right: Array[Byte]): Boolean =
    if left.length != right.length then false
    else {
      var difference = 0
      var index      = 0
      while index < left.length do {
        difference |= left(index) ^ right(index)
        index += 1
      }
      difference == 0
    }
}
