package net.kurobako.gemstone.apispec

import net.kurobako.gemstone.levelzero.LevelZero
import net.kurobako.gemstone.levelzero.ze_init_flags_t

final class SurfaceDefaultsSuite extends munit.FunSuite {
  private object UnsupportedLevelZero extends LevelZero

  test("unimplemented declarations fail closed") {
    intercept[UnsupportedOperationException](UnsupportedLevelZero.zeInit(null.asInstanceOf[ze_init_flags_t]))
  }
}
