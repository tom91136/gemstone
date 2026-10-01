package net.kurobako.gemstone.codegen

import java.nio.file.Files

class GenerateSPIRVConstantsSuite extends munit.FunSuite {
  test("grammar constants preserve wire bits and regenerate both languages deterministically") {
    generator.inDirectory { root =>
      val grammar = root.resolve("grammar.json")
      val _       = Files.writeString(
        grammar,
        """{
        "magic_number": "0x07230203", "major_version": 1, "minor_version": 6, "revision": 1,
        "instructions": [{"opname":"OpNop", "opcode":0, "class":"Miscellaneous"}],
        "operand_kinds": [
          {"kind":"BuiltIn", "category":"ValueEnum", "enumerants":[{"enumerant":"Position","value":0}]},
          {"kind":"Dim", "category":"ValueEnum", "enumerants":[{"enumerant":"2D","value":1}]},
          {"kind":"ImageOperands", "category":"BitEnum", "enumerants":[{"enumerant":"HighBit","value":"0x80000000"}]}
        ]
      }"""
      )
      Vector("extinst.glsl.std.450.grammar.json", "extinst.opencl.std.100.grammar.json").foreach { name =>
        val _ = Files.writeString(root.resolve(name), """{"instructions":[{"opname":"Abs","opcode":1}]}""")
      }
      val scalaRoot  = root.resolve("scala")
      val nativeRoot = root.resolve("native")
      val arguments  =
        Array("instructions", grammar.toString, root.toString, scalaRoot.toString, nativeRoot.toString, "v1_6")
      GenerateSPIRVISA.main(arguments)
      val scalaFile   = scalaRoot.resolve("net/kurobako/gemstone/isa/spirv/v1_6/SPIRV.scala")
      val nativeFile  = nativeRoot.resolve("gemstone_spirv_constants.hpp")
      val scalaSource = Files.readString(scalaRoot.resolve("net/kurobako/gemstone/isa/spirv/v1_6/Dim.scala")) +
        Files.readString(scalaRoot.resolve("net/kurobako/gemstone/isa/spirv/v1_6/ImageOperands.scala"))
      val nativeSource = Files.readString(nativeFile)
      assert(scalaSource.contains("final val Value2D: Int = 1"))
      assert(scalaSource.contains("final val HighBit: Int = -2147483648"))
      assert(nativeSource.contains("uint32_t _2D = 1u"))
      assert(nativeSource.contains("uint32_t HighBit = 2147483648u"))
      val outputs = Vector(
        scalaFile,
        nativeFile,
        nativeRoot.resolve("gemstone_image_descriptor.hpp"),
        scalaRoot.resolve("net/kurobako/gemstone/isa/spirv/v1_6/ImageDescriptor.scala")
      )
      val before = outputs.map(path => Files.readString(path) -> Files.getLastModifiedTime(path))
      GenerateSPIRVISA.main(arguments)
      assertEquals(outputs.map(path => Files.readString(path) -> Files.getLastModifiedTime(path)), before)
    }
  }
}
