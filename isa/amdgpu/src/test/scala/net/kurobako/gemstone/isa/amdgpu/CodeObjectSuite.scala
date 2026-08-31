package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.{Bits, ByteRange}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

class CodeObjectSuite extends munit.FunSuite {
  test("a real ELF index validates descriptors and materializes one shared kernel lazily") {
    val image   = minimalCodeObject()
    val indexed = CodeObject.index(Col.unsafeFromArray(image)).toOption.get
    assertEquals(indexed.target, Target.unsafe("gfx900"))
    assertEquals(indexed.kernelNames, Set("kernel"))

    // The index owns its image: mutating the caller's input after indexing must
    // not affect later lazy kernel materialization.
    java.util.Arrays.fill(image, 0.toByte)
    val pool = Executors.newFixedThreadPool(4)
    try {
      val pending = Col.fill(16)(pool.submit(() => indexed.kernel("kernel").toOption.get))
      val kernels = pending.map(_.get())
      val kernel  = kernels.head
      assert(kernels.forall(_ eq kernel), "concurrent materialization returned distinct kernels")
      assertEquals(kernel.name, "kernel")
      assertEquals(kernel.target, Target.unsafe("gfx900"))
      assertEquals(kernel.kernargSize, 24)
      assertEquals(kernel.groupSegmentSize, 16)
      assertEquals(kernel.privateSegmentSize, 8)
      assertEquals(kernel.computePgmRsrc2, 0x80)
      assertEquals(kernel.kernelCodeProperties, 0x0a)
      assertEquals(kernel.code, LittleEndian.toCol(0xbf810000L, 4))
      assertEquals(kernel.readOnlyAddress, 0x2000L)
      assert(kernel.readOnly.length > 64)
    } finally pool.shutdownNow()
  }

  test("linked code objects infer zero-sized kernel symbols from the next function") {
    assertEquals(CodeObject.inferredFunctionSize(0x1000L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x180))
    assertEquals(CodeObject.inferredFunctionSize(0x1300L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x100))
    assertEquals(CodeObject.inferredFunctionSize(0x1400L, 0x1400L, Seq.empty), None)
  }

  test("inferred linked functions discard alignment padding after their final end-program") {
    val code = Array[Byte](
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x00,
      0x00
    )
    assertEquals(
      CodeObject.trimTrailingKernelPadding(code).toSeq,
      code.take(4).toSeq
    )
  }

