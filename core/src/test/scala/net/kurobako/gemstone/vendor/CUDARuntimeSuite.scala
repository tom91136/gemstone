package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.generated.GeneratedAPI

class CUDARuntimeSuite extends munit.FunSuite {
  test("generated dispatcher routes normalized requests to logical handlers") {
    assertEquals(
      GeneratedAPI.dispatch(GeneratedAPI.Opcode.CUDAGetDeviceCount, 0L, 0L, CUDARuntime.emulated),
      GeneratedAPI.DispatchResult(CUDAError.Success.value, 1L)
    )
    assertEquals(
      GeneratedAPI.dispatch(GeneratedAPI.Opcode.GemstoneAddI32, 20L, 22L, CUDARuntime.emulated),
      GeneratedAPI.DispatchResult(CUDAError.Success.value, 42L)
    )
  }
}
