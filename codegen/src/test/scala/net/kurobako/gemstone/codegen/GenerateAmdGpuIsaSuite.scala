package net.kurobako.gemstone.codegen

import java.nio.file.Files

class GenerateAmdGpuIsaSuite extends munit.FunSuite {
  test("reject a directory which is not a prepared reference view") {
    val directory = Files.createTempDirectory("gemstone-amdgpu-codegen-test")
    try intercept[IllegalArgumentException](GenerateAmdGpuIsa.generate(directory, directory.resolve("missing.py")))
    finally {
      val _ = Files.deleteIfExists(directory)
    }
  }
}
