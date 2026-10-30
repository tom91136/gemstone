package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class ModelSuite extends Suite {
  private def entry(model: Model, target: Option[String], mnemonic: String): View.Entry =
    model.entries(target).find(_.mnemonic == mnemonic).getOrElse(fail(s"missing $mnemonic"))

  private def fixedValue(encoding: View.Encoding, positions: Vec[Int]): BigInt =
    positions.zipWithIndex.foldLeft(BigInt(0)) { case (value, (position, bit)) =>
      if (encoding.fixed.getOrElse(position, fail(s"bit $position is not fixed"))) value.setBit(bit) else value
    }

  test("every ISA has a catalogue and register files for its default target") {
    Language.values.toVec.foreach { language =>
      val model = language.model
      assert(model.entries(model.defaultTarget).nonEmpty, language)
      assert(model.registers(model.defaultTarget).nonEmpty, language)
    }
  }

  private def described(model: Model, target: Option[String], mnemonic: String): Vec[String] = {
    val found = entry(model, target, mnemonic)
    found.forms.flatMap(found.language.model.describe(found, _)).map(value => ScalaBackend.show(value.definition))
  }

  test("SPIR-V operands state the constraint the table places on them") {
    assertEquals(
      entry(SPIRVModel, None, "OpTypeVector").forms.head.operands.map(_.detail),
      Vec("Id", "Id, must be a scalar type", "Literal, must be one of 2, 3, 4, 8, 16")
    )
    val scope = entry(SPIRVModel, None, "OpControlBarrier").forms.head.operands.head
    assertEquals(scope.domain.map(_.summary), Some("Scope"))
    assert(scope.domain.exists(_.values.contains("2" -> "Workgroup")), scope.domain)
  }

  test("SPIR-V forms show a definition per type under the grammar's operand names") {
    assertEquals(
      entry(SPIRVModel, None, "OpIAdd").forms.head.operands.map(_.name),
      Vec("resultType", "dest", "srcA", "srcB")
    )
    val add = entry(SPIRVModel, None, "OpIAdd")
    assertEquals(
      add.forms.flatMap(add.language.model.describe(add, _)).map(_.instance),
      Vec(8, 16, 32, 64).map(width => s"OpIAdd(uint$width, uint$width): uint$width") :+
        "OpIAdd(uint32[2], uint32[2]): uint32[2]"
    )
    val added = described(SPIRVModel, None, "OpIAdd")
    assert(
      added.contains("def OpIAdd(srcA: UInt[32], srcB: UInt[32]): (dest: UInt[32]) =\n  (dest = srcA + srcB)"),
      added
    )
    val less = described(SPIRVModel, None, "OpULessThan")
    assert(
      less.contains("def OpULessThan(srcA: UInt[64], srcB: UInt[64]): (dest: Bool) =\n  (dest = srcA < srcB)"),
      less
    )
    val sum = described(SPIRVModel, None, "OpFAdd")
    assert(
      sum.contains(
        "def OpFAdd(srcA: Float32, srcB: Float32): (dest: Float32) =\n  (dest = FloatingSemantics.add(srcA, srcB))"
      ),
      sum
    )
    val select = described(SPIRVModel, None, "OpSelect")
    assert(
      select.contains(
        "def OpSelect(pred: Bool, object1: UInt[32], object2: UInt[32]): (dest: UInt[32]) =\n  (dest = if (pred) object1 else object2)"
      ),
      select
    )
    val sine = described(SPIRVModel, None, "OpenCL.std sin")
    assert(
      sine.contains(
        "def sin(x: Float32): (dest: Float32) =\n  (dest = Elementary.narrow32(Elementary.sin(Elementary.widen32(x))))"
      ),
      sine
    )
    val dot = described(SPIRVModel, None, "OpFDot4MixAcc32VALVE")
    assertEquals(dot.size, 4)
    assert(
      dot.exists(_.contains("FloatingSemantics.multiply(Elementary.widenE4M3(vector1_0), Elementary.widenE5M2(")),
      dot
    )
    val atomic = described(SPIRVModel, None, "OpAtomicIAdd")
    assert(
      atomic.contains(
        """def OpAtomicIAdd(pointer: UInt[64], value: UInt[32])(using memory: SPIRVMemory): (dest: UInt[32]) = {
          |  val v0 = memory.atomicLoad[UInt[32]](pointer)
          |  memory.atomicStore[UInt[32]](pointer, v0 + value)
          |  (dest = v0)
          |}""".stripMargin
      ),
      atomic
    )
    assertEquals(described(SPIRVModel, None, "OpStore"), Vec.empty[String])
  }

  test("SPIR-V control flow is machine semantics, and declarations and no-ops come from the grammar's class") {
    def semantics(mnemonic: String): Semantics = {
      val found = entry(SPIRVModel, None, mnemonic)
      SPIRVModel.semanticsOf(found, found.forms.head)
    }
    semantics("OpBranchConditional") match {
      case Semantics.Machine(definitions) =>
        assertEquals(
          definitions.map(value => (value.instance, ScalaBackend.show(value.definition))),
          Vec(
            (
              "OpBranchConditional(bool)",
              """def OpBranchConditional(condition: Bool)(using machine: Machine): Unit = {
                |  machine.branch(condition, 2, 3)
                |  ()
                |}""".stripMargin
            )
          )
        )
      case other => fail(other.toString)
    }
    Vec("OpLabel", "OpPhi", "OpFunctionCall", "OpReturn", "OpKill", "OpUnreachable", "OpAbortKHR").foreach { mnemonic =>
      semantics(mnemonic) match {
        case Semantics.Machine(_) => ()
        case other                => fail(s"$mnemonic: $other")
      }
    }
    assertEquals(
      semantics("OpTypeInt"),
      Semantics.LoadTime("Read as the module loads, as the grammar's Type-Declaration class.")
    )
    assertEquals(
      semantics("OpFunction"),
      Semantics.LoadTime("Read as the module loads, as the grammar's Function class.")
    )
    assertEquals(
      semantics("OpLine"),
      Semantics.LoadTime("Read as the module loads, as the grammar's Debug class.")
    )
    assertEquals(semantics("OpNop"), Semantics.NoOp("OpNop takes no operands and has no result."))
    semantics("OpControlBarrier") match {
      case Semantics.Machine(_) => ()
      case other                => fail(other.toString)
    }
  }

  test("SPIR-V reasons come from the grammar's requirements and the VM's lowering of each instance") {
    def unavailable(mnemonic: String): Unavailable = {
      val found = entry(SPIRVModel, None, mnemonic)
      SPIRVModel.unavailable(found, found.forms.head)
    }
    val saturated = described(SPIRVModel, None, "OpSatConvertSToU")
    assert(saturated.exists(_.startsWith("def OpSatConvertSToU(signedValue: SInt[32]): (dest: UInt[8]) =")), saturated)
    assertEquals(
      unavailable("OpStore"),
      Unavailable.Mechanics(
        "Lowers to the VM's variables, loads, stores and pointer operations, which have no staged semantics."
      )
    )
    assertEquals(
      unavailable("OpTypeInt"),
      Unavailable.Mechanics("Read as the module loads, as the grammar's Type-Declaration class.")
    )
    assertEquals(
      unavailable("OpTraceRayKHR"),
      Unavailable.NotModelled("Requires RayTracingKHR: ray tracing.")
    )
    assertEquals(
      unavailable("GLSL.std.450 InterpolateAtCentroid"),
      Unavailable.NotModelled("Requires InterpolationFunction: graphics stages.")
    )
    val product = described(SPIRVModel, None, "OpCooperativeMatrixMulAddKHR")
    assert(product.exists(_.startsWith("def OpCooperativeMatrixMulAddKHR(a_0_0: Float16")), product)
    assertEquals(
      unavailable("OpTypeCooperativeMatrixKHR"),
      Unavailable.Mechanics("Read as the module loads, as the grammar's Type-Declaration class.")
    )
    assertEquals(
      unavailable("OpCooperativeMatrixLoadKHR"),
      Unavailable.Mechanics(
        "Lowers to the VM's cooperative matrix loads, stores and products, which have no staged semantics."
      )
    )
    assertEquals(
      unavailable("OpAsmINTEL"),
      Unavailable.NotModelled("Requires AsmINTEL: inline assembly in the target's native ISA.")
    )
    assertEquals(
      unavailable("OpReadClockKHR"),
      Unavailable.Mechanics("Lowers to the VM's clock reads, which have no staged semantics.")
    )
    assertEquals(
      unavailable("GLSL.std.450 IMix"),
      Unavailable.NotModelled(
        "Removed from GLSL.std.450 before its first release, with no defined semantics."
      )
    )
    assertEquals(
      unavailable("OpenCL.std printf"),
      Unavailable.Mechanics("Lowers to the VM's extended instructions, which have no staged semantics.")
    )
    val unclassified = SPIRVModel.all.filter(found =>
      found.forms.exists(form =>
        SPIRVModel.describe(found, form).isEmpty &&
          SPIRVModel.unavailable(found, form) == Unavailable.Unclassified
      )
    )
    assertEquals(unclassified.map(_.mnemonic), Vec.empty[String])
  }

  test("AMDGPU forms define what an instance does to a lane under the operand table's names") {
    val add = described(AMDGPUModel, Some("gfx906"), "s_add_u32")
    assert(
      add.contains(
        """def s_add_u32(SSRC0: UInt[32], SSRC1: UInt[32]): (SDST: UInt[32], SSRC_SPECIAL_SCC: Bool) =
          |  (
          |    SDST = SSRC0 + SSRC1,
          |    SSRC_SPECIAL_SCC = SSRC0.to[UInt[64]] + SSRC1.to[UInt[64]] > 0xffffffffL
          |  )""".stripMargin
      ),
      add
    )
    val literal = described(AMDGPUModel, Some("gfx1030"), "s_add_u32")
    assert(literal.exists(_.startsWith("def s_add_u32(SSRC0: UInt[32], LITERAL: UInt[32])")), literal)
    val select = described(AMDGPUModel, Some("gfx1030"), "v_cndmask_b32")
    assert(
      select.contains(
        "def v_cndmask_b32(SRC0: UInt[32], VSRC1: UInt[32], VCC: UInt[32])(using wave: Wave): (VDST: UInt[32]) =\n" +
          "  (VDST = if ((VCC >> wave.lane()).to[UInt[1]] != UInt[1](0)) VSRC1 else SRC0)"
      ),
      select
    )
    val pair = described(AMDGPUModel, Some("gfx906"), "v_add_f64")
    assert(
      pair.contains(
        "def v_add_f64(SRC0: UInt[64], SRC1: UInt[64]): (VDST: UInt[64]) =\n" +
          "  (VDST = Floats.add(SRC0.asFloat, SRC1.asFloat).bits)"
      ),
      pair
    )
    val compare = described(AMDGPUModel, Some("gfx906"), "v_cmp_lt_u32")
    assert(
      compare.contains(
        "def v_cmp_lt_u32(SRC0: UInt[32], VSRC1: UInt[32])(using wave: Wave): (VCC: UInt[64]) =\n" +
          "  (VCC = wave.ballot(SRC0 < VSRC1))"
      ),
      compare
    )
    val load = described(AMDGPUModel, Some("gfx906"), "global_load_dword")
    assert(
      load.contains(
        "def global_load_dword(ADDR: UInt[64])(using global: GlobalMemory): (VDST: UInt[32]) =\n" +
          "  (VDST = global.load32(ADDR + 0))"
      ),
      load
    )
  }

  test("AMDGPU scalar control flow is machine semantics and other mechanics stay unavailable") {
    def semantics(target: String, mnemonic: String): Semantics = {
      val found = entry(AMDGPUModel, Some(target), mnemonic)
      AMDGPUModel.semanticsOf(found, found.forms.head)
    }
    semantics("gfx1030", "s_cbranch_scc1") match {
      case Semantics.Machine(definitions) =>
        assertEquals(
          definitions.map(value => ScalaBackend.show(value.definition)),
          Vec(
            """def s_cbranch_scc1(SIMM16: SInt[16], SSRC_SPECIAL_SCC: Bool)(using machine: Machine): Unit = {
              |  machine.branch(SSRC_SPECIAL_SCC, machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
              |  ()
              |}""".stripMargin
          )
        )
      case other => fail(s"$other")
    }
    def isMachine(found: Semantics): Boolean = found match {
      case Semantics.Machine(_) => true
      case _                    => false
    }
    Vec("s_branch", "s_swappc_b64", "s_endpgm", "s_trap", "s_code_end", "s_waitcnt", "s_sleep").foreach(mnemonic =>
      assert(isMachine(semantics("gfx1030", mnemonic)), mnemonic)
    )
    Vec("s_cbranch_g_fork", "s_cbranch_i_fork", "s_cbranch_join").foreach(mnemonic =>
      assert(isMachine(semantics("gfx90a", mnemonic)), mnemonic)
    )
    Vec("s_movrels_b32", "s_movreld_b32", "s_subvector_loop_begin", "s_subvector_loop_end").foreach(mnemonic =>
      assert(isMachine(semantics("gfx1030", mnemonic)), mnemonic)
    )
    assert(semantics("gfx1030", "s_add_u32") match {
      case Semantics.Lane(_) => true
      case _                 => false
    })
  }

  test("AMDGPU buffer and image forms define their addressing and texel conversions over the descriptor operands") {
    val load = described(AMDGPUModel, Some("gfx906"), "buffer_load_dword")
    assert(
      load.exists(
        _.contains("BufferResource.valid(generation = ResourceGeneration.GFX9, `SRSRC+1`, `SRSRC+2`, `SRSRC+3`")
      ),
      load
    )
    val typed = described(AMDGPUModel, Some("gfx1030"), "tbuffer_load_format_x")
    assert(typed.exists(_.contains("TexelFormat.converted(layout = TexelLayout.")), typed)
    val atomic = described(AMDGPUModel, Some("gfx1030"), "buffer_atomic_csub")
    assert(atomic.exists(_.contains("global.store32(")), atomic)
    val image = described(AMDGPUModel, Some("gfx906"), "image_load")
    assert(image.exists(_.contains("images.texel(ISA.GFX9, SRSRC")), image)
    assert(
      image.exists(_.contains("ImageResource.component(generation = ResourceGeneration.GFX9, `SRSRC+1`, `SRSRC+3`")),
      image
    )
    val scalar = described(AMDGPUModel, Some("gfx1201"), "s_buffer_load_b32")
    assert(scalar.exists(_.contains("BufferResource.scalarElements(generation = ResourceGeneration.GFX12")), scalar)
  }

  test("AMDGPU matrix forms take the broadcasts, formats, index sets and modifiers their shapes allow") {
    def endings(target: String, mnemonic: String, format: String, endings: String*): Unit = {
      val found = instances(target, mnemonic, format)
      endings.foreach(ending => assert(found.exists(_.endsWith(ending)), clues(ending, found)))
    }
    endings("gfx942", "v_mfma_f32_4x4x1_16b_f32", "VOP3P_MFMA", "cbsz:4 abid:15", "blgp:3", "a[4:7]")
    endings("gfx942", "v_mfma_f64_16x16x4_f64", "VOP3P_MFMA", "neg:[1,0,1]")
    endings("gfx942", "v_smfmac_f32_16x16x32_f16", "VOP3P_MFMA", "cbsz:1", "abid:3")
    endings("gfx950", "v_mfma_scale_f32_16x16x128_f8f6f4", "VOP3PX2", "v[4:9], v[12:19], v[20:23], v24, v25 cbsz:2")
    val Span                                          = raw"v\[(\d+):(\d+)\]".r
    def span(text: String, operand: Int): Option[Int] = text.split(", ").lift(operand).collect {
      case Span(first, last) => last.toInt - first.toInt + 1
    }
    val unscaled = instances("gfx950", "v_mfma_f32_16x16x128_f8f6f4", "VOP3P_MFMA")
    assert(unscaled.exists(text => text.endsWith("cbsz:4") && span(text, 1).contains(4)), unscaled)
    val sparse = instances("gfx942", "v_smfmac_f32_16x16x32_f16", "VOP3P_MFMA")
    assert(sparse.exists(_.startsWith("v_smfmac_f32_16x16x32_f16 a[")), sparse)
    endings("gfx1100", "v_wmma_f16_16x16x16_f16", "VOP3P", "op_sel:[0,0,1]", "neg_hi:[0,0,1]")
    endings("gfx1201", "v_swmmac_i32_16x16x32_iu8", "VOP3P", "neg_lo:[1,0,0]", "clamp", "index_key:1")
    val products = described(AMDGPUModel, Some("gfx1201"), "v_wmma_i32_16x16x16_iu8")
    assert(products.exists(_.contains("MatrixArithmetic.multiplyAdd(MatrixArithmetic.signed8(")), products)
    val matrixMnemonic = "v_s?(mfma|wmma|mfmac|wmmac)_.*".r
    for {
      target <- Vec("gfx908", "gfx1201")
      found  <- AMDGPUModel.entries(Some(target))
      if matrixMnemonic.matches(found.mnemonic) && found.mnemonic != "v_mfma_ld_scale_b32"
      form <- found.forms
    } assert(AMDGPUModel.describe(found, form).nonEmpty, clues(target, found.mnemonic, form.name))
    val prefix = entry(AMDGPUModel, Some("gfx950"), "v_mfma_ld_scale_b32")
    assertEquals(
      AMDGPUModel.unavailable(prefix, prefix.forms.head).summary,
      Unavailable.NotModelled("").summary
    )
  }

  private def instances(target: String, mnemonic: String, format: String): Vec[String] = {
    val found = entry(AMDGPUModel, Some(target), mnemonic)
    found.forms.filter(_.name.endsWith(s" $format"))
      .flatMap(AMDGPUModel.instances(found.target, found.mnemonic, _).map(_.text))
  }

  test("AMDGPU instances take each addressing mode, returned value and modifier value of their encoding") {
    assertEquals(
      instances("gfx90a", "buffer_load_dword", "MUBUF").take(4),
      Vec(
        "buffer_load_dword v0, off, s[0:3], s4",
        "buffer_load_dword v0, v2, s[0:3], s4 offen",
        "buffer_load_dword v0, v2, s[0:3], s4 idxen",
        "buffer_load_dword v0, v[2:3], s[0:3], s4 idxen offen"
      )
    )
    assert(instances("gfx90a", "buffer_load_dword", "MUBUF").contains("buffer_load_dword a0, off, s[0:3], s4"))
    val atomic = instances("gfx906", "global_atomic_add", "FLAT_GLBL")
    assert(
      Vec(
        "global_atomic_add v[2:3], v4, off",
        "global_atomic_add v0, v[2:3], v4, off glc",
        "global_atomic_add v2, v4, s[0:1]",
        "global_atomic_add v0, v2, v4, s[0:1] glc",
        "global_atomic_add v[2:3], v4, off offset:-16"
      ).forall(atomic.toSet),
      atomic
    )
    assert(instances("gfx906", "scratch_load_dword", "FLAT_SCRATCH").contains("scratch_load_dword v0, off, s0"))
    assert(instances("gfx906", "ds_read2_b32", "DS").contains("ds_read2_b32 v[0:1], v2 offset0:1 offset1:2"))
    assert(instances("gfx906", "s_load_dword", "SMEM").contains("s_load_dword s0, s[2:3], s20"))
    assertEquals(
      instances("gfx1030", "v_mov_b32", "VOP1_VOP_DPP16").count(!_.contains("_mask:")),
      // quad_perm, row_shl, row_shr, row_ror, row_mirror, row_half_mirror, row_share and row_xmask, then
      // bound_ctrl and fi
      8 + 2
    )
    assert(instances("gfx906", "v_add_u32", "VOP2_VOP_SDWA").contains(
      "v_add_u32_sdwa v0, v1, v2 dst_sel:WORD_0 dst_unused:UNUSED_SEXT"
    ))
    assert(instances("gfx1100", "v_dual_add_nc_u32", "VOPDXY").forall(_.startsWith("v_dual_mov_b32 v7, v14 :: ")))
  }

  test("AMDGPU forms without a definition say why from the stage that stops their instances") {
    def reason(target: String, mnemonic: String, format: String): Unavailable = {
      val found = entry(AMDGPUModel, Some(target), mnemonic)
      AMDGPUModel.unavailable(found, found.forms.find(_.name.endsWith(s" $format")).getOrElse(fail(format)))
    }
    assertEquals(
      reason("gfx906", "s_endpgm", "SOPP"),
      Unavailable.Mechanics("Lowers to the end of the wave.")
    )
    // sampling and multisampled loads have no meaning to compute
    assertEquals(reason("gfx906", "image_sample", "MIMG").summary, Unavailable.NotModelled("").summary)
    assertEquals(reason("gfx1030", "image_msaa_load", "MIMG").summary, Unavailable.NotModelled("").summary)
    assertEquals(reason("gfx906", "v_interp_p1_f32", "VOP3").summary, Unavailable.NotModelled("").summary)
    assertEquals(
      reason("gfx1201", "ds_bvh_stack_push4_pop1_rtn_b32", "VDS"),
      Unavailable.NotModelled("ds_bvh_stack_push4_pop1_rtn_b32 belongs to the ray-tracing family.")
    )
    assertEquals(
      reason("gfx906", "v_screen_partition_4se_b32", "VOP3"),
      Unavailable.NotModelled(
        "It looks up the rasteriser's table of shader-engine masks to partition the screen across four shader " +
          "engines. A compute dispatch has no screen, and the ISA description gives its purpose but not the table."
      )
    )
    val relativeMoves = Vec("v_movrels_b32", "v_movreld_b32", "v_movrelsd_b32", "v_movrelsd_2_b32", "v_swaprel_b32")
    for {
      target   <- Vec("gfx1010", "gfx1030", "gfx1100", "gfx1151", "gfx1201")
      mnemonic <- relativeMoves
      found = entry(AMDGPUModel, Some(target), mnemonic)
      form <- found.forms
    } assert(AMDGPUModel.semantics(found, form).isRight, clues(target, mnemonic, form.name))
    // llvm-mc 22 -mcpu=gfx90a rejects either literal: "invalid operand for instruction"
    assertEquals(
      reason("gfx90a", "s_cbranch_g_fork", "SOP2_INST_LITERAL"),
      Unavailable.NotModelled(
        "The catalogue writes a literal for its SSRC_NOLIT operand, which takes no literal; LLVM rejects one there, " +
          "so no toolchain writes it."
      )
    )
    val gfx906 = AMDGPUModel.entries(Some("gfx906"))
    assert(gfx906.forall(found =>
      found.forms.forall(form => AMDGPUModel.semantics(found, form).left.forall(_ != Unavailable.Unclassified))
    ))
  }

  test("AMDGPU forms outside compute or toolchains are not modelled, and unspecified ones are not implemented") {
    def reason(target: String, mnemonic: String, format: String): Unavailable = {
      val found = entry(AMDGPUModel, Some(target), mnemonic)
      AMDGPUModel.unavailable(found, found.forms.find(_.name.endsWith(s" $format")).getOrElse(fail(format)))
    }
    def all(expected: Unavailable)(forms: (String, String, String)*): Unit =
      forms.foreach((target, mnemonic, format) =>
        assertEquals(reason(target, mnemonic, format).summary, expected.summary, clues(target, mnemonic))
      )
    all(Unavailable.NotModelled(""))(
      ("gfx906", "v_interp_p2_hi_f16", "VOP3"),
      ("gfx906", "s_endpgm_ordered_ps_done", "SOPP"),
      ("gfx942", "s_endpgm_ordered_ps_done", "SOPP"),
      ("gfx1100", "ds_add_gs_reg_rtn", "DS"),
      ("gfx1100", "ds_sub_gs_reg_rtn", "DS"),
      ("gfx701", "s_mov_fed_b32", "SOP1"),
      ("gfx1010", "s_mov_fed_b32", "SOP1"),
      ("gfx701", "ds_condxchg32_rtn_b128", "DS"),
      ("gfx803", "ds_condxchg32_rtn_b128", "DS")
    )
    all(Unavailable.NotImplemented(""))(
      ("gfx1100", "s_sendmsg_rtn_b32", "SOP1"),
      ("gfx1201", "s_sendmsg_rtn_b32", "SOP1"),
      ("gfx906", "s_scratch_load_dword", "SMEM"),
      ("gfx906", "s_scratch_store_dwordx4", "SMEM"),
      ("gfx1010", "s_scratch_load_dwordx2", "SMEM"),
      ("gfx1201", "global_load_block", "VGLOBAL"),
      ("gfx1201", "global_store_block", "VGLOBAL"),
      ("gfx1201", "scratch_load_block", "VSCRATCH"),
      ("gfx1201", "scratch_store_block", "VSCRATCH"),
      ("gfx1201", "global_atomic_ordered_add_b64", "VGLOBAL")
    )
    // aco_opcodes.py (Mesa 26.2) lists both, ACO selects neither and LLVM 23 defines neither
    assertEquals(
      reason("gfx1010", "s_mov_fed_b32", "SOP1"),
      Unavailable.NotModelled(
        "Only Mesa's ACO opcode table lists it, for GFX6, GFX7 and GFX10, and the RDNA ISA descriptions do not; ACO " +
          "never selects it and LLVM does not define it, so no toolchain writes it."
      )
    )
    assertEquals(
      reason("gfx803", "ds_condxchg32_rtn_b128", "DS"),
      Unavailable.NotModelled(
        "Only Mesa's ACO opcode table lists it, for GFX7 and GFX8; ACO never selects it and LLVM does not define " +
          "it, so no toolchain writes it."
      )
    )
    assertEquals(
      reason("gfx942", "v_mov_b64", "VOP1_VOP_SDWA"),
      Unavailable.NotModelled(
        "LLVM defines no SDWA variant of v_mov_b64, as SDWA selects bytes and words within a dword and the move " +
          "takes a register pair, so no toolchain writes it."
      )
    )
    assertEquals(
      reason("gfx1100", "s_sendmsg_rtn_b64", "SOP1"),
      Unavailable.Mechanics("Lowers to a read of the real-time clock.")
    )
    assertEquals(
      reason("gfx1030", "s_subvector_loop_begin", "SOPK"),
      Unavailable.Mechanics(
        "Lowers to a loop over the halves of a wave64's lanes, which runs its body once for each half that has " +
          "active lanes."
      )
    )
    assertEquals(
      reason("gfx1010", "s_get_waveid_in_workgroup", "SMEM"),
      Unavailable.Mechanics("Lowers to a read of the wave's index among its workgroup's waves.")
    )
  }

  test("AMDGPU image instances take as many addresses as their dimension needs, in each address encoding") {
    // each spelling assembles with llvm-mc 22 for the target to the bytes the project's assembler writes
    assert(instances("gfx1030", "image_load_mip", "MIMG").contains(
      "image_load_mip v[0:3], v[4:5], s[0:7] dmask:0xf dim:SQ_RSRC_IMG_1D"
    ))
    assert(instances("gfx1030", "image_load_mip", "MIMG_NSA1").contains(
      "image_load_mip v[0:3], [v4, v20], s[0:7] dmask:0xf dim:SQ_RSRC_IMG_1D"
    ))
    assert(instances("gfx1030", "image_atomic_cmpswap", "MIMG").contains(
      "image_atomic_cmpswap v[0:1], v4, s[0:7] dmask:0x3 dim:SQ_RSRC_IMG_1D"
    ))
    assert(instances("gfx1201", "image_load_mip", "VIMAGE").contains(
      "image_load_mip v[0:3], [v4, v5], s[0:7] dmask:0xf dim:SQ_RSRC_IMG_1D"
    ))
    assert(instances("gfx1170", "image_load_mip", "MIMG").exists(_.endsWith("dim:SQ_RSRC_IMG_1D")))
    Vec("gfx1030" -> "image_load_mip", "gfx1100" -> "image_store_mip", "gfx1201" -> "image_atomic_add_uint").foreach {
      (target, mnemonic) =>
        assert(described(AMDGPUModel, Some(target), mnemonic).nonEmpty, clues(target, mnemonic))
    }
  }

  test("AMDGPU forms no toolchain writes say so from their encoding or catalogue record") {
    def reason(target: String, mnemonic: String, format: String): Unavailable = {
      val found = entry(AMDGPUModel, Some(target), mnemonic)
      AMDGPUModel.unavailable(found, found.forms.find(_.name.endsWith(s" $format")).getOrElse(fail(format)))
    }
    assertEquals(
      reason("gfx1030", "image_load", "MIMG_NSA2"),
      Unavailable.NotModelled(
        "MIMG_NSA2 lists 6 to 9 addresses in 2 extra dwords, and image_load takes 1 to 4, so no toolchain writes it."
      )
    )
    assertEquals(
      reason("gfx1100", "image_get_resinfo", "MIMG_NSA1"),
      Unavailable.NotModelled(
        "MIMG_NSA1 lists 2 to 5 addresses in 1 extra dword, and image_get_resinfo takes 1, so no toolchain writes it."
      )
    )
    assertEquals(
      reason("gfx1030", "image_load_by2", "MIMG"),
      Unavailable.NotModelled(
        "No LLVM target defines image_load_by2, so no toolchain emits it and the assembler cannot shape it."
      )
    )
    assertEquals(
      reason("gfx1201", "image_sample", "VSAMPLE"),
      Unavailable.NotModelled("VSAMPLE encodes graphics: sampling, multisampling, exports or interpolation.")
    )
    assertEquals(
      reason("gfx1170", "global_load_dword", "FLAT_GLBL"),
      Unavailable.NotModelled(
        "The GFX11_7 catalogue lists global_load_dword in FLAT_GLBL without operands, as another name for " +
          "global_load_b32 in FLAT_GLOBAL, which is classified there."
      )
    )
    assertEquals(
      reason("gfx1170", "v_dual_add_f32", "VOPD"),
      Unavailable.NotModelled(
        "The GFX11_7 catalogue lists v_dual_add_f32 in VOPD without operands; its VOPDXY encoding names them and is " +
          "classified there."
      )
    )
    assert(reason("gfx1170", "s_subvector_loop_begin", "SOPK").summary == Unavailable.NotModelled("").summary)
    // llvm-mc 22 -mcpu=gfx950: v_accvgpr_mov_b32 a0, 0x1234 is "invalid operand for instruction"
    assertEquals(
      reason("gfx950", "v_accvgpr_mov_b32", "VOP1_INST_LITERAL"),
      Unavailable.NotModelled(
        "The catalogue writes a literal for its SRC_ACCVGPR operand, which takes only AGPRs; LLVM rejects one there, " +
          "so no toolchain writes it."
      )
    )
    assert(reason("gfx1250", "tensor_load_to_lds", "VIMAGE") match {
      case Unavailable.NotImplemented(detail) => detail.contains("tensor data mover")
      case _                                  => false
    })
  }

  test("gfx1250 named barriers run as barrier mechanics, which RDNA4 does not have") {
    def reason(target: String, mnemonic: String, format: String): Unavailable = {
      val found = entry(AMDGPUModel, Some(target), mnemonic)
      AMDGPUModel.unavailable(found, found.forms.find(_.name.endsWith(s" $format")).getOrElse(fail(format)))
    }
    Vec("s_barrier_init" -> "SOP1", "s_barrier_join" -> "SOP1", "s_barrier_leave" -> "SOPP").foreach {
      (mnemonic, format) =>
        assertEquals(
          reason("gfx1250", mnemonic, format),
          Unavailable.Mechanics("Lowers to a named barrier's set-up."),
          mnemonic
        )
        assert(reason("gfx1201", mnemonic, format) match {
          case Unavailable.NotModelled(detail) => detail.contains("GFX12.5")
          case _                               => false
        })
    }
  }

  test("gfx12 and gfx1250 instances spell scalar operands, flat addresses and dual-issue partners as LLVM does") {
    assert(instances("gfx1201", "s_getreg_b32", "SOPK").contains("s_getreg_b32 s0, hwreg(HW_REG_WAVE_MODE)"))
    assert(instances("gfx1201", "s_barrier_join", "SOP1").contains("s_barrier_join m0"))
    val flat = instances("gfx1250", "flat_load_b32", "VFLAT")
    assert(flat.contains("flat_load_b32 v0, v[2:3]") && flat.contains("flat_load_b32 v0, v2, s[0:1]"), flat)
    assert(instances("gfx1250", "flat_atomic_add_u32", "VFLAT").contains(
      "flat_atomic_add_u32 v0, v2, v4, s[0:1] th:TH_ATOMIC_RETURN"
    ))
    val load = described(AMDGPUModel, Some("gfx1250"), "flat_load_b32")
    assert(load.exists(_.contains("flat.load32(SADDR + VADDR.to[UInt[64]]")), load)
    assert(described(AMDGPUModel, Some("gfx1250"), "v_dual_add_f32").nonEmpty)
    val dual  = entry(AMDGPUModel, Some("gfx1250"), "v_dual_cndmask_b32")
    val vopd3 = dual.forms.filter(_.name.endsWith(" VOPD3XY"))
    assert(
      vopd3.flatMap(AMDGPUModel.instances(dual.target, dual.mnemonic, _).map(_.text))
        .contains("v_dual_cndmask_b32 v0, v1, v2, s0 :: v_dual_fma_f32 v7, v14, v15, v16")
    )
    vopd3.foreach(form => assert(AMDGPUModel.describe(dual, form).nonEmpty, form.name))
  }

  test("PTX forms define what each type lowers to over the operand table's names at the types they take") {
    val add = described(PTXModel, None, "add")
    assert(add.contains("def add(srcA: SInt[32], srcB: SInt[32]): (dest: SInt[32]) =\n  (dest = srcA + srcB)"), add)
    val selp = described(PTXModel, None, "selp")
    assertEquals(entry(PTXModel, None, "selp").forms.head.operands.map(_.name), Vec("dest", "srcA", "srcB", "srcC"))
    assert(
      selp.contains(
        "def selp(srcA: UInt[32], srcB: UInt[32], srcC: Bool): (dest: UInt[32]) =\n  (dest = if (srcC) srcA else srcB)"
      ),
      selp
    )
    val carry = described(PTXModel, None, "addc")
    assert(
      carry.contains(
        "def addc(srcA: UInt[32], srcB: UInt[32], `CC.CF`: Bool): (dest: UInt[32]) =\n" +
          "  (dest = srcA + srcB + `CC.CF`.to[UInt[1]].to[UInt[32]])"
      ),
      carry
    )
  }

  test("SASS forms define what an instance lowers to on the selected target over the operand table's names") {
    val iadd3 = described(SASSModel, Some("sm_89"), "IADD3")
    assert(
      iadd3.contains("def IADD3(Ra: UInt[32], Rb: UInt[32], Rc: UInt[32]): (Rd: UInt[32]) =\n  (Rd = Ra + Rb + Rc)"),
      iadd3
    )
    assert(
      iadd3.contains(
        """def IADD3(Ra: UInt[32], Sb_addr: SInt[17] in 0 ~ 0xfffc, Rc: UInt[32])(using constants: Constants): (Rd: UInt[32]) = {
          |  require(Sb_addr.to[UInt[2]] == UInt[2](0))
          |  (Rd = Ra + constants.load[UInt[32]](UInt[32](3), Sb_addr.to[UInt[32]]) + Rc)
          |}""".stripMargin
      ),
      iadd3
    )
    val sel = described(SASSModel, Some("sm_89"), "SEL")
    assert(
      sel.contains("def SEL(Ra: UInt[32], Rb: UInt[32], Pp: Bool): (Rd: UInt[32]) =\n  (Rd = if (Pp) Ra else Rb)"),
      sel
    )
    val selOperands = Vec("Rd", "Ra", "Rb", "Pp")
    assert(
      entry(
        SASSModel,
        Some("sm_89"),
        "SEL"
      ).forms.exists(_.operands.map(_.name).containsSlice(selOperands))
    )
    val dadd = described(SASSModel, Some("sm_89"), "DADD")
    assert(
      dadd.contains(
        "def DADD(Ra: UInt[64], Rc: UInt[64]): (Rd: UInt[64]) =\n  (Rd = FloatArithmetic.add(Ra.asFloat, Rc.asFloat).bits)"
      ),
      dadd
    )
  }

  test("SASS forms instantiate with a selector on an immediate and are classified by what the VM and tables say") {
    val scaled = SASSModel.instances(Some("sm_100"), "F2FP", "f2fp_8b_upconvert_scale__RRI")
    assert(scaled.exists(_.exists(_.text == "F2FP.BF16.E5M2.UNPACK_B R0, R4, 1.H0")), scaled)
    def unavailable(target: String, mnemonic: String, form: String): Unavailable = {
      val found = entry(SASSModel, Some(target), mnemonic)
      SASSModel.unavailable(found, found.forms.find(_.name == form).getOrElse(fail(s"missing $form")))
    }
    assertEquals(unavailable("sm_100", "F2FP", "f2fp_8b_upconvert_scale__RRI").summary, "Not implemented in the VM.")
    val unexposed = Unavailable.NotModelled("No public PTX instruction lowers to it, so no toolchain emits it.")
    assertEquals(unavailable("sm_35", "VSEL2", "VSEL2"), unexposed)
    assertEquals(unavailable("sm_35", "VSEL4", "Imm VSEL4"), unexposed)
    assertEquals(unavailable("sm_80", "HMMA", "hmma_x8_indexedRF_"), unexposed)
    assertEquals(unavailable("sm_90", "HMMA", "hmma_sparse_indexedRF_"), unexposed)
    // the encoding tables allow .PQUAD only on MACTIVE, so the barrier-to-barrier form has only the plain move
    assertEquals(
      SASSModel.instances(Some("sm_89"), "BMOV", "bmov_pquad_bar__RBR").map(_.map(_.text)),
      Right(Vec("BMOV.32 B1, B10"))
    )
    val barrierMove = entry(SASSModel, Some("sm_89"), "BMOV")
    val barrierForm = barrierMove.forms.find(_.name == "bmov_pquad_bar__RBR").getOrElse(fail("no form"))
    SASSModel.semanticsOf(barrierMove, barrierForm) match {
      case Semantics.Machine(definitions) =>
        assertEquals(
          definitions.map(value => ScalaBackend.show(value.definition)),
          Vec("def BMOV(B10: UInt[64]): (B1: UInt[64]) =\n  (B1 = B10.to[UInt[32]].to[UInt[64]])")
        )
      case other => fail(other.toString)
    }
    val uniform = described(SASSModel, Some("sm_89"), "R2UR")
    assert(uniform.exists(_.startsWith("def R2UR(Pu: Bool, Ra: UInt[32])(using warp: Warp)")), uniform)
  }

  test("SASS forms carry their opcode bits") {
    val form     = entry(SASSModel, Some("sm_90"), "IADD3").forms.find(_.name == "iadd3_noimm__RRR_RRR").get
    val encoding = form.encoding.getOrElse(fail("no encoding"))
    val opcode   = encoding.fields.find(_.name == "Opcode").getOrElse(fail("no opcode field"))
    assertEquals(encoding.width, 128)
    assertEquals(fixedValue(encoding, opcode.positions), BigInt(0x210))
    assert(form.operands.map(_.name).containsSlice(Vec("Ra", "Rb", "Rc")), form.operands)
  }

  test("AMDGPU layouts fix the opcode field of each encoding") {
    val form     = entry(AMDGPUModel, Some("gfx1030"), "v_add_f32").forms.find(_.name == "RDNA2 VOP2").get
    val encoding = form.encoding.getOrElse(fail("no encoding"))
    val opcode   = encoding.fields.find(_.name == "OP").getOrElse(fail("no OP field"))
    assertEquals(fixedValue(encoding, opcode.positions), BigInt(3))
    assertEquals(form.operands.map(_.name), Vec("VDST", "SRC0", "VSRC1"))
  }

  test("AMDGPU encodings mark the operand slots their opcode does not read") {
    def field(mnemonic: String, name: String): View.Field = {
      val encoding = entry(AMDGPUModel, Some("gfx906"), mnemonic).forms.head.encoding.getOrElse(fail("no encoding"))
      encoding.fields.find(_.name == name).getOrElse(fail(s"no $name field in $mnemonic"))
    }
    assert(field("s_getpc_b64", "SSRC0").ignored)
    assert(!field("s_getpc_b64", "SDST").ignored)
    assert(!field("s_mov_b32", "SSRC0").ignored)
    assert(!field("s_mov_b32", "SDST").ignored)
    val literal = entry(AMDGPUModel, Some("gfx1030"), "v_add_f32").forms.find(_.name == "RDNA2 VOP2_INST_LITERAL")
    assert(literal.flatMap(_.encoding).flatMap(_.fields.find(_.name == "SRC0")).exists(!_.ignored))
  }

  test("AMDGPU encodings and operands list the values each field takes") {
    def form(target: String, mnemonic: String): View.Form = entry(AMDGPUModel, Some(target), mnemonic).forms.head
    def field(form: View.Form, name: String): View.Field  =
      form.encoding.flatMap(_.fields.find(_.name == name)).getOrElse(fail(s"no $name field in ${form.name}"))
    val getpc = form("gfx906", "s_getpc_b64")
    assertEquals(field(getpc, "SSRC0").domain, None)
    assertEquals(
      field(getpc, "SDST").domain.map(_.summary),
      Some("s0-s101, flat_scratch_lo/hi, xnack_mask_lo/hi, vcc_lo/hi, ttmp0-15, m0, exec_lo/hi")
    )
    val move   = form("gfx1030", "s_mov_b32")
    val source = field(move, "SSRC0").domain.getOrElse(fail("no domain for SSRC0"))
    assert(source.summary.startsWith("s0-s105, vcc_lo/hi, ttmp0-15, m0, null, exec_lo/hi, inline -16..64"))
    assert(source.summary.endsWith("9 float constants, src_vccz, src_execz, src_scc, literal"))
    assert(source.values.contains("128-192" -> "inline 0..64"))
    assert(source.values.contains("193-208" -> "inline -1..-16"))
    assert(source.values.contains("108-123" -> "ttmp0-15"))
    assertEquals(move.operands.find(_.name == "SSRC0").flatMap(_.domain), Some(source))
    val immediate = form("gfx1030", "s_movk_i32")
    assertEquals(
      field(immediate, "SIMM16").domain.map(_.summary),
      Some("16-bit signed integer -32768..32767")
    )
    assertEquals(
      immediate.operands.find(_.name == "SIMM16").flatMap(_.domain).map(_.values),
      Some(Vec("field" -> "16-bit signed integer -32768..32767"))
    )
  }

  test("PTX targets narrow the catalogue and operands keep their model names") {
    assert(PTXModel.entries(Some("sm_50")).size < PTXModel.entries(None).size)
    assertEquals(entry(PTXModel, None, "add").forms.head.operands.map(_.name), Vec("dest", "srcA", "srcB"))
  }

  test("entries of one mnemonic differ where a target narrows their forms") {
    def of(names: Vec[String]) = View.Entry(Language.PTX, "cp.async.bulk", names, () => Vec.empty)
    assertEquals(of(Vec("a 1")), of(Vec("a 1")))
    assertNotEquals(of(Vec("a 1")), of(Vec("a 1", "b 1")))
  }

  test("an entry names the forms it builds") {
    Language.values.toVec.foreach { language =>
      language.model.entries(language.model.defaultTarget).foreach(found =>
        assertEquals(found.formNames, found.forms.map(_.name), s"$language ${found.mnemonic}")
      )
    }
  }

  test("SPIR-V forms fix the opcode half of the first word") {
    val encoding = entry(SPIRVModel, None, "OpLoad").forms.head.encoding.getOrElse(fail("no encoding"))
    assertEquals(fixedValue(encoding, Vec.tabulate(16)(identity)), BigInt(61))
  }

  test("binary ISAs assemble and disassemble in the browser build") {
    assert(SASSModel.disassemble(
      Hex.parse(SASSModel.binaryExample).toOption.get,
      Some("sm_90")
    ).exists(_.contains("EXIT;")))
    assertEquals(AMDGPUModel.assemble(AMDGPUModel.assemblyExample, Some("gfx1030")), Right("01 05 00 06 fe ff 05 b0"))
    assertEquals(
      AMDGPUModel.disassemble(Hex.parse(AMDGPUModel.binaryExample).toOption.get, Some("gfx1030")),
      Right("v_add_f32 v0, v1, v2\ns_movk_i32 s5, -2")
    )
    val spirv = SPIRVModel.assemble(SPIRVModel.assemblyExample, None).flatMap(Hex.parse)
    assert(spirv.flatMap(SPIRVModel.disassemble(_, None)).exists(_.contains("OpFunctionEnd")), spirv)
    assert(PTXModel.assemble(PTXModel.assemblyExample, None).exists(_.contains("mov.u32 %r0, %tid.x;")))
  }
}
