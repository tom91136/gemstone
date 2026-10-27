package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.either.*

class BinarySuite extends munit.FunSuite {
  private val HalfLiteral = "0.5".r

  test("validation shares symbolic operand checking while retaining incomplete processor availability") {
    val target = Target("gfx900")
    Vec("s_waitcnt vmcnt(0)", "s_branch destination", "s_nop 0").foreach { text =>
      val instruction = parse(text)
      Validation.validate(instruction, target) match {
        case ValidationOutcome.Incomplete(issues) =>
          assert(
            issues.forall {
              case Validation.Issue.Incomplete(message) => message.startsWith("exact processor availability")
              case _                                    => false
            },
            text
          )
        case other => fail(s"unexpected validation for $text: $other")
      }
    }
    val overflow = parse("s_nop 65536")
    Validation.validate(overflow, target) match {
      case ValidationOutcome.Rejected(_) => ()
      case other                         => fail(s"out-of-range immediate was not rejected: $other")
    }
  }

  test("image derivative variants use the metadata address count, including offset and clamp") {
    // LLVM gfx10_asm_mimg.s: cd supplies derivatives, without a depth-comparison argument.
    val source =
      "image_sample_cd_cl_o v[64:66], [v32, v16, v0, v2, v1, v4, v5, v6, v7, v8, v9], s[4:11], s[100:103] dmask:0x7 dim:SQ_RSRC_IMG_3D"
    val expected    = hex("16 07 b4 f1 20 40 21 03 10 00 02 01 04 05 06 07 08 09 00 00")
    val target      = Target("gfx1030")
    val instruction = parse(source)
    assertEquals(Binary.encode(instruction, TargetMode(target)), Right(expected))
    val decoded = disassembled(expected, target)
    assertEquals(decoded.operands, instruction.operands)
    assertEquals(Binary.encode(decoded, TargetMode(target)), Right(expected))
    val tooMany =
      parse(source.replace("v8, v9]", "v8, v9, v10]"))
    assert(Binary.encode(tooMany, TargetMode(target)).isLeft)
  }

  test("wave size controls matrix register tuples without changing their encoding") {
    // LLVM MC gfx12 SWMMAC wave32/wave64 encodings share bytes but operate on different tuples.
    val bytes = hex("06 40 53 cc 00 05 22 1c")
    val cases = Vec(
      (32, "v_swmmac_bf16_16x16x32_bf16 v[6:9], v[0:3], v[2:9], v8"),
      (64, "v_swmmac_bf16_16x16x32_bf16 v[6:7], v[0:1], v[2:5], v8")
    )
    cases.foreach { case (wave, source) =>
      val instruction = parse(source)
      val target      = Target("gfx1200")
      val waveSize    = wave.unsafe[Int in (32, 64)]
      assertEquals(Binary.encode(instruction, TargetMode(target, waveSize)), Right(bytes))
      val decoded = disassembled(bytes, target, waveSize)
      assertEquals(decoded.operands, instruction.operands)
      assertEquals(Binary.encode(decoded, TargetMode(target, waveSize)), Right(bytes))
      assert(Binary.encode(instruction, TargetMode(target, (96 - wave).unsafe[Int in (32, 64)])).isLeft)
    }
  }

  test("symbolic swizzles match LLVM bytes and reject invalid controls") {
    // LLVM MC ds.s SICI checks, including every bitmask mode.
    val cases = Vec(
      "SWAP,16"                   -> "1f 40",
      "REVERSE,32"                -> "1f 7c",
      "BROADCAST,8,7"             -> "f8 00",
      """BITMASK_PERM,"01pip" """ -> "07 09"
    )
    val target = Target("bonaire")
    cases.foreach { case (control, prefix) =>
      val instruction = parse(s"ds_swizzle_b32 v8, v2 offset:swizzle(${control.trim})")
      val bytes       = hex(s"$prefix d4 d8 02 00 00 08")
      assertEquals(Binary.encode(instruction, TargetMode(target)), Right(bytes))
      val decoded = disassembled(bytes, target)
      assertEquals(Binary.encode(decoded, TargetMode(target)), Right(bytes))
    }
    Vec("SWAP,0", "SWAP,3", "REVERSE,64", "BROADCAST,8,8", """BITMASK_PERM,"zzzzz" """).foreach { control =>
      val instruction = parse(s"ds_swizzle_b32 v8, v2 offset:swizzle(${control.trim})")
      assert(Binary.encode(instruction, TargetMode(target)).isLeft)
    }
  }

