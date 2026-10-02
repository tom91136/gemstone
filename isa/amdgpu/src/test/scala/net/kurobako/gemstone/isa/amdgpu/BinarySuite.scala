package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.{Printer as _, *}

class BinarySuite extends munit.FunSuite {
  test("validation shares symbolic operand checking while retaining incomplete processor availability") {
    val target = Target("gfx900")
    Vec("s_waitcnt vmcnt(0)", "s_branch destination", "s_nop 0").foreach { text =>
      val instruction = Parser.parseInstruction(text).toOption.get
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
    val overflow = Parser.parseInstruction("s_nop 65536").toOption.get
    Validation.validate(overflow, target) match {
      case ValidationOutcome.Rejected(_) => ()
      case other                         => fail(s"out-of-range immediate was not rejected: $other")
    }
  }

  test("image derivative variants use the metadata address count, including offset and clamp") {
    // LLVM gfx10_asm_mimg.s: cd supplies derivatives, without a depth-comparison argument.
    val source =
      "image_sample_cd_cl_o v[64:66], [v32, v16, v0, v2, v1, v4, v5, v6, v7, v8, v9], s[4:11], s[100:103] dmask:0x7 dim:SQ_RSRC_IMG_3D"
    val expected = Vec(0x16, 0x07, 0xb4, 0xf1, 0x20, 0x40, 0x21, 0x03, 0x10, 0x00, 0x02, 0x01, 0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x00, 0x00).map(_.toByte)
    val codec       = Binary(Target("gfx1030"))
    val instruction = Parser.parseInstruction(source).fold(error => fail(error.toString), identity)
    assertEquals(codec.encode(instruction), Right(expected))
    val decoded = codec.decode(expected).fold(error => fail(error.toString), identity)
    assertEquals(decoded.operands, instruction.operands)
    assertEquals(codec.encode(decoded), Right(expected))
    val tooMany =
      Parser.parseInstruction(source.replace("v8, v9]", "v8, v9, v10]")).fold(error => fail(error.toString), identity)
    assert(codec.encode(tooMany).isLeft)
  }

  test("wave size controls matrix register tuples without changing their encoding") {
    // LLVM MC gfx12 SWMMAC wave32/wave64 encodings share bytes but operate on different tuples.
    val bytes = Vec(0x06, 0x40, 0x53, 0xcc, 0x00, 0x05, 0x22, 0x1c).map(_.toByte)
    val cases = Vec(
      (32, "v_swmmac_bf16_16x16x32_bf16 v[6:9], v[0:3], v[2:9], v8"),
      (64, "v_swmmac_bf16_16x16x32_bf16 v[6:7], v[0:1], v[2:5], v8")
    )
    cases.foreach { case (wave, source) =>
      val instruction = Parser.parseInstruction(source).fold(error => fail(error.toString), identity)
      val codec       = Binary(Target("gfx1200"), wave.unsafe[Int in (32, 64)])
      assertEquals(codec.encode(instruction), Right(bytes))
      val decoded = codec.decode(bytes).fold(error => fail(error.toString), identity)
      assertEquals(decoded.operands, instruction.operands)
      assertEquals(codec.encode(decoded), Right(bytes))
      val other = Binary(Target("gfx1200"), (96 - wave).unsafe[Int in (32, 64)])
      assert(other.encode(instruction).isLeft)
    }
  }

  test("symbolic swizzles match LLVM bytes and reject invalid controls") {
    // LLVM MC ds.s SICI checks, including every bitmask mode.
    val cases = Vec(
      "SWAP,16"                   -> Vec(0x1f, 0x40),
      "REVERSE,32"                -> Vec(0x1f, 0x7c),
      "BROADCAST,8,7"             -> Vec(0xf8, 0x00),
      """BITMASK_PERM,"01pip" """ -> Vec(0x07, 0x09)
    )
    val codec = Binary(Target("bonaire"))
    cases.foreach { case (control, prefix) =>
      val instruction = Parser.parseInstruction(s"ds_swizzle_b32 v8, v2 offset:swizzle(${control.trim})").fold(
        error => fail(error.toString),
        identity
      )
      val bytes = (prefix ++ Vec(0xd4, 0xd8, 0x02, 0x00, 0x00, 0x08)).map(_.toByte)
      assertEquals(codec.encode(instruction), Right(bytes))
      val decoded = codec.decode(bytes).fold(error => fail(error.toString), identity)
      assertEquals(codec.encode(decoded), Right(bytes))
    }
    Vec("SWAP,0", "SWAP,3", "REVERSE,64", "BROADCAST,8,8", """BITMASK_PERM,"zzzzz" """).foreach { control =>
      val instruction = Parser.parseInstruction(s"ds_swizzle_b32 v8, v2 offset:swizzle(${control.trim})").fold(
        error => fail(error.toString),
        identity
      )
      assert(codec.encode(instruction).isLeft)
    }
  }

  test("vector and memory instructions match independent LLVM MC bytes") {
    // LLVM MC gfx1030 fixtures: offsets, register groups and modifiers are intentional.
    val cases = Vec(
      "s_movk_i32 s5, 0xfffe"               -> Vec(0xfe, 0xff, 0x05, 0xb0).map(_.toByte),
      "v_mov_b32_e32 v0, v1"                -> Vec(0x01, 0x03, 0x00, 0x7e).map(_.toByte),
      "v_add_f32_e32 v0, v1, v2"            -> Vec(0x01, 0x05, 0x00, 0x06).map(_.toByte),
      "v_add_f32_e32 v0, 0x12345678, v2"    -> Vec(0xff, 0x04, 0x00, 0x06, 0x78, 0x56, 0x34, 0x12).map(_.toByte),
      "v_cmp_eq_f32_e32 vcc_lo, v1, v2"     -> Vec(0x01, 0x05, 0x04, 0x7c).map(_.toByte),
      "v_fma_f32 v3, v4, v5, v6"            -> Vec(0x03, 0x00, 0x4b, 0xd5, 0x04, 0x0b, 0x1a, 0x04).map(_.toByte),
      "ds_read_b32 v3, v4 offset:1025"      -> Vec(0x01, 0x04, 0xd8, 0xd8, 0x04, 0x00, 0x00, 0x03).map(_.toByte),
      "ds_write_b64 v3, v[4:5] offset:4096" -> Vec(0x00, 0x10, 0x34, 0xd9, 0x03, 0x04, 0x00, 0x00).map(_.toByte),
      "ds_read2_b32 v[3:4], v5 offset0:3 offset1:7" ->
        Vec(0x03, 0x07, 0xdc, 0xd8, 0x05, 0x00, 0x00, 0x03).map(_.toByte),
      "s_load_dword s3, s[4:5], 0xc" -> Vec(0xc2, 0x00, 0x00, 0xf4, 0x0c, 0x00, 0x00, 0xfa).map(_.toByte),
      "s_load_dwordx4 s[8:11], s[4:5], s6 offset:0x10" ->
        Vec(0x02, 0x02, 0x08, 0xf4, 0x10, 0x00, 0x00, 0x0c).map(_.toByte),
      "buffer_load_dword v1, off, s[4:7], 0 offset:32" ->
        Vec(0x20, 0x00, 0x30, 0xe0, 0x00, 0x01, 0x01, 0x80).map(_.toByte),
      "buffer_load_dword v1, v2, s[4:7], s3 offen glc" ->
        Vec(0x00, 0x50, 0x30, 0xe0, 0x02, 0x01, 0x01, 0x03).map(_.toByte),
      "flat_load_dword v0, v[1:2] offset:12" -> Vec(0x0c, 0x00, 0x30, 0xdc, 0x01, 0x00, 0x7d, 0x00).map(_.toByte),
      "global_load_dword v0, v[1:2], off offset:12" ->
        Vec(0x0c, 0x80, 0x30, 0xdc, 0x01, 0x00, 0x7d, 0x00).map(_.toByte),
      "global_store_dword v1, v2, s[4:5] offset:12" -> Vec(0x0c, 0x80, 0x70, 0xdc, 0x01, 0x02, 0x04, 0x00).map(_.toByte)
    )
    cases.foreach { case (source, expected) =>
      val instruction = Parser.parseInstruction(source).fold(error => fail(s"$source: $error"), identity)
      val codec       = Binary(Target("gfx1030"))
      assertEquals(codec.encode(instruction), Right(expected), source)
      val decoded = codec.decode(expected).fold(error => fail(s"$source: $error"), identity)
      assertEquals(decoded.definition, instruction.definition, source)
      val reparsed = Parser.parseInstruction(Printer.render(decoded)).fold(error => fail(error.toString), identity)
      assertEquals(codec.encode(reparsed), Right(expected), source)
    }
  }

  test("vector and memory codecs reject invalid register classes, widths, alignment, offsets and literals") {
    val codec   = Binary(Target("gfx1030"))
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
      val instruction = Parser.parseInstruction(source).fold(error => fail(s"$source: $error"), identity)
      assert(codec.encode(instruction).isLeft, source)
    }
    val source  = Parser.parseInstruction("v_add_f32_e32 v0, lit(1), v2").toOption.get
    val literal = codec.encode(source).toOption.get
    assert(codec.decode(literal.take(4)).isLeft)
    val memory = Parser.parseInstruction("ds_read_b32 v0, v1").toOption.get
    val bytes  = codec.encode(memory).toOption.get
    assert(codec.decode(bytes.take(4)).isLeft)
    // DATA0 is reserved for DS_READ; accepting it would silently lose bits when printed.
    assert(codec.decode(bytes.updated(5, 1.toByte)).isLeft)
    assert(codec.decode(bytes :+ 0.toByte).isLeft)
  }

