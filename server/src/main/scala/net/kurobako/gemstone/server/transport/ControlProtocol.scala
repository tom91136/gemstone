package net.kurobako.gemstone.server.transport

private[server] object ControlProtocol {
  val Magic          = 0x474d4330
  val RegisterClient = 1
  val QueryUsage     = 2
  val ReleaseClient  = 3
  val Shutdown       = 4
}
