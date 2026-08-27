package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.execution.Effects
import net.kurobako.gemstone.execution.LaneMask

class WaveStateSuite extends munit.FunSuite {
  private val target = Target.unsafe("gfx1010")

  test("stage register and special-register writes until an atomic commit") {
    val state   = WaveState.create(target, laneCount = 32, sgprCount = 8, vgprCount = 4, agprCount = 2).toOption.get
    val effects = new Effects[AMDWrite]
    effects.stage(AMDWrite.Sgpr(1, 11))
    effects.stage(AMDWrite.Vgpr(2, 7, 22))
    effects.stage(AMDWrite.Agpr(1, 31, 33))
    effects.stage(AMDWrite.Scc(true))
    effects.stage(AMDWrite.M0(44))

    assertEquals(state.readSgpr(1), Right(0))
    assertEquals(state.commit(effects), Right(()))
    assertEquals(state.readSgpr(1), Right(11))
    assertEquals(state.readVgpr(2, 7), Right(22))
    assertEquals(state.readAgpr(1, 31), Right(33))
    assert(state.scc)
    assertEquals(state.m0, 44)
    assert(effects.isEmpty)
  }

  test("reads-before-writes permit a staged register swap") {
    val state = WaveState.create(target, laneCount = 64, sgprCount = 2, vgprCount = 0).toOption.get
    val seed  = new Effects[AMDWrite]
    seed.stage(AMDWrite.Sgpr(0, 10))
    seed.stage(AMDWrite.Sgpr(1, 20))
    assertEquals(state.commit(seed), Right(()))

    val left    = state.readSgpr(0).toOption.get
    val right   = state.readSgpr(1).toOption.get
    val effects = new Effects[AMDWrite]
    effects.stage(AMDWrite.Sgpr(0, right))
    effects.stage(AMDWrite.Sgpr(1, left))
    assertEquals(state.commit(effects), Right(()))
    assertEquals(state.readSgpr(0), Right(20))
    assertEquals(state.readSgpr(1), Right(10))
  }

  test("an invalid destination prevents every staged write") {
    val state   = WaveState.create(target, laneCount = 32, sgprCount = 1, vgprCount = 0).toOption.get
    val effects = new Effects[AMDWrite]
    effects.stage(AMDWrite.Sgpr(0, 99))
    effects.stage(AMDWrite.Sgpr(1, 100))

    assert(state.commit(effects).isLeft)
    assertEquals(state.readSgpr(0), Right(0))
    assert(effects.isEmpty)
  }

  test("duplicate destinations and masks outside the wave fail closed") {
    val state      = WaveState.create(target, laneCount = 32, sgprCount = 1, vgprCount = 0).toOption.get
    val duplicates = new Effects[AMDWrite]
    duplicates.stage(AMDWrite.Sgpr(0, 1))
    duplicates.stage(AMDWrite.Sgpr(0, 2))
    assert(duplicates.snapshot.nonEmpty)
    assert(state.commit(duplicates).isLeft)
    assertEquals(state.readSgpr(0), Right(0))

    val invalidMask = new Effects[AMDWrite]
    invalidMask.stage(AMDWrite.Vcc(LaneMask.unsafe(1L << 40)))
    assert(state.commit(invalidMask).isLeft)
    assert(state.vcc.isEmpty)
  }
}
