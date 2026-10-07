package net.kurobako.gemstone.isa.sass

/** Instruction classes shared by the control-flow analyses, the assembler and lowering. */
private object ControlFlow {

  /** Instructions that leave straight-line execution, including the legacy `.S` synchronisation suffix. */
  def transfers(instruction: Instruction): Boolean = instruction match {
    case _: Instruction.Bra | _: Instruction.Brx | _: Instruction.Brxu | _: Instruction.Call | _: Instruction.Cal |
        _: Instruction.Jmp | _: Instruction.Jmx | _: Instruction.Ret | _: Instruction.Exit | _: Instruction.Bssy |
        _: Instruction.Bsync | _: Instruction.Ssy | _: Instruction.Pbk | _: Instruction.Pcnt | _: Instruction.Sync |
        _: Instruction.Pret | _: Instruction.Brk | _: Instruction.Cont | _: Instruction.Bpt | _: Instruction.Kill |
        _: Instruction.Yield => true
    case _ => instruction.modifiers.contains(Instruction.Modifier.S)
  }

  /** Instructions whose final operand is a code address. */
  def directTarget(instruction: Instruction): Boolean = instruction match {
    case _: Instruction.Bra | _: Instruction.Cal | _: Instruction.Call | _: Instruction.Jcal | _: Instruction.Jmp |
        _: Instruction.Bssy | _: Instruction.Ssy | _: Instruction.Pbk | _: Instruction.Pcnt | _: Instruction.Pret =>
      true
    case _ => false
  }
}