  test("vector and memory instructions match independent LLVM MC bytes") {
    // LLVM MC gfx1030 fixtures: offsets, register groups and modifiers are intentional.
    val cases = Vec(
      "s_movk_i32 s5, 0xfffe"                       -> hex("fe ff 05 b0"),
      "v_mov_b32_e32 v0, v1"                        -> hex("01 03 00 7e"),
      "v_add_f32_e32 v0, v1, v2"                    -> hex("01 05 00 06"),
      "v_add_f32_e32 v0, 0x12345678, v2"            -> hex("ff 04 00 06 78 56 34 12"),
      "v_cmp_eq_f32_e32 vcc_lo, v1, v2"             -> hex("01 05 04 7c"),
      "v_fma_f32 v3, v4, v5, v6"                    -> hex("03 00 4b d5 04 0b 1a 04"),
      "ds_read_b32 v3, v4 offset:1025"              -> hex("01 04 d8 d8 04 00 00 03"),
      "ds_write_b64 v3, v[4:5] offset:4096"         -> hex("00 10 34 d9 03 04 00 00"),
      "ds_read2_b32 v[3:4], v5 offset0:3 offset1:7" ->
        hex("03 07 dc d8 05 00 00 03"),
      "s_load_dword s3, s[4:5], 0xc"                   -> hex("c2 00 00 f4 0c 00 00 fa"),
      "s_load_dwordx4 s[8:11], s[4:5], s6 offset:0x10" ->
        hex("02 02 08 f4 10 00 00 0c"),
      "buffer_load_dword v1, off, s[4:7], 0 offset:32" ->
        hex("20 00 30 e0 00 01 01 80"),
      "buffer_load_dword v1, v2, s[4:7], s3 offen glc" ->
        hex("00 50 30 e0 02 01 01 03"),
      "flat_load_dword v0, v[1:2] offset:12"        -> hex("0c 00 30 dc 01 00 7d 00"),
      "global_load_dword v0, v[1:2], off offset:12" ->
        hex("0c 80 30 dc 01 00 7d 00"),
      "global_store_dword v1, v2, s[4:5] offset:12" -> hex("0c 80 70 dc 01 02 04 00")
    )
    cases.foreach { case (source, expected) =>
      val instruction = parse(source)
      val target      = Target("gfx1030")
      assertEquals(Binary.encode(instruction, TargetMode(target)), Right(expected), source)
      val decoded = disassembled(expected, target)
      assertEquals(decoded.definition, instruction.definition, source)
      val reparsed = parse(Printer.render(decoded))
      assertEquals(Binary.encode(reparsed, TargetMode(target)), Right(expected), source)
    }
  }

  test("vector and memory codecs reject invalid register classes, widths, alignment, offsets and literals") {
    val target  = Target("gfx1030")
    val invalid = Vec(
      "v_add_f32_e32 s0, v1, v2",
      "v_add_f32_e32 v0, v1, s2",
      "v_fma_f64 v0, v[2:3], v[4:5], v[6:7]",
      "v_fma_f32 v0, lit(1), lit(2), v3",
      "ds_write_b64 v0, v1",
      "ds_read_b32 v0, v1 offset:65536",
      "buffer_load_dword v0, off, s[3:6], 0",
      "buffer_load_dword v0, off, s[4:7], 0 offen",
      "s_load_dword s0, s[1:2], 0",
      "s_load_dword s0, s[2:3], 4 offset:8"
    )
    invalid.foreach { source =>
      val instruction = parse(source)
      assert(Binary.encode(instruction, TargetMode(target)).isLeft, source)
    }
    val source  = parse("v_add_f32_e32 v0, lit(1), v2")
    val literal = encoded(source, target)
    assert(Binary.decode(literal.take(4), TargetMode(target)).isLeft)
    val memory = parse("ds_read_b32 v0, v1")
    val bytes  = encoded(memory, target)
    assert(Binary.decode(bytes.take(4), TargetMode(target)).isLeft)
    // DATA0 is reserved for DS_READ; accepting it would silently lose bits when printed.
    assert(Binary.decode(bytes.updated(5, 1.toByte), TargetMode(target)).isLeft)
    assert(Binary.decode(bytes :+ 0.toByte, TargetMode(target)).isLeft)
  }

