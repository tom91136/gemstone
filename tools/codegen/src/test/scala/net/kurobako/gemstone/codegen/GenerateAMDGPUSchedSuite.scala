package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.testing.*

class GenerateAMDGPUSchedSuite extends Suite {
  private val catalogue = Vector(
    "s_add_u32",
    "v_mul_wide_f32",
    "flat_load_dword",
    "v_mov_b32",
    "v_variant_op",
    "v_wave64_only",
    "s_two_writes",
    "s_no_writes",
    "s_pseudo_op",
    "s_not_in_llvm"
  )

  private val aliases = Vector("gfx1031" -> "gfx1030")

  private def list(items: Seq[Any]): String = items.mkString("[", ", ", "]")

  private def definition(name: String, superclass: String, statements: String*): String =
    (s"def $name {\t// $superclass" +: statements.map(statement => s"  $statement") :+ "}").mkString("\n")

  private def instruction(
      name: String,
      asm: String,
      size: Int,
      writes: Seq[String],
      predicates: Seq[String] = Nil,
      pseudo: Int = 0
  ): String =
    definition(
      name,
      "Instruction",
      s"""string AsmString = "$asm";""",
      s"int Size = $size;",
      s"bit isPseudo = $pseudo;",
      "bit isCodeGenOnly = 0;",
      s"list<Predicate> Predicates = ${list(predicates)};",
      s"list<SchedReadWrite> SchedRW = ${list(writes)};"
    )

  private def writeRes(
      name: String,
      model: String,
      write: String,
      resources: Seq[String],
      cycles: Seq[Int],
      latency: Int
  ): String =
    definition(
      name,
      "WriteRes",
      s"SchedMachineModel SchedModel = $model;",
      s"SchedWrite WriteType = $write;",
      s"list<ProcResourceKind> ProcResources = ${list(resources)};",
      s"list<int> ReleaseAtCycles = ${list(cycles)};",
      s"int Latency = $latency;",
      "bit Unsupported = 0;"
    )

  private def processor(name: String, model: String, features: String*): String =
    definition(
      name,
      "Processor",
      s"""string Name = "$name";""",
      s"SchedMachineModel SchedModel = $model;",
      s"list<SubtargetFeature> Features = ${list(features)};"
    )

  private def fixture(variantWrites: Seq[String] = Seq("WriteSALU")): String = {
    val instructions = Vector(
      instruction("S_ADD_U32", "s_add_u32 $sdst, $ssrc0, $ssrc1", 4, Seq("WriteSALU")),
      instruction("V_MUL_WIDE_F32", "v_mul_wide_f32 $vdst, $src0, $src1", 8, Seq("WriteVALU")),
      instruction("FLAT_LOAD_DWORD", "flat_load_dword $vdst, $addr", 8, Seq("WriteVMEM")),
      instruction("FLAT_LOAD_DWORD_LDS", "flat_load_dword $vdst, $addr lds", 8, Seq("WriteSALU")),
      instruction("V_MOV_B32_e32", "v_mov_b32 $vdst, $src0", 4, Seq("WriteSALU")),
      instruction("V_MOV_B32_e64", "v_mov_b32 $vdst, $src0", 8, variantWrites),
      instruction("V_VARIANT_OP", "v_variant_op $vdst", 4, Seq("WriteVariant")),
      instruction("V_WAVE64_ONLY", "v_wave64_only $vdst", 4, Seq("WriteSALU"), Seq("HasWave64")),
      instruction("S_TWO_WRITES", "s_two_writes $sdst", 4, Seq("WriteSALU", "WriteSALU")),
      instruction("S_NO_WRITES", "s_no_writes", 4, Nil),
      instruction("S_PSEUDO_OP", "s_pseudo_op", 4, Seq("WriteSALU"), pseudo = 1)
    )
    val defs = Vector(
      definition("WriteSALU", "SchedWrite"),
      definition("WriteVALU", "SchedWrite"),
      definition("WriteVMEM", "SchedWrite"),
      definition("WriteVariant", "SchedWriteVariant SchedWrite"),
      definition("SALU", "ProcResource", "int NumUnits = 1;"),
      definition("VALU", "ProcResource", "int NumUnits = 2;"),
      definition("VMEM", "ProcResource", "int NumUnits = 1;"),
      writeRes("WriteRes_SI_SALU", "SIModel", "WriteSALU", Seq("SALU"), Seq(1), 2),
      writeRes("WriteRes_SI_VALU", "SIModel", "WriteVALU", Seq("VALU"), Seq(3), 4),
      writeRes("WriteRes_SI_VMEM", "SIModel", "WriteVMEM", Seq("SALU", "VMEM"), Seq(1, 4), 300),
      writeRes("WriteRes_GFX10_SALU", "GFX10Model", "WriteSALU", Seq("SALU"), Seq(1), 1),
      definition(
        "HasWave64",
        "Predicate",
        "bit AssemblerMatcherPredicate = 1;",
        "dag AssemblerCondDag = (all_of FeatureWave64);"
      ),
      definition("FeatureWave64", "SubtargetFeature", "list<SubtargetFeature> Implies = [];"),
      definition("FeatureGfx9", "SubtargetFeature", "list<SubtargetFeature> Implies = [FeatureWave64];"),
      definition("FeatureGfx10", "SubtargetFeature", "list<SubtargetFeature> Implies = [];"),
      processor("gfx900", "SIModel", "FeatureGfx9"),
      processor("gfx1030", "GFX10Model", "FeatureGfx10"),
      processor("generic", "NoSchedModel"),
      definition(
        "InstRW_SI",
        "InstRW",
        "SchedMachineModel SchedModel = SIModel;",
        "dag Instrs = (instrs V_VARIANT_OP);",
        "list<SchedReadWrite> OperandReadWrites = [WriteSALU];"
      )
    )
    dumpOf((instructions ++ defs)*)
  }

