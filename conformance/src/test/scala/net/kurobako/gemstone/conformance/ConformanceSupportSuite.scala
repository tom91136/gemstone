package net.kurobako.gemstone.conformance

import ConformanceSupport.*

import scala.concurrent.duration.*

final class ConformanceSupportSuite extends munit.FunSuite {
  test("explicit provider paths fail closed instead of falling back") {
    assertEquals(configuredPath("PROVIDER", environment = Map.empty), None)
    assertEquals(
      configuredPath("PROVIDER", directory = true, environment = Map("PROVIDER" -> root.toString)),
      Some(root)
    )
    val _ =
      intercept[IllegalArgumentException](configuredPath("PROVIDER", environment = Map("PROVIDER" -> root.toString)))
    val _ = intercept[IllegalArgumentException](configuredPath(
      "PROVIDER",
      directory = true,
      environment = Map("PROVIDER" -> root.resolve("build.sbt").toString)
    ))
  }

  test("CTest case drivers preserve validation and no-GPU skip behavior") {
    runChecked(Vector("python3", root.resolve("conformance/tools/test-case-run.py").toString), root)
  }

  test("scheduler audit validates canonical zero-work exemptions") {
    runChecked(Vector("python3", root.resolve("conformance/tools/test-audit.py").toString), root)
  }

  test("HIP route evidence validates library ownership and retains compressed logs") {
    runChecked(Vector("python3", root.resolve("conformance/tools/test-runtime-provenance.py").toString), root)
  }

  test("container wrappers share image, isolation, and bounded build settings") {
    runChecked(Vector("python3", root.resolve("conformance/tools/test-containers.py").toString), root)
  }

  test("CTS builds recover interrupted artifacts and source preparation") {
    runChecked(Vector("python3", root.resolve("conformance/tools/test-cts-builds.py").toString), root)
  }

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

  test("an explicit target list supplies the default oracle target") {
    assertEquals(
      AMDGPUTargetMatrix.fromEnvironment(Map("GEMSTONE_AMDGPU_TARGETS" -> "gfx1036,gfx1100")).oracleTarget,
      "gfx1036"
    )
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

  test("anchored compound filters retain their literal fixture prefix") {
    assert(prefixMayMatch("unified/hip/", "^unified/hip/(algorithms/copy|blas/axpy)/i8/n257$"))
    assert(!prefixMayMatch("unified/cuda/", "^unified/hip/(algorithms/copy|blas/axpy)/i8/n257$"))
    assert(prefixMayMatch("unified/hip/", "copy/i8"))
  }
}