  test("scalar codecs preserve asymmetric widths and shared literal words across ISAs") {
    val sources = Seq(
      "s_mov_b32 s0, 1",
      "s_mov_b64 s[0:1], s[2:3]",
      "s_ashr_i64 s[0:1], s[2:3], s4",
      "s_getpc_b64 s[0:1]",
      "s_cmp_eq_u32 s0, -1",
      "s_add_u32 s2, lit(1), lit(1)"
    )
    Seq(Target("gfx600"), Target("gfx900"), Target("gfx1030"), Target("gfx1100")).foreach { target =>
      sources.foreach { source =>
        val instruction = parse(source)
        val bytes       = encoded(instruction, target)
        val decoded     = disassembled(bytes, target)
        assertEquals(decoded.definition, instruction.definition)
        if (!source.contains("lit(")) assertEquals(decoded.operands, instruction.operands)
        val reparsed = parse(Printer.render(decoded))
        assertEquals(Binary.encode(reparsed, TargetMode(target)), Right(bytes))
      }
    }
  }

  test("scalar instruction bytes match the GFX9 hardware layout") {
    // Independent machine words: SOP1 MOV_B32, SOP2 ADD_U32, SOPC CMP_EQ_U32.
    Seq(
      "s_mov_b32 s0, 1"      -> "81 00 80 be",
      "s_add_u32 s2, s0, s1" -> "00 01 02 80",
      "s_cmp_eq_u32 s0, s1"  -> "00 01 06 bf"
    ).foreach { case (source, expected) =>
      assertEquals(Binary.encode(parse(source), TargetMode(Target("gfx900"))), Right(hex(expected)))
    }
  }

  test("trap temporary codes decode to the ttmp names llvm-mc prints") {
    // LLVM MC: gfx8 maps ttmp0-11 to 112-123, while gfx9 and gfx10 map ttmp0-15 to 108-123.
    Seq(
      (Target("tahiti"), "s_setpc_b64 ttmp[2:3]", "72 20 80 be"),
      (Target("tonga"), "s_mov_b32 s0, ttmp0", "70 00 80 be"),
      (Target("tonga"), "s_mov_b64 s[0:1], ttmp[0:1]", "70 01 80 be"),
      (Target("tonga"), "s_mov_b32 ttmp0, s0", "00 00 f0 be"),
      (Target("tonga"), "s_load_dwordx4 ttmp[4:7], s[2:3], s4", "01 1d 08 c0 04 00 00 00"),
      (Target("gfx908"), "s_mov_b32 s0, ttmp15", "7b 00 80 be"),
      (Target("gfx908"), "s_mov_b64 s[0:1], ttmp[14:15]", "7a 01 80 be"),
      (Target("gfx908"), "s_load_dwordx4 ttmp[4:7], s[2:3], s4", "01 1c 08 c0 04 00 00 00"),
      (Target("gfx1030"), "s_mov_b32 s0, ttmp0", "6c 03 80 be"),
      (Target("gfx1030"), "s_mov_b64 s[0:1], ttmp[0:1]", "6c 04 80 be"),
      (Target("gfx1030"), "s_mov_b64 ttmp[2:3], s[0:1]", "00 04 ee be")
    ).foreach { case (target, source, expected) =>
      assertEquals(encoded(parse(source), target), hex(expected), source)
      assertEquals(Printer.render(disassembled(hex(expected), target)), source, source)
    }
    assert(!Printer.render(disassembled(hex("6c 00 80 be"), Target("tonga"))).contains("ttmp"))
  }