  private def withText[A](text: String)(body: Path => A): A = {
    val directory = Files.createTempDirectory("amdgpu-sched")
    val dump      = directory.resolve("dump.txt")
    try {
      val _ = Files.writeString(dump, text)
      body(dump)
    } finally {
      val _ = Files.deleteIfExists(dump)
      val _ = Files.deleteIfExists(directory)
    }
  }

  private def withDump[A](body: Path => A): A = withText(fixture())(body)

  private def dumpOf(records: String*): String = ("------------- Defs -----------------" +: records).mkString("\n")

  private def render(dump: Path, committed: Option[ujson.Value] = None): GenerateAMDGPUSched.Rendered =
    GenerateAMDGPUSched.render(dump, catalogue, aliases, References.LlvmVersion, committed)

  private def entries(resource: ujson.Value, processor: String): ujson.Value =
    resource("tables")(resource("processors")(processor).num.toInt)("entries")

  private def ldsEntries(resource: ujson.Value, processor: String): ujson.Value =
    resource("tables")(resource("processors")(processor).num.toInt)("lds")

  test("planned cycles divide wide writes and sum repeated resources") {
    withDump { dump =>
      val resource = ujson.read(render(dump).resource)
      assertEquals(entries(resource, "gfx900")("v_mul_wide_f32"), ujson.Arr(4, 2, "WriteVALU"))
      assertEquals(entries(resource, "gfx900")("flat_load_dword"), ujson.Arr(300, 4, "WriteVMEM"))
      assertEquals(entries(resource, "gfx900")("s_two_writes"), ujson.Arr(2, 2, "WriteSALU+WriteSALU"))
    }
  }

  test("processors with equal schedules share a table") {
    withDump { dump =>
      val resource = ujson.read(render(dump).resource)
      assertEquals(resource("processors")("gfx1031"), resource("processors")("gfx1030"))
      assert(resource("processors")("gfx900").num != resource("processors")("gfx1030").num)
      assertEquals(entries(resource, "gfx1030").obj.keys.toSet, Set("s_add_u32", "v_mov_b32", "s_two_writes"))
      assertEquals(entries(resource, "gfx900")("v_variant_op"), ujson.Arr(2, 1, "WriteSALU"))
      assertEquals(entries(resource, "generic"), ujson.Obj())
    }
  }

  test("lds forms are keyed apart from the plain form") {
    withDump { dump =>
      val resource = ujson.read(render(dump).resource)
      assertEquals(entries(resource, "gfx900")("flat_load_dword"), ujson.Arr(300, 4, "WriteVMEM"))
      assertEquals(ldsEntries(resource, "gfx900")("flat_load_dword"), ujson.Arr(2, 1, "WriteSALU"))
      assertEquals(ldsEntries(resource, "gfx1030").obj.keys.toSet, Set("flat_load_dword"))
    }
  }

  test("lds-only mnemonics are keyed in the plain table") {
    val text = dumpOf(
      instruction("BUFFER_STORE_LDS_DWORD", "buffer_store_lds_dword $vaddr, $srsrc lds", 8, Seq("WriteSALU")),
      definition("WriteSALU", "SchedWrite"),
      definition("SALU", "ProcResource", "int NumUnits = 1;"),
      writeRes("WriteRes_SI_SALU", "SIModel", "WriteSALU", Seq("SALU"), Seq(1), 2),
      processor("gfx900", "SIModel")
    )
    withText(text) { dump =>
      val rendered = GenerateAMDGPUSched.render(
        dump,
        Vector("buffer_store_lds_dword"),
        Vector.empty,
        References.LlvmVersion,
        None
      )
      val resource = ujson.read(rendered.resource)
      assertEquals(entries(resource, "gfx900").obj.get("buffer_store_lds_dword"), Some(ujson.Arr(2, 1, "WriteSALU")))
      assertEquals(ldsEntries(resource, "gfx900").obj.keys.toSet, Set.empty[String])
    }
  }

