package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.DevicePreset
import net.kurobako.gemstone.testing.*

class GenerateDevicePresetsSuite extends Suite {

  test("each preset renders its own multiprocessors, boost clock, bus width and L2 bytes") {
    val custom = DevicePreset(
      name = "Custom sm_52",
      target = "sm_52",
      multiprocessors = 24,
      boostMhz = 1500,
      memoryBytes = 6L << 30,
      memoryMTps = 8000,
      busWidthBits = 192,
      l2Bytes = 1536 * 1024
    )
    val rendered = GenerateDevicePresets.render(DevicePreset.all :+ custom)
    assert(
      rendered.contains(""""GeForce GTX 980", "sm_52", 16, 1216, 4294967296, 7000, 256, 2097152},""")
    )
    assert(
      rendered.contains(""""GeForce RTX 5080", "sm_120", 84, 2617, 17179869184, 30000, 256, 67108864},""")
    )
    assert(
      rendered.contains(""""Custom sm_52", "sm_52", 24, 1500, 6442450944, 8000, 192, 1572864},""")
    )
  }

  test("every preset is rendered exactly once") {
    val rendered = GenerateDevicePresets.render(DevicePreset.all)
    assertEquals(
      Vec.from(DevicePreset.all.map(preset => rendered.split(s""""${preset.name}", """, -1).length - 1).distinct),
      Vec(1)
    )
  }
}