  test("implicit sources are encoded only where the ISA table or LLVM provides them") {
    val vccz                         = "fb 02 00 81"
    val execz                        = "fc 02 00 81"
    val pops                         = "ef 02 00 81"
    def target(name: String): Target = name.refine[Target].getOrElse(fail(name))
    Seq(
      "gfx600"  -> Seq("src_vccz" -> vccz, "src_execz" -> execz),
      "gfx802"  -> Seq("src_vccz" -> vccz, "src_execz" -> execz),
      "gfx900"  -> Seq("src_vccz" -> vccz, "src_execz" -> execz, "src_pops_exiting_wave_id" -> pops),
      "gfx908"  -> Seq("src_vccz" -> vccz, "src_execz" -> execz, "src_pops_exiting_wave_id" -> pops),
      "gfx1030" -> Seq("src_vccz" -> vccz, "src_execz" -> execz, "src_pops_exiting_wave_id" -> pops),
      "gfx1170" -> Seq("src_vccz" -> vccz, "src_execz" -> execz)
    ).foreach { case (cpu, accepted) =>
      accepted.foreach { case (name, bytes) =>
        assertEquals(
          Binary.encode(parse(s"s_add_i32 s0, $name, s2"), TargetMode(target(cpu))),
          Right(hex(bytes)),
          s"$cpu $name"
        )
      }
    }
    Seq(
      "gfx600"  -> Seq("src_pops_exiting_wave_id"),
      "gfx802"  -> Seq("src_pops_exiting_wave_id"),
      "gfx1100" -> Seq("src_vccz", "src_execz", "src_pops_exiting_wave_id"),
      "gfx1150" -> Seq("src_vccz", "src_execz", "src_pops_exiting_wave_id"),
      "gfx1170" -> Seq("src_pops_exiting_wave_id"),
      "gfx1200" -> Seq("src_vccz", "src_execz", "src_pops_exiting_wave_id"),
      "gfx1250" -> Seq("src_vccz", "src_execz", "src_pops_exiting_wave_id")
    ).foreach { case (cpu, rejected) =>
      rejected.foreach { name =>
        assert(Binary.encode(parse(s"s_add_i32 s0, $name, s2"), TargetMode(target(cpu))).isLeft, s"$cpu $name")
      }
    }
  }

  test("literal truncation, mismatched widths, reserved bits, and trailing bytes are rejected") {
    val target                 = Target("gfx1030")
    def encode(source: String) = Binary.encode(parse(source), TargetMode(target))
    val literal                = encoded(parse("s_mov_b32 s0, lit(1)"), target)
    assertEquals(literal.size, 8)
    assert(Binary.decode(literal.take(4), TargetMode(target)).isLeft)
    assert(Binary.decode(literal :+ 0.toByte, TargetMode(target)).isLeft)
    assert(encode("s_mov_b64 s0, s1").isLeft)
    assert(encode("s_add_u32 s0, lit(1), lit(2)").isLeft)
    val getpc = encoded(parse("s_getpc_b64 s[0:1]"), target)
    assert(Binary.decode(getpc.updated(0, 1.toByte), TargetMode(target)).isLeft)
    assert(Binary.decodeStatementAt(literal, 100, TargetMode(target)).isLeft)
  }

  test("SOPP disassembly retains distinct opcodes and operands before execution lowering") {
    val sources = Seq("s_nop 7", "s_waitcnt 0", "s_branch -2", "s_endpgm")
    Seq(Target("gfx900"), Target("gfx1030"), Target("gfx1100")).foreach { target =>
      sources.foreach { source =>
        val instruction = parse(source)
        val bytes       = encoded(instruction, target)
        val decoded     = disassembled(bytes, target)
        assertEquals(decoded.definition, instruction.definition)
        assertEquals(decoded.operands, instruction.operands)
        val text = Printer.render(decoded)
        assertEquals(
          parseInstruction(text).flatMap(value =>
            Binary.encode(value, TargetMode(target)).left.map(error => s"encoding failed: $error")
          ),
          Right(bytes)
        )
      }
    }
  }

