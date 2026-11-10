package net.kurobako.gemstone.profile

import net.kurobako.gemstone.testing.*

class DevicePresetSuite extends Suite {
  test("every preset is the shipping card of an existing target") {
    DevicePreset.all.foreach { preset =>
      val card = Architecture.named(preset.target).flatMap(_.card)
      assertEquals(card.map(_.name), Some(preset.name), preset.name)
    }
  }
}
