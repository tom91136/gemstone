package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*

/** A card a run can report as. */
case class DevicePreset(
    name: String,
    target: String,
    multiprocessors: Int,
    boostMhz: Int,
    memoryBytes: Long,
    memoryMTps: Int,
    busWidthBits: Int,
    l2Bytes: Int
)

object DevicePreset {
  val all: Vec[DevicePreset] = Vec(
    DevicePreset(
      "GeForce GTX 980",
      "sm_52",
      multiprocessors = 16,
      boostMhz = 1216,
      memoryBytes = 4L << 30,
      memoryMTps = 7000,
      busWidthBits = 256,
      l2Bytes = 2 << 20
    ),
    DevicePreset(
      "GeForce GTX 1080",
      "sm_61",
      multiprocessors = 20,
      boostMhz = 1733,
      memoryBytes = 8L << 30,
      memoryMTps = 10000,
      busWidthBits = 256,
      l2Bytes = 2 << 20
    ),
    DevicePreset(
      "Quadro GV100",
      "sm_70",
      multiprocessors = 80,
      boostMhz = 1627,
      memoryBytes = 32L << 30,
      memoryMTps = 1696,
      busWidthBits = 4096,
      l2Bytes = 6 << 20
    ),
    DevicePreset(
      "GeForce RTX 2080",
      "sm_75",
      multiprocessors = 46,
      boostMhz = 1710,
      memoryBytes = 8L << 30,
      memoryMTps = 14000,
      busWidthBits = 256,
      l2Bytes = 4 << 20
    ),
    DevicePreset(
      "GeForce RTX 5080",
      "sm_120",
      multiprocessors = 84,
      boostMhz = 2617,
      memoryBytes = 16L << 30,
      memoryMTps = 30000,
      busWidthBits = 256,
      l2Bytes = 64 << 20
    ),
    DevicePreset(
      "Radeon RX 9070 XT",
      "gfx1201",
      multiprocessors = 64,
      boostMhz = 2970,
      memoryBytes = 16L << 30,
      memoryMTps = 20100,
      busWidthBits = 256,
      l2Bytes = 8 << 20
    )
  )
}