  test("SOPP words match the reference layout and the GFX11 opcode renumbering") {
    Vec(
      ("s_nop 0x1234", Target("gfx1010"), "34 12 80 bf", "s_nop 4660"),
      ("s_branch 0x1234", Target("gfx1100"), "34 12 a0 bf", "s_branch 4660")
    ).foreach { case (source, target, expected, text) =>
      val instruction = parse(source)
      val bytes       = hex(expected)
      assertEquals(Binary.encode(instruction, TargetMode(target)), Right(bytes))
      assertEquals(Binary.decode(bytes, TargetMode(target)).map(Printer.render), Right(text))
    }
  }

  test("s_delay_alu takes the instid0, instskip and instid1 syntax hipcc emits for GFX11 and GFX12") {
    // llvm-mc -triple=amdgcn -mcpu=gfx1100 -show-encoding
    Vec(
      ("s_delay_alu instid0(VALU_DEP_1) | instskip(NEXT) | instid1(SALU_CYCLE_1)", "91 04 87 bf"),
      ("s_delay_alu instid0(TRANS32_DEP_3)", "07 00 87 bf"),
      ("s_delay_alu instid0(VALU_DEP_2) | instskip(SKIP_3) | instid1(VALU_DEP_1)", "c2 00 87 bf"),
      ("s_delay_alu instskip(NEXT)", "10 00 87 bf"),
      ("s_delay_alu instid0(VALU_DEP_1)|instskip(NEXT)", "11 00 87 bf"),
      ("s_delay_alu 0", "00 00 87 bf")
    ).foreach { (source, expected) =>
      Vec(Target("gfx1100"), Target("gfx1151"), Target("gfx1201")).foreach { target =>
        assertEquals(Binary.encode(parse(source), TargetMode(target)), Right(hex(expected)), s"$source on $target")
      }
    }
    assert(Binary.encode(parse("s_delay_alu instid0(VALU_DEP_9)"), TargetMode(Target("gfx1100"))).isLeft)
    assert(Binary.encode(
      parse("s_delay_alu instid0(VALU_DEP_1) | instid0(VALU_DEP_2)"),
      TargetMode(Target("gfx1100"))
    ).isLeft)
  }

