package net.kurobako.gemstone.codegen

import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import net.kurobako.gemstone.codegen.AMDGPUDomainRules.*
import net.kurobako.gemstone.codegen.AMDGPUOperandDomains.*
import net.kurobako.gemstone.codegen.OperandDomain.*
import net.kurobako.gemstone.codegen.OperandDomainLLVM.{read as _, *}
import net.kurobako.gemstone.codegen.OperandDomainOracle.{read as _, *}
import net.kurobako.gemstone.isa.catalogue.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import scala.collection.immutable.SortedMap
import upickle.default.*

class OperandDomainSuite extends munit.FunSuite {

  private def document(types: String) =
    xmlDocument(s"<Root><OperandTypes>$types</OperandTypes></Root>")

  private def xmlDocument(text: String): Document = {
    val factory = DocumentBuilderFactory.newInstance()
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.newDocumentBuilder().parse(InputSource(StringReader(text)))
  }

  private def value(name: String, code: Int) =
    s"<PredefinedValue><Name>$name</Name><Description>d</Description><Value>$code</Value></PredefinedValue>"

  private def operandType(name: String, values: Seq[(String, Int)], extra: String) =
    s"""<OperandType IsPartitioned="false"><OperandTypeName>OPR_$name</OperandTypeName>
       |<Description>d</Description>$extra
       |<OperandPredefinedValues>${values.map((name, code) =>
        value(name, code)
      ).mkString}</OperandPredefinedValues></OperandType>""".stripMargin

  private val model = OperandDomainLLVM.Model(
    encValues = Map(
      "SGPR_MAX_SI"                   -> 101,
      "SGPR_MAX_GFX10"                -> 105,
      "TTMP_VI_MIN"                   -> 112,
      "TTMP_VI_MAX"                   -> 123,
      "TTMP_GFX9PLUS_MIN"             -> 108,
      "TTMP_GFX9PLUS_MAX"             -> 123,
      "INLINE_INTEGER_C_MIN"          -> 128,
      "INLINE_INTEGER_C_POSITIVE_MAX" -> 192,
      "INLINE_INTEGER_C_MAX"          -> 208,
      "INLINE_FLOATING_C_MIN"         -> 240,
      "INLINE_FLOATING_C_MAX"         -> 248,
      "LITERAL_CONST"                 -> 255,
      "LITERAL64_CONST"               -> 254
    ),
    registers = Vector(
      RegisterDef("vcc_lo", 106, ""),
      RegisterDef("vcc_hi", 107, ""),
      RegisterDef("m0", 124, "gfxpre11"),
      RegisterDef("m0", 125, "gfx11plus"),
      RegisterDef("src_vccz", 251, "")
    ),
    fileLast = Map("sgpr" -> 105, "vgpr" -> 255, "ttmp" -> 15, "agpr" -> 255),
    operandClasses = Map.empty,
    exportTargets = Map.empty
  )

  private def entry(domains: OperandDomain*) = Entry("SRC", union(domains.toVector), Vector.empty, "RDNA3")

  private val scalars = Vector(
    Registers("sgpr", first = 0, last = 105, code = 0),
    Named("vcc_lo", 106),
    Named("vcc_hi", 107),
    Registers("ttmp", first = 0, last = 15, code = 108)
  )

  private def evidence(oracle: Vector[Observation] = Vector.empty) =
    Evidence(model, oracle, Set("src_vccz"), Set("SRC"))

