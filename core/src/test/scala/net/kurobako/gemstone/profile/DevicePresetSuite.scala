package net.kurobako.gemstone.profile

import net.kurobako.gemstone.testing.*

class DevicePresetSuite extends Suite {
  test("every preset matches the sourced card of its target") {
    DevicePreset.all.foreach { preset =>
      val card = Architecture.named(preset.target).flatMap(_.card)
      assertEquals(card.map(_.name), Some(preset.name), preset.name)
      assertEquals(card.flatMap(_.multiprocessors), Some(preset.multiprocessors), preset.name)
      assertEquals(card.flatMap(_.boostMhz), Some(preset.boostMhz), preset.name)
      assertEquals(card.map(_.busWidthBits), Some(preset.busWidthBits), preset.name)
      assertEquals(card.map(_.l2KiB * 1024L), Some(preset.l2Bytes.toLong), preset.name)
      assert(preset.memoryBytes > 0L, preset.name)
      assert(preset.memoryMTps > 0, preset.name)
      val bandwidthGBs = card.flatMap(_.bandwidthGBs).getOrElse(fail(s"${preset.name} has no bandwidth"))
      assertEqualsDouble(
        preset.memoryMTps.toDouble * preset.busWidthBits / 8000.0,
        bandwidthGBs,
        bandwidthGBs * 0.01,
        preset.name
      )
    }
  }
}
