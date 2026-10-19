package net.kurobako.gemstone.isa.sass

/** Instruction classes shared by the control-flow analyses, the assembler and lowering. */
private object ControlFlow {

  /** Instructions that leave straight-line execution, including the legacy `.S` synchronisation suffix. */
  def transfers(instruction: Instruction): Boolean = instruction.form match {
    case _: Form.Bra | _: Form.Brx | _: Form.Brxu | _: Form.Call | _: Form.Cal |
        _: Form.Jmp | _: Form.Jmx | _: Form.Ret | _: Form.Exit | _: Form.Bssy |
        _: Form.Bsync | _: Form.Ssy | _: Form.Pbk | _: Form.Pcnt | _: Form.Sync |
        _: Form.Pret | _: Form.Brk | _: Form.Cont | _: Form.Bpt | _: Form.Kill |
        _: Form.Yield => true
    case _ => instruction.modifiers.contains(Instruction.Modifier.S)
  }

  /** Instructions whose final operand is a code address. */
  def directTarget(instruction: Instruction): Boolean = instruction.form match {
    case _: Form.Bra | _: Form.Cal | _: Form.Call | _: Form.Jcal | _: Form.Jmp |
        _: Form.Bssy | _: Form.Ssy | _: Form.Pbk | _: Form.Pcnt | _: Form.Pret =>
      true
    case _ => false
  }
}