  test("operand types parse values, subtypes and partitioned fields") {
    val fields =
      s"""<MicrocodeFormat><BitMap><Field IsConditional="false"><FieldName>LENGTH</FieldName>
         |<BitLayout RangeCount="1"><Range Order="0"><BitCount>6</BitCount><BitOffset>0</BitOffset></Range></BitLayout>
         |</Field><Field IsConditional="false"><FieldName>KIND</FieldName>
         |<FieldPredefinedValues>${value("A", 0)}${value("B", 1)}</FieldPredefinedValues>
         |<BitLayout RangeCount="1"><Range Order="0"><BitCount>2</BitCount><BitOffset>6</BitOffset></Range></BitLayout>
         |</Field></BitMap></MicrocodeFormat>""".stripMargin
    val types = OperandDomainXML.parse(document(
      operandType(
        "SSRC",
        Seq("s0" -> 0, "s1" -> 1, "vcc_lo" -> 106),
        "<Subtypes><Subtype>OPR_SREG</Subtype></Subtypes>"
      ) +
        operandType("CLAUSE", Nil, fields)
    ))
    assertEquals(types.map(_.name), Vector("SSRC", "CLAUSE"))
    assertEquals(types.head.subtypes, Vector("SREG"))
    assertEquals(
      OperandDomainXML.domain(types.head),
      Union(Vector(Registers("sgpr", first = 0, last = 1, code = 0), Named("vcc_lo", 106)))
    )
    val Partitioned(parts) = OperandDomainXML.domain(types(1)): @unchecked
    assertEquals(parts.map(_.name), Vector("LENGTH", "KIND"))
    assertEquals(parts.head.domain, Immediate(6, signed = false))
    assertEquals(parts(1).domain, Union(Vector(Named("a", 0), Named("b", 1))))
  }

  test("a lone VALUE field is a bare immediate and src_simm markers name an immediate form") {
    val format = """<MicrocodeFormat><BitMap><Field IsConditional="false"><FieldName>VALUE</FieldName>
                   |<BitLayout RangeCount="1"><Range Order="0"><BitCount>21</BitCount><BitOffset>0</BitOffset></Range>
                   |</BitLayout></Field></BitMap></MicrocodeFormat>""".stripMargin
    val types = OperandDomainXML.parse(document(
      operandType("SMEM_OFFSET", Seq("s0" -> 0, "SRC_SIMM21" -> 0), "") + operandType("LABEL", Nil, format)
    ))
    assertEquals(
      OperandDomainXML.domain(types.head),
      Union(Vector(Registers("sgpr", first = 0, last = 0, code = 0), Immediate(21, signed = false)))
    )
    assertEquals(OperandDomainXML.domain(types(1)), Immediate(21, signed = false))
    val signed = AMDGPUOperandDomains.entriesOf("RDNA3", types).find(_.name == "LABEL").get
    assertEquals(signed.domain, Immediate(21, signed = true))
  }

  test("consecutive registers, integers, numbered names, floats and literals compress to ranges") {
    val named = (0 to 105).map(i => s"s$i" -> i) ++ (0 to 15).map(i => s"TTMP$i" -> (108 + i)) ++
      Seq("VCC_LO" -> 106, "VCC_HI" -> 107, "M0" -> 125, "NULL" -> 124, "EXEC_LO" -> 126, "EXEC_HI" -> 127) ++
      (0 to 64).map(i => s"$i" -> (128 + i)) ++ (1 to 16).map(i => s"-$i" -> (192 + i)) ++
      Seq("0.5" -> 240, "-0.5" -> 241, "1.0" -> 242, "SRC_SCC" -> 253, "SRC_LITERAL" -> 255, "SRC_SIMM8" -> 0) ++
      (0 to 3).map(i => s"mrt$i" -> i) ++ Seq("pos0" -> 12, "pos1" -> 13, "pos3" -> 15)
    val result = compress(named.toVector)
    assert(result.contains(Registers("sgpr", first = 0, last = 105, code = 0)))
    assert(result.contains(Registers("ttmp", first = 0, last = 15, code = 108)))
    assert(result.contains(IntegerRange(first = 0, last = 64, code = 128)))
    assert(result.contains(IntegerRange(first = -1, last = -16, code = 193)))
    assert(result.contains(NamedRange("mrt", first = 0, last = 3, code = 0)))
    assert(result.contains(NamedRange("pos", first = 0, last = 1, code = 12)))
    assert(result.contains(NamedRange("pos", first = 3, last = 3, code = 15)))
    assert(result.contains(FloatSet(Vector(
      FloatConstant("0.5", 240),
      FloatConstant("-0.5", 241),
      FloatConstant("1.0", 242)
    ))))
    assert(result.contains(Literal(bytes = 4, code = 255)))
    assert(result.contains(Named("src_scc", 253)))
    assert(!result.exists(_.codes.exists(_._1 == "src_simm8")))
    assertEquals(result.map(OperandDomain.firstCode), result.map(OperandDomain.firstCode).sorted)
  }

