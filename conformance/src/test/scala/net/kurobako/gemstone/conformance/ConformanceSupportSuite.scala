package net.kurobako.gemstone.conformance

import ConformanceSupport.*

import scala.concurrent.duration.*

final class ConformanceSupportSuite extends munit.FunSuite {
  test("AMDGPU target matrix accepts a list and an oracle target") {
    val matrix = AMDGPUTargetMatrix.fromEnvironment(
      Map(
        "GEMSTONE_AMDGPU_TARGETS"       -> "gfx1036, gfx1100",
        "GEMSTONE_AMDGPU_ORACLE_TARGET" -> "gfx1036"
      )
    )

    assertEquals(matrix.targets, Vector("gfx1036", "gfx1100"))
    assertEquals(matrix.oracleTarget, "gfx1036")
    assertEquals(matrix.targetList, "gfx1036,gfx1100")
    assertEquals(matrix.targetFlags, "--offload-arch=gfx1036 --offload-arch=gfx1100")
  }

  test("AMDGPU target matrix defaults to gfx900") {
    val matrix = AMDGPUTargetMatrix.fromEnvironment(Map.empty)

    assertEquals(matrix.targets, Vector("gfx900"))
    assertEquals(matrix.oracleTarget, "gfx900")
  }

  test("AMDGPU target matrix rejects an oracle outside the selected list") {
    intercept[IllegalArgumentException] {
      AMDGPUTargetMatrix.fromEnvironment(
        Map(
          "GEMSTONE_AMDGPU_TARGETS"       -> "gfx1036,gfx1100",
          "GEMSTONE_AMDGPU_ORACLE_TARGET" -> "gfx900"
        )
      )
    }
  }

  test("external commands fail deterministically when their deadline expires") {
    val result = run(Vector("sh", "-c", "sleep 30"), root, timeout = 100.millis)

    assertEquals(result.exit, -1)
    assert(result.diagnostic.contains("timed out after"))
  }
}
