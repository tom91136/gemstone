package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.compiletime.testing.typeCheckErrors

class RegisterBoundsSuite extends munit.FunSuite {

  test("decoder rejects scalar pairs that cross the 128-slot bank") {
    val target   = Target("gfx900")
    val parsed   = asm.Parser.parse("s_mov_b64 s[0:1], 0").value
    val bytes    = asm.Assembler.encode(parsed, target).value
    val lastPair = bytes.updated(2, ((bytes(2) & 0x80) | 126).toByte)
    val crossing = bytes.updated(2, (bytes(2) | 127).toByte)
    assert(VMCompiler.decodeInstruction(lastPair, target).isRight)
    assert(VMCompiler.decodeInstruction(crossing, target).isLeft)
    assert(VMCompiler.validate(crossing, target).isLeft)
  }

  test("decoder rejects scalar loads crossing the bank boundary") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("s_load_dwordx4 s[124:127], s[0:1], 0").value
    val bytes  = asm.Assembler.encode(parsed, target).value
    assert(VMCompiler.decodeInstruction(bytes, target).isRight)
    val field = InstructionLayout.Catalogue.forFormat(target, EncodingFormat.SMEM).get.byName(EncodingField.SDATA)
    val word  = bytes.zipWithIndex.foldLeft(0L) { case (word, (byte, index)) =>
      word | ((byte.toLong & 0xffL) << (index * 8))
    }
    val changed  = (word & ~field.mask.toLong) | field.put(BigInt(125)).get.toLong
    val crossing = bytes.zipWithIndex.map { case (_, index) => (changed >>> (index * 8)).toByte }
    assert(VMCompiler.decodeInstruction(crossing, target).isLeft)
    assert(VMCompiler.validate(crossing, target).isLeft)
  }

  test("ldexp decodes a scalar exponent in the final vector register") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("v_ldexp_f64 v[0:1], v[2:3], v255").value
    assert(VMCompiler.decodeInstruction(asm.Assembler.encode(parsed, target).value, target).isRight)
  }

  test("register spans fit their banks before execution") {
    val zero                                                                 = InstructionSource.Constant(0)
    def valid(instruction: Instruction, wave: Int in (32, 64) = 64): Boolean =
      VMRegisterBounds.validate(instruction, wave).isRight
    assert(valid(Instruction.SLoad(124, 4, 126, 0, None)))
    assert(!valid(Instruction.SLoad(125, 4, 126, 0, None)))
    assert(!valid(Instruction.SLoad(0, 1, 127, 0, None)))
    assert(valid(Instruction.SMove(127, InstructionSource.Constant(0))))
    assert(valid(Instruction.SMove64(0, InstructionSource.Scalar(127))))   // EXEC_HI is a scalar special source.
    assert(valid(Instruction.SMove64(126, InstructionSource.Scalar(106)))) // EXEC and VCC pairs.
    assert(valid(Instruction.VBinary64(
      VectorBinary64Operation.Add,
      254,
      InstructionSource.Scalar(126),
      InstructionSource.VectorRegister(254),
      SourceModifiers.Unmodified
    )))
    assert(valid(Instruction.VBinary64(
      VectorBinary64Operation.Add,
      0,
      InstructionSource.Scalar(127),
      InstructionSource.VectorRegister(0),
      SourceModifiers.Unmodified
    )))
    assert(valid(Instruction.VShiftRight64(254, InstructionSource.Constant(1), InstructionSource.VectorRegister(254))))
    assert(!valid(Instruction.VShiftRight64(255, InstructionSource.Constant(1), InstructionSource.VectorRegister(0))))
    assert(!valid(Instruction.VShiftRight64(0, InstructionSource.Constant(1), InstructionSource.VectorRegister(255))))
    assert(valid(Instruction.Load(252, MemoryAddress.Flat(254, 0), 16, false, LoadPlacement.Full)))
    assert(!valid(Instruction.Load(253, MemoryAddress.Flat(254, 0), 16, false, LoadPlacement.Full)))
    assert(!valid(Instruction.Load(0, MemoryAddress.Flat(255, 0), 4, false, LoadPlacement.Full)))
    assert(valid(Instruction.SLoad(112, 16, 126, 0, None)))
    assert(!valid(Instruction.SLoad(113, 16, 126, 0, None)))
    assert(valid(Instruction.LoadPair(252, 0, 8, 0, 8)))
    assert(!valid(Instruction.LoadPair(253, 0, 8, 0, 8)))
    assert(valid(Instruction.Atomic(AtomicOperation.Add, 254, MemoryAddress.Flat(254, 0), 254, 8, true)))
    assert(!valid(Instruction.Atomic(AtomicOperation.Add, 255, MemoryAddress.Flat(254, 0), 254, 8, true)))
    assert(valid(Instruction.Atomic(AtomicOperation.Add, 255, MemoryAddress.Flat(254, 0), 254, 8, false)))
    assert(valid(Instruction.VPackedMove32(254, zero, zero, false, false)))
    assert(!valid(Instruction.VPackedMove32(255, zero, zero, false, false)))
    assert(!valid(Instruction.VShiftLeftAdd64(0, InstructionSource.VectorRegister(255), zero, zero)))
    // ACC and VGPR banks each have 256 entries; the mask changes the bank, never the span.
    for (bank <- Vec[Int in 0 ~ 7](0, 7)) {
      assert(valid(Instruction.VMatrixF32(240, zero, zero, zero, 32, 0, 0, bank, 0)))
      assert(!valid(Instruction.VMatrixF32(241, zero, zero, zero, 32, 0, 0, bank, 0)))
    }
    assert(valid(Instruction.VWaveMatrixF16(248, 248, 248, zero, false, false, 0, 0), 32))
    assert(!valid(Instruction.VWaveMatrixF16(249, 248, 248, zero, false, false, 0, 0), 32))
    assert(!valid(Instruction.VWaveMatrixF16(252, 249, 248, zero, false, false, 0, 0), 64))
    assert(valid(Instruction.SMove64(126, InstructionSource.Constant(1))))
    for (register <- Seq[ScalarRegisterIndex](106, 107, 126, 127))
      assert(valid(Instruction.SMove64(4, InstructionSource.Scalar(register))))
    for (register <- Seq[ScalarRegisterIndex](107, 127))
      assert(valid(Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(register))))
    // a wave64 carry-out mask is a register pair, so it cannot start at the last scalar slot
    val carry = Instruction.VAddCarry(2, 127, InstructionSource.Constant(1), InstructionSource.Constant(1), None)
    assert(valid(carry, 32))
    assert(!valid(carry))
    assert(!valid(Instruction.VBinary64(
      VectorBinary64Operation.Add,
      0,
      InstructionSource.VectorRegister(2),
      InstructionSource.VectorRegister(255),
      SourceModifiers.Unmodified
    )))
  }

  private def unsatisfied(errors: List[scala.compiletime.testing.Error]): Boolean =
    errors.exists(_.message.contains("does not satisfy"))

  test("register indices, atomic widths, and scratch sizes are bounded at compile time") {
    assert(unsatisfied(typeCheckErrors("InstructionSource.Scalar(128)")))
    assert(unsatisfied(typeCheckErrors("Instruction.SMove(128, InstructionSource.Constant(0))")))
    assert(unsatisfied(typeCheckErrors("VMState.VectorRegisters(1)(256)")))
    assert(
      unsatisfied(typeCheckErrors("Instruction.Atomic(AtomicOperation.Add, 0, MemoryAddress.Flat(0, 0), 0, 5, true)"))
    )
    assert(unsatisfied(typeCheckErrors("Instruction.CompareSwap(0, MemoryAddress.Flat(0, 0), 0, 6, true)")))
    assert(unsatisfied(typeCheckErrors("VMState.ScratchMemory(65, 1)")))
    assert(unsatisfied(typeCheckErrors("VMState.ScratchMemory(1, -1)")))
    val scratch = VMState.ScratchMemory(64, 4)
    scratch(63)(3) = 42.toByte
    assertEquals(scratch(63)(3), 42.toByte)
    assertEquals(VMState.ScratchMemory(1, 0)(0).length, 0)
    assert(unsatisfied(typeCheckErrors("VMState.ScratchMemory(64, 1)(-1)")))
    val _ = intercept[IllegalArgumentException](VMState.ScratchMemory(32, 1)(40))
  }

  test("scalar and vector register offsets retain their bank bound") {
    val scalar: ScalarRegisterIndex = 126
    assertEquals(scalar.offsetBy(1).raw, 127)
    val _                           = intercept[IllegalArgumentException](scalar.offsetBy(2))
    val _                           = intercept[IllegalArgumentException](scalar.offsetBy(Int.MaxValue))
    val vector: VectorRegisterIndex = 254
    assertEquals(vector.offsetBy(1).raw, 255)
    val _ = intercept[IllegalArgumentException](vector.offsetBy(2))
    val _ = intercept[IllegalArgumentException](vector.offsetBy(Int.MaxValue))
  }

  test("decoding rejects a register pair that crosses the bank boundary") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("v_add_f64 v[254:255], v[0:1], v[2:3]").value
    val bytes  = asm.Assembler.encode(parsed, target).value
    assert(VMCompiler.decodeInstruction(bytes, target).isRight)
    // VOP3 VDST is the low byte; v255 is encodable but cannot start a register pair.
    val invalid = bytes.updated(0, 255.toByte)
    assert(VMCompiler.decodeInstruction(invalid, target).left.exists(_.contains("register span")))
    assert(VMCompiler.validate(invalid, target).isLeft)
  }

  test("lane bounds prevent shift aliasing and access outside a partial wave") {
    val full = VMState.LaneBits(64, 1L)
    full.update(63, true)
    assert(full(0))
    assert(full(63))
    assertEquals(full.count, 2)
    assert(unsatisfied(typeCheckErrors("VMState.LaneBits(64, 1L).update(64, false)")))
    assert(unsatisfied(typeCheckErrors("VMState.LaneBits(64, 1L)(-1)")))
    val partial = VMState.LaneBits(32, 1L)
    val _       = intercept[IllegalArgumentException](partial(40))
    val _       = intercept[IllegalArgumentException](partial.update(40, true))
    assertEquals(partial.bits, 1L)
    assertEquals(partial.count, 1)
  }

  test("RDNA4 scalar reciprocal rejects vector-only destination slots as a decode error") {
    // LLVM: v_s_rcp_f32 s4, s6. VOP3's eight-bit destination also encodes invalid SGPR slots.
    val bytes = Vec(0x04, 0x00, 0x84, 0xd6, 0x06, 0x00, 0x00, 0x00).map(_.toByte)
    assert(VMCompiler.decodeInstruction(bytes, Target("gfx1200")).isRight)
    assert(VMCompiler.decodeInstruction(bytes.updated(0, 0x80.toByte), Target("gfx1200"))
      .left.exists(_.contains("scalar destination")))
  }
}
