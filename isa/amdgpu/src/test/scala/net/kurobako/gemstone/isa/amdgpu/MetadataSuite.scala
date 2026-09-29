package net.kurobako.gemstone.isa.amdgpu

import scala.util.Try
import upickle.default.read

class MetadataSuite extends munit.FunSuite {
  test("catalogue decoding validates interned strings at the JSON boundary") {
    def catalogue(text: String): ujson.Value = ujson.Obj(
      "strings"   -> ujson.Arr(text),
      "schemas"   -> ujson.Arr(),
      "encodings" -> ujson.Arr(),
      "metadata"  -> ujson.Arr()
    )
    assertEquals(read[Metadata.Catalogue](catalogue("VOP2")).strings, Vector("VOP2"))
    assert(Try(read[Metadata.Catalogue](catalogue(""))).isFailure)
    assertEquals(read[Metadata.Catalogue](catalogue("VO\u0000P2")).strings, Vector("VO\u0000P2"))
  }

  test("every instruction definition exposes metadata, an opcode lookup, and decoder forms") {
    Instruction.definitions.foreach { definition =>
      assert(definition.meta.encodings.nonEmpty, definition.toString)
      assertEquals(Instruction.Definition.forOpcode(definition.opcode).contains(definition), true)
      assert(definition.operandCounts.nonEmpty)
    }
  }
}
