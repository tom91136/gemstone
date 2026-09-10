package net.kurobako.gemstone.server

private[server] object SystemEntropy {
  private val random                 = java.security.SecureRandom()
  def fill(bytes: Array[Byte]): Unit = random.nextBytes(bytes)
}
