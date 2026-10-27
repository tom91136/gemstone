package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.RenderMachine.*

private object AmdGpuMachineRender {
  val Control: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.amdgpu.Control", "control")

  /** The type of the immediate a branch displaces its target by, in dwords. */
  val Displacement: Type = Type.signed(Type.width(16))

  /** The type of the code a trap carries. */
  val TrapCode: Type = Type.unsigned(Type.width(16))

  private val Dword = Type.unsigned(Type.width(32))
  private val Quad  = Type.unsigned(Type.width(64))

  /** The address of the instruction that follows, displaced by `immediate` dwords. */
  private def relative(immediate: Term): Term = Render.add(
    Render.add(Term.context(Machine, MachineCall("pc", Vec.empty, Vec.empty), Some(Word)), Term.Literal(4L)),
    Render.multiply(Term.Literal(4L), Term.Call("zext64", Vec(immediate)))
  )

  private def lane(naming: RenderContext.Names): LaneRenderer = LaneRenderer(naming, None)
}

/** Renders AMDGPU machine operations as statements for a [[Definition]] over the operands `naming` names. A program
  * location is the address `immediate`, the operand that holds a displacement or a trap code, locates relative to the
  * next instruction.
  */
private class AmdGpuMachineRender(naming: RenderContext.Names, immediate: => Term)
    extends RenderMachine[InstructionSource, ScalarRegisterIndex, Int, Int](
      AmdGpuMachineRender.lane(naming).source(_),
      register => naming.name(naming.scalar(register)),
      _ => AmdGpuMachineRender.relative(immediate),
      () => naming.temporary(),
      locationType = Some(Word)
    ) with AmdGpuMachine[Term, Unit, Term, RenderMachine.Rendered] {
  import AmdGpuMachineRender.*

  private def control(method: String, arguments: Vec[(Term, Option[Type])]): Term.Steps =
    statement(Control, MachineCall(method, Vec.empty, arguments))

  inline def condition(condition: ScalarBranchCondition): Term = condition match {
    case ScalarBranchCondition.Always      => Term.Literal(1L)
    case ScalarBranchCondition.SccZero     => Render.not(naming.scc)
    case ScalarBranchCondition.SccNonZero  => naming.scc
    case ScalarBranchCondition.VccZero     => Render.equal(naming.vcc, Term.Literal(0L))
    case ScalarBranchCondition.VccNonZero  => Render.notEqual(naming.vcc, Term.Literal(0L))
    case ScalarBranchCondition.ExecZero    => Render.equal(naming.exec, Term.Literal(0L))
    case ScalarBranchCondition.ExecNonZero => Render.notEqual(naming.exec, Term.Literal(0L))
  }

  inline def uniformPair(operand: InstructionSource): Term = lane(naming).pair(operand, PairRead.Integer)

  inline def writePair(target: ScalarRegisterIndex, value: Term)(inline next: => Term.Steps): Term.Steps =
    binding(naming.name(naming.scalarPair(target)), value) ++ next

  inline def callTo(link: ScalarRegisterIndex, returnAddress: Term, target: Term): Term.Steps =
    writePair(link, returnAddress)(jumpTo(target))

  inline def fork(mask: Term, taken: Int): Term.Steps =
    control("fork", Vec((mask, Some(Word)), (relative(immediate), Some(Word))))

  inline def forkTo(mask: Term, taken: Term): Term.Steps = control("fork", Vec((mask, Some(Word)), (taken, Some(Word))))

  inline def join(saved: Term): Term.Steps = control("join", Vec((saved, Some(Dword))))

  inline def trap(code: Int): Term.Steps = control("trap", Vec((immediate, Some(TrapCode))))

  inline def halt(reason: String): Term.Steps = control("halt", Vec((Term.Constant(s"\"$reason\""), None)))

  inline def fenceHost: Term.Steps = control("fenceHost", Vec.empty)

  private def valued(method: String, arguments: Vec[(Term, Option[Type])])(next: Term => Term.Steps): Term.Steps = {
    val name = naming.temporary()
    binding(name, Term.context(Control, MachineCall(method, Vec.empty, arguments), Some(Type.Bool), effect = true)) ++
      next(Term.Variable(name))
  }

  inline def barrier: Term.Steps = control("barrier", Vec.empty)

  inline def initBarrier(id: Term, count: Term): Term.Steps =
    control("initBarrier", Vec((id, Some(Word)), (count, Some(Word))))

  inline def joinBarrier(id: Term): Term.Steps = control("joinBarrier", Vec((id, Some(Word))))

  inline def leaveBarrier(inline next: Term => Term.Steps): Term.Steps = valued("leaveBarrier", Vec.empty)(next)

  inline def signalBarrier(id: Term, count: Term)(inline next: Term => Term.Steps): Term.Steps =
    valued("signalBarrier", Vec((id, Some(Word)), (count, Some(Word))))(next)

  inline def awaitBarrier(id: Term): Term.Steps = control("awaitBarrier", Vec((id, Some(Word))))

  inline def writeCarry(value: Term)(inline next: => Term.Steps): Term.Steps =
    binding(naming.name(naming.scc), value) ++ next

  inline def modeRegister: Term = naming.mode

  inline def writeMode(value: Term)(inline next: => Term.Steps): Term.Steps =
    binding(naming.name(naming.mode), value) ++ next

  inline def writeWord(target: ScalarRegisterIndex, value: Term)(inline next: => Term.Steps): Term.Steps =
    Term.Steps(naming.scalarWrite(target)(value)) ++ next

  inline def writeSlot(target: ScalarRegisterIndex, value: Term)(inline next: => Term.Steps): Term.Steps =
    Term.Steps(naming.slotWrite(target)(value)) ++ next

  inline def relativeOffset: Term = naming.m0

  inline def scalarAt(index: Term): Term =
    Term.context(Control, MachineCall("scalarAt", Vec.empty, Vec((index, Some(Word)))), Some(Dword))

  inline def writeScalarAt(index: Term, value: Term)(inline next: => Term.Steps): Term.Steps =
    control("writeScalarAt", Vec((index, Some(Word)), (value, Some(Dword)))) ++ next

  inline def requireScalars(destination: Term, origin: Term, words: Int)(inline next: => Term.Steps): Term.Steps =
    control(
      "requireScalars",
      Vec((destination, Some(Word)), (origin, Some(Word)), (Term.Literal(words.toLong), Some(Dword)))
    ) ++ next

  inline def bind(value: Term)(inline next: Term => Term.Steps): Term.Steps = value match {
    case Term.Literal(_) => next(value)
    case _               =>
      val name = naming.temporary()
      naming.keep(name)
      binding(name, value) ++ next(Term.Variable(name))
  }

  inline def clock(source: Clock): Term = Term.context(
    Control,
    MachineCall("clock", Vec.empty, Vec((Term.Constant(s"VMIR.Clock.$source"), None))),
    Some(Quad)
  )

  inline def waveId: Term = Term.context(Control, MachineCall("waveId", Vec.empty, Vec.empty), Some(Dword))

  inline def invalidateHostReads: Term.Steps = control("invalidateHostReads", Vec.empty)

  inline def writebackInvalidateHost: Term.Steps = fenceHost ++ invalidateHostReads

  inline def globalWaveSync(
      name: String,
      operation: GlobalWaveSyncOperation,
      offset: Int,
      data: VectorRegisterIndex,
      bank: RegisterBank
  ): Term.Steps = control(
    "globalWaveSync",
    Vec(
      (Term.Constant(s"\"$name\""), None),
      (Term.Constant(s"VMIR.GlobalWaveSyncOperation.$operation"), None),
      (Term.Literal(offset.toLong), Some(Dword)),
      (
        bank match {
          case RegisterBank.Vector      => naming.vector(data)
          case RegisterBank.Accumulator => naming.accumulator(data)
        },
        Some(Dword)
      )
    )
  )

  inline def localCounter(
      offset: Int,
      decrement: Boolean,
      dest: VectorRegisterIndex,
      bank: RegisterBank
  ): Term.Steps = {
    val before = naming.temporary()
    binding(
      before,
      Term.context(
        Control,
        MachineCall(
          "localCounter",
          Vec.empty,
          Vec((Term.Constant(decrement.toString), None), (Term.Literal(offset.toLong), Some(Dword)))
        ),
        Some(Dword),
        effect = true
      )
    ) ++ binding(
      naming.name(bank match {
        case RegisterBank.Vector      => naming.vector(dest)
        case RegisterBank.Accumulator => naming.accumulator(dest)
      }),
      Term.Variable(before)
    )
  }
}