  test("lane, image, export, interpolation, literal, matrix and dual encodings match LLVM bytes") {
    // Fixed LLVM MC encodings keep these families mandatory even if a codec starts rejecting them.
    val cases = Vec(
      (
        Target("gfx1030"),
        "image_bvh_intersect_ray v[4:7], v[9:19], s[4:7]",
        hex("01 9f 98 f1 09 04 01 00")
      ),
      (
        Target("gfx1030"),
        "image_bvh_intersect_ray v[4:7], v[9:16], s[4:7] a16",
        hex("01 9f 98 f1 09 04 01 40")
      ),
      (
        Target("gfx900"),
        "v_add_f32_sdwa v5, |-1|, v2 dst_sel:DWORD dst_unused:UNUSED_PRESERVE src0_sel:DWORD src1_sel:DWORD",
        hex("f9 04 0a 02 c1 16 a6 06")
      ),
      (
        Target("gfx900"),
        "exp mrt0, off, off, v0, off",
        hex("04 00 00 c4 00 00 00 00")
      ),
      (
        Target("gfx1100"),
        "image_atomic_add v[4:5], [v32, v1, v2], s[96:103] dmask:0x3 dim:SQ_RSRC_IMG_CUBE glc",
        hex("0d 43 30 f0 20 04 18 00 01 02 00 00")
      ),
      (
        Target("gfx1100"),
        "v_interp_p10_f32 v0, v1, -v2, v3 wait_exp:0",
        hex("00 00 00 cd 01 05 0e 44")
      ),
      (
        Target("gfx1100"),
        "s_setreg_imm32_b32 hwreg(52, 8, 3), 0x11213141",
        hex("34 12 80 b9 41 31 21 11")
      ),
      (
        Target("gfx1100"),
        "v_dual_add_f32 v255, v4, v2 :: v_dual_mul_dx9_zero_f32 v6, v1, v3",
        hex("04 05 0e c9 01 07 06 ff")
      ),
      (
        Target("gfx1200"),
        "image_load v0, v0, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D th:TH_LOAD_HT scope:SCOPE_SE",
        hex("00 00 40 d0 00 00 24 00 00 00 00 00")
      ),
      (
        Target("gfx1200"),
        "buffer_load_b32 v5, off, s[8:11], s3 offset:8388607 th:TH_LOAD_NT_HT scope:SCOPE_DEV",
        hex("03 00 05 c4 05 10 e8 00 00 ff ff 7f")
      ),
      (Target("gfx1200"), "ds_direct_load v9 wait_va_vdst:7 wait_vm_vsrc:0", hex("09 00 17 ce")),
      (
        Target("gfx908"),
        "v_mfma_f32_16x16x1f32 a[0:15], v0, v1, a[17:32]",
        hex("00 00 c1 d3 00 03 46 04")
      )
    )
    cases.foreach { case (target, source, expected) =>
      val statement = parseStatement(source)
      assertEquals(Binary.encodeStatement(statement, TargetMode(target)), Right(expected), source)
      val decoded = Binary.decodeStatementAt(expected, 0, TargetMode(target)).value
      assertEquals(decoded.size.raw, expected.size)
      val printed  = Printer.render(Program(Vec(decoded.statement)))
      val reparsed = parseStatement(printed)
      assertEquals(Binary.encodeStatement(reparsed, TargetMode(target)), Right(expected), printed)
      assert(Binary.decodeStatementAt(expected.dropRight(1), 0, TargetMode(target)).isLeft, source)
    }
  }

  test("GFX12 image atomics take LLVM's renamed mnemonics with the base opcode's operand shapes") {
    // llvm-mc 22 -triple=amdgcn -mcpu=gfx1201 -show-encoding
    val target = Target("gfx1201")
    Vec(
      "image_atomic_add_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 00 43 d0 00 00 00 00 04 00 00 00",
      "image_atomic_sub_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 40 43 d0 00 00 00 00 04 00 00 00",
      "image_atomic_min_int v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D"  -> "00 80 43 d0 00 00 00 00 04 00 00 00",
      "image_atomic_min_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 c0 43 d0 00 00 00 00 04 00 00 00",
      "image_atomic_max_int v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D"  -> "00 00 44 d0 00 00 00 00 04 00 00 00",
      "image_atomic_max_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 40 44 d0 00 00 00 00 04 00 00 00",
      "image_atomic_inc_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 40 45 d0 00 00 00 00 04 00 00 00",
      "image_atomic_dec_uint v0, v4, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D" -> "00 80 45 d0 00 00 00 00 04 00 00 00",
      "image_atomic_add_uint v0, [v4, v5], s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_2D th:TH_ATOMIC_RETURN" ->
        "01 00 43 d0 00 00 10 00 04 05 00 00"
    ).foreach { (source, bytes) =>
      val expected = hex(bytes)
      assertEquals(Binary.encodeStatement(parseStatement(source), TargetMode(target)), Right(expected), source)
      assert(Binary.decodeStatementAt(expected, 0, TargetMode(target)).isRight, source)
    }
  }

