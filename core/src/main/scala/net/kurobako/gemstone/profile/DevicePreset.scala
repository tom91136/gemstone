package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*

/** A card a run can report as. Bus, L2, bandwidth, multiprocessors, and boost come from the `card` of `target`. */
case class DevicePreset(name: String, target: String, memoryBytes: Long, memoryMTps: Int)

object DevicePreset {
  val all: Vec[DevicePreset] = Vec(
    DevicePreset("GeForce GTX 980", "sm_52", 4L << 30, 7000),
    DevicePreset("GeForce GTX 1080", "sm_61", 8L << 30, 10000),
    DevicePreset("Quadro GV100", "sm_70", 32L << 30, 1696),
    DevicePreset("GeForce RTX 2080", "sm_75", 8L << 30, 14000),
    DevicePreset("GeForce RTX 5080", "sm_120", 16L << 30, 30000),
    DevicePreset("Radeon RX 9070 XT", "gfx1201", 16L << 30, 20100)
  )
}
