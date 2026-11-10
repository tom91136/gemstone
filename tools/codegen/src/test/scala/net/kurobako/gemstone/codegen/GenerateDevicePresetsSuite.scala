package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.DevicePreset
import net.kurobako.gemstone.testing.*

class GenerateDevicePresetsSuite extends Suite {

  test("each preset takes its multiprocessors, boost clock, bus width and L2 bytes from its card") {
    val rendered = GenerateDevicePresets.render(DevicePreset.all)
    assert(
      rendered.contains(""""GeForce GTX 980", "sm_52", 16, 1216, 4294967296, 7000, 256, 2097152},""")
    )
    assert(
      rendered.contains(""""GeForce RTX 5080", "sm_120", 84, 2617, 17179869184, 30000, 256, 67108864},""")
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
