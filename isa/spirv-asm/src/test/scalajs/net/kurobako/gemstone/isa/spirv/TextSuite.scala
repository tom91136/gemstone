package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.testing.Suite

class TextSuite extends Suite {
  test("SPIR-V text assembles to a binary that disassembles to the same text") {
    val source = """OpCapability Shader
                   |OpMemoryModel Logical GLSL450
                   |OpEntryPoint GLCompute %main "main"
                   |OpExecutionMode %main LocalSize 1 1 1
                   |%void = OpTypeVoid
                   |%function = OpTypeFunction %void
                   |%main = OpFunction %void None %function
                   |%entry = OpLabel
                   |OpReturn
                   |OpFunctionEnd
                   |""".stripMargin
    val module  = Parser.parse(source).fold(error => fail(error.toString), identity)
    val bytes   = ISA.encode(module).fold(error => fail(error.toString), identity)
    val decoded = ISA.decode(bytes).fold(error => fail(error.message), identity)
    assertEquals(Printer.render(decoded), Printer.render(module.copy(comments = module.comments.empty)))
    assertEquals(decoded.instructions.size, 10)
  }
}
