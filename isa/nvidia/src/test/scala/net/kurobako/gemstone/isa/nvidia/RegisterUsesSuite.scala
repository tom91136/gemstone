package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.*

class RegisterUsesSuite extends Suite {
  private def register(index: Int): Register   = Register(index.unsafe[Int >= 0])
  private def predicate(index: Int): Predicate = Predicate(index.unsafe[Int >= 0])

  test("a three-input add reads the carry it consumes and writes the one it produces") {
    def slots(mode: AddThreeCarryMode) = RegisterUses.of(Instruction.IntegerAddThree(
      register(0),
      Value.Register(register(1)),
      Value.Register(register(2)),
      Value.Register(register(3)),
      32,
      mode,
      false,
      false,
      false
    ))
    assertEquals(slots(AddThreeCarryMode.Consume).flagsRead, Vec(VMConditionCodes.Flag.Carry))
    assertEquals(slots(AddThreeCarryMode.Consume).flagsWritten, Vec.empty[VMConditionCodes.Flag])
    assertEquals(slots(AddThreeCarryMode.Produce).flagsWritten, Vec(VMConditionCodes.Flag.Carry))
    assertEquals(slots(AddThreeCarryMode.None).flagsRead, Vec.empty[VMConditionCodes.Flag])
  }

  test("a partial move split reads the half of the destination it leaves") {
    val partial = RegisterUses.of(Instruction.MoveSplit(
      Vec(Some(register(4)), Some(register(5))),
      Value.Register(register(6)),
      16
    ))
    val whole = RegisterUses.of(Instruction.MoveSplit(
      Vec(Some(register(4)), Some(register(5))),
      Value.Register(register(6)),
      32
    ))
    assertEquals(partial.registersRead, Vec(register(6), register(4), register(5)))
    assertEquals(partial.registersWritten, Vec(register(4), register(5)))
    assertEquals(whole.registersRead, Vec(register(6)))
  }

  test("convergence, the return stack and predicates keep the directions of their register fields") {
    assertEquals(
      RegisterUses.of(Instruction.Convergence(register(0), ConvergenceAction.Setup)).registersWritten,
      Vec(register(0))
    )
    assertEquals(
      RegisterUses.of(Instruction.Convergence(register(0), ConvergenceAction.Break)).registersRead,
      Vec(register(0))
    )
    assertEquals(RegisterUses.of(Instruction.ReturnStackSave(register(1))).registersRead, Vec(register(1)))
    assertEquals(RegisterUses.of(Instruction.ReturnStackPop(register(1))).registersWritten, Vec(register(1)))
    assertEquals(
      RegisterUses.of(Instruction.Select(
        register(2),
        Value.Register(register(3)),
        Value.Immediate(0L),
        predicate(0),
        32
      )).predicatesRead,
      Vec(predicate(0))
    )
  }

  test("a guarded write reads the slot it may leave unchanged and the guard") {
    val slots = RegisterUses.of(PredicatedInstruction(
      Instruction.Move(register(2), Value.Register(register(3)), 32),
      Some(ExecutionPredicate(predicate(1), negated = true))
    ))
    assertEquals(slots.registersRead, Vec(register(3), register(2)))
    assertEquals(slots.registersWritten, Vec(register(2)))
    assertEquals(slots.predicatesRead, Vec(predicate(1)))
    assertEquals(
      RegisterUses.of(Instruction.Store(
        Value.Address(Value.Register(register(4)), 8),
        Value.Immediate(1L),
        32,
        MemoryAccess.Global(system = false)
      )).registersRead,
      Vec(register(4))
    )
  }
}