  test("scalar codecs preserve asymmetric widths and shared literal words across ISAs") {
    Seq(Target("gfx600"), Target("gfx900"), Target("gfx1030"), Target("gfx1100")).foreach { target =>
      Seq(
        "s_mov_b32 s0, 1",
        "s_mov_b64 s[0:1], s[2:3]",
        "s_ashr_i64 s[0:1], s[2:3], s4",
        "s_getpc_b64 s[0:1]",
        "s_cmp_eq_u32 s0, -1",
        "s_add_u32 s2, lit(1), lit(1)"
      ).foreach { source =>
        val instruction = Parser.parseInstruction(source).fold(error => fail(error.toString), identity)
        val codec       = Binary(target)
        val bytes       = codec.encode(instruction).fold(error => fail(s"$target: $source: $error"), identity)
        val decoded     = codec.decode(bytes).fold(error => fail(s"$target: $source: $error"), identity)
        assertEquals(decoded.definition, instruction.definition)
        if (!source.contains("lit(")) assertEquals(decoded.operands, instruction.operands)
        val reparsed = Parser.parseInstruction(Printer.render(decoded)).fold(error => fail(error.toString), identity)
        assertEquals(codec.encode(reparsed), Right(bytes))
      }
    }
  }

  test("scalar instruction bytes match the GFX9 hardware layout") {
    // Independent machine words: SOP1 MOV_B32, SOP2 ADD_U32, SOPC CMP_EQ_U32.
    val cases = Seq(
      "s_mov_b32 s0, 1"      -> Seq(0x81, 0x00, 0x80, 0xbe),
      "s_add_u32 s2, s0, s1" -> Seq(0x00, 0x01, 0x02, 0x80),
      "s_cmp_eq_u32 s0, s1"  -> Seq(0x00, 0x01, 0x06, 0xbf)
    )
    cases.foreach { case (source, expected) =>
      val instruction = Parser.parseInstruction(source).fold(error => fail(error.toString), identity)
      assertEquals(Binary.encode(instruction, Target("gfx900")), Right(Vec.from(expected.map(_.toByte))))
    }
  }

