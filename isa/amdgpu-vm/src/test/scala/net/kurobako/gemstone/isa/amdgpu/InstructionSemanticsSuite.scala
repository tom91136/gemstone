package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

class InstructionSemanticsSuite extends munit.FunSuite {
  private def vector(index: VectorRegisterIndex): InstructionSource = InstructionSource.VectorRegister(index)
  private def scalar(index: ScalarRegisterIndex): InstructionSource = InstructionSource.Scalar(index)

  /** The definition of `instruction` for one lane of a wave64, over its registers and state as spelt. */
  private def defined(instruction: Instruction)(using munit.Location): String = {
    val naming = RenderContext.Names(_ => None, 64)
    LaneRenderer.describe(instruction, naming).fold(fail(s"$instruction has no semantics"))(steps =>
      Definition.of("op", steps.all, naming.declared()).fold(fail(_), ScalaBackend.show(_))
    )
  }

  test("instructions define the operands and implicit state they write from those they read") {
    assertEquals(
      defined(Instruction.VBinary(VectorBinaryOperation.AddU32, 3, vector(1), vector(2), clamp = false)),
      "def op(v1: UInt[32], v2: UInt[32]): (v3: UInt[32]) =\n  (v3 = v1 + v2)"
    )
    assertEquals(
      defined(Instruction.SBinary(ScalarBinaryOperation.AddU32, 3, scalar(1), scalar(2))),
      """def op(s1: UInt[32], s2: UInt[32]): (s3: UInt[32], scc: Bool) =
        |  (
        |    s3 = s1 + s2,
        |    scc = s1.to[UInt[64]] + s2.to[UInt[64]] > 0xffffffffL
        |  )""".stripMargin
    )
    assertEquals(
      defined(Instruction.SSaveExec(SaveExecOperation.AndNot, 4, scalar(2), 64)),
      """def op(`s[2:3]`: UInt[64], exec: UInt[64]): (`s[4:5]`: UInt[64], exec: UInt[64], scc: Bool) = {
        |  val v0 = `s[2:3]` & ~exec
        |  (
        |    `s[4:5]` = exec,
        |    exec = v0,
        |    scc = v0 != UInt[64](0)
        |  )
        |}""".stripMargin
    )
    assertEquals(
      defined(Instruction.VBinary64(VectorBinary64Operation.Add, 4, vector(2), vector(6), SourceModifiers.Unmodified)),
      "def op(`v[2:3]`: UInt[64], `v[6:7]`: UInt[64]): (`v[4:5]`: UInt[64]) =\n" +
        "  (`v[4:5]` = Floats.add(`v[2:3]`.asFloat, `v[6:7]`.asFloat).bits)"
    )
    assertEquals(
      defined(Instruction.VBitOp3(3, vector(1), vector(2), vector(4), 0xca, half = false)),
      "def op(v1: UInt[32], v2: UInt[32], v4: UInt[32]): (v3: UInt[32]) =\n  (v3 = blend(v1, v2, v4))"
    )
  }

