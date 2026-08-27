package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.generated.GeneratedAPI

class CUDARuntimeSuite extends munit.FunSuite {
  test("CUDA-shaped logical handler reports the canonical device") {
    assertEquals(CUDARuntime.emulated.getDeviceCount, APIResult(Some(1), CUDAError.Success))
  }

  test("generated dispatcher routes normalized requests to logical handlers") {
    assertEquals(
      GeneratedAPI.dispatch(GeneratedAPI.Opcode.CUDAGetDeviceCount, 0L, 0L, CUDARuntime.emulated),
      GeneratedAPI.DispatchResult(CUDAError.Success.value, 1L)
    )
    assertEquals(
      GeneratedAPI.dispatch(GeneratedAPI.Opcode.GemsiAddI32, 20L, 22L, CUDARuntime.emulated),
      GeneratedAPI.DispatchResult(CUDAError.Success.value, 42L)
    )
  }
}