  test("literal truncation, mismatched widths, reserved bits, and trailing bytes are rejected") {
    val codec                  = Binary(Target("gfx1030"))
    def encode(source: String) = Parser.parseInstruction(source).fold(error => fail(error.toString), codec.encode(_))
    val literal                = encode("s_mov_b32 s0, lit(1)").fold(error => fail(error.toString), identity)
    assertEquals(literal.size, 8)
    assert(codec.decode(literal.take(4)).isLeft)
    assert(codec.decode(literal :+ 0.toByte).isLeft)
    assert(encode("s_mov_b64 s0, s1").isLeft)
    assert(encode("s_add_u32 s0, lit(1), lit(2)").isLeft)
    val getpc = encode("s_getpc_b64 s[0:1]").fold(error => fail(error.toString), identity)
    assert(codec.decode(getpc.updated(0, 1.toByte)).isLeft)
    assert(Binary.decodeAt(literal, 100, Target("gfx1030")).isLeft)
  }

  test("SOPP disassembly retains distinct opcodes and operands before execution lowering") {
    Seq(Target("gfx900"), Target("gfx1030"), Target("gfx1100")).foreach { target =>
      val codec = Binary(target)
      Seq("s_nop 7", "s_waitcnt 0", "s_branch -2", "s_endpgm").foreach { source =>
        val instruction = Parser.parseInstruction(source).fold(error => fail(error.toString), identity)
        val bytes       = codec.encode(instruction).fold(error => fail(error.toString), identity)
        val decoded     = codec.decode(bytes).fold(error => fail(error.toString), identity)
        assertEquals(decoded.definition, instruction.definition)
        assertEquals(decoded.operands, instruction.operands)
        val text = Printer.render(decoded)
        assertEquals(
          Parser.parseInstruction(text).flatMap(value =>
            codec.encode(value).left.map(error =>
              Parser.Error(1, 1, s"encoding failed: $error")
            )
          ),
          Right(bytes)
        )
      }
    }
  }

