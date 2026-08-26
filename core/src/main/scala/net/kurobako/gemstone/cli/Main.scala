package net.kurobako.gemstone.cli

import net.kurobako.gemstone.runtime.RuntimeState

object Main {
  def main(arguments: Array[String]): Unit = {
    val runtime = RuntimeState.singleEmulatedDevice
    println(s"gemsi: ${runtime.deviceCount} emulated device(s)")
  }
}