  test("gfx900 conditional scalar move retains the destination when SCC is clear") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x05, 0x02, 0x84.toByte, 0xbe.toByte),
        Target.unsafe("gfx900")
      ),
      Right(
        DecodedInstruction.SCSelect(
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(4),
          32
        )
      )
    )
  }

  test("gfx900 scalar set-PC decodes its 64-bit source") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x1e, 0x1d, 0x80.toByte, 0xbe.toByte),
        Target.unsafe("gfx900")
      ),
      Right(DecodedInstruction.SSetPc(InstructionSource.Scalar(30)))
    )
  }

  test("gfx900 scalar priority hint validates as a virtual no-op") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x41, 0x31, 0x8f.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(DecodedInstruction.Wait)
    )
  }

  test("gfx900 VOP3 CMPX reuses the VOPC opcode and decodes its scalar mask destination") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x36, 0x00, 0xda.toByte, 0xd0.toByte, 0x0c, 0x17, 0x02, 0x00),
        Target.unsafe("gfx900")
      ),
      Right(
        DecodedInstruction.VCompareTo(
          54,
          "v_cmpx_eq_u32",
          InstructionSource.VectorRegister(12),
          InstructionSource.VectorRegister(11),
          false,
          false,
          false,
          false
        )
      )
    )
  }

  test("gfx900 packed-half FMA decoding preserves low and high operand selection") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x00, 0x40, 0x8e.toByte, 0xd3.toByte, 0x02, 0x09, 0x02, 0x0c),
        Target.unsafe("gfx900")
      ),
      Right(
        DecodedInstruction.VPackedFmaF16(
          0,
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(4),
          InstructionSource.VectorRegister(0),
          lowSelection = 0,
          highSelection = 5,
          negateLow = 0,
          negateHigh = 0,
          clamp = false
        )
      )
    )
  }

  test("gfx900 packed-half FMA applies operand selection, negation, and clamp independently") {
    def packed(low: Float, high: Float): Long =
      Bits.low16(IEEE754.floatToBinary16(low)) | (Bits.low16(IEEE754.floatToBinary16(high)) << 16)

    assertEquals(
      DispatchInterpreter.packedFmaF16(
        packed(2.0f, 3.0f),
        packed(4.0f, 5.0f),
        packed(6.0f, 7.0f),
        lowSelection = 0,
        highSelection = 5,
        negateLow = 0,
        negateHigh = 0,
        clamp = false
      ),
      packed(14.0f, 19.0f)
    )
    assertEquals(
      DispatchInterpreter.packedFmaF16(
        packed(2.0f, 3.0f),
        packed(4.0f, 5.0f),
        packed(6.0f, 7.0f),
        lowSelection = 0,
        highSelection = 7,
        negateLow = 1,
        negateHigh = 4,
        clamp = true
      ),
      packed(0.0f, 1.0f)
    )
  }

  test("linked code-object kernels infer an omitted kernarg size from typed launch arguments") {
    val kernel = CodeObjectKernel(
      "linked",
      Target.unsafe("gfx900"),
      0,
      0,
      0,
      0,
      0,
      Col.empty,
      codeAddress = 0x1000L
    )
    val allocation = AllocationId.fromLong(1L).toOption.get
    assertEquals(
      DispatchInterpreter.effectiveKernargSize(
        kernel,
        Col(KernelArgument.Allocation(allocation, 0L), KernelArgument.Scalar(Col.fill(12)(0.toByte)))
      ),
      Right(20)
    )
    assertEquals(DispatchInterpreter.effectiveKernargSize(kernel.copy(codeAddress = 0L), Col.empty), Right(0))
  }

  test("function aliases resolve deterministically and reject conflicting call semantics") {
    assertEquals(
      CodeObject.selectFunctionTarget(Col("alias", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Col("_Z9aliasLogIfv", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Col("_Z5logIfv", "_Z5expIfv")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases have conflicting call semantics: _Z5expIfv, _Z5logIfv"))
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Col("first", "second")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases are ambiguous: first, second"))
    )
  }

  test("packed executable instructions retain typed VMove construction and extraction") {
    val scalar   = ExecutableInstruction.VMove(7, InstructionSource.Scalar(23))
    val vector   = ExecutableInstruction.VMove(8, InstructionSource.VectorRegister(42))
    val constant = ExecutableInstruction.VMove(9, InstructionSource.Constant(0xdeadbeefL))

    assertEquals(ExecutableInstruction.VMove.unapply(scalar), Some(7 -> InstructionSource.Scalar(23)))
    assertEquals(ExecutableInstruction.VMove.unapply(vector), Some(8 -> InstructionSource.VectorRegister(42)))
    assertEquals(ExecutableInstruction.VMove.unapply(constant), Some(9 -> InstructionSource.Constant(0xdeadbeefL)))
    assertEquals(ExecutableInstruction.VMove.unapply(ExecutableInstruction.Fallback), None)
  }

  test("packed executable memory and binary instructions retain typed construction and extraction") {
    val load   = ExecutableInstruction.GlobalLoad(7, 8, Some(10), 4, 16)
    val store  = ExecutableInstruction.GlobalStore(8, None, 7, 8, 24)
    val binary = ExecutableInstruction.VBinary(
      "v_max_f16",
      9,
      InstructionSource.VectorRegister(7),
      InstructionSource.Scalar(12),
      clamp = false
    )

    assertEquals(ExecutableInstruction.GlobalLoad.unapply(load), Some((7, 8, Some(10), 4, 16)))
    assertEquals(ExecutableInstruction.GlobalStore.unapply(store), Some((8, None, 7, 8, 24)))
    assertEquals(
      ExecutableInstruction.VBinary.unapply(binary),
      Some(("v_max_f16", 9, InstructionSource.VectorRegister(7), InstructionSource.Scalar(12), false))
    )
  }

  test("packed executable memory instructions fall back when their offset is not representable") {
    val negative = ExecutableInstruction(
      DecodedInstruction.GlobalLoad(7, 8, None, 4, -1)
    )
    val tooLarge = ExecutableInstruction(
      DecodedInstruction.GlobalStore(8, None, 7, 4, 0x2000)
    )

    assertEquals(ExecutableInstruction.tag(negative), ExecutableInstruction.FallbackTag)
    assertEquals(ExecutableInstruction.tag(tooLarge), ExecutableInstruction.FallbackTag)
  }

  test("gfx900 single-offset DS instructions retain all 16 offset bits") {
    assertEquals(DispatchInterpreter.singleDSOffset(0xd83e0200L), 512)
    assertEquals(DispatchInterpreter.singleDSOffset(0xd8781e00L), 7680)
  }

  test("gfx900 V_PERM_B32 selectors address the second source before the first") {
    assertEquals(
      DispatchInterpreter.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x05040100L),
      0xa1a0b1b0L
    )
  }

  test("gfx900 scalar unsigned multiply returns the high 32 bits") {
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh32(0xffffffffL, 0xffffffffL), 0xfffffffeL)
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh32(0x12345678L, 0x100000000L), 0L)
  }

  test("gfx900 signed multiply returns the high 32 bits") {
    assertEquals(DispatchInterpreter.signedMultiplyHigh32(0x7fffffffL, 0x7fffffffL), 0x3fffffffL)
    assertEquals(DispatchInterpreter.signedMultiplyHigh32(0x80000000L, 1L), 0xffffffffL)
    assertEquals(DispatchInterpreter.signedMultiplyHigh32(0x80000000L, 0x80000000L), 0x40000000L)
  }

  test("gfx900 vector unsigned 24-bit multiply-high returns the high 32 bits") {
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh24(0xffffffL, 0xffffffL), 0xffffL)
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh24(0x1000000L, 0xffffffL), 0L)
  }

  test("gfx900 ANDN2_SAVEEXEC uses source AND NOT old EXEC") {
    assertEquals(DispatchInterpreter.andn2SaveExecMask(0x0f0fL, 0x3333L), 0x3030L)
    assertEquals(
      DispatchInterpreter.andn2SaveExecMask(0xf000000000000000L, 0x3000000000000000L),
      0L
    )
  }

  test("gfx900 signed first-bit-high counts leading sign bits") {
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(0L), 0xffffffffL)
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(0xffffffffL), 0xffffffffL)
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(1L), 31L)
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(0x40000000L), 1L)
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(0x80000000L), 1L)
    assertEquals(DispatchInterpreter.signedFirstBitHigh32(0xfffffffeL), 31L)
  }

  test("gfx900 MUBUF cache invalidation instructions validate") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0xfc.toByte,
          0xe0.toByte,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 MUBUF offen load validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x10,
          0x50,
          0xe0.toByte,
          0x01,
          0x07,
          0x00,
          0x80.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 MUBUF load retains its scalar resource descriptor") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x00, 0x10, 0x50, 0xe0.toByte, 0x12, 0x13, 0x05, 0x80.toByte),
        Target.unsafe("gfx900")
      ),
      Right(DecodedInstruction.BufferLoad(19, Some(18), 20, None, 4, 0))
    )
  }

  test("gfx900 MUBUF short D16 decoding preserves resource and half selection") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x10, 0x10, 0x90.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target.unsafe("gfx900")
      ),
      Right(DecodedInstruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = false))
    )
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Col[Byte](0x10, 0x10, 0x94.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target.unsafe("gfx900")
      ),
      Right(DecodedInstruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = true))
    )
  }

  test("gfx900 MUBUF short D16 loads replace only the selected half") {
    assertEquals(DispatchInterpreter.mergeD16(0x11223344L, 0xaabbL, high = false), 0x1122aabbL)
    assertEquals(DispatchInterpreter.mergeD16(0x11223344L, 0xaabbL, high = true), 0xaabb3344L)
    assertEquals(DispatchInterpreter.mergeD16(0xfeed11223344L, 0xffffaabbL, high = false), 0x1122aabbL)
  }

  test("gfx900 VOP2 unsigned 24-bit multiply-high validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x01,
          0x05,
          0x0a,
          0x18,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 signed maximum of three validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xff.toByte,
          0x00,
          0xd4.toByte,
          0xd1.toByte,
          0x01,
          0x05,
          0x0e,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 signed multiply-high validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0x87.toByte,
          0xd2.toByte,
          0x01,
          0x05,
          0x02,
          0x00
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 signed 64-bit multiply-add decodes its carry destination") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0xfe.toByte,
        0x0c,
        0xe9.toByte,
        0xd1.toByte,
        0x01,
        0x05,
        0x0e,
        0x04
      ),
      Target.unsafe("gfx900")
    )

    instruction match {
      case Right(
            DecodedInstruction.VMadI64I32(
              254,
              12,
              InstructionSource.VectorRegister(1),
              InstructionSource.VectorRegister(2),
              InstructionSource.VectorRegister(3)
            )
          ) => ()
      case other => fail(s"decoded VOP3 signed 64-bit multiply-add incorrectly: $other")
    }
  }

  test("gfx900 VOP1 half-to-single conversion decodes") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](0x01, 0x17, 0x0a, 0x7e.toByte),
      Target.unsafe("gfx900")
    )

    assertEquals(
      instruction,
      Right(DecodedInstruction.VConvert("v_cvt_f32_f16", 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx900 VOP1 aperture source encodings decode") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](0xeb.toByte, 0x02, 0x00, 0x7e.toByte),
      Target.unsafe("gfx900")
    )

    assertEquals(instruction, Right(DecodedInstruction.VMove(0, InstructionSource.Constant(0L))))
  }

  test("gfx900 VOP1 single-to-half conversion decodes") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](0x01, 0x15, 0x0a, 0x7e.toByte),
      Target.unsafe("gfx900")
    )

    assertEquals(
      instruction,
      Right(DecodedInstruction.VConvert("v_cvt_f16_f32", 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx900 VOP1 half-to-signed-16 conversion decodes") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](0x01, 0x79, 0x0a, 0x7e.toByte),
      Target.unsafe("gfx900")
    )

    assertEquals(
      instruction,
      Right(DecodedInstruction.VConvert("v_cvt_i16_f16", 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx900 VOP3 signed median of three decodes") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](0x05, 0x00, 0xd7.toByte, 0xd1.toByte, 0x01, 0x05, 0x0e, 0x04),
      Target.unsafe("gfx900")
    )

    assertEquals(
      instruction,
      Right(
        DecodedInstruction.VTernary(
          "v_med3_i32",
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      )
    )
  }

  test("gfx900 VOP3 signed 16-bit maximum of three validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xff.toByte,
          0x00,
          0xf8.toByte,
          0xd1.toByte,
          0x01,
          0x05,
          0x0e,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 SDWA low 16-bit multiplication validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf9.toByte,
          0x06,
          0x2e,
          0x52,
          0x16,
          0x06,
          0x06,
          0x05,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 SDWA signed 24-bit multiplication preserves the decoded sub-dword operands") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0xf9.toByte,
        0x00,
        0x3c,
        0x0c,
        0x35,
        0x06,
        0x0c,
        0x86.toByte
      ),
      Target.unsafe("gfx900")
    )

    instruction match {
      case Right(
            DecodedInstruction.VBinarySubDword(
              actualOpcode,
              30,
              InstructionSource.VectorRegister(53),
              InstructionSource.Scalar(0),
              SubDwordSelection.DWord,
              SubDwordDestinationUnused.Pad,
              SubDwordSelection.Word0,
              SubDwordSelection.DWord,
              true,
              false
            )
          ) => assertEquals(actualOpcode, "v_mul_i32_i24")
      case other => fail(s"decoded SDWA instruction lost its operand selections: $other")
    }
  }

  test("gfx900 SDWA signed 16-bit maximum validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf9.toByte,
          0x20,
          0x12,
          0x60,
          0x05,
          0x06,
          0x08,
          0x08,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 SDWA half-precision minimum validates and uses floating-point ordering") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf9.toByte,
          0x04,
          0x02,
          0x5c,
          0x03,
          0x04,
          0x04,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
    val positive = IEEE754.floatToBinary16(1.5f)
    val negative = IEEE754.floatToBinary16(-2.0f)
    assertEquals(
      IEEE754.binary16ToFloat(DispatchInterpreter.minimumF16(positive, negative)),
      -2.0f
    )
  }

  test("gfx900 VOP2 half-precision maximum validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x01,
          0x05,
          0x02,
          0x5a,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOPC half-precision not-less-than validates and includes unordered values") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x01,
          0x05,
          0x5c,
          0x7c,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
    val one = IEEE754.floatToBinary16(1.0f)
    val two = IEEE754.floatToBinary16(2.0f)
    assert(!DispatchInterpreter.compareF16("v_cmp_nlt_f16", one, two))
    assert(DispatchInterpreter.compareF16("v_cmp_nlt_f16", two, one))
    assert(DispatchInterpreter.compareF16("v_cmp_nlt_f16", 0x7e00L, one))
  }

  test("gfx900 unsupported VOP1 DPP operations fail closed") {
    val result = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0xfa.toByte,
        0x58,
        0x0a,
        0x7e,
        0x01,
        0xe4.toByte,
        0x00,
        0x30
      ),
      Target.unsafe("gfx900")
    )

    assert(result.left.exists(_.contains("v_bfrev_b32 DPP execution is not implemented")))
  }

  test("gfx900 VOP3 f32 binary decoding preserves clamp") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0x01,
        0x80.toByte,
        0x01,
        0xd1.toByte,
        0x03,
        0x0b,
        0x02,
        0x00
      ),
      Target.unsafe("gfx900")
    )

    instruction match {
      case Right(
            DecodedInstruction.VBinaryF32Modified(
              "v_add_f32",
              1,
              InstructionSource.VectorRegister(3),
              InstructionSource.VectorRegister(5),
              false,
              false,
              false,
              false,
              true
            )
          ) => ()
      case other => fail(s"decoded VOP3 f32 clamp was lost: $other")
    }
  }

  test("VOP3 floating-point clamp uses the [0, 1] range and preserves NaN") {
    assertEquals(DispatchInterpreter.clampF32(-2.0f), 0.0f)
    assertEquals(DispatchInterpreter.clampF32(0.5f), 0.5f)
    assertEquals(DispatchInterpreter.clampF32(2.0f), 1.0f)
    assert(DispatchInterpreter.clampF32(Float.NaN).isNaN)
  }

  test("gfx900 VOPC SDWA preserves a scalar source-0 encoding") {
    val instruction = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0xf9.toByte,
        0x04,
        0x94.toByte,
        0x7d,
        0x03,
        0x80.toByte,
        0x86.toByte,
        0x06
      ),
      Target.unsafe("gfx900")
    )

    instruction match {
      case Right(
            DecodedInstruction.VCompareSubDword(
              "v_cmp_eq_u32",
              InstructionSource.Scalar(3),
              InstructionSource.VectorRegister(2),
              SubDwordSelection.DWord,
              SubDwordSelection.DWord,
              false,
              false,
              Some(0)
            )
          ) => ()
      case other => fail(s"decoded VOPC SDWA source-0 mode was lost: $other")
    }
  }

  test("gfx1100 VOP3 DPP instructions fail closed") {
    val result = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0x05,
        0x00,
        0x55,
        0xd6.toByte,
        0xfa.toByte,
        0x04,
        0x0e,
        0x04,
        0x01,
        0x1b,
        0x00,
        0xff.toByte
      ),
      Target.unsafe("gfx1100")
    )

    if (!result.left.exists(_.contains("source encoding 250"))) fail(s"unexpected VOP3 DPP result: $result")
  }

  test("gfx1010 VOP3 literal instructions fail closed") {
    val result = DispatchInterpreter.decodeInstruction(
      Col[Byte](
        0x01,
        0x01,
        0x03,
        0xd5.toByte,
        0xff.toByte,
        0x06,
        0x02,
        0x22,
        0x23,
        0x01,
        0x00,
        0x00
      ),
      Target.unsafe("gfx1010")
    )

    assert(result.left.exists(_.contains("missing literal dword")))
  }

  test("gfx900 SDWA 32-bit add honors byte source selections") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf9.toByte,
          0x00,
          0x02,
          0x68,
          0x00,
          0x06,
          0x01,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 packed low unsigned multiplication validates") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xff.toByte,
          0x40,
          0x81.toByte,
          0xd3.toByte,
          0x01,
          0x05,
          0x02,
          0x18,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 scalar unsigned minimum accepts a literal operand") {
    assertEquals(DispatchInterpreter.unsignedMin32(300L, 256L), 256L)
    assertEquals(DispatchInterpreter.unsignedMin32(0xffffffffL, 256L), 256L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x02,
          0xff.toByte,
          0x84.toByte,
          0x83.toByte,
          0x00,
          0x01,
          0x00,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 scalar min/max SCC reports whether source zero won") {
    assertEquals(DispatchInterpreter.scalarMinMax("s_min_u32", 3L, 4L), (3L, true))
    assertEquals(DispatchInterpreter.scalarMinMax("s_min_u32", 4L, 3L), (3L, false))
    assertEquals(DispatchInterpreter.scalarMinMax("s_max_u32", 4L, 3L), (4L, true))
    assertEquals(DispatchInterpreter.scalarMinMax("s_max_u32", 3L, 4L), (4L, false))
    assertEquals(DispatchInterpreter.scalarMinMax("s_min_i32", 0xffffffffL, 1L), (0xffffffffL, true))
    assertEquals(DispatchInterpreter.scalarMinMax("s_max_i32", 0xffffffffL, 1L), (1L, false))
    assertEquals(DispatchInterpreter.scalarMinMax("s_min_u32", 7L, 7L), (7L, false))
    assertEquals(DispatchInterpreter.scalarMinMax("s_max_i32", 7L, 7L), (7L, false))
  }

  test("gfx900 initial work-item VGPRs use multidimensional local IDs") {
    assertEquals(DispatchInterpreter.workItemIds(0, 64, 16), (0, 0, 0))
    assertEquals(DispatchInterpreter.workItemIds(63, 64, 16), (63, 0, 0))
    assertEquals(DispatchInterpreter.workItemIds(64, 64, 16), (0, 1, 0))
    assertEquals(DispatchInterpreter.workItemIds(1023, 64, 16), (63, 15, 0))
    assertEquals(DispatchInterpreter.workItemIds(1024, 64, 16), (0, 0, 1))
  }

  test("gfx900 SOPK signed multiply uses a sign-extended immediate and reports overflow") {
    assertEquals(DispatchInterpreter.signedMultiplyImmediate(0xffffffffL, 0x300), (0xfffffd00L, false))
    assertEquals(DispatchInterpreter.signedMultiplyImmediate(Int.MaxValue.toLong, 2), (0xfffffffeL, true))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x00, 0x03, 0x82.toByte, 0xb7.toByte, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 unsigned subtraction returns the low word and no-borrow flag") {
    assertEquals(DispatchInterpreter.unsignedSubtract32(9L, 4L), (5L, true))
    assertEquals(DispatchInterpreter.unsignedSubtract32(0L, 1L), (0xffffffffL, false))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x0e, 0x02, 0x02, 0x34, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 chained subtraction consumes and replaces the no-borrow flag") {
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(9L, 4L, true), (5L, true))
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(9L, 4L, false), (4L, true))
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(0L, 0xffffffffL, false), (0L, false))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x03, 0x05, 0x04, 0x3a, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOPC accepts canonical 16-bit and 64-bit not-equal aliases") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x04, 0x0b, 0x5a, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x80.toByte, 0x02, 0xda.toByte, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 compares signed 64-bit VGPR pairs into an explicit scalar mask") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0xe1.toByte,
          0xd0.toByte,
          0x01,
          0x07,
          0x02,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 mask bit counts compose across both halves of a wave") {
    val executionMask = 0xf000000f0000000fL
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 0, false, 0L), 0L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 4, false, 0L), 4L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 32, false, 0L), 4L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 60, true, 8L), 12L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 64 - 1, true, 8L), 15L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x03,
          0x00,
          0x8c.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x00,
          0x01,
          0x00,
          0x03,
          0x00,
          0x8d.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x06,
          0x02,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 bit reverse accepts canonical inline floating constants as raw bits") {
    assertEquals(DispatchInterpreter.bitReverse32(0x3f000000L), 0x000000fcL)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf0.toByte,
          0x58,
          0x08,
          0x7e,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 unsigned 24-bit vector multiply and multiply-add mask their multiplicands") {
    assertEquals(DispatchInterpreter.unsignedMultiply24(0x1ffffffL, 2L), 0x1fffffeL)
    assertEquals(DispatchInterpreter.unsignedMultiplyAdd24(0xffffffL, 2L, 3L), 0x2000001L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x94.toByte,
          0x00,
          0x08,
          0x10,
          0x05,
          0x00,
          0xc3.toByte,
          0xd1.toByte,
          0x00,
          0x29,
          0x15,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 stride-64 DS pairs decode their independently scaled offsets") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x14,
          0x1e,
          0xd8.toByte,
          0x05,
          0x02,
          0x01,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DS bpermute selects a source lane from a byte address") {
    assertEquals(DispatchInterpreter.bpermuteSourceLane(0L), 0)
    assertEquals(DispatchInterpreter.bpermuteSourceLane(4L * 37L), 37)
    assertEquals(DispatchInterpreter.bpermuteSourceLane(4L * 69L), 5)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0x7e,
          0xd8.toByte,
          0x04,
          0x05,
          0x00,
          0x06,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DS operations strip the local address-space tag") {
    assertEquals(DispatchInterpreter.ldsOffset(0x18000018L), 0x18L)
    assertEquals(DispatchInterpreter.ldsOffset(0x00000018L), 0x18L)
    assertEquals(DispatchInterpreter.ldsOffset(0x00010018L), 0x18L)
  }

  test("gfx900 reverse unsigned vector subtraction wraps to 32 bits") {
    assertEquals(DispatchInterpreter.unsignedSubtractReverse32(20L, 19L), 0xffffffffL)
    assertEquals(DispatchInterpreter.unsignedSubtractReverse32(20L, 99L), 79L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x94.toByte, 0x06, 0x0a, 0x6c, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 signed 24-bit vector multiply sign-extends both operands") {
    assertEquals(DispatchInterpreter.signedMultiply24(0xffffd8L, 0xfffffeL), 80L)
    assertEquals(DispatchInterpreter.signedMultiply24(0x1000001L, 3L), 3L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xff.toByte,
          0x00,
          0x12,
          0x0c,
          0xd8.toByte,
          0xff.toByte,
          0xff.toByte,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DPP routes quad, row-rotate, and row-broadcast sources") {
    assertEquals(
      Col.tabulate(8)(lane => DispatchInterpreter.dppQuadSourceLane(lane, 0xb1)),
      Col(1, 0, 3, 2, 5, 4, 7, 6)
    )
    assert((0 until 64).forall(lane => DispatchInterpreter.dppLaneEnabled(lane, 0xf, 0xf)))
    assert(!DispatchInterpreter.dppLaneEnabled(0, 0xe, 0xf))
    assert(!DispatchInterpreter.dppLaneEnabled(1, 0xf, 0xd))
    assertEquals(
      Col(0, 4, 15, 16).map(DispatchInterpreter.dppSourceLane(_, 0x124)),
      Col(Some(12), Some(0), Some(11), Some(28))
    )
    assertEquals(
      Col(0, 16, 32, 48).map(DispatchInterpreter.dppSourceLane(_, 0x142)),
      Col(None, Some(15), None, Some(47))
    )
    assertEquals(
      Col(0, 16, 32, 48).map(DispatchInterpreter.dppSourceLane(_, 0x143)),
      Col(None, None, Some(31), Some(31))
    )
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xfa.toByte,
          0x02,
          0x06,
          0x7e,
          0x01,
          0xb1.toByte,
          0x00,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x24,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x28,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x42,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x43,
          0x01,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 resolves VCC branches by byte address across mixed instruction widths") {
    val code = Col[Byte](
      0x80.toByte,
      0x02,
      0x00,
      0x7e,
      0x81.toByte,
      0x00,
      0x98.toByte,
      0x7d,
      0x01,
      0x00,
      0x86.toByte,
      0xbf.toByte,
      0x01,
      0x00,
      0x82.toByte,
      0xbf.toByte,
      0xff.toByte,
      0xff.toByte,
      0x82.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte
    )
    val kernel   = CodeObjectKernel("branch", Target.unsafe("gfx900"), 0, 0, 0, 0x80, 0x0a, code)
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "branch",
      Dimensions.One,
      Dimensions.One,
      Col.empty
    ).toOption.get

    assertEquals(DispatchInterpreter.execute(kernel, dispatch, TestMemory()), Right(()))
    val limited = ExecutionControl.create(ExecutionLimits(maxInstructions = 1L, quantum = 1))
    assert(
      DispatchInterpreter.execute(kernel, dispatch, TestMemory(), limited).left
        .exists(_.message.contains("instruction budget of 1"))
    )
  }

  test("gfx900 executes the official ROCr vector-copy kernel subset") {
    val words = Col[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code   = words.flatMap(word => LittleEndian.toCol(word, 4))
    val target = Target.unsafe("gfx900")
    val kernel = CodeObjectKernel("vector_copy", target, 80, 0, 0, 0x80, 0x0b, code)
    val memory = TestMemory()
    val input  = AllocationId.fromLong(1L).toOption.get
    val output = AllocationId.fromLong(2L).toOption.get
    val unused = AllocationId.fromLong(3L).toOption.get
    memory.allocate(input, 256)
    memory.allocate(output, 256)
    memory.allocate(unused, 256)
    val expected = Array.tabulate(64)(index => index * 97 - 11)
    expected.zipWithIndex.foreach { case (value, index) => memory.putInt(input, index * 4, value) }

    val zero      = KernelArgument.Scalar(Col.fill(8)(0.toByte))
    val arguments = Col(
      KernelArgument.Allocation(input, 0L),
      KernelArgument.Allocation(output, 0L),
      KernelArgument.Allocation(unused, 0L),
      zero,
      zero,
      zero,
      zero,
      zero,
      zero,
      zero
    )
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "vector_copy",
      Dimensions.create(64, 1, 1).toOption.get,
      Dimensions.One,
      arguments
    ).toOption.get

    assertEquals(DispatchInterpreter.execute(kernel, dispatch, memory), Right(()))
    assertEquals(Array.tabulate(64)(index => memory.getInt(output, index * 4)).toSeq, expected.toSeq)
  }

  private final class TestMemory extends ExecutionMemory {
    private var allocations = Map.empty[AllocationId, Array[Byte]]

    def allocate(id: AllocationId, size: Int): Unit = allocations = allocations.updated(id, Array.ofDim[Byte](size))

    def putInt(id: AllocationId, offset: Int, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset, 4)

    def getInt(id: AllocationId, offset: Int): Int =
      LittleEndian.decode(allocations(id), offset, 4).toInt

    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          ExecutionFailure.InvalidRequest("test read range")
        )
      }

    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { destination =>
        Either.cond(
          ByteRange.contains(destination.length, offset, bytes.length.toLong), {
            val copied = bytes.copyToArray(destination, offset.toInt, bytes.length)
            assertEquals(copied, bytes.length)
            ()
          },
          ExecutionFailure.InvalidRequest("test write range")
        )
      }

    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  private def minimalCodeObject(): Array[Byte] = {
    def aligned(value: Int): Int = (value + 7) & ~7
    val sectionNames             = "\u0000.shstrtab\u0000.strtab\u0000.symtab\u0000.text\u0000.rodata\u0000"
      .getBytes(StandardCharsets.US_ASCII)
    val symbolNames       = "\u0000kernel\u0000kernel.kd\u0000".getBytes(StandardCharsets.US_ASCII)
    val target            = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(StandardCharsets.US_ASCII)
    val sectionNameOffset = 64
    val symbolNameOffset  = aligned(sectionNameOffset + sectionNames.length)
    val symbolTableOffset = aligned(symbolNameOffset + symbolNames.length)
    val textOffset        = aligned(symbolTableOffset + 3 * 24)
    val readOnlyOffset    = aligned(textOffset + 4)
    val readOnlySize      = 64 + target.length
    val sectionTable      = aligned(readOnlyOffset + readOnlySize)
    val bytes             = Array.ofDim[Byte](sectionTable + 6 * 64)
    val buffer            = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    buffer.put(0, 0x7f.toByte)
    buffer.put(1, 'E'.toByte)
    buffer.put(2, 'L'.toByte)
    buffer.put(3, 'F'.toByte)
    buffer.put(4, 2.toByte)
    buffer.put(5, 1.toByte)
    buffer.putShort(16, 3.toShort)
    buffer.putShort(18, 224.toShort)
    buffer.putInt(20, 1)
    buffer.putLong(40, sectionTable.toLong)
    buffer.putShort(52, 64.toShort)
    buffer.putShort(58, 64.toShort)
    buffer.putShort(60, 6.toShort)
    buffer.putShort(62, 1.toShort)
    System.arraycopy(sectionNames, 0, bytes, sectionNameOffset, sectionNames.length)
    System.arraycopy(symbolNames, 0, bytes, symbolNameOffset, symbolNames.length)
    LittleEndian.write(0xbf810000L, bytes, textOffset, 4)
    LittleEndian.write(16L, bytes, readOnlyOffset, 4)
    LittleEndian.write(8L, bytes, readOnlyOffset + 4, 4)
    LittleEndian.write(24L, bytes, readOnlyOffset + 8, 4)
    LittleEndian.write(0x80L, bytes, readOnlyOffset + 52, 4)
    LittleEndian.write(0x0aL, bytes, readOnlyOffset + 56, 2)
    System.arraycopy(target, 0, bytes, readOnlyOffset + 64, target.length)

    def section(
        index: Int,
        name: Int,
        kind: Int,
        address: Long,
        offset: Int,
        size: Int,
        link: Int = 0,
        alignment: Int = 1,
        entrySize: Int = 0
    ): Unit = {
      val start = sectionTable + index * 64
      buffer.putInt(start, name)
      buffer.putInt(start + 4, kind)
      buffer.putLong(start + 16, address)
      buffer.putLong(start + 24, offset.toLong)
      buffer.putLong(start + 32, size.toLong)
      buffer.putInt(start + 40, link)
      buffer.putLong(start + 48, alignment.toLong)
      buffer.putLong(start + 56, entrySize.toLong)
    }
    section(1, 1, 3, 0L, sectionNameOffset, sectionNames.length)
    section(2, 11, 3, 0L, symbolNameOffset, symbolNames.length)
    section(3, 19, 2, 0L, symbolTableOffset, 3 * 24, link = 2, alignment = 8, entrySize = 24)
    section(4, 27, 1, 0x1000L, textOffset, 4, alignment = 4)
    section(5, 33, 1, 0x2000L, readOnlyOffset, readOnlySize, alignment = 8)

    def symbol(index: Int, name: Int, kind: Int, section: Int, value: Long, size: Long): Unit = {
      val start = symbolTableOffset + index * 24
      buffer.putInt(start, name)
      buffer.put(start + 4, kind.toByte)
      buffer.putShort(start + 6, section.toShort)
      buffer.putLong(start + 8, value)
      buffer.putLong(start + 16, size)
    }
    symbol(1, 1, 2, 4, 0x1000L, 4L)
    symbol(2, 8, 1, 5, 0x2000L, 64L)
    bytes
  }
}
