package net.kurobako.gemstone.apispec

import net.kurobako.gemstone.levelzero.LevelZero
import net.kurobako.gemstone.levelzero.LevelZeroError
import net.kurobako.gemstone.levelzero.LevelZeroResult
import net.kurobako.gemstone.opencl.OpenCL
import net.kurobako.gemstone.opencl.OpenCLError
import net.kurobako.gemstone.opencl.OpenCLResult

final class ComputeSurfaceSuite extends munit.FunSuite {
  test("generated OpenCL surface uses exact C names and fails closed") {
    val implementation = new OpenCL {}
    assertEquals(OpenCL.SurfaceSize, 253)
    assertEquals(
      implementation.clGetPlatformIDs(),
      OpenCLResult.Failure(OpenCLError(-1, "clGetPlatformIDs is not implemented"))
    )
  }

  test("generated Level Zero surface uses exact C names and fails closed") {
    val implementation = new LevelZero {}
    assertEquals(LevelZero.SurfaceSize, 257)
    assertEquals(
      implementation.zeInit(),
      LevelZeroResult.Failure(LevelZeroError(-1, "zeInit is not implemented"))
    )
  }
}
