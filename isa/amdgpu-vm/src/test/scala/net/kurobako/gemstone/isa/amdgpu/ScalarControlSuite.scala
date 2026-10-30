package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class ScalarControlSuite extends Suite {
  private def run(operations: Instruction*): Either[String, Unit] =
    runIR(Vec.from(operations), RegionMemory()).left.map(_.message)

  private def constant(value: Long): InstructionSource = InstructionSource.Constant(value)

  test("branches, calls and computed jumps skip the instructions between them and their target") {
    assertEquals(
      run(Instruction.Branch(ScalarBranchCondition.Always, 2), Instruction.Trap(1), Instruction.End),
      Right(())
    )
    assertEquals(
      run(Instruction.Branch(ScalarBranchCondition.SccNonZero, 2), Instruction.End, Instruction.Trap(1)),
      Right(())
    )
    assertEquals(
      run(Instruction.Branch(ScalarBranchCondition.SccZero, 2), Instruction.End, Instruction.Trap(1)),
      Left("AMDGPU s_trap 1 at pc 0x8")
    )
    assertEquals(run(Instruction.Call(0, 2), Instruction.Trap(1), Instruction.End), Right(()))
    assertEquals(run(Instruction.SSetPc(constant(8)), Instruction.Trap(1), Instruction.End), Right(()))
    assertEquals(
      run(
        Instruction.SGetPc(0, 0),
        Instruction.SBinary(ScalarBinaryOperation.AddU32, 0, InstructionSource.Scalar(0), constant(12)),
        Instruction.SSetPc(InstructionSource.Scalar(0)),
        Instruction.Trap(1),
        Instruction.End
      ),
      Right(())
    )
    assertEquals(run(Instruction.SSwapPc(0, constant(8)), Instruction.Trap(1), Instruction.End), Right(()))
  }

  test("a jump to an address with no instruction faults, naming the instruction and the address") {
    assertEquals(run(Instruction.SSetPc(constant(0x40))), Left("AMDGPU setpc at 0x0 targets non-instruction 0x40"))
    assertEquals(
      run(Instruction.SSwapPc(0, constant(0x40))),
      Left("AMDGPU call target 0x40 is not present in the code object")
    )
    assertEquals(
      run(Instruction.ForkIndirect(constant(-1L), constant(0x40))),
      Left("AMDGPU fork at 0x0 targets non-instruction 0x40")
    )
  }

  test("a halt and a trap fault, naming the instruction's address") {
    assertEquals(
      run(Instruction.Wait, Instruction.Halt("s_sethalt halts the wave")),
      Left("AMDGPU s_sethalt halts the wave at pc 0x4")
    )
    assertEquals(run(Instruction.Trap(7)), Left("AMDGPU s_trap 7 at pc 0x0"))
  }

  test("a MODE read into VCC stores the slot and leaves the lane mask as it was") {
    assertEquals(
      run(
        Instruction.SSetMode(0, 4, constant(0xf)),
        Instruction.SMove(EncodingConstants.Fixed.VccLo, constant(0)),
        Instruction.SGetMode(EncodingConstants.Fixed.VccLo, 0, 4),
        Instruction.Branch(ScalarBranchCondition.VccZero, 5),
        Instruction.Trap(1),
        Instruction.End
      ),
      Right(())
    )
  }

  test(
    "a relative SGPR move faults outside the SGPR file, and a source that is not an SGPR faults where it is relative"
  ) {
    assertEquals(
      run(
        Instruction.SMove(EncodingConstants.Fixed.M0, constant(127L)),
        Instruction.SMoveRelative(1, constant(0L), 1, relativeSource = false, relativeDest = true)
      ),
      Left("relative SGPR move from s-1 to s128 is out of range")
    )
    assertEquals(
      run(Instruction.SMoveRelative(0, constant(5L), 1, relativeSource = true, relativeDest = false)),
      Left("s_movrels requires a scalar register source, found Constant(5)")
    )
  }

  test("global_wbinv flushes host writes, then drops the host reads the wave cached") {
    val events = mutable.ArrayBuffer.empty[String]
    class Recording extends ForwardingMemory(RegionMemory()) {
      override def flushHostWrites(): Either[Fault, Unit] = {
        events += "flush"
        super.flushHostWrites()
      }
      override def invalidateHostReads(): Unit = {
        events += "invalidate"
        super.invalidateHostReads()
      }
    }
    assertEquals(runIR(Vec(Instruction.CacheWritebackInvalidate, Instruction.End), Recording()), Right(()))
    assertEquals(Vec.from(events), Vec("flush", "invalidate", "flush"))
  }
}
