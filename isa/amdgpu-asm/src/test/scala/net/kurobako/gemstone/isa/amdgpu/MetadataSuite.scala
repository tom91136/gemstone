package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
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

  test("every opcode exposes metadata and an opcode lookup") {
    Opcode.definitions.foreach { definition =>
      assert(definition.meta.encodings.nonEmpty, definition.toString)
      assertEquals(Opcode.forOpcode(definition.opcode).contains(definition), true)
    }
  }
  test("source selectors retain ISA-specific literal and inline-constant meanings") {
    // Encodings from the AMD ISA selector tables; deliberately independent of generated constants.
    assertEquals(SourceOperand.decode(254, ISA.CDNA1), Some(SourceOperand.Special(SpecialSource.src_lds_direct)))
    assertEquals(SourceOperand.decode(254, ISA.CDNA5), Some(SourceOperand.Literal(8)))
    assertEquals(SourceOperand.decode(254, ISA.RDNA4), None)
    assertEquals(SourceOperand.literalSize(254, ISA.CDNA1), None)
    assertEquals(SourceOperand.literalSize(254, ISA.CDNA5).map(_.raw), Some(8))
    assertEquals(SourceOperand.decode(248, ISA.GFX6), None)
    assertEquals(
      SourceOperand.decode(248, ISA.GFX8),
      Some(SourceOperand.Floating(java.lang.Double.longBitsToDouble(0x3fc45f306dc9c882L)))
    )
    assertEquals(SourceOperand.decode(193, ISA.CDNA1), Some(SourceOperand.Integer(-1)))
    assertEquals(SourceOperand.decode(-1, ISA.CDNA1), None)
    assertEquals(SourceOperand.decode(512, ISA.CDNA1), None)
  }

  test("literal metadata accepts only four-byte and eight-byte encodings") {
    val valid = ujson.Obj(
      "isa"      -> ujson.Str("CDNA5"),
      "kind"     -> ujson.Str("SRC"),
      "fields"   -> ujson.Arr(),
      "literals" -> ujson.Obj("255" -> ujson.Num(4), "254" -> ujson.Num(8))
    )
    val decoded = read[OperandEncoding](valid)
    assertEquals(decoded.literals.view.mapValues(_.raw).toMap, Map(255 -> 4, 254 -> 8))
    valid("literals") = ujson.Obj("255" -> ujson.Num(3))
    assert(Try(read[OperandEncoding](valid)).isFailure)
  }

}
