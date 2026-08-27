package net.kurobako.gemstone.apispec

import net.kurobako.gemstone.hip.HIP
import net.kurobako.gemstone.hsa.HSA
import net.kurobako.gemstone.levelzero.LevelZero
import net.kurobako.gemstone.levelzero.ze_init_flags_t
import net.kurobako.gemstone.opencl.OpenCL

final class ComputeSurfaceSuite extends munit.FunSuite {
  test("official API surfaces have their complete normalized declaration counts") {
    assertEquals(HIP.SurfaceSize, 498)
    assertEquals(HSA.SurfaceSize, 256)
    assertEquals(LevelZero.SurfaceSize, 485)
    assertEquals(OpenCL.SurfaceSize, 190)
  }

  test("unimplemented declarations fail closed") {
    val implementation = new LevelZero {}
    intercept[UnsupportedOperationException](implementation.zeInit(null.asInstanceOf[ze_init_flags_t]))
  }
}