  test("VOPD3 accepts inline constants but rejects appended literals") {
    val target = Target("gfx1250")
    val source = "v_dual_add_f64 v[254:255], 0.5, v[8:9] :: v_dual_add_f32 v7, 0.5, v2"
    // LLVM MC gfx1250_asm_vopd3.s.
    val bytes  = hex("f0 40 84 cf f0 00 08 00 fe 02 00 07")
    val parsed = parseStatement(source)
    assertEquals(Binary.encodeStatement(parsed, TargetMode(target)), Right(bytes))
    val decoded = Binary.decodeStatementAt(bytes, 0, TargetMode(target)).value
    assertEquals(Binary.encodeStatement(decoded.statement, TargetMode(target)), Right(bytes))
    Vec("lit(1)", "lit64(0x123456789abcdef0)", "0x12345678").foreach { literal =>
      val invalid =
        parseStatement(HalfLiteral.replaceFirstIn(source, literal))
      assert(Binary.encodeStatement(invalid, TargetMode(target)).isLeft, literal)
    }
    assert(Binary.decodeStatementAt(
      bytes.updated(0, 0xff.toByte) ++ Vec.fill(4)(0.toByte),
      0,
      TargetMode(target)
    ).isLeft)
  }

  test("VOPD3 selects take their lane mask from an explicit SGPR") {
    // llvm-mc 22 -triple=amdgcn -mcpu=gfx1250 -show-encoding
    val target = Target("gfx1250")
    Vec(
      "v_dual_cndmask_b32 v0, v1, v2, s0 :: v_dual_fma_f32 v7, v14, v15, v16" ->
        "01 31 25 cf 0e 01 02 00 00 0f 10 07",
      "v_dual_cndmask_b32 v0, v1, v2, vcc_lo :: v_dual_fma_f32 v7, v14, v15, v16" ->
        "01 31 25 cf 0e 01 02 6a 00 0f 10 07",
      "v_dual_fma_f32 v7, v14, v15, v16 :: v_dual_cndmask_b32 v0, v1, v2, s1" ->
        "0e 91 4c cf 01 01 0f 10 07 02 01 00"
    ).foreach { (source, bytes) =>
      val expected = hex(bytes)
      assertEquals(Binary.encodeStatement(parseStatement(source), TargetMode(target)), Right(expected), source)
      val decoded = Binary.decodeStatementAt(expected, 0, TargetMode(target)).value
      assertEquals(Binary.encodeStatement(decoded.statement, TargetMode(target)), Right(expected), source)
    }
  }

  test("VOPD3 negates the float sources NEGX and NEGY name and gives bitop2 its truth table") {
    // llvm-mc 22 -triple=amdgcn -mcpu=gfx1250 -show-encoding
    val target = Target("gfx1250")
    Vec(
      "v_dual_add_f32 v0, -v1, v2 :: v_dual_fma_f32 v7, v14, -v15, -v16" -> "01 31 11 cf 0e 63 02 00 00 0f 10 07",
      "v_dual_fmac_f32 v0, -v1, v2 :: v_dual_mov_b32 v7, v14"            -> "01 81 00 cf 0e 03 02 00 00 00 00 07",
      "v_dual_lshlrev_b32 v0, v1, v2 :: v_dual_subrev_f32 v7, -v14, v15" -> "01 61 44 cf 0e 11 02 00 00 0f 00 07",
      "v_dual_fma_f64 v[10:11], -v[2:3], v[4:5], v[6:7] :: v_dual_add_f32 v12, v9, -v7" ->
        "02 41 80 cf 09 23 04 06 0a 07 00 0c",
      "v_dual_fma_f32 v7, v14, v15, v16 :: v_dual_bitop2_b32 v0, v1, v2 bitop3:0x3c" ->
        "0e 21 4d cf 01 01 0f 10 07 02 3c 00"
    ).foreach { (source, bytes) =>
      val expected = hex(bytes)
      assertEquals(Binary.encodeStatement(parseStatement(source), TargetMode(target)), Right(expected), source)
      val decoded = Binary.decodeStatementAt(expected, 0, TargetMode(target)).value
      assertEquals(Printer.render(Program(Vec(decoded.statement))).replace(" e96", ""), source, source)
    }
  }

