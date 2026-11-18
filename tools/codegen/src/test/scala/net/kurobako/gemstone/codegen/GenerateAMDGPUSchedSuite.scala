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
      pseudo: Int = 0,
      extra: Seq[String] = Nil
  ): String =
    definition(
      name,
      "Instruction",
      Seq(
        s"""string AsmString = "$asm";""",
        s"int Size = $size;",
        s"bit isPseudo = $pseudo;",
        "bit isCodeGenOnly = 0;",
        s"list<Predicate> Predicates = ${list(predicates)};",
        s"list<SchedReadWrite> SchedRW = ${list(writes)};"
      ) ++ extra*
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
      assertEquals(entries(resource, "gfx900")("v_mul_wide_f32"), ujson.Arr(4, 2, "WriteVALU", ujson.Arr()))
      assertEquals(entries(resource, "gfx900")("flat_load_dword"), ujson.Arr(300, 4, "WriteVMEM", ujson.Arr()))
      assertEquals(entries(resource, "gfx900")("s_two_writes"), ujson.Arr(2, 2, "WriteSALU+WriteSALU", ujson.Arr()))
    }
  }

  private def traitsOf(resource: ujson.Value, processor: String, mnemonic: String): Set[String] =
    entries(resource, processor)(mnemonic)(3).arr.map(_.str).toSet

  private def traitFixture: String = dumpOf(
    instruction(
      "V_FMAC_E32",
      "v_fmac $vdst, $src0, $src1",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit VALU = 1;", "field bit TRANS = 0;", """string Constraints = "$vdst = $src2";""")
    ),
    instruction(
      "V_FMAC_SDWA",
      "v_fmac $vdst, $src0, $src1 sdwa",
      8,
      Seq("WriteSALU"),
      extra = Seq("field bit VALU = 1;", "field bit SDWA = 1;", """string Constraints = "$vdst = $old";""")
    ),
    instruction(
      "V_RCP_E32",
      "v_rcp $vdst, $src0",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit VALU = 1;", "field bit TRANS = 1;")
    ),
    instruction(
      "V_RCP_F64_E32",
      "v_rcp64 $vdst, $src0",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit VALU = 1;", "field bit TRANS = 1;", "bit IsDPMACCInstruction = 1;")
    ),
    instruction("S_MOV_E32", "s_mov $sdst, $ssrc0", 4, Seq("WriteSALU"), extra = Seq("field bit SALU = 1;")),
    instruction("S_LOAD_E32", "s_load $sdst, $sbase", 4, Seq("WriteSALU"), extra = Seq("field bit SMRD = 1;")),
    instruction("DS_READ_E32", "ds_read $vdst, $addr", 4, Seq("WriteSALU"), extra = Seq("field bit DS = 1;")),
    instruction(
      "BUFFER_LOAD_E32",
      "buffer_load $vdst, $addr",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit MUBUF = 1;")
    ),
    instruction(
      "V_ATOMIC_RTN",
      "v_atomic $vdst, $addr",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit MUBUF = 1;", """string Constraints = "$vdst = $vdata";""")
    ),
    instruction(
      "BUFFER_STORE_E32",
      "buffer_store $vdata, $addr",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit MUBUF = 1;", "bit mayStore = 1;")
    ),
    instruction(
      "GLOBAL_ATOMIC_FMIN",
      "global_atomic_fmin $vdst, $addr",
      4,
      Seq("WriteSALU"),
      pseudo = 1,
      extra = Seq("field bit FLAT = 1;", "field bit FPAtomic = 1;")
    ),
    instruction(
      "GLOBAL_ATOMIC_FMIN_gfx9",
      "global_atomic_fmin $vdst, $addr",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit FLAT = 1;", "field bit FPAtomic = 0;", """string PseudoInstr = "GLOBAL_ATOMIC_FMIN";""")
    ),
    instruction(
      "GLOBAL_ATOMIC_ADD",
      "global_atomic_add $vdst, $addr",
      4,
      Seq("WriteSALU"),
      extra = Seq("field bit FLAT = 1;")
    ),
    instruction("S_NOP_E32", "s_nopx", 4, Seq("WriteSALU")),
    definition("WriteSALU", "SchedWrite"),
    definition("SALU", "ProcResource", "int NumUnits = 1;"),
    writeRes("WriteRes_SI_SALU", "SIModel", "WriteSALU", Seq("SALU"), Seq(1), 2),
    processor("gfx900", "SIModel")
  )

  test("entries carry the delay type, wait classes and tied destination LLVM records") {
    withText(traitFixture) { dump =>
      val catalogue = Vector(
        "v_fmac",
        "v_rcp",
        "v_rcp64",
        "s_mov",
        "s_load",
        "ds_read",
        "buffer_load",
        "v_atomic",
        "buffer_store",
        "s_nopx"
      )
      val resource = ujson.read(
        GenerateAMDGPUSched.render(dump, catalogue, Vector.empty, References.LlvmVersion, None).resource
      )
      def of(mnemonic: String) = traitsOf(resource, "gfx900", mnemonic)
      assertEquals(of("v_fmac"), Set("valu", "tied"))
      assertEquals(of("v_rcp"), Set("trans"))
      assertEquals(of("v_rcp64"), Set("valu"))
      assertEquals(of("s_mov"), Set("salu"))
      assertEquals(of("s_load"), Set("smrd"))
      assertEquals(of("ds_read"), Set("waits-valu"))
      assertEquals(of("buffer_load"), Set("waits-valu", "vmem"))
      assertEquals(of("buffer_store"), Set("waits-valu", "vmem", "store"))
      assertEquals(of("v_atomic"), Set("waits-valu", "vmem", "tied"))
      assertEquals(of("s_nopx"), Set.empty[String])
    }
  }

  test("floating-point global atomics carry the fp-atomic trait") {
    withText(traitFixture) { dump =>
      val catalogue = Vector("global_atomic_fmin", "global_atomic_add")
      val resource  = ujson.read(
        GenerateAMDGPUSched.render(dump, catalogue, Vector.empty, References.LlvmVersion, None).resource
      )
      assertEquals(traitsOf(resource, "gfx900", "global_atomic_fmin"), Set("waits-valu", "vmem", "fp-atomic"))
      assertEquals(traitsOf(resource, "gfx900", "global_atomic_add"), Set("waits-valu", "vmem"))
    }
  }

  test("processors with equal schedules share a table") {
    withDump { dump =>
      val resource = ujson.read(render(dump).resource)
      assertEquals(resource("processors")("gfx1031"), resource("processors")("gfx1030"))
      assert(resource("processors")("gfx900").num != resource("processors")("gfx1030").num)
      assertEquals(entries(resource, "gfx1030").obj.keys.toSet, Set("s_add_u32", "v_mov_b32", "s_two_writes"))
      assertEquals(entries(resource, "gfx900")("v_variant_op"), ujson.Arr(2, 1, "WriteSALU", ujson.Arr()))
      assertEquals(entries(resource, "generic"), ujson.Obj())
    }
  }

  test("lds forms are keyed apart from the plain form") {
    withDump { dump =>
      val resource = ujson.read(render(dump).resource)
      assertEquals(entries(resource, "gfx900")("flat_load_dword"), ujson.Arr(300, 4, "WriteVMEM", ujson.Arr()))
      assertEquals(ldsEntries(resource, "gfx900")("flat_load_dword"), ujson.Arr(2, 1, "WriteSALU", ujson.Arr()))
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
      assertEquals(
        entries(resource, "gfx900").obj.get("buffer_store_lds_dword"),
        Some(ujson.Arr(2, 1, "WriteSALU", ujson.Arr()))
      )
      assertEquals(ldsEntries(resource, "gfx900").obj.keys.toSet, Set.empty[String])
    }
  }

  test("a suffixed record joins its base mnemonic where the bare record is unavailable") {
    val text = dumpOf(
      instruction("V_CMPX_EQ_U32_sdst", "v_cmpx_eq_u32$sdst, $src0, $src1", 8, Seq("WriteVALU"), Seq("HasWave64")),
      instruction("V_CMPX_EQ_U32_e32", "v_cmpx_eq_u32_e32 $src0, $src1", 4, Seq("WriteVALU"), Seq("HasGfx10")),
      definition("WriteVALU", "SchedWrite"),
      definition("VALU", "ProcResource", "int NumUnits = 2;"),
      writeRes("WriteRes_SI_VALU", "SIModel", "WriteVALU", Seq("VALU"), Seq(3), 4),
      writeRes("WriteRes_GFX10_VALU", "GFX10Model", "WriteVALU", Seq("VALU"), Seq(2), 3),
      definition(
        "HasWave64",
        "Predicate",
        "bit AssemblerMatcherPredicate = 1;",
        "dag AssemblerCondDag = (all_of FeatureWave64);"
      ),
      definition(
        "HasGfx10",
        "Predicate",
        "bit AssemblerMatcherPredicate = 1;",
        "dag AssemblerCondDag = (all_of FeatureGfx10);"
      ),
      definition("FeatureWave64", "SubtargetFeature", "list<SubtargetFeature> Implies = [];"),
      definition("FeatureGfx9", "SubtargetFeature", "list<SubtargetFeature> Implies = [FeatureWave64];"),
      definition("FeatureGfx10", "SubtargetFeature", "list<SubtargetFeature> Implies = [];"),
      processor("gfx900", "SIModel", "FeatureGfx9"),
      processor("gfx1030", "GFX10Model", "FeatureGfx10")
    )
    withText(text) { dump =>
      val rendered = GenerateAMDGPUSched.render(
        dump,
        Vector("v_cmpx_eq_u32"),
        Vector.empty,
        References.LlvmVersion,
        None
      )
      val resource = ujson.read(rendered.resource)
      assertEquals(entries(resource, "gfx900")("v_cmpx_eq_u32"), ujson.Arr(4, 2, "WriteVALU", ujson.Arr()))
      assertEquals(entries(resource, "gfx1030")("v_cmpx_eq_u32"), ujson.Arr(3, 1, "WriteVALU", ujson.Arr()))
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