  test("compressing the expansion of a domain list returns the list") {
    val domains = Vector(
      Registers("sgpr", first = 0, last = 101, code = 0),
      Registers("ttmp", first = 0, last = 15, code = 108),
      Named("m0", 125),
      IntegerRange(first = 0, last = 64, code = 128),
      IntegerRange(first = -1, last = -16, code = 193),
      NamedRange("attr", first = 0, last = 32, code = 0),
      Literal(bytes = 8, code = 254)
    )
    assertEquals(
      compress(domains.flatMap(_.codes)).sortBy(OperandDomain.firstCode),
      domains.sortBy(OperandDomain.firstCode)
    )
  }

  test("domains survive a JSON round trip") {
    val domain: OperandDomain = Union(Vector(
      Registers("vgpr", first = 0, last = 255, code = 256, alignment = 2),
      Partitioned(Vector(SubField("ID", Vector(BitRange(0, 6, 0)), Immediate(6, signed = false)))),
      Ignored,
      Unmodelled
    ))
    assertEquals(read[OperandDomain](write(domain)), domain)
  }

  test("OPR_SSRC of RDNA3 lists the registers, constants and special values of the ISA document") {
    val path = Path.of("target/references/amdgpu/amd/xml/amdgpu_isa_rdna3.xml")
    assume(Files.isRegularFile(path), "missing AMD ISA XML; run sbt codegen")
    val types  = OperandDomainXML.parse(GenerateAMDGPUISA.xmlDocument(path))
    val ssrc   = types.find(_.name == "SSRC").get
    val domain = OperandDomainXML.domain(ssrc)
    val parts  = AMDGPUOperandDomains.flatten(domain)
    assertEquals(parts.size, 17)
    assert(parts.contains(Registers("sgpr", first = 0, last = 105, code = 0)))
    assert(parts.contains(Registers("ttmp", first = 0, last = 15, code = 108)))
    assertEquals(
      parts.collect { case Named(name, code) => name -> code }.toMap,
      Map(
        "vcc_lo"            -> 106,
        "vcc_hi"            -> 107,
        "null"              -> 124,
        "m0"                -> 125,
        "exec_lo"           -> 126,
        "exec_hi"           -> 127,
        "src_shared_base"   -> 235,
        "src_shared_limit"  -> 236,
        "src_private_base"  -> 237,
        "src_private_limit" -> 238,
        "src_scc"           -> 253
      )
    )
    assert(parts.contains(IntegerRange(first = 0, last = 64, code = 128)))
    assert(parts.contains(IntegerRange(first = -1, last = -16, code = 193)))
    val floats = parts.collect { case FloatSet(constants) => constants.map(c => c.text -> c.code) }.flatten
    assertEquals(
      floats,
      Vector(
        "0.5"        -> 240,
        "-0.5"       -> 241,
        "1.0"        -> 242,
        "-1.0"       -> 243,
        "2.0"        -> 244,
        "-2.0"       -> 245,
        "4.0"        -> 246,
        "-4.0"       -> 247,
        "0.15915494" -> 248
      )
    )
    assert(parts.contains(Literal(bytes = 4, code = 255)))
    assertEquals(ssrc.subtypes, Vector("SDST", "SDST_EXEC", "SDST_M0", "SDST_NULL", "SREG", "SSRC_SPECIAL_SCC"))
  }

