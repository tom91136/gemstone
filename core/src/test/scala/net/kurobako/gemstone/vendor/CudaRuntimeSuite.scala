package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.generated.GeneratedApi

class CudaRuntimeSuite extends munit.FunSuite {
  test("CUDA-shaped logical handler reports the canonical device") {
    assertEquals(CudaRuntime.emulated.getDeviceCount, ApiResult(Some(1), CudaError.Success))
  }

  test("generated dispatcher routes normalized requests to logical handlers") {
    assertEquals(
      GeneratedApi.dispatch(GeneratedApi.Opcode.CudaGetDeviceCount, 0L, 0L, CudaRuntime.emulated),
      GeneratedApi.DispatchResult(CudaError.Success.value, 1L)
    )
    assertEquals(
      GeneratedApi.dispatch(GeneratedApi.Opcode.GemsiAddI32, 20L, 22L, CudaRuntime.emulated),
      GeneratedApi.DispatchResult(CudaError.Success.value, 42L)
    )
  }
}