  test("encode and decode the reference SOPP layout") {
    val instruction = Parser.parseInstruction("s_nop 0x1234").fold(error => fail(error.toString), value => value)
    val target      = Target("gfx1010")
    val encoded     = Binary.encodeSOPP(instruction, target)
    assert(encoded.exists(_.sameElements(Seq(0x34, 0x12, 0x80, 0xbf).map(_.toByte))))
    val decoded = encoded.flatMap(bytes => Binary.decodeSOPP(bytes, target))
    assertEquals(decoded.map(Printer.render), Right("s_nop 4660"))
  }

  test("reject a non-SOPP instruction instead of guessing an encoding") {
    val instruction =
      Parser.parseInstruction("v_add_f32 v0, v1, v2").fold(error => fail(error.toString), value => value)
    assert(Binary.encodeSOPP(instruction, Target("gfx1010")).isLeft)
  }

  test("encode the GFX11 SOPP opcode renumbering from official XML") {
    val instruction = Parser.parseInstruction("s_branch 0x1234").fold(error => fail(error.toString), value => value)
    assert(
      Binary
        .encodeSOPP(instruction, Target("gfx1100"))
        .exists(_.sameElements(Seq(0x34, 0x12, 0xa0, 0xbf).map(_.toByte)))
    )
  }
  test("lane, image, export, interpolation, literal, matrix and dual encodings match LLVM bytes") {
    // Fixed LLVM MC encodings keep these families mandatory even if a codec starts rejecting them.
    val cases = Vec(
      (
        Target("gfx1030"),
        "image_bvh_intersect_ray v[4:7], v[9:19], s[4:7]",
        Vec(0x01, 0x9f, 0x98, 0xf1, 0x09, 0x04, 0x01, 0x00).map(_.toByte)
      ),
      (
        Target("gfx1030"),
        "image_bvh_intersect_ray v[4:7], v[9:16], s[4:7] a16",
        Vec(0x01, 0x9f, 0x98, 0xf1, 0x09, 0x04, 0x01, 0x40).map(_.toByte)
      ),
      (
        Target("gfx900"),
        "v_add_f32_sdwa v5, |-1|, v2 dst_sel:DWORD dst_unused:UNUSED_PRESERVE src0_sel:DWORD src1_sel:DWORD",
        Vec(0xf9, 0x04, 0x0a, 0x02, 0xc1, 0x16, 0xa6, 0x06).map(_.toByte)
      ),
      (
        Target("gfx900"),
        "exp mrt0, off, off, v0, off",
        Vec(0x04, 0x00, 0x00, 0xc4, 0x00, 0x00, 0x00, 0x00).map(_.toByte)
      ),
      (
        Target("gfx1100"),
        "image_atomic_add v[4:5], [v32, v1, v2], s[96:103] dmask:0x3 dim:SQ_RSRC_IMG_CUBE glc",
        Vec(0x0d, 0x43, 0x30, 0xf0, 0x20, 0x04, 0x18, 0x00, 0x01, 0x02, 0x00, 0x00).map(_.toByte)
      ),
      (
        Target("gfx1100"),
        "v_interp_p10_f32 v0, v1, -v2, v3 wait_exp:0",
        Vec(0x00, 0x00, 0x00, 0xcd, 0x01, 0x05, 0x0e, 0x44).map(_.toByte)
      ),
      (
        Target("gfx1100"),
        "s_setreg_imm32_b32 hwreg(52, 8, 3), 0x11213141",
        Vec(0x34, 0x12, 0x80, 0xb9, 0x41, 0x31, 0x21, 0x11).map(_.toByte)
      ),
      (
        Target("gfx1100"),
        "v_dual_add_f32 v255, v4, v2 :: v_dual_mul_dx9_zero_f32 v6, v1, v3",
        Vec(0x04, 0x05, 0x0e, 0xc9, 0x01, 0x07, 0x06, 0xff).map(_.toByte)
      ),
      (
        Target("gfx1200"),
        "image_load v0, v0, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D th:TH_LOAD_HT scope:SCOPE_SE",
        Vec(0x00, 0x00, 0x40, 0xd0, 0x00, 0x00, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00).map(_.toByte)
      ),
      (
        Target("gfx1200"),
        "buffer_load_b32 v5, off, s[8:11], s3 offset:8388607 th:TH_LOAD_NT_HT scope:SCOPE_DEV",
        Vec(0x03, 0x00, 0x05, 0xc4, 0x05, 0x10, 0xe8, 0x00, 0x00, 0xff, 0xff, 0x7f).map(_.toByte)
      ),
      (Target("gfx1200"), "ds_direct_load v9 wait_va_vdst:7 wait_vm_vsrc:0", Vec(0x09, 0x00, 0x17, 0xce).map(_.toByte)),
      (
        Target("gfx908"),
        "v_mfma_f32_16x16x1f32 a[0:15], v0, v1, a[17:32]",
        Vec(0x00, 0x00, 0xc1, 0xd3, 0x00, 0x03, 0x46, 0x04).map(_.toByte)
      )
    )
    cases.foreach { case (target, source, expected) =>
      val statement = Parser.parse(source).fold(error => fail(error.toString), _.statements.head)
      assertEquals(Binary.encodeStatement(statement, target), Right(expected), source)
      val decoded = Binary.decodeStatementAt(expected, 0, target).fold(error => fail(s"$source: $error"), identity)
      assertEquals(decoded.size.raw, expected.size)
      val printed  = Printer.render(Program(Vec(decoded.statement)))
      val reparsed = Parser.parse(printed).fold(error => fail(error.toString), _.statements.head)
      assertEquals(Binary.encodeStatement(reparsed, target), Right(expected), printed)
      assert(Binary.decodeStatementAt(expected.dropRight(1), 0, target).isLeft, source)
    }
  }

