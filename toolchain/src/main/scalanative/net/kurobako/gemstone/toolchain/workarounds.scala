package net.kurobako.gemstone.toolchain

/** Installs every Scala Native workaround; entry points call this first. */
object workarounds {
  def install(): Unit = ReferenceHandlerRelay.install()
}