  test("image register counts, lane controls and dual-issue literal constraints are checked") {
    val target = Target("gfx1100")
    Vec(
      "image_load v[0:3], v0, s[0:7] dmask:1 dim:SQ_RSRC_IMG_1D",
      "image_load v0, [v1, v2, v3], s[0:7] dmask:1 dim:SQ_RSRC_IMG_2D",
      "image_load v0, v0, s[1:8] dmask:1 dim:SQ_RSRC_IMG_1D",
      "v_add_f32 v0, v1, v2 quad_perm:[0,1,2,4]",
      "v_dual_add_f32 v0, v2, v4 :: v_dual_mul_f32 v2, v3, v5",
      "v_dual_add_f32 v0, lit(123), v4 :: v_dual_mul_f32 v1, lit(456), v5"
    ).foreach { source =>
      val parsed = parseStatement(source)
      assert(Binary.encodeStatement(parsed, TargetMode(target)).isLeft, source)
    }
  }

  test("DPP16 row controls follow each target's control set") {
    // LLVM MC: GCN keeps wave shifts and row broadcasts, CDNA2 adds row_newbcast, RDNA has row_share and row_xmask.
    val accepted = Vec(
      (Target("gfx90a"), "row_newbcast:1", hex("fa 02 00 7e 01 51 01 ff")),
      (Target("gfx942"), "row_newbcast:1", hex("fa 02 00 7e 01 51 01 ff")),
      (Target("gfx90a"), "wave_shl:1", hex("fa 02 00 7e 01 30 01 ff")),
      (Target("gfx900"), "row_bcast:15", hex("fa 02 00 7e 01 42 01 ff")),
      (Target("gfx1030"), "row_share:1", hex("fa 02 00 7e 01 51 01 ff")),
      (Target("gfx1030"), "row_xmask:1", hex("fa 02 00 7e 01 61 01 ff"))
    )
    accepted.foreach { case (target, control, bytes) =>
      val instruction = parse(s"v_mov_b32_dpp v0, v1 $control row_mask:0xf bank_mask:0xf")
      assertEquals(Binary.encode(instruction, TargetMode(target)), Right(bytes), s"$target $control")
      val printed = Printer.render(disassembled(bytes, target))
      assert(printed.contains(control), s"$target: $printed")
    }
    val rejected = Vec(
      Target("gfx900")  -> Vec("row_newbcast:1", "row_share:1", "row_xmask:1"),
      Target("gfx90a")  -> Vec("row_share:1", "row_xmask:1"),
      Target("gfx942")  -> Vec("row_share:1", "row_xmask:1"),
      Target("gfx1030") -> Vec("row_newbcast:1", "wave_shl:1", "wave_ror:1", "row_bcast:15", "row_bcast:31")
    )
    for {
      (target, controls) <- rejected
      control            <- controls
    } {
      val instruction = parse(s"v_mov_b32_dpp v0, v1 $control row_mask:0xf bank_mask:0xf")
      assert(Binary.encode(instruction, TargetMode(target)).isLeft, s"$target $control")
    }
    assert(Binary.decode(hex("fa 02 00 7e 01 61 01 ff"), TargetMode(Target("gfx90a"))).isLeft)
  }

  test("codes outside an operand domain are decode errors naming the field, code, family and domain") {
    // buffer_load_dword v1, v2, s[4:7], s125 offen: GFX9 and CDNA2 have no null register at s125
    val bufferLoad = Vec(0x00, 0x10, 0x00, 0xe0, 0x02, 0x01, 0x01, 0x7d).map(_.toByte)
    Vec(Target("gfx900") -> "gfx9", Target("gfx90a") -> "cdna2").foreach { case (target, family) =>
      assert(
        Binary.decode(bufferLoad, TargetMode(target)).left.toOption.exists {
          case CodecError.InvalidEncoding(message) =>
            message.contains(s"SOFFSET code 125 is outside the ssrc_nolit domain on $family")
          case _ => false
        },
        target.name
      )
    }
    // s_mov_b32 s2, source code 209: reserved on RDNA3
    assert(Binary.decode(
      Vec(0xd1, 0x00, 0x82, 0xbe).map(_.toByte),
      TargetMode(Target("gfx1100"))
    ).left.toOption.exists {
      case CodecError.InvalidEncoding(message) => message.contains("SSRC0 code 209 is outside the src domain on rdna3")
      case _                                   => false
    })
  }
}