  test("lane masks are the wave's ballots, and a lane reads its own bit") {
    assertEquals(
      defined(Instruction.VCndMask(3, vector(1), vector(2), scalar(106), SourceModifiers(absolute = 1, negate = 2))),
      "def op(v1: UInt[32], v2: UInt[32], vcc: UInt[64])(using wave: Wave): (v3: UInt[32]) =\n" +
        "  (v3 = if ((vcc >> wave.lane()).to[UInt[1]] != UInt[1](0)) v2 ^ 0x80000000L else v1 & ~0x80000000L)"
    )
    assertEquals(
      defined(
        Instruction.VCompare(
          VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Less, updateExec = true),
          106,
          vector(1),
          vector(2)
        )
      ),
      """def op(v1: UInt[32], v2: UInt[32])(using wave: Wave): (exec: UInt[64], vcc: UInt[64]) = {
        |  val v0 = wave.ballot(v1 < v2)
        |  (
        |    exec = v0,
        |    vcc = v0
        |  )
        |}""".stripMargin
    )
    assertEquals(
      defined(Instruction.VReadLane(3, vector(1), scalar(2))),
      "def op(s2: UInt[32], v1: UInt[32])(using wave: Wave): (s3: UInt[32]) =\n  (s3 = wave.read(v1, s2 & 63))"
    )
  }

  test("atomics load the old value, store the update and may return the old value") {
    assertEquals(
      defined(Instruction.Atomic(AtomicOperation.Add, 4, MemoryAddress.Local(2, 16), 6, 4, returnValue = true)),
      """def op(v2: UInt[32], v6: UInt[32])(using lds: LocalMemory): (v4: UInt[32]) = {
        |  val v0 = lds.load32(AtomicAlignment.offset(v2 + 0 + 16, bytes = 4))
        |  lds.store32(AtomicAlignment.offset(v2 + 0 + 16, bytes = 4), v0 + v6)
        |  (v4 = v0)
        |}""".stripMargin
    )
    assertEquals(
      defined(Instruction.Atomic(AtomicOperation.MinimumUnsigned, 4, MemoryAddress.Local(2, 0), 6, 8, false)),
      """def op(v2: UInt[32], `v[6:7]`: UInt[64])(using lds: LocalMemory): Unit = {
        |  val v0 = lds.load64(AtomicAlignment.offset(v2 + 0 + 0, bytes = 8))
        |  lds.store64(AtomicAlignment.offset(v2 + 0 + 0, bytes = 8), min(v0, `v[6:7]`))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      defined(Instruction.CompareSwap(4, MemoryAddress.Local(2, 0), 6, 7, 4, returnValue = true)),
      """def op(v2: UInt[32], v7: UInt[32], v6: UInt[32])(using lds: LocalMemory): (v4: UInt[32]) = {
        |  val v0 = lds.load32(AtomicAlignment.offset(v2 + 0 + 0, bytes = 4))
        |  lds.store32(AtomicAlignment.offset(v2 + 0 + 0, bytes = 4), if (v0 == v7) v6 else v0)
        |  (v4 = v0)
        |}""".stripMargin
    )
    assert(
      LaneRenderer.describe(
        Instruction.Atomic(AtomicOperation.Add, 4, MemoryAddress.Buffer(Some(2), 8, None, 0), 6, 4, returnValue = true),
        RenderContext.Names(_ => None, 64)
      ).isEmpty
    )
  }

  test("loads and stores reach memory through the context of the space they address") {
    assertEquals(
      defined(Instruction.Load(4, MemoryAddress.Flat(2, 8), 2, signed = true, LoadPlacement.Full)),
      "def op(`v[2:3]`: UInt[64])(using flat: FlatMemory): (v4: UInt[32]) =\n" +
        "  (v4 = flat.load16(`v[2:3]` + 8).to[SInt[16]].to[UInt[32]])"
    )
    assertEquals(
      defined(Instruction.Load(4, MemoryAddress.Global(2, Some(6), 0), 2, signed = false, LoadPlacement.High)),
      "def op(`s[6:7]`: UInt[64], v2: UInt[32], v4: UInt[32])(using global: GlobalMemory): (v4: UInt[32]) =\n" +
        "  (v4 = (v4.to[UInt[16]].to[UInt[64]] | (global.load16(`s[6:7]` + v2.to[UInt[64]] + 0).to[UInt[64]] << 16)).to[UInt[32]])"
    )
    assertEquals(
      defined(Instruction.Store(4, MemoryAddress.Local(2, 4), 8, high = false)),
      """def op(v2: UInt[32], `v[4:5]`: UInt[64])(using lds: LocalMemory): Unit = {
        |  lds.store64(v2 + 0 + 4, `v[4:5]`)
        |  ()
        |}""".stripMargin
    )
  }

  test("matrix products read each element from the lane that holds it") {
    assertEquals(
      defined(
        Instruction.VMatrix(
          4,
          vector(0),
          vector(2),
          vector(8),
          MatrixShape.of("v_mfma_f32_4x4x1f32", ISA.CDNA2, 64).toOption.get,
          MatrixControls(banks = 4)
        )
      ),
      """def op(a8: UInt[32], v0: UInt[32], v2: UInt[32], mode: UInt[32], a9: UInt[32], a10: UInt[32], a11: UInt[32])(using wave: Wave): (a4: UInt[32], a5: UInt[32], a6: UInt[32], a7: UInt[32]) = {
        |  val v1 = wave.lane()
        |  val v3 = (v1 >> 2).to[UInt[4]].to[UInt[64]] << 2
        |  val v4 = MatrixArithmetic.input(wave.read(v2, v1.to[UInt[6]].to[UInt[32]]).asFloat, mode)
        |  (
        |    a4 = MatrixArithmetic.fma(MatrixArithmetic.input(wave.read(v0, v3.to[UInt[32]]).asFloat, mode), v4, a8.asFloat, mode).bits,
        |    a5 = MatrixArithmetic.fma(MatrixArithmetic.input(wave.read(v0, (v3 + 1).to[UInt[32]]).asFloat, mode), v4, a9.asFloat, mode).bits,
        |    a6 = MatrixArithmetic.fma(MatrixArithmetic.input(wave.read(v0, (v3 + 2).to[UInt[32]]).asFloat, mode), v4, a10.asFloat, mode).bits,
        |    a7 = MatrixArithmetic.fma(MatrixArithmetic.input(wave.read(v0, (v3 + 3).to[UInt[32]]).asFloat, mode), v4, a11.asFloat, mode).bits
        |  )
        |}""".stripMargin
    )
  }

  test("wrappers define the operation they run before their own result") {
    assertEquals(
      defined(
        Instruction.VHalfResult(
          Instruction.VBinary(VectorBinaryOperation.AddF16, 3, vector(1), vector(2), clamp = false),
          3,
          high = true
        )
      ),
      """def op(v3: UInt[32], v1: UInt[32], v2: UInt[32], mode: UInt[32]): (v3: UInt[32]) = {
        |  val v0 = mode & 64
        |  val v4 = Floats.add(if (((v1 & 0x7c00) | v0) == UInt[32](0)) (v1 & -0x8000).to[UInt[16]].asFloat else v1.to[UInt[16]].asFloat, if (((v2 & 0x7c00) | v0) == UInt[32](0)) (v2 & -0x8000).to[UInt[16]].asFloat else v2.to[UInt[16]].asFloat).bits
        |  (v3 = (v3.to[UInt[16]].to[UInt[64]] | ((if (((v4 & 0x7c00).to[UInt[32]] | (mode & 128)) == UInt[32](0)) (v4 & -0x8000).to[UInt[64]] else v4.to[UInt[64]]) << 16)).to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      defined(
        Instruction.VDPP(
          Instruction.VMove(3, InstructionSource.DPP),
          1,
          DPPControl.DPP16(0x111, rowMask = 15, bankMask = 15, boundControl = false),
          fetchInactive = false,
          wide = false
        )
      ),
      """def op(v1: UInt[32], v3: UInt[32])(using wave: Wave): (v3: UInt[32]) = {
        |  val v0 = wave.lane()
        |  val v2 = if (v0.to[UInt[4]] >= 1) v0.to[UInt[64]] - 1 else UInt[64](-1)
        |  val v4 = v2.to[SInt[64]]
        |  val v5 = v4 >= 0 && v4 < 64 && (wave.exec() >> v2).to[UInt[1]] != UInt[1](0)
        |  (v3 = if (!v5) v3 else if (v5) wave.read(v1, v2.to[UInt[32]]) else UInt[32](0))
        |}""".stripMargin
    )
    assert(
      defined(
        Instruction.VDPP(
          Instruction.VCompare(
            VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Less, updateExec = true),
            106,
            InstructionSource.DPP,
            vector(2)
          ),
          1,
          DPPControl.DPP16(0x111, rowMask = 14, bankMask = 15, boundControl = false),
          fetchInactive = false,
          wide = false
        )
      ).contains("exec = "),
      "a DPP compare-and-update writes EXEC"
    )
  }

  test("DPP into an operation without a definition has none either") {
    // OMOD applies by the MODE register, which definitions do not model
    val scaled = Instruction.VBinary(VectorBinaryOperation.AddF32, 0, InstructionSource.DPP, vector(2), clamp = false)
    val routed = Instruction.VDPP(
      Instruction.VOutputModified(scaled, scaled, 0, OutputScale.Multiply2, 32, clamp = false),
      1,
      DPPControl.DPP8(0xfac688),
      fetchInactive = false,
      wide = false
    )
    assertEquals(LaneRenderer.describe(routed, RenderContext.Names(_ => None, 64)), None)
  }

  test("SDWA sign extension widens the placed selection before it fills the dword") {
    assertEquals(
      defined(
        Instruction.VBinarySubDword(
          VectorSubDwordOperation.AddU32,
          0,
          vector(1),
          vector(2),
          SubDwordSelection.Word0,
          SubDwordDestinationUnused.SignExtend,
          SubDwordSelection.DWord,
          SubDwordSelection.DWord,
          srcASignExtend = false,
          srcBSignExtend = false
        )
      ),
      "def op(v1: UInt[32], v2: UInt[32]): (v0: UInt[32]) =\n  (v0 = ((v1 >> 0) + (v2 >> 0) << 0).to[UInt[16]].to[SInt[16]].to[UInt[32]])"
    )
  }

  test("assembled instructions take the names their spellings map to, and the target's lane mask width") {
    def describe(target: Target, text: String, operands: (String, String)*): Option[String] =
      InstructionSemantics.describe(
        target,
        text,
        operands.toMap,
        Vec.from(operands.map(_._2))
      ).toOption.map(ScalaBackend.show(_))
    assertEquals(
      describe(
        Target("gfx906"),
        "s_add_u32 s0, s1, s2",
        "s0"  -> "SDST",
        "s1"  -> "SSRC0",
        "s2"  -> "SSRC1",
        "scc" -> "SSRC_SPECIAL_SCC"
      ),
      Some(
        """def s_add_u32(SSRC0: UInt[32], SSRC1: UInt[32]): (SDST: UInt[32], SSRC_SPECIAL_SCC: Bool) =
          |  (
          |    SDST = SSRC0 + SSRC1,
          |    SSRC_SPECIAL_SCC = SSRC0.to[UInt[64]] + SSRC1.to[UInt[64]] > 0xffffffffL
          |  )""".stripMargin
      )
    )
    assertEquals(
      describe(Target("gfx906"), "v_add_f64 v[0:1], v[2:3], v[4:5]", "v[0:1]" -> "VDST", "v[2:3]" -> "SRC0"),
      Some(
        "def v_add_f64(SRC0: UInt[64], `v[4:5]`: UInt[64]): (VDST: UInt[64]) =\n" +
          "  (VDST = Floats.add(SRC0.asFloat, `v[4:5]`.asFloat).bits)"
      )
    )
    assertEquals(
      describe(Target("gfx1030"), "v_add_nc_u32_e32 v0, 0x1234, v1", "v0" -> "VDST", "0x1234" -> "LITERAL"),
      Some("def v_add_nc_u32_e32(LITERAL: UInt[32], v1: UInt[32]): (VDST: UInt[32]) =\n  (VDST = LITERAL + v1)")
    )
    assertEquals(
      describe(Target("gfx1030"), "v_cmp_lt_u32_e32 vcc, v0, v1", "vcc" -> "VCC", "v0" -> "SRC0", "v1" -> "VSRC1"),
      Some(
        "def v_cmp_lt_u32_e32(SRC0: UInt[32], VSRC1: UInt[32])(using wave: Wave): (VCC: UInt[32]) =\n" +
          "  (VCC = wave.ballot(SRC0 < VSRC1).to[UInt[32]])"
      )
    )
    assertEquals(describe(Target("gfx906"), "s_add_u32 s0, s1"), None)
    assertEquals(
      describe(Target("gfx906"), "global_load_dword v0, v[2:3], off", "v0" -> "VDST", "v[2:3]" -> "ADDR"),
      Some(
        "def global_load_dword(ADDR: UInt[64])(using global: GlobalMemory): (VDST: UInt[32]) =\n  (VDST = global.load32(ADDR + 0))"
      )
    )
    assertEquals(describe(Target("gfx906"), "s_endpgm"), None)
  }

  test("an instruction without a definition says why") {
    val target = Target("gfx906")
    assertEquals(
      InstructionSemantics.describe(target, "s_endpgm", Map.empty),
      Left(Unrendered.Unstaged(Vec("the end of the wave")))
    )
    assert(InstructionSemantics.describe(target, "s_bogus s0", Map.empty) match {
      case Left(Unrendered.Malformed(reason)) => reason.startsWith("s_bogus s0 does not ")
      case _                                  => false
    })
  }

  test("buffer accesses address through their descriptor operands, out of range reading zero and writing nothing") {
    def describe(target: Target, text: String, operands: (String, String)*): Option[String] =
      InstructionSemantics.describe(
        target,
        text,
        operands.toMap,
        Vec.from(operands.map(_._2))
      ).toOption.map(ScalaBackend.show(_))
    val descriptor = Seq("s0" -> "SRSRC", "s1" -> "SRSRC+1", "s2" -> "SRSRC+2", "s3" -> "SRSRC+3")
    assertEquals(
      describe(
        Target("gfx1030"),
        "buffer_store_dword v0, v1, s[0:3], s4 offen offset:8",
        (Seq("v0" -> "VDATA", "v1" -> "VADDR") ++ descriptor :+ ("s4" -> "SOFFSET"))*
      ),
      Some(
        """def buffer_store_dword(VDATA: UInt[32], VADDR: UInt[32], SRSRC: UInt[32], `SRSRC+1`: UInt[32], `SRSRC+2`: UInt[32], `SRSRC+3`: UInt[32], SOFFSET: UInt[32])(using wave: Wave, scratch: PrivateMemory, global: GlobalMemory): Unit = {
          |  val v0 = BufferResource.base(SRSRC, `SRSRC+1`)
          |  val v1 = BufferResource.index(generation = ResourceGeneration.GFX10, `SRSRC+3`, indexed = false, UInt[32](0), wave.lane())
          |  val v2 = BufferResource.valid(generation = ResourceGeneration.GFX10, `SRSRC+1`, `SRSRC+2`, `SRSRC+3`, indexed = false, v1, VADDR + 8, SOFFSET, bytes = 4, component = 4)
          |  val v3 = VADDR.to[UInt[64]] + 8 + (SOFFSET / 32).to[UInt[64]]
          |  val v4 = v0 + SOFFSET.to[UInt[64]] + BufferResource.bufferOffset(generation = ResourceGeneration.GFX10, `SRSRC+1`, `SRSRC+3`, indexed = false, v1, VADDR + 8)
          |  if (v0 == UInt[64](0)) scratch.store32(v3, VDATA) else ()
          |  if (v0 != UInt[64](0) && v2 > 0) global.store32(v4, VDATA) else ()
          |  ()
          |}""".stripMargin
      )
    )
    assertEquals(
      describe(
        Target("gfx1030"),
        "image_load v[0:1], v[2:3], s[0:7] dmask:0x3 dim:SQ_RSRC_IMG_2D",
        (Seq("v0" -> "VDATA", "v1" -> "VDATA+1", "v2" -> "VADDR", "v3" -> "VADDR+1") ++ descriptor ++
          Seq("s4" -> "SRSRC+4", "s5" -> "SRSRC+5", "s6" -> "SRSRC+6", "s7" -> "SRSRC+7"))*
      ),
      Some(
        """def image_load(VADDR: UInt[32], `VADDR+1`: UInt[32], SRSRC: UInt[32], `SRSRC+1`: UInt[32], `SRSRC+2`: UInt[32], `SRSRC+3`: UInt[32], `SRSRC+4`: UInt[32], `SRSRC+5`: UInt[32], `SRSRC+6`: UInt[32], `SRSRC+7`: UInt[32])(using images: Images, global: GlobalMemory): (VDATA: UInt[32], `VDATA+1`: UInt[32]) = {
          |  val v0 = images.texel(ISA.RDNA2, SRSRC, `SRSRC+1`, `SRSRC+2`, `SRSRC+3`, `SRSRC+4`, `SRSRC+5`, `SRSRC+6`, `SRSRC+7`, VADDR, `VADDR+1`, UInt[32](0), UInt[32](0), UInt[32](0), dimension = Some(ImageDimension.Image2d), mip = false)
          |  val v1 = ImageResource.texelBytes(generation = ResourceGeneration.GFX10, `SRSRC+1`)
          |  val v2 = if (v0 != UInt[64](-1)) global.load(v0, min(v1, UInt[32](8))) else UInt[64](0)
          |  val v3 = if (v0 != UInt[64](-1) && v1 > 8) global.load(v0 + 8, v1 - 8) else UInt[64](0)
          |  (
          |    VDATA = ImageResource.component(generation = ResourceGeneration.GFX10, `SRSRC+1`, `SRSRC+3`, v2, v3, component = 0, half = false),
          |    `VDATA+1` = ImageResource.component(generation = ResourceGeneration.GFX10, `SRSRC+1`, `SRSRC+3`, v2, v3, component = 1, half = false)
          |  )
          |}""".stripMargin
      )
    )
  }

  /** What each name holds after `steps`, as a term over what the names held before. */
  private def written(steps: Term.Steps): Map[String, Term] =
    steps.all.foldLeft(Map.empty[String, Term])((values, step) =>
      values + (step.name -> step.value.substitute(values.get))
    )

  private val Extension = raw"([sz])ext(\d+)".r

  /** An integer term's value where each variable holds what `values` gives it. */
  private def evaluated(term: Term, values: Map[String, Long]): Long = {
    def value(term: Term): Long = evaluated(term, values)
    term match {
      case Term.Variable(name)                            => values(name)
      case Term.Literal(literal)                          => literal
      case Term.Unary("~", operand)                       => ~value(operand)
      case Term.Call(Extension(kind, bits), Vec(operand)) =>
        val width = bits.toInt.unsafe[Int in 1 ~ 64]
        if (kind == "z") Bits.truncate(value(operand), width) else Bits.signExtend(value(operand), width)
      case Term.Binary(operator, left, right) =>
        val (a, b) = (value(left), value(right))
        operator match {
          case "+"   => a + b
          case "-"   => a - b
          case "&"   => a & b
          case "|"   => a | b
          case "^"   => a ^ b
          case "<<"  => a << b.toInt
          case ">>u" => a >>> b.toInt
          case ">>s" => a >> b.toInt
          case other => fail(s"no evaluation of $other")
        }
      case other => fail(s"no evaluation of $other")
    }
  }

  test("a wave64 write to half of VCC or EXEC keeps the other half, as execution does") {
    val target                                                     = Target("gfx906")
    val vcc                                                        = 0x1234567889abcdefL
    val value                                                      = 0xcafef00dL
    val previous                                                   = Map("vcc" -> vcc, "exec" -> vcc, "s2" -> value)
    def rendered(register: ScalarRegisterIndex): Map[String, Long] = {
      val steps = LaneRenderer.describe(Instruction.SMove(register, scalar(2)), RenderContext.Names(_ => None, 64))
        .getOrElse(fail("unstaged"))
      previous ++ written(steps).view.mapValues(evaluated(_, previous))
    }
    def executed(register: ScalarRegisterIndex): (low: Long, high: Long) = {
      val setup = Vec(
        Instruction.SMove64(Fixed.VccLo, InstructionSource.Constant(vcc)),
        Instruction.SMove(register, scalar(2))
      )
      (
        scalarResult(target, Vec(0L, 0L, value), setup, Fixed.VccLo),
        scalarResult(target, Vec(0L, 0L, value), setup, Fixed.VccHi)
      )
    }
    val (low, high) = executed(Fixed.VccLo)
    assertEquals((low, high), (value, vcc >>> 32))
    assertEquals(rendered(Fixed.VccLo)("vcc"), (high << 32) | low)
    val (kept, replaced) = executed(Fixed.VccHi)
    assertEquals((kept, replaced), (vcc & 0xffffffffL, value))
    assertEquals(rendered(Fixed.VccHi)("vcc"), (replaced << 32) | kept)
    assertEquals(rendered(Fixed.ExecHi)("exec"), (value << 32) | (vcc & 0xffffffffL))
  }

  test("a block of results reads its sources before it writes any, as execution does") {
    def defined(dest: VectorRegisterIndex): Map[String, Term] = {
      val conversion = Instruction.VScaledConvert(
        ScaledConversion(Minifloats.Wide.Single, ScaledConversion.Layout.WidenPair(Minifloat.E4M3)),
        dest,
        vector(0),
        vector(2),
        InstructionSource.Constant(0L),
        selection = 0,
        SourceModifiers.Unmodified
      )
      written(LaneRenderer.describe(conversion, RenderContext.Names(_ => None, 64)).getOrElse(fail("unstaged")))
    }
    val (overlapping, disjoint) = (defined(0), defined(4))
    assertEquals(overlapping("v0"), disjoint("v4"))
    assertEquals(overlapping("v1"), disjoint("v5"))
  }

  test("a sequence with a part that has no definition has none either") {
    val sinks   = LaneRenderer(RenderContext.Names(_ => None, 64), None)
    val defined = Some(Term.Steps(Term.Step("v0", Term.Literal(1L))))
    assertEquals(sinks.sequence(defined, None), None)
    assertEquals(sinks.sequence(None, defined), None)
  }
}
