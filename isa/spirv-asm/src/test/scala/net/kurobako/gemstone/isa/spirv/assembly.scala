package net.kurobako.gemstone.isa.spirv

import munit.Location
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** SPIR-V test scaffolding for assembling and inspecting modules without executing them. */
private object assembly extends munit.Assertions {

  extension (context: StringContext) def spv(arguments: Any*)(using Location): Module = assemble(context.s(arguments*))

  def assemble(source: String)(using Location): Module =
    asm.Parser.parse(source).fold(error => fail(s"$error in\n$source"), identity)

  val openCL: String =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpCapability Int8
      |OpCapability Int16
      |OpCapability Int64
      |OpCapability Float16
      |OpCapability Float64
      |OpCapability Vector16
      |%opencl = OpExtInstImport "OpenCL.std"
      |OpMemoryModel Physical64 OpenCL
      |""".stripMargin

  val vulkan: String =
    """OpCapability Shader
      |%glsl = OpExtInstImport "GLSL.std.450"
      |OpMemoryModel Logical GLSL450
      |""".stripMargin

  def environment(module: Module): Validation.Environment = {
    val models = module.instructions.collect { case Instruction.EntryPoint(executionModel = model) =>
      model.words.head.raw
    }
    if (models.contains(ExecutionModel.Kernel)) Validation.Environment.OpenCLCompute
    else if (models.contains(ExecutionModel.GLCompute)) Validation.Environment.VulkanCompute
    else if (models.nonEmpty) Validation.Environment.VulkanGraphics
    else Validation.Environment.OpenCLLibrary
  }

  /** Raw operands, only for modules that the assembler cannot express. */
  def operand(kind: OperandKind, values: Int*): Operand =
    Operand(kind, Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]])
}