  test("XML wins where LLVM defines more than the document lists") {
    val base   = Vector(entry((scalars :+ Named("m0", 125))*))
    val result = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence())
    assertEquals(result.entries, base)
    val only = result.findings.filter(_.check == "llvm-only")
    assertEquals(only.map(_.subject), Vector("src_vccz"))
    assertEquals(only.flatMap(_.resolution), Vector("XML wins"))
    assertEquals(result.findings.filter(_.resolution.isEmpty), Vector.empty)
  }

  test("a code LLVM and the document disagree on is unresolved until an override covers it") {
    val base = Vector(entry((scalars :+ Named("m0", 124))*))
    val open = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence())
    assertEquals(open.findings.filter(_.resolution.isEmpty).map(_.subject), Vector("m0"))
    val row = Override(
      isa = "RDNA3",
      remove = Some("m0"),
      add = Vector("m0" -> 125),
      anchor = None,
      resolves = Set("m0"),
      reason = "gfx11 moved m0",
      source = "SIRegisterInfo.td M0_gfx11plus"
    )
    val fixed = AMDGPUOperandDomains.merge("RDNA3", base, Vector(row), evidence())
    assert(fixed.entries.head.domain.codes.contains("m0" -> 125))
    assertEquals(fixed.findings.filter(_.resolution.isEmpty), Vector.empty)
    assert(fixed.findings.exists(_.resolution.exists(_.startsWith("override"))))
  }

  test("an override that changes and resolves nothing is rejected") {
    val base  = Vector(entry((scalars :+ Named("m0", 125))*))
    val stale = Override(
      isa = "RDNA3",
      remove = None,
      add = Vector.empty,
      anchor = None,
      resolves = Set("m1"),
      reason = "stale",
      source = "none"
    )
    intercept[IllegalArgumentException](AMDGPUOperandDomains.merge("RDNA3", base, Vector(stale), evidence()))
  }

  test("the hand-written overrides stay within the bound declared for merge") {
    assert(AMDGPUDomainRules.Overrides.size <= 24)
  }

  test("the oracle breaks the tie: a rejected name is unresolved unless another processor accepts it") {
    val base     = Vector(entry((scalars :+ Named("m0", 125))*))
    val rejected = Observation("gfx1100", "s_mov_b32 vcc_hi, 0", accepted = false, "vcc_hi")
    val open     = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence(Vector(rejected)))
    assertEquals(open.findings.filter(_.resolution.isEmpty).map(_.subject), Vector("vcc_hi"))
    val accepted = Observation("gfx1101", "s_mov_b32 vcc_hi, 0", accepted = true, "")
    val shared   = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence(Vector(rejected, accepted)))
    assertEquals(shared.findings.filter(_.resolution.isEmpty), Vector.empty)
  }

  test("a name the oracle accepts but the document lacks is unresolved") {
    val base     = Vector(entry((scalars :+ Named("m0", 125))*))
    val accepted = Observation("gfx1100", "s_mov_b32 s0, src_vccz", accepted = true, "")
    val open     = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence(Vector(accepted)))
    assertEquals(open.findings.filter(_.resolution.isEmpty).map(_.subject), Vector("src_vccz"))
  }

  test("codegen fails on an unresolved disagreement and passes once none remain") {
    val base    = Vector(entry((scalars :+ Named("m0", 124))*))
    val result  = AMDGPUOperandDomains.merge("RDNA3", base, Vector.empty, evidence())
    val fields  = AMDGPUOperandFields.Mapping(Vector.empty, SortedMap.empty, SortedMap.empty, Vector.empty)
    val table   = GenerateAMDGPUDomains.Table("RDNA3", result, fields)
    val failure = intercept[IllegalStateException](GenerateAMDGPUDomains.check(Vector(table), Path.of("report.md")))
    assert(failure.getMessage.contains("RDNA3 register-code m0"))
    val clean = table.copy(result = result.copy(findings = result.findings.filter(_.resolution.nonEmpty)))
    GenerateAMDGPUDomains.check(Vector(clean), Path.of("report.md"))
  }

  test("fields map to operand domains, ignored when unused, unmodelled when no operand describes them") {
    val layouts = ujson.read(
      """[{"isa":"RDNA3","format":"VOP2","bits":32,"mask":"80000000","prefix":"0","fields":[
        |{"name":"ENCODING","ranges":[{"offset":31,"bits":1,"padding":0}]},
        |{"name":"OP","ranges":[{"offset":25,"bits":6,"padding":0}]},
        |{"name":"SRC0","ranges":[{"offset":0,"bits":9,"padding":0}]},
        |{"name":"VSRC1","ranges":[{"offset":9,"bits":8,"padding":0}]},
        |{"name":"VDST","ranges":[{"offset":17,"bits":8,"padding":0}]},
        |{"name":"SBASE","ranges":[{"offset":0,"bits":6,"padding":1}]},
        |{"name":"MODIFIER","ranges":[{"offset":26,"bits":1,"padding":0}]}]}]""".stripMargin
    )
    val metadata = ujson.read(
      """{"strings":["RDNA3","VOP2","SRC0","VGPR","SRC","VSRC1","VDST"],
        |"schemas":[[[2,4,false,32,false],[5,3,false,32,false],[6,3,false,32,true]],
        |           [[2,4,false,32,false]]],
        |"encodings":[{"isa":0,"format":1,"opcode":1,"schema":0,"groups":[]},
        |             {"isa":0,"format":1,"opcode":2,"schema":1,"groups":[]}]}""".stripMargin
    )
    val vgpr = Entry("VGPR", Registers("vgpr", first = 0, last = 255, code = 0), Vector.empty, "RDNA3")
    val src  =
      Entry(
        "SRC",
        union(Vector(
          Registers("sgpr", first = 0, last = 105, code = 0),
          Registers("vgpr", first = 0, last = 255, code = 256)
        )),
        Vector.empty,
        "RDNA3"
      )
    val mapping = AMDGPUOperandFields.map(
      "RDNA3",
      AMDGPUOperandFields.readEncodings(metadata),
      AMDGPUOperandFields.readLayouts(layouts),
      Vector(vgpr, src)
    )
    assertEquals(mapping.signatures.size, 2)
    assertEquals(
      mapping.signatures.head.fields.toMap,
      Map("SRC0" -> "src", "VSRC1" -> "vgpr", "VDST" -> "vgpr", "SBASE" -> "unmodelled", "MODIFIER" -> "unmodelled")
    )
    assertEquals(mapping.signatures(1).fields("SRC0"), "src")
    assertEquals(mapping.signatures(1).fields("VSRC1"), "ignored")
    assertEquals(mapping.signatures(1).fields("VDST"), "ignored")
    assertEquals(mapping.encodings("VOP2"), Vector(1 -> 0, 2 -> 1))
    assertEquals(mapping.findings.filter(_.resolution.isEmpty), Vector.empty)
  }

  test("a type wider than its field is reported, and padding counts towards the field width") {
    val layouts = ujson.read(
      """[{"isa":"RDNA3","format":"SMEM","bits":64,"mask":"0","prefix":"0","fields":[
        |{"name":"SBASE","ranges":[{"offset":0,"bits":6,"padding":1}]},
        |{"name":"SDATA","ranges":[{"offset":6,"bits":7,"padding":0}]}]}]""".stripMargin
    )
    val metadata = ujson.read(
      """{"strings":["RDNA3","SMEM","SBASE","SREG","SDATA","SIMM8","WIDE"],
        |"schemas":[[[2,3,false,64,false],[4,5,false,8,false]]],
        |"encodings":[{"isa":0,"format":1,"opcode":1,"schema":0,"groups":[]}]}""".stripMargin
    )
    val entries = Vector(
      Entry("SREG", Registers("sgpr", first = 0, last = 105, code = 0), Vector.empty, "RDNA3"),
      Entry("SIMM8", Immediate(8, signed = false), Vector.empty, "RDNA3")
    )
    val mapping = AMDGPUOperandFields.map(
      "RDNA3",
      AMDGPUOperandFields.readEncodings(metadata),
      AMDGPUOperandFields.readLayouts(layouts),
      entries
    )
    assertEquals(mapping.signatures.head.fields.toMap, Map("SBASE" -> "sreg", "SDATA" -> "simm8@7"))
    assertEquals(mapping.narrowed.toMap, Map("simm8@7" -> Immediate(7, signed = false)))
    assertEquals(mapping.findings.map(_.check), Vector("field-fit"))
    assert(mapping.findings.forall(_.resolution.nonEmpty))
  }
}
