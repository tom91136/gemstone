package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

class RegisterBoundsSuite extends munit.FunSuite {
  test("scratch storage requires bounded lanes and nonnegative allocation sizes") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMState.ScratchMemory(64, 1)(64)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMState.ScratchMemory(1, -1)
    """).nonEmpty)
    val scratch = VMState.ScratchMemory(64, 4)
    scratch(63)(3) = 42.toByte
    assertEquals(scratch(63)(3), 42.toByte)
    assertEquals(VMState.ScratchMemory(1, 0)(0).length, 0)
    val _ = intercept[IllegalArgumentException](VMState.ScratchMemory(32, 1)(32))
  }

  test("vector bank requires a bounded index throughout access") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMState.VectorRegisters(1)(256)
    """).nonEmpty)
    val bank = VMState.VectorRegisters(1)
    val first: VectorRegisterIndex = 254
    bank(first.offsetBy(1))(0) = 42L
    assertEquals(bank(255)(0), 42L)
  }

  test("decoder rejects scalar pairs that cross the 128-slot bank") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("s_mov_b64 s[0:1], 0").toOption.get
    val bytes = asm.Assembler.encode(parsed, target).toOption.get
    val lastPair = bytes.updated(2, ((bytes(2) & 0x80) | 126).toByte)
    val crossing = bytes.updated(2, (bytes(2) | 127).toByte)
    assert(VMCompiler.decodeInstruction(lastPair, target).isRight)
    assert(VMCompiler.decodeInstruction(crossing, target).isLeft)
    assert(VMCompiler.validate(crossing, target).isLeft)
  }

  test("decoder rejects scalar loads crossing the bank boundary") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("s_load_dwordx4 s[124:127], s[0:1], 0").toOption.get
    val bytes = asm.Assembler.encode(parsed, target).toOption.get
    assert(VMCompiler.decodeInstruction(bytes, target).isRight)
    val field = InstructionLayout.Catalogue.forFormat(target, EncodingFormat.SMEM).get.byName(EncodingField.SDATA)
    val word = bytes.zipWithIndex.foldLeft(0L) { case (word, (byte, index)) =>
      word | ((byte.toLong & 0xffL) << (index * 8))
    }
    val changed = (word & ~field.mask.toLong) | field.put(BigInt(125)).get.toLong
    val crossing = bytes.zipWithIndex.map { case (_, index) => (changed >>> (index * 8)).toByte }
    assert(VMCompiler.decodeInstruction(crossing, target).isLeft)
    assert(VMCompiler.validate(crossing, target).isLeft)
  }

  test("scalar spans use scalar bounds while preserving special mask sources") {
    def valid(instruction: Instruction): Boolean = VMRegisterBounds.validate(instruction).isRight
    assert(valid(Instruction.SLoad(124, 4, 126, 0, None)))
    assert(!valid(Instruction.SLoad(125, 4, 126, 0, None)))
    assert(!valid(Instruction.SLoad(0, 1, 127, 0, None)))
    assert(valid(Instruction.SMove(127, InstructionSource.Constant(0))))
    assert(valid(Instruction.SMove64(0, InstructionSource.Scalar(127)))) // EXEC_HI is a scalar special source.
    assert(valid(Instruction.SMove64(126, InstructionSource.Scalar(106)))) // EXEC and VCC pairs.
    assert(valid(Instruction.VBinary64Modified(VectorBinary64Operation.Add, 254,
      InstructionSource.Scalar(126), InstructionSource.VectorRegister(254), false, false, false, false)))
    assert(valid(Instruction.VBinary64Modified(VectorBinary64Operation.Add, 0,
      InstructionSource.Scalar(127), InstructionSource.VectorRegister(0), false, false, false, false)))
  }

  test("ldexp decodes a scalar exponent in the final vector register") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("v_ldexp_f64 v[0:1], v[2:3], v255").toOption.get
    val bytes = asm.Assembler.encode(parsed, target).toOption.get
    assert(VMCompiler.decodeInstruction(bytes, target).isRight)
    val paired = Instruction.VBinary64Modified(
      VectorBinary64Operation.Add, 0,
      InstructionSource.VectorRegister(2), InstructionSource.VectorRegister(255),
      false, false, false, false
    )
    assert(VMRegisterBounds.validate(paired).isLeft)
  }

  test("scalar indices cannot be constructed from vector-only slots") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMIR.InstructionSource.Scalar(128)
    """).exists(_.message.contains("does not satisfy")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMIR.Instruction.SMove(128, VMIR.InstructionSource.Constant(0))
    """).exists(_.message.contains("does not satisfy")))
  }

  test("scalar register offsets retain their bank bound") {
    val first: ScalarRegisterIndex = 126
    val last: ScalarRegisterIndex = first.offsetBy(1)
    assertEquals(last.raw, 127)
    val _ = intercept[IllegalArgumentException](last.offsetBy(1))
    val _ = intercept[IllegalArgumentException](first.offsetBy(Int.MaxValue))
  }

  test("register offsets retain the upper bound") {
    val first: VectorRegisterIndex = 254
    val last: VectorRegisterIndex  = first.offsetBy(1)
    assertEquals(last.raw, 255)
    intercept[IllegalArgumentException](last.offsetBy(1))
    intercept[IllegalArgumentException](first.offsetBy(Int.MaxValue))
  }

  test("multiword instruction operands must fit before execution") {
    def valid(instruction: Instruction): Boolean = VMRegisterBounds.validate(instruction).isRight
    assert(valid(Instruction.VShiftRight64(254, InstructionSource.Constant(1), InstructionSource.VectorRegister(254))))
    assert(!valid(Instruction.VShiftRight64(255, InstructionSource.Constant(1), InstructionSource.VectorRegister(0))))
    assert(!valid(Instruction.VShiftRight64(0, InstructionSource.Constant(1), InstructionSource.VectorRegister(255))))
    assert(valid(Instruction.FlatLoad(252, 254, 16, 0)))
    assert(!valid(Instruction.FlatLoad(253, 254, 16, 0)))
    assert(!valid(Instruction.FlatLoad(0, 255, 4, 0)))
    assert(valid(Instruction.SLoad(112, 16, 126, 0, None)))
    assert(!valid(Instruction.SLoad(113, 16, 126, 0, None)))
    assert(valid(Instruction.DSRead(252, 0, 8, 0, Some(8))))
    assert(!valid(Instruction.DSRead(253, 0, 8, 0, Some(8))))
    assert(valid(Instruction.GlobalAtomicAdd(254, 254, None, 254, 8, 0, true)))
    assert(!valid(Instruction.GlobalAtomicAdd(255, 254, None, 254, 8, 0, true)))
    assert(valid(Instruction.GlobalAtomicAdd(255, 254, None, 254, 8, 0, false)))
  }

  test("decoding rejects a register pair that crosses the bank boundary") {
    val target = Target("gfx900")
    val parsed = asm.Parser.parse("v_add_f64 v[254:255], v[0:1], v[2:3]").toOption.get
    val bytes  = asm.Assembler.encode(parsed, target).toOption.get
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
    val partial = VMState.LaneBits(32, 1L)
    intercept[IllegalArgumentException](partial(32))
    intercept[IllegalArgumentException](partial.update(32, true))
    assertEquals(partial.bits, 1L)
    assertEquals(partial.count, 1)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMState.LaneBits(64, 1L).update(64, false)
    """).exists(_.message.contains("does not satisfy")))
  }

  test("atomic instructions reject intermediate byte widths at compile time") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMIR.Instruction.GlobalAtomicAdd(0, 0, None, 0, 5, 0, true)
    """).exists(_.message.contains("does not satisfy")))
  }
}
