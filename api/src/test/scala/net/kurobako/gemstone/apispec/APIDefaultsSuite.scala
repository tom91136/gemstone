package net.kurobako.gemstone.apispec

import net.kurobako.gemstone.abi.ABI.CValue
import net.kurobako.gemstone.levelzero.LevelZero
import net.kurobako.gemstone.levelzero.LevelZero.ze_init_flags_t

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class APIDefaultsSuite extends munit.FunSuite {
  private object UnsupportedLevelZero extends LevelZero

  test("unimplemented declarations fail closed") {
    val flags: ze_init_flags_t = CValue.fromUnsignedLong["ze_init_flags_t"](0L, 4)
    intercept[UnsupportedOperationException](UnsupportedLevelZero.zeInit(flags))
  }
}
