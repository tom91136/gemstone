package net.kurobako.gemstone.isa.spirv

import assembly.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.Suite

class ValidationSuite extends Suite {
  test("OpenCL compute environment is accepted") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "main"
    """
    assertEquals(Validation.validate(module, Validation.Environment.OpenCLCompute), ValidationOutcome.Accepted)
  }

  test("Vulkan compute environment accepts Shader GLCompute with Logical GLSL450 memory") {
    val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main"
    """
    assertEquals(Validation.validate(module, Validation.Environment.VulkanCompute), ValidationOutcome.Accepted)
    assertNotEquals(Validation.validate(module, Validation.Environment.OpenCLCompute), ValidationOutcome.Accepted)
  }

  test("an empty module fails closed") {
    Validation.validate(spv"", Validation.Environment.OpenCLCompute) match {
      case ValidationOutcome.Rejected(issues) => assertEquals(issues.length, 3)
      case other                              => fail(s"expected invalid, found $other")
    }
  }

  test("OpenCL libraries may omit an entry point but still require the OpenCL environment") {
    assertEquals(Validation.validate(spv"$openCL", Validation.Environment.OpenCLLibrary), ValidationOutcome.Accepted)
    assertNotEquals(Validation.validate(spv"$vulkan", Validation.Environment.OpenCLLibrary), ValidationOutcome.Accepted)
  }
}
