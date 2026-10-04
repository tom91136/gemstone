package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import EncodingConstants.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionOperandSuite extends munit.FunSuite {
  for (target <- Vec(Target("gfx900"), Target("gfx1036")))
    test(s"$target mask-producing instructions reset temporary masks and preserve architectural VCC width") {
      val zero                                          = InstructionSource.Constant(0L)
      val one                                           = InstructionSource.Constant(1L)
      def arithmetic(signal: Boolean): Vec[Instruction] = Vec(
        Instruction.VSubCarry(6, Fixed.VccLo, zero, if (signal) one else zero, None),
        Instruction.VSubReverseCarry(6, Fixed.VccLo, if (signal) one else zero, zero, None),
        Instruction.VMadU64U32(
          6,
          Fixed.VccLo,
          if (signal) one else zero,
          one,
          if (signal) InstructionSource.Constant(-1L) else zero
        ),
        Instruction.VMadI64I32(
          6,
          Fixed.VccLo,
          if (signal) one else zero,
          one,
          if (signal) InstructionSource.Constant(-1L) else zero
        ),
        Instruction.VDivScaleF32(
          6,
          Fixed.VccLo,
          InstructionSource.Floating(1.0),
          InstructionSource.Floating(if (signal) java.lang.Float.MIN_NORMAL.toDouble else 1.0),
          InstructionSource.Floating(1.0)
        ),
        Instruction.VDivScaleF64(
          6,
          Fixed.VccLo,
          InstructionSource.Floating(1.0),
          InstructionSource.Floating(if (signal) java.lang.Double.MIN_NORMAL else 1.0),
          InstructionSource.Floating(1.0)
        ),
        Instruction.VCompareSubDword(
          operation = VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, false),
          srcA = zero,
          srcB = if (signal) zero else one,
          srcASelection = SubDwordSelection.DWord,
          srcBSelection = SubDwordSelection.DWord,
          srcASignExtend = false,
          srcBSignExtend = false,
          destination = Some(Fixed.VccLo)
        )
      )
      for {
        signal               <- Vec(false, true)
        operation            <- arithmetic(signal)
        (register, expected) <-
          Vec[(ScalarRegisterIndex, Int)](
            (Fixed.VccLo, (if (signal) 1 else 0)),
            (Fixed.VccHi, (if (target.isa.isRDNA) -1 else 0))
          )
      } {
        val operations = Vec(
          Instruction.SMove(Fixed.VccLo, InstructionSource.Constant(0xffffffffL)),
          Instruction.SMove(Fixed.VccHi, InstructionSource.Constant(0xffffffffL)),
          Instruction.VAddCarry(8, Fixed.VccLo, InstructionSource.Constant(0xffffffffL), one, None),
          operation,
          Instruction.VMove(2, InstructionSource.Scalar(register))
        )
        assertEquals(runSequence(target, operations), expected, clues(operation, register))
      }
    }

  test("scalar moves materialize inline floating constants at their operand width") {
    val target = Target("gfx1036")
    for (
      (encoding, value) <- Vec(
        0xf0 -> 0.5,
        0xf1 -> -0.5,
        0xf2 -> 1.0,
        0xf3 -> -1.0,
        0xf4 -> 2.0,
        0xf5 -> -2.0,
        0xf6 -> 4.0,
        0xf7 -> -4.0
      )
    ) {
      // RDNA2 s_mov_b64 s[26:27], inline; GCC uses this to construct its FP64 0.5 multiplier.
      val move64 = VMCompiler.decodeInstruction(Vec.from(Vec(encoding, 0x04, 0x9a, 0xbe).map(_.toByte)), target)
        .fold(error => fail(error.raw), identity)
      val bits64 = java.lang.Double.doubleToRawLongBits(value)
      for ((register, expected) <- Vec[(ScalarRegisterIndex, Int)]((26, bits64.toInt), (27, (bits64 >>> 32).toInt)))
        assertEquals(
          runSequence(target, Vec(move64, Instruction.VMove(2, InstructionSource.Scalar(register)))),
          expected,
          clues(value, register)
        )
      val move32 = VMCompiler.decodeInstruction(Vec.from(Vec(encoding, 0x03, 0x9a, 0xbe).map(_.toByte)), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(target, Vec(move32, Instruction.VMove(2, InstructionSource.Scalar(26)))),
        java.lang.Float.floatToRawIntBits(value.toFloat)
      )
    }
  }

  test("FP64 inline floats retain their identity through moves and arithmetic") {
    for (
      (operand, bits) <- Vec(
        InstructionSource.Floating(0.5)         -> java.lang.Double.doubleToRawLongBits(1.0),
        InstructionSource.Constant(1L)          -> 2L,
        InstructionSource.Constant(0x3f000000L) -> 0x7e000000L,
        InstructionSource.Literal(0x3fe00000L)  -> java.lang.Double.doubleToRawLongBits(1.0)
      )
    ) {
      val multiply = Instruction.VBinary64Modified(
        operation = VectorBinary64Operation.Multiply,
        dest = 4,
        srcA = operand,
        srcB = InstructionSource.Floating(2.0),
        absA = false,
        absB = false,
        negateA = false,
        negateB = false
      )
      for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((4, bits.toInt), (5, (bits >>> 32).toInt)))
        assertEquals(
          runSequence(
            Target("gfx1036"),
            Vec(multiply, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
          ),
          expected,
          clues(operand, register)
        )
    }
    val target = Target("gfx942")
    val move   = VMCompiler.decodeInstruction(Vec.from(Vec(0xf0, 0x70, 0x06, 0x7e).map(_.toByte)), target)
      .fold(error => fail(error.raw), identity)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((3, 0), (4, 0x3fe00000)))
      assertEquals(
        runSequence(target, Vec(move, Instruction.VMove(2, InstructionSource.VectorRegister(register)))),
        expected
      )
  }

  test("RDNA lane operations honor descriptor wave width, inactive lanes and wrapped selectors") {
    val target = Target("gfx1036")
    for (wave32 <- Vec(false, true)) {
      val width = if (wave32) 32 else 64
      for (selector <- Vec(31, 63, 95, -1))
        assertEquals(
          runSequence(
            target = target,
            operations = Vec(
              Instruction.VWriteLane(6, InstructionSource.Constant(11), InstructionSource.Constant(31)),
              Instruction.VWriteLane(6, InstructionSource.Constant(22), InstructionSource.Constant(selector.toLong)),
              Instruction.VReadLane(
                22,
                InstructionSource.VectorRegister(6),
                InstructionSource.Constant(selector & (width - 1))
              ),
              Instruction.VMove(2, InstructionSource.Scalar(22))
            ),
            wave32 = wave32
          ),
          22
        )
      assertEquals(
        runSequence(
          target = target,
          operations = Vec(
            Instruction.VWriteLane(6, InstructionSource.Constant(11), InstructionSource.Constant(31)),
            Instruction.VWriteLane(6, InstructionSource.Constant(22), InstructionSource.Constant(63)),
            Instruction.VReadLane(22, InstructionSource.VectorRegister(6), InstructionSource.Constant(31)),
            Instruction.VMove(2, InstructionSource.Scalar(22))
          ),
          wave32 = wave32
        ),
        if (wave32) 22 else 11
      )
    }
  }

  test("allocation-backed kernargs preserve the packet address and implicit bytes beyond the declared block") {
    for (declaredSize <- Vec(0, 8)) {
      val memory     = TestMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 128)
      val bufferOffset: Long >= 0 = 16L
      val address                 = memory.address(allocation, bufferOffset).toOption.get.raw
      memory.putInt(allocation, 68, 0x12345678)
      // Dispatch pointer occupies s[0:1], original kernarg pointer s[2:3].
      val operations = Vec(
        Instruction.SLoad(dest = 4, count = 2, base = 0, offset = 40, scalarOffset = None),
        Instruction.SLoad(dest = 6, count = 1, base = 2, offset = 52, scalarOffset = None),
        Instruction.VMove(0, InstructionSource.Scalar(2)),
        Instruction.VMove(1, InstructionSource.Scalar(3)),
        Instruction.VMove(2, InstructionSource.Scalar(4)),
        Instruction.VMove(3, InstructionSource.Scalar(5)),
        Instruction.GlobalStore(address = 0, scalarAddress = None, src = 2, bytes = 8, offset = 0),
        Instruction.VMove(2, InstructionSource.Scalar(6)),
        Instruction.GlobalStore(address = 0, scalarAddress = None, src = 2, bytes = 4, offset = 60),
        Instruction.End
      )
      val offsets = Vec.tabulate(operations.size)(_ * 4)
      val source  = CodeObjectKernel(
        name = "implicit",
        target = Target("gfx1036"),
        kernargSize = declaredSize,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 0,
        kernelCodeProperties = 0xa,
        code = Vec.empty
      )
      val kernel = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
      val task   = KernelTask.validated(
        kernelName = "implicit",
        groupSize = Dimensions.One,
        groupCount = Dimensions.One,
        arguments = Vec.empty,
        argumentBuffer = Some(KernelTask.ArgumentBuffer(allocation, bufferOffset))
      ).toOption.get
      assertEquals(executeKernel(kernel, task, memory), Right(()))
      assertEquals(memory.readValue(allocation, bufferOffset, 8), Right(address))
      assertEquals(memory.getInt(allocation, 76), 0x12345678)
      val invalid = KernelTask.validated(
        kernelName = "implicit",
        groupSize = Dimensions.One,
        groupCount = Dimensions.One,
        arguments = Vec.empty,
        argumentBuffer = Some(KernelTask.ArgumentBuffer(allocation, 128L))
      ).toOption.get
      assert(executeKernel(kernel, invalid, memory).isLeft)
    }
  }

  test("scalar leading-bit opcodes distinguish unsigned zeros from signed sign runs") {
    val target = Target("gfx1036")
    // LLVM encodings: s_flbit_i32_b32 and s_flbit_i32, both s25, s25.
    for ((word, signed) <- Vec(0xbe991519L -> false, 0xbe991719L -> true)) {
      val decoded = VMCompiler.decodeInstruction(LittleEndian.toVec(word, 4), target)
        .fold(error => fail(error.toString), identity)
      for (
        (value, unsignedResult, signedResult) <- Vec(
          (0, -1, -1),
          (-1, 0, -1),
          (1, 31, 31),
          (-2, 0, 31),
          (2, 30, 30),
          (-3, 0, 30),
          (Int.MaxValue, 1, 1),
          (Int.MinValue, 0, 1),
          (0x40000000, 1, 1),
          (0xc0000000, 0, 2),
          (0x00800000, 8, 8),
          (0xff7fffff, 0, 8)
        )
      ) assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMoveImmediate(25, value),
            decoded,
            Instruction.VMove(2, InstructionSource.Scalar(25))
          )
        ),
        if (signed) signedResult else unsignedResult,
        clues(value, signed)
      )
    }
  }

  test("64-bit scalar leading-bit opcodes read both words and distinguish signed inputs") {
    val target = Target("gfx1036")
    // LLVM encodings: s_flbit_i32_b64 and s_flbit_i32_i64, both s26, s[26:27].
    for ((word, signed) <- Vec(0xbe9a161aL -> false, 0xbe9a181aL -> true)) {
      val decoded = VMCompiler.decodeInstruction(LittleEndian.toVec(word, 4), target)
        .fold(error => fail(error.toString), identity)
      for (
        (value, unsignedResult, signedResult) <- Vec(
          (0L, -1, -1),
          (-1L, 0, -1),
          (1L, 63, 63),
          (-2L, 0, 63),
          (2L, 62, 62),
          (-3L, 0, 62),
          (Long.MaxValue, 1, 1),
          (Long.MinValue, 0, 1),
          (0x80000000L, 32, 32),
          (0x100000000L, 31, 31),
          (0xffffffff00000000L, 0, 32),
          (0xfffffffeffffffffL, 0, 31),
          (0xc000000000000000L, 0, 2)
        )
      ) assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(26, InstructionSource.Constant(value)),
            decoded,
            Instruction.VMove(2, InstructionSource.Scalar(26))
          )
        ),
        if (signed) signedResult else unsignedResult,
        clues(value, signed)
      )
    }
  }

  test("SOPK comparisons use signed or unsigned 16-bit immediates across boundary values") {
    val target = Target("gfx1036")
    // LLVM instruction upper words for s_cmpk_* s22; the lower word is the immediate.
    val comparisons = Vec[(Int, Boolean, Int => Boolean)](
      (0xb196, true, _ == 0),
      (0xb216, true, _ != 0),
      (0xb296, true, _ > 0),
      (0xb316, true, _ >= 0),
      (0xb396, true, _ < 0),
      (0xb416, true, _ <= 0),
      (0xb496, false, _ == 0),
      (0xb516, false, _ != 0),
      (0xb596, false, _ > 0),
      (0xb616, false, _ >= 0),
      (0xb696, false, _ < 0),
      (0xb716, false, _ <= 0)
    )
    for {
      (encoding, signed, predicate) <- comparisons
      immediate                     <- Vec(0, 0x7fff, 0x8000, 0xffff)
    } {
      val bytes   = LittleEndian.toVec((encoding.toLong << 16) | immediate.toLong, 4)
      val decoded = VMCompiler.decodeInstruction(bytes, target).toOption.get
      for (value <- Vec(Int.MinValue, -1, 0, 32767, 32768, 65535, 65536, Int.MaxValue)) {
        val left     = if (signed) value.toLong else Integer.toUnsignedLong(value)
        val right    = if (signed) immediate.toShort.toLong else immediate.toLong
        val expected = if (predicate(java.lang.Long.compare(left, right))) 1 else 0
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.SMove(22, InstructionSource.Constant(Integer.toUnsignedLong(value))),
              decoded,
              Instruction.VMove(2, InstructionSource.Condition(ScalarBranchCondition.SccNonZero))
            )
          ),
          expected,
          clues(encoding, immediate, value)
        )
      }
    }
  }

  test("condition sources read current SCC, VCCZ and EXECZ state") {
    val target = Target("gfx1036")
    // LLVM gfx1036 encodings of s_mov_b32 s22, src_scc/src_vccz/src_execz.
    for {
      (selector, condition) <- Vec(
        0xfd -> ScalarBranchCondition.SccNonZero,
        0xfb -> ScalarBranchCondition.VccZero,
        0xfc -> ScalarBranchCondition.ExecZero
      )
      expected <- Vec(0, 1)
    } {
      val decoded = VMCompiler.decodeInstruction(Vec(selector, 0x03, 0x96, 0xbe).map(_.toByte), target).toOption.get
      assertEquals(decoded, Instruction.SMove(22, InstructionSource.Condition(condition)))
      val setup = condition match {
        case ScalarBranchCondition.SccNonZero => Instruction.SCmp(
            ScalarCompareOperation.EqualU32,
            InstructionSource.Constant(expected.toLong),
            InstructionSource.Constant(1L)
          )
        case ScalarBranchCondition.VccZero => Instruction.SMove(Fixed.VccLo, InstructionSource.Constant(1L - expected))
        case _                             => Instruction.SMove(Fixed.ExecLo, InstructionSource.Constant(1L - expected))
      }
      for (move <- Vec(decoded, Instruction.SMove64(22, InstructionSource.Condition(condition))))
        assertEquals(
          runSequence(
            target,
            Vec(
              setup,
              move,
              Instruction.SMove(Fixed.ExecLo, InstructionSource.Constant(1L)),
              Instruction.VMove(2, InstructionSource.Scalar(22))
            )
          ),
          expected
        )
    }
    assertEquals(
      runSequence(
        target,
        Vec(
          Instruction.SCmp(
            ScalarCompareOperation.EqualU32,
            InstructionSource.Constant(1L),
            InstructionSource.Constant(1L)
          ),
          Instruction.VMove(2, InstructionSource.Condition(ScalarBranchCondition.SccNonZero))
        )
      ),
      1
    )
  }

  for (
    (target, groupRegister, descriptor) <- Vec[(Target, ScalarRegisterIndex, Int)](
      (Target("gfx1100"), 15, 0x9e),
      (Target("gfx1200"), 117, 0x84)
    )
  )
    test(s"$target workgroup ID follows its kernel ABI register allocation") {
      val memory     = TestMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 12)
      val address    = memory.address(allocation, 0L).toOption.get.raw
      val operations = Vec(
        Instruction.SMove(6, InstructionSource.Constant(address & 0xffffffffL)),
        Instruction.SMove(7, InstructionSource.Constant(address >>> 32)),
        Instruction.VBinary(
          VectorBinaryOperation.AddU32,
          2,
          InstructionSource.Scalar(groupRegister),
          InstructionSource.Constant(10),
          false
        ),
        Instruction.VBinary(
          VectorBinaryOperation.ShiftLeft32,
          4,
          InstructionSource.Constant(2),
          InstructionSource.Scalar(groupRegister),
          false
        ),
        Instruction.GlobalStore(4, Some(6), 2, 4, 0),
        Instruction.End
      )
      val offsets = Vec.tabulate(operations.size)(_ * 4)
      val source  = CodeObjectKernel(
        name = "padded",
        target = target,
        kernargSize = 0,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        // The real HSA probe requests 15 user SGPRs but initializes only the kernarg pointer.
        computePgmRsrc2 = descriptor,
        kernelCodeProperties = 0x8,
        code = Vec.empty
      )
      val kernel = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
      val task   = KernelTask.validated(
        kernelName = "padded",
        groupSize = Dimensions.One,
        groupCount = Dimensions(3, 1, 1),
        arguments = Vec.empty
      ).toOption.get
      assertEquals(executeKernel(kernel, task, memory), Right(()))
      assertEquals(Vec[Int >= 0](0, 4, 8).map(offset => memory.getInt(allocation, offset)), Vec(10, 11, 12))
    }

  test("RDNA global and flat increment aliases preserve width and return operands") {
    // LLVM fixtures: *_atomic_inc_u32/u64 v4/v[4:5], v[0:1], v2/v[2:3], returning.
    for (
      (target, bytes, width) <- Vec[(Target, Vec[Int], Int in (4, 8))](
        (Target("gfx1100"), Vec(0x00, 0x40, 0xfe, 0xdc, 0x00, 0x02, 0x7c, 0x04), 4),
        (Target("gfx1100"), Vec(0x00, 0x40, 0x32, 0xdd, 0x00, 0x02, 0x7c, 0x04), 8),
        (Target("gfx1100"), Vec(0x00, 0x40, 0xfc, 0xdc, 0x00, 0x02, 0x7c, 0x04), 4),
        (Target("gfx1100"), Vec(0x00, 0x40, 0x30, 0xdd, 0x00, 0x02, 0x7c, 0x04), 8),
        (Target("gfx1200"), Vec(0x7c, 0xc0, 0x0f, 0xee, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 4),
        (Target("gfx1200"), Vec(0x7c, 0x00, 0x13, 0xee, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 8),
        (Target("gfx1200"), Vec(0x7c, 0xc0, 0x0f, 0xec, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 4),
        (Target("gfx1200"), Vec(0x7c, 0x00, 0x13, 0xec, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 8)
      )
    ) {
      val decoded =
        VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      assertEquals(
        decoded,
        Instruction.GlobalAtomicInc(
          dest = 4,
          address = 0,
          scalarAddress = None,
          src = 2,
          bytes = width,
          offset = 0,
          returnValue = true
        )
      )
    }
  }

  test("gfx906 fmac uses fused rounding in e32 and e64 encodings") {
    val target   = Target("gfx906")
    val value    = 1.0000001192092896f
    val previous = -1.000000238418579f
    val expected = java.lang.Float.floatToRawIntBits(Math.fma(value, value, previous))
    assert(expected != java.lang.Float.floatToRawIntBits(value * value + previous))
    // LLVM gfx906: v_fmac_f32_e32/e64 v2, v3, v4.
    for (bytes <- Vec(Vec(0x03, 0x09, 0x04, 0x76), Vec(0x02, 0x00, 0x3b, 0xd1, 0x03, 0x09, 0x02, 0x00))) {
      val decoded = VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target = target,
          operations = Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            decoded
          ),
          initial = java.lang.Float.floatToRawIntBits(previous)
        ),
        expected
      )
      for (unsupported <- Vec(Target("gfx9"), Target("gfx900"), Target("gfx904"), Target("gfx90c")))
        assert(VMCompiler.decodeInstruction(bytes.map(_.toByte), unsupported).isLeft, unsupported.name)
    }
  }

  test("CDNA and RDNA launch packed work-item coordinates across waves") {
    for (
      target <- Seq(
        Target("gfx90a"),
        Target("gfx942"),
        Target("gfx950"),
        Target("gfx1250"),
        Target("gfx1100"),
        Target("gfx1200")
      )
    ) {
      val memory     = TestMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 512)
      val address = memory.address(allocation, 0L).toOption.get.raw
      def binary(
          op: VectorBinaryOperation,
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource
      ): Instruction =
        Instruction.VBinary(op, dest, a, b, false)
      import InstructionSource.{VectorRegister as Register, *}
      val operations = Vec(
        Instruction.SMove64(6, Constant(address)),
        binary(VectorBinaryOperation.And32, 3, Register(0), Constant(1023)),
        binary(VectorBinaryOperation.ShiftRight32, 4, Constant(10), Register(0)),
        binary(VectorBinaryOperation.And32, 4, Register(4), Constant(1023)),
        binary(VectorBinaryOperation.ShiftLeft32, 4, Constant(3), Register(4)),
        binary(VectorBinaryOperation.ShiftRight32, 5, Constant(20), Register(0)),
        binary(VectorBinaryOperation.ShiftLeft32, 5, Constant(5), Register(5)),
        binary(VectorBinaryOperation.AddU32, 3, Register(3), Register(4)),
        binary(VectorBinaryOperation.AddU32, 3, Register(3), Register(5)),
        binary(VectorBinaryOperation.ShiftLeft32, 3, Constant(2), Register(3)),
        binary(VectorBinaryOperation.Or32, 0, Register(0), Constant(0x40000000)),
        Instruction.GlobalStore(3, Some(6), 0, 4, 0),
        Instruction.End
      )
      val offsets = Vec.tabulate(operations.size)(_ * 4)
      val source  = CodeObjectKernel(
        name = "coordinates",
        target = target,
        kernargSize = 0,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 2 << 11,
        kernelCodeProperties = 0,
        code = Vec.empty
      )
      val kernel = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
      val task   = KernelTask.validated("coordinates", Dimensions(8, 4, 4), Dimensions.One, Vec.empty).toOption.get
      assertEquals(executeKernel(kernel, task, memory), Right(()))
      for {
        z <- 0 until 4
        y <- 0 until 4
        x <- 0 until 8
      } {
        val offset = ((x + 8 * y + 32 * z) * 4).unsafe[Int >= 0]
        assertEquals(memory.getInt(allocation, offset), 0x40000000 | x | (y << 10) | (z << 20))
      }
    }
  }

  test("RDNA4 scalar arithmetic rounds unsigned conversion and preserves full-width multiplication") {
    val target              = Target("gfx1200")
    def decode(bytes: Int*) =
      VMCompiler.decodeInstruction(Vec.from(bytes.map(_.toByte)), target).fold(error => fail(error.raw), identity)
    val convert = decode(1, 0x65, 0x85, 0xbe)
    for ((input, expected) <- Seq(0xffffffffL -> 0x4f800000, 16777217L -> 0x4b800000))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(1, InstructionSource.Constant(input)),
            convert,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    val toInteger = decode(1, 0x67, 0x85, 0xbe)
    for ((input, expected) <- Seq(3.75f -> 3, -1.0f -> 0, Float.NaN -> 0, Float.PositiveInfinity -> -1))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(1, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(input).toLong)),
            toInteger,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    val multiply = decode(1, 2, 5, 0xa2)
    assertEquals(
      runSequence(
        target,
        Vec(
          Instruction.SMove(1, InstructionSource.Constant(0x3fc00000L)),
          Instruction.SMove(2, InstructionSource.Constant(0xc0000000L)),
          multiply,
          Instruction.VMove(2, InstructionSource.Scalar(5))
        )
      ),
      0xc0400000
    )
    val multiply64 = decode(0, 2, 0x84, 0xaa)
    for ((register, expected) <- Vec[(ScalarRegisterIndex, Int)]((4, -2), (5, -1)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(0, InstructionSource.Constant(-1)),
            Instruction.SMove64(2, InstructionSource.Constant(2)),
            multiply64,
            Instruction.VMove(2, InstructionSource.Scalar(register))
          )
        ),
        expected
      )
  }

  test("GCN scalar loads distinguish register, immediate, and combined offsets") {
    for (target <- Seq(Target("gfx900"), Target("gfx942"), Target("gfx950")))
      // LLVM s_load_dword s22,s[12:13] with s1+4, s1, and immediate8 offsets.
      for (
        (bytes, registerOffset) <- Seq(
          (Vec(0x86, 0x45, 2, 0xc0, 4, 0, 0, 2), 4),
          (Vec(0x86, 5, 0, 0xc0, 1, 0, 0, 0), 8),
          (Vec(0x86, 5, 2, 0xc0, 8, 0, 0, 0), 123)
        )
      ) {
        val load = VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.VReadFirstLane(12, InstructionSource.VectorRegister(0)),
              Instruction.VReadFirstLane(13, InstructionSource.VectorRegister(1)),
              Instruction.SMove(1, InstructionSource.Constant(registerOffset)),
              load,
              Instruction.VMove(2, InstructionSource.Scalar(22))
            )
          ),
          0x89ab8080
        )
      }
  }

  test("CDNA4 ternary bit operations use all three inputs and truncate 16-bit results") {
    val target = Target("gfx950")
    // LLVM encodings for XOR3, majority, and 16-bit conditional selection.
    for (
      (bytes, expected) <- Seq(
        (Vec(2, 2, 0x34, 0xd2, 3, 9, 0x16, 0xd4), 0x96969696),
        (Vec(2, 5, 0x34, 0xd2, 3, 9, 0x16, 0x1c), 0xe8e8e8e8),
        (Vec(2, 1, 0x33, 0xd2, 3, 9, 0x16, 0x5c), 0xcaca)
      )
    ) {
      val operation = VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0xf0f0f0f0L)),
            Instruction.VMove(4, InstructionSource.Constant(0xccccccccL)),
            Instruction.VMove(5, InstructionSource.Constant(0xaaaaaaaaL)),
            operation
          )
        ),
        expected
      )
    }
  }

  test("RDNA4 numeric min/max preserves the numeric operand and signed zero") {
    val target = Target("gfx1200")
    // Fixed LLVM VOP2 min/max and VOP3 min with absolute/negate modifiers.
    for (
      (bytes, modified, maximum) <- Seq(
        (Vec(3, 9, 4, 0x2a), false, false),
        (Vec(3, 9, 4, 0x2c), false, true),
        (Vec(2, 1, 0x15, 0xd5, 3, 9, 2, 0x40), true, false)
      )
    ) {
      val operation = VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      for ((left, right) <- Seq((Float.NaN, 2.0f), (2.0f, Float.NaN), (0.0f, -0.0f), (Float.NaN, Float.NaN))) {
        val a        = if (modified) math.abs(left) else left
        val b        = if (modified) -right else right
        val expected = if (a.isNaN) b else if (b.isNaN) a else if (maximum) math.max(a, b) else math.min(a, b)
        val actual   = runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(left).toLong)),
            Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(right).toLong)),
            operation
          )
        )
        if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
        else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
      }
    }
  }

  test("16-bit logical right shift excludes the upper source half") {
    // Fixed LLVM encoding: v_lshrrev_b16 v2, 8, v3.
    for (target <- Seq(Target("gfx1100"), Target("gfx1200"))) {
      val shift =
        VMCompiler.decodeInstruction(Vec(0x02, 0x00, 0x39, 0xd7, 0x88, 0x06, 0x02, 0x00).map(_.toByte), target)
          .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0xa5ff8012L)),
            shift
          )
        ),
        0x80
      )
    }
  }

  test("RDNA4 scalar fused multiply-add reads the accumulator and instruction literals") {
    val target = Target("gfx1200")
    // LLVM encodings: fmac s0,s1,s2; fmaak s0,s1,s2,1.5; fmamk s0,s1,1.5,s2.
    for (
      (bytes, expected) <- Seq(
        (Vec(1, 2, 0x80, 0xa3), -5.5f),
        (Vec(1, 2, 0x80, 0xa2, 0, 0, 0xc0, 0x3f), -4.5f),
        (Vec(1, 2, 0, 0xa3, 0, 0, 0xc0, 0x3f), 0.0f)
      )
    ) {
      val operation = VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(0, InstructionSource.Constant(0x3f000000L)),
            Instruction.SMove(1, InstructionSource.Constant(0x40000000L)),
            Instruction.SMove(2, InstructionSource.Constant(0xc0400000L)),
            operation,
            Instruction.VMove(2, InstructionSource.Scalar(0))
          )
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
      if (bytes.size == 8) assert(VMCompiler.decodeInstruction(bytes.take(4).map(_.toByte), target).isLeft)
    }
  }

  test("gfx803 double fractional part clamps the rounded result below one") {
    val target = Target("gfx803")
    // LLVM gfx803: v_fract_f64 v[6:7], v[4:5].
    val fraction = VMCompiler.decodeInstruction(Vec[Byte](0x04, 0x65, 0x0c, 0x7e), target)
      .fold(error => fail(error.raw), identity)
    // LLVM's amdgcn_fract constant folding clamps both widths to the predecessor of one.
    for (
      (value, expected) <- Vec(
        1.75                        -> 0.75,
        -1.75                       -> 0.25,
        -0.0                        -> 0.0,
        -2.0                        -> 0.0,
        java.lang.Double.MIN_VALUE  -> java.lang.Double.MIN_VALUE,
        -java.lang.Double.MIN_VALUE -> Math.nextDown(1.0),
        Double.PositiveInfinity     -> Double.NaN,
        Double.NegativeInfinity     -> Double.NaN,
        Double.NaN                  -> Double.NaN
      )
    ) {
      val words = Vec[VectorRegisterIndex](6, 7).map { register =>
        runSequence(
          target,
          Vec(
            Instruction.VMove64(4, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(value))),
            fraction,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        )
      }
      val actual = java.lang.Double.longBitsToDouble((words(0).toLong & 0xffffffffL) | (words(1).toLong << 32))
      if (expected.isNaN) assert(actual.isNaN)
      else assertEquals(java.lang.Double.doubleToRawLongBits(actual), java.lang.Double.doubleToRawLongBits(expected))
    }
  }

  test("RDNA4 scalar float comparisons preserve unordered and signed-zero semantics in SCC") {
    val target = Target("gfx1200")
    // LLVM fixed s_cmp_{neq,ngt,eq}_f32 and s_cmp_neq_f16 encodings.
    for (
      (opcode, left, right, expected) <- Seq(
        (0x45, 0x7fc00000L, 0L, 7),
        (0x45, 0x3f800000L, 0L, 42),
        (0x4d, 0x7fc00000L, 0L, 42),
        (0x4d, 0x3f800000L, 0x3f800000L, 7),
        (0x4b, 0x7fc00000L, 0L, 42),
        (0x4b, 0x40000000L, 0x3f800000L, 7),
        (0x42, 0x80000000L, 0L, 42),
        (0x5d, 0x7e00L, 0L, 42),
        (0x5d, 0x8000L, 0L, 7)
      )
    ) {
      val comparison = VMCompiler.decodeInstruction(Vec[Byte](0, 1, opcode.toByte, 0xbf.toByte), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(0, InstructionSource.Constant(left)),
            Instruction.SMove(1, InstructionSource.Constant(right)),
            comparison,
            Instruction.SCSelect(8, InstructionSource.Constant(42), InstructionSource.Constant(7), 32),
            Instruction.VMove(2, InstructionSource.Scalar(8))
          )
        ),
        expected
      )
    }
  }

  test("RDNA4 narrow scalar loads zero extend and do not read past the allocation") {
    val target = Target("gfx1200")
    // LLVM s_load_u8/u16 s5,s[2:3],offset; allocation ends at byte 16.
    for ((encoding, offset, expected) <- Seq((0x21, 8, 0x80), (0x61, 8, 0x8080), (0x21, 15, 0), (0x61, 14, 0))) {
      val load = VMCompiler.decodeInstruction(
        Vec[Byte](0x41, encoding.toByte, 1, 0xf4.toByte, offset.toByte, 0, 0, 0xf8.toByte),
        target
      )
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VReadFirstLane(2, InstructionSource.VectorRegister(0)),
            Instruction.VReadFirstLane(3, InstructionSource.VectorRegister(1)),
            load,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    }
  }

  test("RDNA scratch stores round trip register pairs without aliasing global memory") {
    for (
      (target, store, load) <- Seq(
        (Target("gfx1100"), Vec(8, 0, 0x6d, 0xdc, 0, 2, 0x7c, 0), Vec(8, 0, 0x55, 0xdc, 0, 0, 0x7c, 2)),
        (
          Target("gfx1200"),
          Vec(0x7c, 0xc0, 6, 0xed, 0, 0, 0, 1, 0, 8, 0, 0),
          Vec(0x7c, 0x40, 5, 0xed, 2, 0, 0, 0, 0, 8, 0, 0)
        )
      )
    ) {
      def decode(bytes: Vec[Int]) =
        VMCompiler.decodeInstruction(bytes.map(_.toByte), target).fold(error => fail(error.raw), identity)
      for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((2, 0x11223344), (3, 0x55667788)))
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.VMove(3, InstructionSource.Constant(0x55667788L)),
              decode(store),
              Instruction.VMove(2, InstructionSource.Constant(0)),
              Instruction.VMove(3, InstructionSource.Constant(0)),
              decode(load),
              Instruction.VMove(2, InstructionSource.VectorRegister(register))
            )
          ),
          expected
        )
    }
  }

  test("RDNA dual moves read both sources before either destination is written") {
    val target = Target("gfx1100")
    // LLVM gfx11 VOPD fixture, with source X set to v2: mov v255,v2 :: mov v6,v255.
    val dual = VMCompiler.decodeInstruction(
      Vec[Byte](0x02, 0x01, 0x10, 0xca.toByte, 0xff.toByte, 0x01, 0x06, 0xff.toByte),
      target
    ).fold(error => fail(error.raw), identity)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((255, 23), (6, 17)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(255, InstructionSource.Constant(17)),
            dual,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          ),
          initial = 23
        ),
        expected
      )
  }

  test("RDNA dual arithmetic uses a fused accumulator and consumes its shared literal") {
    val target     = Target("gfx1100")
    val arithmetic = VMCompiler.decodeInstruction(
      Vec[Byte](0x02, 0x05, 0x00, 0xc9.toByte, 0x03, 0x07, 0x06, 0xff.toByte),
      target
    ).fold(error => fail(error.raw), identity)
    val literal = VMCompiler.decodeInstruction(
      Vec[Byte](0xff.toByte, 0x00, 0x10, 0xca.toByte, 0x7c, 0x00, 0x06, 0xff.toByte, 0x56, 0x34, 0x12, 0xaf.toByte),
      target
    ).fold(error => fail(error.raw), identity)
    val value    = 1.0000001192092896f
    val previous = -1.000000238418579f
    for ((register, expected) <- Vec[(VectorRegisterIndex, Float)]((255, 4.0f), (6, Math.fma(value, value, previous))))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            Instruction.VMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(previous).toLong)),
            arithmetic,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          ),
          initial = java.lang.Float.floatToRawIntBits(2.0f)
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
    assertEquals(
      runSequence(target, Vec(literal, Instruction.VMove(2, InstructionSource.VectorRegister(255)))),
      0xaf123456
    )
  }

  test("gfx803 legacy unsigned short multiply-add resolves its alias and truncates the result") {
    // LLVM VI fixture: v_mad_u16 v5, -1, v2, v3 (opcode 0x1eb).
    val target = Target("gfx803")
    val mad    = VMCompiler.decodeInstruction(
      Vec[Byte](0x05, 0x00, 0xeb.toByte, 0xd1.toByte, 0xc1.toByte, 0x04, 0x0e, 0x04),
      target
    ).fold(error => fail(error.raw), identity)
    assertEquals(
      runSequence(
        target = target,
        operations = Vec(
          Instruction.VMove(3, InstructionSource.Constant(0x12340003L)),
          Instruction.VMove(5, InstructionSource.Constant(0xaaaaaaaaL)),
          mad,
          Instruction.VMove(2, InstructionSource.VectorRegister(5))
        ),
        initial = 2
      ),
      1
    )
  }

  test("gfx942 packed float add selects dwords, negates inputs, and preserves overlapping pairs") {
    val target = Target("gfx942")
    // llvm-mc gfx942: v_pk_add_f32 v[4:5], v[4:5], v[6:7], with default,
    // swapped source selection, and independent low/high negation modifiers.
    for {
      (control, sources, low, high) <- Seq(
        (0x40, 0x18, 11.5f, 22.25f),
        (0x48, 0x10, 12.25f, 21.5f),
        (0x42, 0x38, 8.5f, -17.75f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val add = VMCompiler.decodeInstruction(
        Vec[Byte](0x04, control.toByte, 0xb2.toByte, 0xd3.toByte, 0x04, 0x0d, 0x02, sources.toByte),
        target
      ).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Float)]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(add, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(control, sources, register)
      )
    }
  }

  test("CDNA packed multiply and FMA preserve selection, negation, and overlapping pairs") {
    val target = Target("gfx942")
    // LLVM gfx942 fixtures: default multiply/FMA, then independently selected and negated FMA sources.
    for {
      (bytes, low, high) <- Seq(
        (Seq(0x04, 0x40, 0xb1, 0xd3, 0x04, 0x0d, 0x02, 0x18), 15.0f, 45.0f),
        (Seq(0x04, 0x40, 0xb0, 0xd3, 0x04, 0x0d, 0x22, 0x1c), 18.0f, 49.0f),
        (Seq(0x04, 0x2d, 0xb0, 0xd3, 0x04, 0x0d, 0x22, 0x54), -18.5f, -33.0f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val operation = VMCompiler.decodeInstruction(Vec.from(bytes.map(_.toByte)), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(
              VectorRegisterIndex,
              Float
          )]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f), (8, 3.0f), (9, 4.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(operation, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(bytes, register)
      )
    }
  }

  test("CDNA packed float arithmetic materializes inline constants as single precision") {
    // MiniBUDE's compiled charge terms: 1 - x, and 1 - x*y. Both results select
    // the constant's low dword while reading different dwords of the register pairs.
    // The multiply fixture covers a constant in the first operand as well.
    for {
      target             <- Vec(Target("gfx90a"), Target("gfx942"), Target("gfx950"))
      (bytes, low, high) <- Seq(
        (Seq(0x04, 0x41, 0xb2, 0xd3, 0x04, 0xe5, 0x01, 0x28), -0.5f, -1.25f),
        (Seq(0x04, 0x01, 0xb0, 0xd3, 0x04, 0x0d, 0xca, 0x3b), -14.0f, -44.0f),
        (Seq(0x04, 0x40, 0xb1, 0xd3, 0xf4, 0x08, 0x02, 0x10), 3.0f, 4.5f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val operation = VMCompiler.decodeInstruction(Vec.from(bytes.map(_.toByte)), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Float)]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(operation, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(target, bytes, register)
      )
    }
  }

  test("CDNA double FMA accumulates the destination and sub-dword subtraction selects bytes") {
    val target = Target("gfx942")
    // LLVM gfx942: v_fmac_f64_e32 v[4:5], v[6:7], v[8:9].
    val fma = VMCompiler.decodeInstruction(Vec.from(Seq(0x06, 0x11, 0x08, 0x08).map(_.toByte)), target)
      .fold(error => fail(error.raw), identity)
    for {
      (accumulator, a, b, result) <- Seq(
        (1.25, 1.5, 2.0, 4.25),
        // A separately rounded product loses the low bit and incorrectly yields zero.
        (-18014398509481984.0, 134217729.0, 134217727.0, -1.0)
      )
      expected = java.lang.Double.doubleToRawLongBits(result)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt))
    }
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Double)]((4, accumulator), (6, a), (8, b)).map { case (index, value) =>
            Instruction.VMove64(index, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(value)))
          }) ++ Vec(fma, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        word
      )
    // LLVM gfx942: v_sub_u32_sdwa v2, v2, v3, BYTE_0 sources, DWORD destination.
    val subtract = VMCompiler.decodeInstruction(
      Vec.from(Seq(0xf9, 0x06, 0x04, 0x6a, 0x02, 0x16, 0x00, 0x00).map(_.toByte)),
      target
    ).fold(error => fail(error.raw), identity)
    assertEquals(
      runSequence(
        target,
        Vec(
          Instruction.VMove(3, InstructionSource.Constant(0xaaff0010L)),
          subtract
        ),
        initial = 0x12345609
      ),
      -7
    )
  }

  test("gfx803 fractional part rounds negative inputs down and accepts denormals") {
    val target = Target("gfx803")
    // LLVM gfx803: v_fract_f32 v2, v4.
    val fraction = VMCompiler.decodeInstruction(Vec[Byte](0x04, 0x37, 0x04, 0x7e), target)
      .fold(error => fail(error.raw), identity)
    for (
      (value, expected) <- Vec(
        1.75f                      -> 0.75f,
        -1.75f                     -> 0.25f,
        -0.25f                     -> 0.75f,
        2.0f                       -> 0.0f,
        -2.0f                      -> 0.0f,
        -0.0f                      -> 0.0f,
        java.lang.Float.MIN_VALUE  -> java.lang.Float.MIN_VALUE,
        -java.lang.Float.MIN_VALUE -> Math.nextDown(1.0f),
        Float.PositiveInfinity     -> Float.NaN,
        Float.NegativeInfinity     -> Float.NaN,
        Float.NaN                  -> Float.NaN
      )
    ) {
      val actual = runSequence(
        target,
        Vec(
          Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
          fraction
        )
      )
      if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
      else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
    }
  }

  test("RDNA4 scalar float operations preserve signed zero, special values, and source modifiers") {
    val target = Target("gfx1200")
    // LLVM gfx1200: v_s_rcp_f32 s4, s6; s_trunc_f32 s4, s6.
    val truncate = VMCompiler.decodeInstruction(Vec.from(Seq(0x06, 0x62, 0x84, 0xbe).map(_.toByte)), target)
      .fold(error => fail(error.raw), identity)
    for (
      (value, expected) <- Seq(
        1.75f                  -> 1.0f,
        -1.75f                 -> -1.0f,
        -0.25f                 -> -0.0f,
        -0.0f                  -> -0.0f,
        Float.PositiveInfinity -> Float.PositiveInfinity,
        Float.NaN              -> Float.NaN
      )
    ) {
      val actual = runSequence(
        target,
        Vec(
          Instruction.SMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
          truncate,
          Instruction.VMove(2, InstructionSource.Scalar(4))
        )
      )
      if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
      else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
    }
    for (
      (control, modifier, input, expected) <- Seq(
        (0, 0, -4.0f, -0.25f),
        (0, 0x20, -4.0f, 0.25f),
        (1, 0, -4.0f, 0.25f),
        (0x80, 0, 0.5f, 1.0f),
        (0, 0, -0.0f, Float.NegativeInfinity)
      )
    ) {
      val reciprocal = VMCompiler.decodeInstruction(
        Vec.from(Seq(0x04, control, 0x84, 0xd6, 0x06, 0x00, 0x00, modifier).map(_.toByte)),
        target
      ).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(input).toLong)),
            reciprocal,
            Instruction.VMove(2, InstructionSource.Scalar(4))
          )
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
    }
  }

  test("RDNA4 double numeric min/max ignores a single NaN and orders signed zero") {
    val target = Target("gfx1200")
    // LLVM gfx1200 min/max v[4:5], v[6:7], v[8:9].
    for {
      (opcode, left, right, expected) <- Seq(
        (0x1c, Double.NaN, 2.5, 2.5),
        (0x1c, -4.0, Double.NaN, -4.0),
        (0x1c, -0.0, 0.0, 0.0),
        (0x1a, -0.0, 0.0, -0.0),
        (0x1a, Double.NaN, -2.5, -2.5)
      )
      bits = java.lang.Double.doubleToRawLongBits(expected)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, bits.toInt), (5, (bits >>> 32).toInt))
    } {
      val operation = VMCompiler.decodeInstruction(Vec.from(Seq(0x06, 0x11, 0x08, opcode).map(_.toByte)), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove64(6, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(left))),
            Instruction.VMove64(8, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(right))),
            operation,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word
      )
    }
  }

  test("gfx1200 VOP2 64-bit shift handles cross-word bits, masked counts, and overlapping pairs") {
    val target = Target("gfx1200")
    // llvm-mc gfx1200: v_lshlrev_b64_e32 v[4:5], s6, v[4:5].
    val shift = VMCompiler.decodeInstruction(Vec[Byte](0x06, 0x08, 0x08, 0x3e), target)
      .fold(error => fail(error.raw), identity)
    for {
      value  <- Seq(0x0123456789abcdefL, -1L)
      amount <- Seq(0, 1, 31, 32, 63, 64, 65)
      expected = value << (amount & 63)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt))
    }
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(4, InstructionSource.Constant(value & 0xffffffffL)),
            Instruction.VMove(5, InstructionSource.Constant(value >>> 32)),
            Instruction.SMove(6, InstructionSource.Constant(amount.toLong)),
            shift,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word,
        clues(value, amount, register)
      )
  }

  test("gfx1200 scalar 64-bit subtract preserves SCC and handles borrow, wrap, and overlapping pairs") {
    val target = Target("gfx1200")
    for {
      (left, right, expected) <- Seq(
        (0x0000000200000000L, 1L, 0x00000001ffffffffL),
        (0L, 1L, -1L),
        (0x0123456789abcdefL, 0x100000000L, 0x0123456689abcdefL),
        (5L, 5L, 0L)
      )
      carry            <- Seq(false, true)
      (register, word) <-
        Vec[(ScalarRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt), (8, (if (carry) 1 else 0)))
    } {
      // llvm-mc gfx1200: s_sub_nc_u64 s[4:5], s[4:5], s[6:7].
      val sub = VMCompiler.decodeInstruction(Vec[Byte](0x04, 0x06, 0x04, 0xaa.toByte), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.SMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.SMove(6, InstructionSource.Constant(right & 0xffffffffL)),
            Instruction.SMove(7, InstructionSource.Constant(right >>> 32)),
            Instruction.SCmp(
              ScalarCompareOperation.EqualU32,
              InstructionSource.Constant(0L),
              InstructionSource.Constant(if (carry) 0L else 1L)
            ),
            sub,
            Instruction.SCSelect(8, InstructionSource.Constant(1L), InstructionSource.Constant(0L), 32),
            Instruction.VMove(2, InstructionSource.Scalar(register))
          )
        ),
        word,
        clues(left, right, carry, register)
      )
    }
  }

  test("gfx1200 scalar 64-bit add preserves SCC and handles carry, wrap, and overlapping pairs") {
    val target = Target("gfx1200")
    for {
      (left, right, expected) <- Seq(
        (0x00000001ffffffffL, 1L, 0x0000000200000000L),
        (-1L, 1L, 0L),
        (0x0123456789abcdefL, 0x100000000L, 0x0123456889abcdefL)
      )
      carry            <- Seq(false, true)
      (register, word) <-
        Vec[(ScalarRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt), (8, (if (carry) 1 else 0)))
    } {
      // llvm-mc gfx1200: s_add_nc_u64 s[4:5], s[4:5], s[6:7].
      val add = VMCompiler.decodeInstruction(Vec[Byte](0x04, 0x06, 0x84.toByte, 0xa9.toByte), target)
        .fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.SMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.SMove(6, InstructionSource.Constant(right & 0xffffffffL)),
            Instruction.SMove(7, InstructionSource.Constant(right >>> 32)),
            Instruction.SCmp(
              ScalarCompareOperation.EqualU32,
              InstructionSource.Constant(0L),
              InstructionSource.Constant(if (carry) 0L else 1L)
            ),
            add,
            Instruction.SCSelect(8, InstructionSource.Constant(1L), InstructionSource.Constant(0L), 32),
            Instruction.VMove(2, InstructionSource.Scalar(register))
          )
        ),
        word,
        clues(left, right, carry, register)
      )
    }
  }

  test("gfx942 packed move selects complete dwords and reads overlapping sources before writing") {
    // llvm-mc gfx942 encodings: gather, in-place swap, scalar pair copy, and inline floats.
    for {
      (encoding, low, high) <- Seq(
        (Vec[Byte](0x02, 0x50, 0xb3.toByte, 0xd3.toByte, 0x04, 0x05, 0x02, 0x18), 0x76543210, 0x89abcdef),
        (Vec[Byte](0x02, 0x48, 0xb3.toByte, 0xd3.toByte, 0x02, 0x05, 0x02, 0x18), 0x89abcdef, 0x11223344),
        (Vec[Byte](0x02, 0x50, 0xb3.toByte, 0xd3.toByte, 0x04, 0x08, 0x00, 0x18), 0x01234567, 0x89abcdef),
        (Vec[Byte](0x02, 0x40, 0xb3.toByte, 0xd3.toByte, 0xf2.toByte, 0xe2.toByte, 0x01, 0x18), 0x3f800000, 0xbf000000)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Int)]((2, low), (3, high))
    } {
      val target = Target("gfx942")
      val move   = VMCompiler.decodeInstruction(encoding, target).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(0x01234567L)),
            Instruction.SMove(5, InstructionSource.Constant(0x89abcdefL)),
            Instruction.VMove(3, InstructionSource.Constant(0x89abcdefL)),
            Instruction.VMove(4, InstructionSource.Constant(0x76543210L)),
            Instruction.VMove(5, InstructionSource.Constant(0xfedcba98L)),
            move,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        expected,
        clues(encoding, register)
      )
    }
  }

  test("gfx942 shift-add preserves 64-bit inputs, overlapping pairs, and restricted shift counts") {
    val target = Target("gfx942")
    for {
      (left, shift, addend, expected) <- Seq(
        (0x00000001ffffffffL, 1, 0x0000000100000002L, 0x0000000500000000L),
        (-1L, 4, 17L, 1L),
        (0x1234567887654321L, 5, 0x1111111111111111L, 0x2345678998765432L),
        (0x1234567887654321L, 7, 0x1111111111111111L, 0x2345678998765432L),
        (1L, 12, 2L, 18L),
        (0x100000000L, 0, 1L, 0x100000001L)
      )
      (destination, vectorInput) <- Vec[(VectorRegisterIndex, Boolean)]((2, false), (5, true), (9, true))
      (register, word)           <- Vec[(VectorRegisterIndex, Int)](
        (destination, expected.toInt),
        (destination.offsetBy(1), (expected >>> 32).toInt)
      )
    } {
      // LLVM oracle: v_lshl_add_u64 v[2:3], s[4:5], v7, v[8:9].
      // Also overlap either input pair with the destination and use v[4:5].
      val instruction = VMCompiler.decodeInstruction(
        Vec[Byte](destination.raw.toByte, 0x00, 0x08, 0xd2.toByte, 0x04, if (vectorInput) 0x0f else 0x0e, 0x22, 0x04),
        target
      ).fold(error => fail(error.raw), identity)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.SMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.VMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.VMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.VMove(7, InstructionSource.Constant(shift.toLong)),
            Instruction.VMove(8, InstructionSource.Constant(addend & 0xffffffffL)),
            Instruction.VMove(9, InstructionSource.Constant(addend >>> 32)),
            instruction,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word,
        clues(left, shift, addend, destination, register)
      )
    }
  }

  test("gfx942 64-bit move reads both source registers before writing an overlapping pair") {
    val target = Target("gfx942")
    // CDNA3 VOP1 encoding: v_mov_b64 v[3:4], v[2:3].
    val move = VMCompiler.decodeInstruction(
      Vec[Byte](0x02, 0x71, 0x06, 0x7e),
      target
    ).fold(error => fail(error.raw), identity)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((3, 0x11223344), (4, 0x89abcdef)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0x89abcdefL)),
            move,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        expected
      )
  }

  // Decoder field tests live in the modifier/memory suites. This fixture exercises
  // lowered instructions through register state and observable memory writes.
  private def run(target: Target, instruction: Instruction, initial: Int = 0x11223344): Int =
    runSequence(target, Vec(instruction), initial)

  private def runSequence(
      target: Target,
      operations: Vec[Instruction],
      initial: Int = 0x11223344,
      functions: Map[Long, String] = Map.empty,
      wave32: Boolean = true
  ): Int = {
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 16)
    memory.putInt(allocation, 8, 0x89ab8080)
    val address      = memory.address(allocation, 0L).toOption.get.raw
    val instructions = Vec(
      Instruction.VMove(0, InstructionSource.Constant(address & 0xffffffffL)),
      Instruction.VMove(1, InstructionSource.Constant(address >>> 32)),
      Instruction.VMove(2, InstructionSource.Constant(initial.toLong & 0xffffffffL))
    ) ++ operations ++ Vec(
      Instruction.GlobalStore(0, None, 2, 4, 0),
      Instruction.End
    )
    val offsets = Vec.tabulate(instructions.size)(_ * 4)
    val source  = CodeObjectKernel(
      functionTargets = functions,
      name = "operand",
      target = target,
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = if (wave32 && target.isa.isRDNA) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0,
      code = Vec.empty
    )
    val kernel = Kernel(source, instructions, offsets.zipWithIndex.toMap, offsets, 0)
    val task   = KernelTask.validated("operand", Dimensions.One, Dimensions.One, Vec.empty).toOption.get
    assertEquals(executeKernel(kernel, task, memory), Right(()))
    assertEquals(memory.getInt(allocation, 8), 0x89ab8080)
    memory.getInt(allocation, 0)
  }

  test("memory transfers can end at the final vector register") {
    val target = Target("gfx900")
    for (width <- Seq[Int in (1, 2, 4)](1, 2, 4)) {
      val load     = Instruction.GlobalLoad(255, 0, None, width.widen[Int in 1 ~ 16], 8, false)
      val expected = width.raw match {
        case 1 => 0x80
        case 2 => 0x8080
        case _ => 0x89ab8080
      }
      assertEquals(
        runSequence(
          target,
          Vec(
            load,
            Instruction.VMove(2, InstructionSource.VectorRegister(255))
          )
        ),
        expected
      )
    }
  }

  test("wave32 mask writes decode and execute at the last scalar slot") {
    val target = Target("gfx1030")
    for (text <- Seq("v_add_co_u32 v2, exec_hi, v3, v4", "v_cmp_eq_u32 exec_hi, v3, v4")) {
      val bytes = asm.Assembler.encode(asm.Parser.parse(text).toOption.get, target).toOption.get
      assert(VMCompiler.decodeInstruction(bytes, target).isRight, text)
    }
    val operation = Instruction.VAddCarry(2, 127, InstructionSource.Constant(1), InstructionSource.Constant(1), None)
    assert(VMRegisterBounds.validate(operation, 32).isRight)
    assert(VMRegisterBounds.validate(operation, 64).isLeft)
    assertEquals(run(target, operation), 2)
  }

  test("64-bit vector operations read live EXEC and VCC values") {
    val target = Target("gfx900")
    for (scalar <- Seq[ScalarRegisterIndex](106, 126)) {
      val initialize = Instruction.SMove64(scalar, InstructionSource.Constant(1))
      for (
        operation <- Seq(
          Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VShiftLeft64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VShiftRightArithmetic64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VBinary64Modified(
            VectorBinary64Operation.Add,
            2,
            InstructionSource.Scalar(scalar),
            InstructionSource.Constant(0),
            false,
            false,
            false,
            false
          )
        )
      ) assertEquals(runSequence(target, Vec(initialize, operation)), 1)
      val fullMask = Instruction.SMove64(scalar, InstructionSource.Constant(0x1234567800000001L))
      assertEquals(
        runSequence(
          target,
          Vec(
            fullMask,
            Instruction.VShiftRight64(2, InstructionSource.Constant(32), InstructionSource.Scalar(scalar))
          )
        ),
        0x12345678
      )
      val high     = scalar.offsetBy(1)
      val highRead = Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(high))
      assert(VMRegisterBounds.validate(highRead).isRight)
      assertEquals(runSequence(target, Vec(fullMask, highRead)), 0x12345678)
    }
  }

  test("scalar bank validation preserves EXEC and VCC special sources") {
    val target = Target("gfx900")
    for (register <- Seq[ScalarRegisterIndex](106, 107, 126, 127)) {
      val move = Instruction.SMove64(4, InstructionSource.Scalar(register))
      assert(VMRegisterBounds.validate(move).isRight)
      val expected = if (register.raw == 126) 1 else 0
      assertEquals(runSequence(target, Vec(move, Instruction.VMove(2, InstructionSource.Scalar(4)))), expected)
    }
    val writeExec = Instruction.SMove64(126, InstructionSource.Constant(1))
    assert(VMRegisterBounds.validate(writeExec).isRight)
    assertEquals(run(target, writeExec), 0x11223344)
  }

  test("ldexp executes with a signed scalar exponent in v255") {
    for (exponent <- Seq(3, -2)) {
      val operations = Vec(
        Instruction.VMove(4, InstructionSource.Constant(0L)),
        Instruction.VMove(5, InstructionSource.Constant(0x3ff80000L)), // 1.5
        Instruction.VMove(255, InstructionSource.Constant(exponent.toLong)),
        Instruction.VBinary64Modified(
          VectorBinary64Operation.LoadExponent,
          2,
          InstructionSource.VectorRegister(4),
          InstructionSource.VectorRegister(255),
          false,
          false,
          false,
          false
        ),
        Instruction.VMove(2, InstructionSource.VectorRegister(3))
      )
      val expected = (java.lang.Double.doubleToRawLongBits(Math.scalb(1.5, exponent)) >>> 32).toInt
      assertEquals(runSequence(Target("gfx900"), operations), expected)
    }
  }

  private val destinations = Seq(
    (SubDwordSelection.Byte0, 0x00000080L, 0xffffff80L, 0x11223380L),
    (SubDwordSelection.Byte1, 0x00008000L, 0xffff8000L, 0x11228044L),
    (SubDwordSelection.Byte2, 0x00800000L, 0xff800000L, 0x11803344L),
    (SubDwordSelection.Byte3, 0x80000000L, 0x80000000L, 0x80223344L),
    (SubDwordSelection.Word0, 0x00008080L, 0xffff8080L, 0x11228080L),
    (SubDwordSelection.Word1, 0x80800000L, 0x80800000L, 0x80803344L),
    (SubDwordSelection.DWord, 0xffff8080L, 0xffff8080L, 0xffff8080L)
  )

  for (target <- Seq(Target("gfx900"), Target("gfx1030"))) {
    test(s"$target SDWA destination placement pads, sign-extends, or preserves each selection") {
      for {
        (selection, pad, signed, preserved) <- destinations
        (unused, expected)                  <- Seq(
          SubDwordDestinationUnused.Pad        -> pad,
          SubDwordDestinationUnused.SignExtend -> signed,
          SubDwordDestinationUnused.Preserve   -> preserved
        )
      } {
        val instruction = Instruction.VBinarySubDword(
          VectorSubDwordOperation.Or32,
          2,
          InstructionSource.Constant(0xffff8080L),
          InstructionSource.Constant(0L),
          selection,
          unused,
          SubDwordSelection.DWord,
          SubDwordSelection.DWord,
          false,
          false
        )
        assertEquals(run(target, instruction), expected.toInt, clues(selection, unused))
      }
    }

    test(s"$target SDWA source selection and sign extension feed the operation") {
      val instruction = Instruction.VBinarySubDword(
        VectorSubDwordOperation.AddU32,
        2,
        InstructionSource.Constant(0x00800000L),
        InstructionSource.Constant(0x00000300L),
        SubDwordSelection.DWord,
        SubDwordDestinationUnused.Pad,
        SubDwordSelection.Byte2,
        SubDwordSelection.Byte1,
        true,
        false
      )
      assertEquals(run(target, instruction), -125)
    }

    test(s"$target narrow loads sign-extend and D16 loads preserve the other half") {
      assertEquals(run(target, Instruction.GlobalLoadSigned(2, 0, None, 1, 8)), -128)
      assertEquals(run(target, Instruction.GlobalLoadSigned(2, 0, None, 2, 8)), -32640)
      assertEquals(run(target, Instruction.GlobalLoadD16(2, 0, None, 8, false)), 0x11228080)
      assertEquals(run(target, Instruction.GlobalLoadD16(2, 0, None, 8, true)), 0x80803344)
    }

    test(s"$target packed arithmetic retains signedness, lane overflow, and source selection") {
      val cases = Seq(
        (VectorPackedBinaryOperation.MaximumI16, 0x80007fffL, 0x7fff8000L, true, true, 0x7fff7fffL),
        (VectorPackedBinaryOperation.MaximumU16, 0x80007fffL, 0x7fff8000L, true, true, 0x80008000L),
        (VectorPackedBinaryOperation.AddU16, 0xffff0001L, 0x00010002L, true, true, 0x00000003L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, false, true, 0x000f0015L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, true, false, 0x000e0015L)
      )
      cases.foreach { (operation, left, right, leftHigh, rightHigh, expected) =>
        assertEquals(
          run(
            target,
            Instruction.VPackedBinary(
              operation,
              2,
              InstructionSource.Constant(left),
              InstructionSource.Constant(right),
              leftHigh,
              rightHigh
            )
          ),
          expected.toInt,
          clues(operation, leftHigh, rightHigh)
        )
      }
    }
  }
  test("unsigned byte dot product uses all four lanes and wraps the accumulator") {
    for (
      (left, right, accumulator, expected) <- Seq(
        (0x04030201L, 0x08070605L, 11L, 81L),
        (0xffffffffL, 0xffffffffL, 0xfffffff0L, 260084L),
        (0x80000000L, 0x02000000L, 9L, 265L)
      )
    ) assertEquals(
      run(
        Target("gfx1030"),
        Instruction.VDot4U32U8(
          2,
          InstructionSource.Constant(left),
          InstructionSource.Constant(right),
          InstructionSource.Constant(accumulator)
        )
      ),
      expected.toInt
    )
  }

  test(
    "mixed FMA selects half inputs independently, applies absolute before negate, and preserves the destination half"
  ) {
    // A.high = -2, B is f32 3, C.low = -0.5. abs(A), -abs(C) => 2*3-0.5 = 5.5.
    val a = InstructionSource.Constant(0xc0003c00L)
    val b = InstructionSource.Constant(0x40400000L)
    val c = InstructionSource.Constant(0x4400b800L)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, false)), 0x40b00000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, true)), 0x3f800000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, false)), 0x11224580)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, true)), 0x45803344)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, true, true)), 0x3c003344)
  }

  for (
    (kind, values) <- Seq(
      VectorComparisonKind.ClassFloat32 -> Seq(0x7f800001L, 0x7fc00001L, 0xff800000L, 0xbf800000L, 0x80000001L,
        0x80000000L, 0L, 1L, 0x3f800000L, 0x7f800000L),
      VectorComparisonKind.ClassFloat64 -> Seq(
        0x7ff0000000000001L, 0x7ff8000000000001L, 0xfff0000000000000L,
        0xbff0000000000000L, 0x8000000000000001L, 0x8000000000000000L, 0L, 1L, 0x3ff0000000000000L, 0x7ff0000000000000L
      )
    )
  ) test(s"$kind distinguishes all ten floating classes and tests membership in the supplied mask") {
    values.zipWithIndex.foreach { (bits, classBit) =>
      for (mask <- Seq(1L << classBit, 1023L ^ (1L << classBit))) {
        val instructions = Vec(
          Instruction.SMove(12, InstructionSource.Constant(bits & 0xffffffffL)),
          Instruction.SMove(13, InstructionSource.Constant(bits >>> 32)),
          Instruction.VCompareTo(
            10,
            VectorComparison(kind, VectorComparisonRelation.Class, false),
            InstructionSource.Scalar(12),
            InstructionSource.Constant(mask),
            false,
            false,
            false,
            false
          ),
          Instruction.VMove(2, InstructionSource.Scalar(10))
        )
        assertEquals(
          runSequence(Target("gfx900"), instructions),
          if (mask == (1L << classBit)) 1 else 0,
          clues(bits, mask)
        )
      }
    }
  }

}
