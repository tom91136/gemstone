package net.kurobako.gemstone.server.execution

trait RuntimeBackend {
  def encoding: ProgramEncoding
  def open(services: MachineServices): BackendSession
}