  test("VOPD3 accepts inline constants but rejects appended literals") {
    val target = Target("gfx1250")
    val source = "v_dual_add_f64 v[254:255], 0.5, v[8:9] :: v_dual_add_f32 v7, 0.5, v2"
    // LLVM MC gfx1250_asm_vopd3.s.
    val bytes  = Vec(0xf0, 0x40, 0x84, 0xcf, 0xf0, 0x00, 0x08, 0x00, 0xfe, 0x02, 0x00, 0x07).map(_.toByte)
    val parsed = Parser.parse(source).fold(error => fail(error.toString), _.statements.head)
    assertEquals(Binary.encodeStatement(parsed, target), Right(bytes))
    val decoded = Binary.decodeStatementAt(bytes, 0, target).fold(error => fail(error.toString), identity)
    assertEquals(Binary.encodeStatement(decoded.statement, target), Right(bytes))
    Vec("lit(1)", "lit64(0x123456789abcdef0)", "0x12345678").foreach { literal =>
      val invalid =
        Parser.parse(source.replaceFirst("0.5", literal)).fold(error => fail(error.toString), _.statements.head)
      assert(Binary.encodeStatement(invalid, target).isLeft, literal)
    }
    assert(Binary.decodeStatementAt(bytes.updated(0, 0xff.toByte) ++ Vec.fill(4)(0.toByte), 0, target).isLeft)
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
      val parsed = Parser.parse(source).fold(error => fail(error.toString), _.statements.head)
      assert(Binary.encodeStatement(parsed, target).isLeft, source)
    }
  }

}
