package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path

class GenerateSPIRVISASuite extends munit.FunSuite {
  private val grammar  = """{
    "magic_number":"0x07230203", "major_version":1,"minor_version":6,"revision":1,
    "instructions":[
      {"opname":"OpFixture","opcode":7,"class":"Miscellaneous","operands":[
        {"kind":"IdResult"},
        {"kind":"IdRef","name":"Type"},
        {"kind":"IdRef","name":"Value"},
        {"kind":"IdRef","name":"Value"},
        {"kind":"IdRef","name":"Optional","quantifier":"?"},
        {"kind":"IdRef","name":"Operands","quantifier":"*"}]},
      {"opname":"OpNop","opcode":0,"class":"Miscellaneous"}],
    "operand_kinds":[{"kind":"IdResult","category":"Id"},{"kind":"IdRef","category":"Id"},
      {"kind":"Scope","category":"ValueEnum","enumerants":[
        {"enumerant":"Second","value":2},{"enumerant":"First","value":1}]}]
  }"""
  private val extended = """{"instructions":[{"opname":"fixture_op","opcode":17}]}"""

  private def fixture[A](body: (Path, Path, Path) => A): A = generator.inDirectory { directory =>
    val input  = directory.resolve("grammar.json")
    val ext    = Files.createDirectory(directory.resolve("ext"))
    val output = directory.resolve("output")
    Files.writeString(input, grammar)
    Seq("extinst.glsl.std.450.grammar.json", "extinst.opencl.std.100.grammar.json").foreach { name =>
      val _ = Files.writeString(ext.resolve(name), extended)
    }
    body(input, ext, output)
  }

  private def generate(input: Path, ext: Path, output: Path): Unit =
    GenerateSPIRVISA.main(Array(
      "instructions",
      input.toString,
      ext.toString,
      output.toString,
      output.resolveSibling("native").toString
    ))

  private def source(output: Path, name: String): String =
    Files.readString(output.resolve("net/kurobako/gemstone/isa/spirv").resolve(name))

  test("small grammar preserves optional/repeated operands, disambiguates names, and orders opcodes") {
    fixture { (input, ext, output) =>
      generate(input, ext, output)
      val instructions = source(output, "Instruction.scala").replaceAll("\\s+", " ")
      assert(
        instructions.contains(
          "case Fixture( dest: Operand, dataType: Operand, valueA: Operand, valueB: Operand, optional: Option[Operand], srcs: Vec[Operand] )"
        ),
        instructions
      )
      assert(instructions.indexOf("case Nop()") < instructions.indexOf("case Fixture("))
      val modes = source(output, "Scope.scala").replaceAll("\\s+", " ")
      assert(modes.contains("final val First: Int = 1"))
      assert(modes.contains("final val Second: Int = 2"))
      assert(modes.indexOf("First") < modes.indexOf("Second"))
      assertEquals(source(output, "ExtendedInstruction.scala").split("final val FixtureOp = 17", -1).length, 3)
      val before = source(output, "Instruction.scala")
      generate(input, ext, output)
      assertEquals(source(output, "Instruction.scala"), before)
    }
  }

  test("invalid opcodes fail before replacing existing generated files") {
    fixture { (input, ext, output) =>
      generate(input, ext, output)
      val before = source(output, "Instruction.scala")
      Files.writeString(input, grammar.replace("\"opcode\":7", "\"opcode\":65536"))
      val error = intercept[IllegalArgumentException](generate(input, ext, output))
      assert(error.getMessage.contains("between 0 and 65535"))
      assertEquals(source(output, "Instruction.scala"), before)
    }
  }

  test("duplicate extended instruction opcodes are rejected with a useful diagnostic") {
    fixture { (input, ext, output) =>
      Files.writeString(
        ext.resolve("extinst.opencl.std.100.grammar.json"),
        """{"instructions":[{"opname":"first","opcode":17},{"opname":"second","opcode":17}]}"""
      )
      val error = intercept[IllegalArgumentException](generate(input, ext, output))
      assert(error.getMessage.contains("duplicate extended instruction opcodes"))
      assert(!Files.exists(output))
    }
  }
}