  test("candidates of one form that disagree fail naming the processor, mnemonic, and records") {
    withText(fixture(Seq("WriteVALU"))) { dump =>
      val message = intercept[IllegalStateException](render(dump)).getMessage
      assert(message.startsWith("AMDGPU schedule candidates disagree on gfx"))
      assert(message.contains(" for v_mov_b32: "))
      assert(message.contains("V_MOV_B32_e32"))
      assert(message.contains("V_MOV_B32_e64"))
    }
  }

  test("report lists unmatched outcomes by reason and processor") {
    withDump { dump =>
      val lines = render(dump).report.linesIterator.toSet
      assert(lines.contains("| gfx900 | SIModel | 10 | 7 | 2 | 0 | 1 | 0 | 0 |"))
      assert(lines.contains("| gfx1030 | GFX10Model | 9 | 3 | 2 | 0 | 1 | 2 | 1 |"))
      assert(lines.contains("| generic | NoSchedModel | 9 | 0 | 2 | 7 | 0 | 0 | 0 |"))
      assert(lines.contains("- `absent` for all processors (2): `s_not_in_llvm`, `s_pseudo_op`"))
      assert(
        lines.contains(
          "- `no-model` for generic (7): `flat_load_dword`, `s_add_u32`, `s_no_writes`, `s_two_writes`, `v_mov_b32`, `v_mul_wide_f32`, `v_variant_op`"
        )
      )
      assert(lines.contains("- `no-writes` for gfx1030, gfx1031, gfx900 (1): `s_no_writes`"))
      assert(lines.contains("- `unmapped` for gfx1030, gfx1031 (2): `flat_load_dword`, `v_mul_wide_f32`"))
      assert(lines.contains("- `variant` for gfx1030, gfx1031 (1): `v_variant_op`"))
    }
  }

  test("committed matches fail codegen only when they stop matching") {
    withDump { dump =>
      val text = render(dump).resource
      assertEquals(render(dump, Some(ujson.read(text))).resource, text)
      val stale = ujson.Obj(
        "processors" -> ujson.Obj("gfx1030" -> 0),
        "tables"     -> ujson.Arr(
          ujson.Obj(
            "model"   -> "GFX10Model",
            "entries" ->
              ujson.Obj("v_wave64_only" -> ujson.Arr(2, 1, "WriteSALU"), "s_gone" -> ujson.Arr(2, 1, "WriteSALU")),
            "lds" -> ujson.Obj("s_add_u32" -> ujson.Arr(2, 1, "WriteSALU"))
          )
        )
      )
      val error = intercept[IllegalStateException](render(dump, Some(stale)))
      assertEquals(
        error.getMessage,
        "2 previously matched AMDGPU schedule entries no longer match: gfx1030 s_add_u32 lds; gfx1030 v_wave64_only"
      )
    }
  }

  test("committed processors outside the catalogue drop out without failing") {
    withDump { dump =>
      val removed = ujson.Obj(
        "processors" -> ujson.Obj("gfx1159" -> 0),
        "tables"     -> ujson.Arr(
          ujson.Obj("model" -> "GFX10Model", "entries" -> ujson.Obj("s_add_u32" -> ujson.Arr(2, 1, "WriteSALU")))
        )
      )
      val resource = ujson.read(render(dump, Some(removed)).resource)
      assert(!resource("processors").obj.contains("gfx1159"))
    }
  }

  test("kept fields after a bits initialiser are read") {
    val text = dumpOf(
      definition(
        "S_BITS",
        "Instruction",
        """string AsmString = "s_bits $sdst";""",
        "bits<32> Inst = { 0, 1 };",
        "int Size = 4;"
      )
    )
    withText(text) { dump =>
      assertEquals(AMDGPUSchedRecords.read(dump).records("S_BITS")("Size"), ujson.Num(4))
    }
  }

  test("malformed kept fields fail naming the record and field") {
    val text = dumpOf(definition("S_BAD", "Instruction", "int Size = 4 5;"))
    withText(text) { dump =>
      val error = intercept[IllegalArgumentException](AMDGPUSchedRecords.read(dump))
      assert(error.getMessage.startsWith("TableGen record S_BAD field Size: "))
    }
  }
}
