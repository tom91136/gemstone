package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

private object LaneRenderer {

  /** `instruction`'s semantics for one lane as assignments to what it writes, named and typed by `naming`, or `None`
    * when it has no staged semantics.
    */
  def describe(instruction: Instruction, naming: RenderContext.Names): Option[Term.Steps] = {
    val sinks = LaneRenderer(naming, None)
    sinks.access(instruction)
      .orElse(naming.isa.flatMap(ResourceRender(naming, _).access(instruction)))
      .orElse(render(instruction, sinks))
  }

  def render(instruction: Instruction, sinks: LaneRenderer): Option[Term.Steps] =
    AMDGPUStaged.staged(Render, instruction)(sinks)(None)
}

/** Renders staged semantics for one lane as assignments from each written operand's name to its value over the read
  * operands' names, as `naming` names and types them. Under DPP, `guard` holds when the lane takes part.
  */
private class LaneRenderer(naming: RenderContext.Names, guard: Option[Term])
    extends AMDGPUSinks[Term, Option[Term.Steps]] {
  import RenderContext.*
  import Term.*

  private val dword = Type.unsigned(32)
  private val lanes = Literal(naming.waveSize.toLong)

  /** A per-lane write, which a lane DPP leaves out keeps; the kept value comes first, so the write takes its type. */
  private def lane(destination: Term, value: Term): Term.Step =
    Term.Step(naming.name(destination), guard.fold(value)(enabled => Select(Render.not(enabled), destination, value)))

  /** EXEC as this lane's operation sees it. */
  private def exec: Term = guard.fold(ExecMask)(enabled => ballot(enabled))

  /** The lane mask of the participating lanes whose `predicate` holds. */
  private def ballot(predicate: Term): Term =
    Term.context(
      Wave,
      "ballot",
      Vec.empty,
      Vec((guard.fold(predicate)(Render.and(_, predicate)), Some(Type.Bool))),
      Some(Type.unsigned(64))
    )

  /** `mask` with the participating lanes' bits replaced by `predicate`. */
  private def laneBits(mask: Term, predicate: Term): Term =
    Render.or(Render.and(mask, Render.not(exec)), ballot(predicate))

  /** This lane's bit of a lane mask, as 1 or 0. */
  private def bit(mask: Term): Term = Render.and(Render.shiftRightLogical(mask, Lane), Literal(1L))

  /** A source as a lane reads it: registers, state and literals named, constants as their bits. */
  private def source(value: InstructionSource): Term = value match {
    case InstructionSource.Scalar(index)         => naming.scalar(index)
    case InstructionSource.VectorRegister(index) => naming.vector(index)
    case InstructionSource.Constant(raw)         => Literal(raw)
    case InstructionSource.Literal(raw)          => naming.literal(raw)
    case InstructionSource.Floating(constant)    => Literal(IEEE754.floatToBinary32(constant.toFloat).toLong)
    case InstructionSource.Condition(condition)  => this.condition(condition)
    case InstructionSource.DPP                   => Variable(Routed)
    case InstructionSource.HighHalf(half)        =>
      Render.and(Render.shiftRightLogical(source(half), Literal(16L)), Literal(0xffffL))
    case InstructionSource.Selected(part, selection, signExtend) =>
      Render.truncate(selected(source(part), selection, signExtend), 32)
  }

  private def condition(value: ScalarBranchCondition): Term = value match {
    case ScalarBranchCondition.Always      => Literal(1L)
    case ScalarBranchCondition.SccZero     => Render.xor(naming.scc, Literal(1L))
    case ScalarBranchCondition.SccNonZero  => naming.scc
    case ScalarBranchCondition.VccZero     => Render.equal(naming.vcc, Literal(0L))
    case ScalarBranchCondition.VccNonZero  => Render.notEqual(naming.vcc, Literal(0L))
    case ScalarBranchCondition.ExecZero    => Render.equal(naming.exec, Literal(0L))
    case ScalarBranchCondition.ExecNonZero => Render.notEqual(naming.exec, Literal(0L))
  }

  /** A source's low dword; registers and literals are dwords already. */
  private def low32(value: InstructionSource): Term = value match {
    case InstructionSource.Scalar(_) | InstructionSource.VectorRegister(_) | InstructionSource.Literal(_) |
        InstructionSource.DPP | InstructionSource.HighHalf(_) | InstructionSource.Condition(_) |
        InstructionSource.Selected(source = _) =>
      source(value)
    case other => Render.truncate(source(other), 32)
  }

  /** A source read as 64 bits. */
  private def pair(value: InstructionSource, read: PairRead): Term = value match {
    case InstructionSource.Scalar(index)         => naming.scalarPair(index)
    case InstructionSource.VectorRegister(index) => naming.vectorPair(index)
    case InstructionSource.Literal(raw)          =>
      if (read == PairRead.Floating) Render.shiftLeft(naming.literal(raw), Literal(32L)) else naming.literal(raw)
    case InstructionSource.Floating(constant) =>
      if (read == PairRead.Packed) Literal(IEEE754.floatToBinary32(constant.toFloat).toLong)
      else Literal(IEEE754.doubleToBinary64(constant))
    case InstructionSource.HighHalf(half) =>
      Render.and(Render.shiftRightLogical(pair(half, PairRead.Integer), Literal(16L)), Literal(0xffffL))
    case InstructionSource.DPP => Variable(Routed)
    case other                 => source(other)
  }

  /** A source read as a lane mask. */
  private def mask(value: InstructionSource): Term = value match {
    case InstructionSource.Scalar(index) => naming.scalarMask(index)
    case other                           => pair(other, PairRead.Integer)
  }

  private def maskDestination(index: ScalarRegisterIndex): Option[String] =
    Option.when(!naming.discards(index))(naming.name(naming.scalarMask(index)))

  private def bank(registers: RegisterBank, index: VectorRegisterIndex): Term = registers match {
    case RegisterBank.Vector      => naming.vector(index)
    case RegisterBank.Accumulator => naming.accumulator(index)
  }

  private def read(value: Term, index: Term): Term =
    Term.context(Wave, "read", Vec.empty, Vec((value, Some(dword)), (index, Some(dword))), Some(dword))

  /** The VGPR `index` past the register `field` of M0 offsets it by, or 256 where that is beyond the last. */
  private def relative(index: Int, field: RelativeField): Term = {
    val moved =
      if (field.shift == 0) naming.m0 else Render.shiftRightLogical(naming.m0, Literal(field.shift.toLong))
    val offset = if (field.mask == 0xffffffffL) moved else Render.and(moved, Literal(field.mask))
    Select(
      Render.lessThan(offset, Literal(256L - index), signed = false),
      Render.add(Literal(index.toLong), offset),
      Literal(256L)
    )
  }

  private def relativeRead(index: Int, field: RelativeField): Term =
    Term.context(Wave, "vgpr", Vec.empty, Vec((relative(index, field), Some(dword))), Some(dword))

  /** A write of `value` to the VGPR M0 offsets, which a lane DPP leaves out keeps its dword through, and which goes
    * nowhere unless `paired` holds.
    */
  private def relativeWrite(index: Int, field: RelativeField, value: => Term, paired: Option[Term] = None): Term =
    Term.context(
      Wave,
      "writeVgpr",
      Vec.empty,
      Vec(
        (paired.fold(relative(index, field))(Select(_, relative(index, field), Literal(256L))), Some(dword)),
        (guard.fold(value)(enabled => Select(Render.not(enabled), relativeRead(index, field), value)), Some(dword))
      ),
      None,
      effect = true
    )

  /** The source a relative move reads: the VGPR M0 offsets, selected as SDWA says. */
  private def relativeSource(value: InstructionSource, field: RelativeField): Option[Term] = value match {
    case InstructionSource.VectorRegister(index) => Some(relativeRead(index, field))
    case InstructionSource.Selected(InstructionSource.VectorRegister(index), selection, signExtend) =>
      Some(Render.truncate(selected(relativeRead(index, field), selection, signExtend), 32))
    case _ => None
  }

  /** A source as lane `index` holds it: a vector register of that lane, or a uniform source. */
  private def from(value: InstructionSource, index: Term): Term = value match {
    case InstructionSource.VectorRegister(register) => read(naming.vector(register), index)
    case other                                      => source(other)
  }

  /** `term` with the modifiers for source `index` applied to its sign at `width` bits. */
  private def modified(term: Term, modifiers: SourceModifiers, index: Int, width: Int in (16, 32, 64)): Term = {
    val sign      = Literal(1L << (width - 1))
    val magnitude = if ((modifiers.absolute & (1 << index)) != 0) Render.and(term, Render.not(sign)) else term
    if ((modifiers.negate & (1 << index)) != 0) Render.xor(magnitude, sign) else magnitude
  }

  private def modified32(value: InstructionSource, modifiers: SourceModifiers, index: Int, width: Int in (16, 32, 64))
      : Term = Render.truncate(modified(source(value), modifiers, index, width), 32)

  private def selected(term: Term, selection: SubDwordSelection, signExtend: Boolean): Term =
    Render.selectedPart(term, selection, signExtend)

  /** The nested instruction's assignments for the lanes `enabled` selects. */
  private def nested(instruction: Instruction, enabled: Option[Term]): Option[Term.Steps] =
    LaneRenderer.render(instruction, LaneRenderer(naming, enabled))

  private val Routed = "dpp"

  inline def modeRegister: Term = naming.mode

  inline def section(inline body: Option[Term.Steps]): Option[Term.Steps] = {
    def render(): Option[Term.Steps] = body
    render()
  }

  inline def sequence(inline before: Option[Term.Steps], inline after: Option[Term.Steps]): Option[Term.Steps] =
    before.flatMap(first => after.map(first ++ _))

  inline def scalarDword(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(naming.scalarWrite(dest)(result(low32(a), low32(b), naming.scc))))

  inline def scalarDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val (x, y, carry) = (low32(a), low32(b), naming.scc)
    Some(Term.Steps(
      Term.Step("result", result(x, y, carry)),
      Term.Step("flag", flag(x, y, carry, Variable("result"))),
      naming.scalarWrite(dest)(Variable("result")),
      Term.Step(naming.name(naming.scc), Variable("flag"))
    ))
  }

  inline def scalarPair(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(Term.Step(
      naming.name(naming.scalarPair(dest)),
      result(pair(a, PairRead.Integer), pair(b, PairRead.Integer), naming.scc)
    )))

  inline def scalarPairFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] =
    pairFlagged(dest, pair(a, PairRead.Integer), pair(b, PairRead.Integer), naming.scc)(result, flag)

  inline def scalarPairDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = pairFlagged(dest, pair(a, PairRead.Integer), low32(b), naming.scc)(result, flag)

  private inline def pairFlagged(dest: ScalarRegisterIndex, x: Term, y: Term, carry: Term)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(
    Term.Step("result", result(x, y, carry)),
    Term.Step("flag", flag(x, y, carry, Variable("result"))),
    Term.Step(naming.name(naming.scalarPair(dest)), Variable("result")),
    Term.Step(naming.name(naming.scc), Variable("flag"))
  ))

  private def scalarOperand(value: InstructionSource, wide: Boolean): Term =
    if (wide) pair(value, PairRead.Integer) else low32(value)

  /** The destination's previous dword, and a write to it as a dword or register pair. */
  private def scalarWritten(dest: ScalarRegisterIndex, wide: Boolean): (Term, Term => Term.Step) =
    if (wide) {
      val written = naming.scalarPair(dest)
      val name    = naming.name(written)
      (Render.truncate(written, 32), value => Term.Step(name, value))
    } else (naming.scalar(dest), naming.scalarWrite(dest))

  inline def scalarUnary(dest: ScalarRegisterIndex, a: InstructionSource, readsPair: Boolean, writesPair: Boolean)(
      inline result: (Term, Term) => Term
  ): Option[Term.Steps] = {
    val (previous, write) = scalarWritten(dest, writesPair)
    Some(Term.Steps(write(result(scalarOperand(a, readsPair), previous))))
  }

  inline def scalarUnaryFlagged(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      readsPair: Boolean,
      writesPair: Boolean
  )(inline result: (Term, Term) => Term, inline flag: (Term, Term, Term) => Term): Option[Term.Steps] = {
    val (previous, write) = scalarWritten(dest, writesPair)
    val value             = scalarOperand(a, readsPair)
    Some(Term.Steps(
      Term.Step("previous", previous),
      Term.Step("result", result(value, Variable("previous"))),
      write(Variable("result")),
      Term.Step(naming.name(naming.scc), flag(value, Variable("previous"), Variable("result")))
    ))
  }

  inline def scalarImmediate(dest: ScalarRegisterIndex, immediate: Long)(
      inline result: (Term, Term) => Term,
      inline flag: (Term, Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(
    Term.Step("previous", naming.scalar(dest)),
    Term.Step("result", result(Variable("previous"), Literal(immediate))),
    naming.scalarWrite(dest)(Variable("result")),
    Term.Step(naming.name(naming.scc), flag(Variable("previous"), Literal(immediate), Variable("result")))
  ))

  inline def saveExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
      inline result: (Term, Term) => Term,
      inline flag: (Term, Term, Term) => Term
  ): Option[Term.Steps] = execWrite(dest, a, width, saved = true)(result, flag)

  inline def writeExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
      inline result: (Term, Term) => Term,
      inline flag: (Term, Term, Term) => Term
  ): Option[Term.Steps] = execWrite(dest, a, width, saved = false)(result, flag)

  /** EXEC as `result` computes it from a source and its previous value, a dword of it at a `width` of 32, and into
    * `dest` the previous value when `saved` and the next otherwise.
    */
  private inline def execWrite(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      width: Int in (32, 64),
      inline saved: Boolean
  )(
      inline result: (Term, Term) => Term,
      inline flag: (Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val narrow = width == 32
    val value  = if (narrow) Render.truncate(pair(a, PairRead.Integer), 32) else pair(a, PairRead.Integer)
    val write: Term => Term.Step =
      if (narrow) naming.scalarWrite(dest)
      else {
        val name = naming.name(naming.scalarPair(dest))
        value => Term.Step(name, value)
      }
    // a dword write keeps a wave64 EXEC's high half
    val next =
      if (narrow && naming.waveSize == 64)
        Render.or(
          Render.and(naming.exec, Literal(0xffffffff00000000L)),
          if (saved) Render.truncate(Variable("next"), 32) else Variable("next")
        )
      else Variable("next")
    Some(Term.Steps(
      Term.Step("previous", if (narrow) Render.truncate(naming.exec, 32) else naming.exec),
      Term.Step(
        "next",
        if (narrow && !saved) Render.truncate(result(value, Variable("previous")), 32)
        else result(value, Variable("previous"))
      ),
      write(Variable(if (saved) "previous" else "next")),
      Term.Step(naming.name(naming.exec), next),
      Term.Step(naming.name(naming.scc), flag(value, Variable("previous"), Variable("next")))
    ))
  }

  inline def scalarCompare(a: InstructionSource, b: InstructionSource, pair: Boolean)(
      inline kernel: (Term, Term) => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(Term.Step(naming.name(naming.scc), kernel(scalarOperand(a, pair), scalarOperand(b, pair)))))

  inline def scalarMove(dest: ScalarRegisterIndex, a: InstructionSource, pair: Boolean)(
      inline kernel: Term => Term
  ): Option[Term.Steps] =
    if (pair) Some(Term.Steps(Term.Step(naming.name(naming.scalarPair(dest)), kernel(this.pair(a, PairRead.Integer)))))
    else Some(Term.Steps(naming.scalarWrite(dest)(kernel(source(a)))))

  inline def scalarModified(dest: ScalarRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Term => Term
  ): Option[Term.Steps] = Some(Term.Steps(naming.scalarWrite(dest)(kernel(modified32(a, modifiers, 0, 32)))))

  inline def scalarTernary(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] =
    Some(Term.Steps(naming.scalarWrite(dest)(kernel(source(a), source(b), source(c)))))

  inline def readFirstLane(dest: ScalarRegisterIndex, a: InstructionSource)(inline kernel: Term => Term)
      : Option[Term.Steps] =
    Some(Term.Steps(naming.scalarWrite(dest)(kernel(from(a, Render.trailingZeros(ExecMask, 64))))))

  inline def readLane(dest: ScalarRegisterIndex, a: InstructionSource, lane: InstructionSource)(
      inline index: (Term, Term) => Term
  )(inline kernel: Term => Term): Option[Term.Steps] =
    Some(Term.Steps(naming.scalarWrite(dest)(kernel(from(a, index(source(lane), lanes))))))

  inline def writeLane(dest: VectorRegisterIndex, a: InstructionSource, lane: InstructionSource)(
      inline index: (Term, Term) => Term
  )(inline kernel: Term => Term): Option[Term.Steps] = {
    val target = naming.vector(dest)
    Some(Term.Steps(
      Term.Step("selected", index(source(lane), lanes)),
      Term.Step(
        naming.name(target),
        Select(Render.equal(Variable("selected"), Lane), kernel(from(a, Variable("selected"))), target)
      )
    ))
  }

  inline def vectorUnary(bank: RegisterBank, dest: VectorRegisterIndex, a: InstructionSource)(
      inline kernel: Term => Term
  ): Option[Term.Steps] = Some(Term.Steps(lane(this.bank(bank, dest), kernel(source(a)))))

  inline def accumulatorMove(destinationBank: RegisterBank, dest: VectorRegisterIndex, src: VectorRegisterIndex)(
      inline kernel: Term => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(lane(bank(destinationBank, dest), kernel(bank(RegisterBank.Accumulator, src)))))

  inline def moveRelative(dest: VectorRegisterIndex, a: InstructionSource, field: RelativeField)(
      inline kernel: Term => Term
  ): Option[Term.Steps] = relativeSource(a, field).map(value => Term.Steps(lane(naming.vector(dest), kernel(value))))

  inline def moveRelativeDestination(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      sourceField: Option[RelativeField],
      field: RelativeField
  )(
      inline kernel: Term => Term
  ): Option[Term.Steps] =
    sourceField.fold(Option(source(a)))(relativeSource(a, _)).map(value =>
      Term.Steps(Term.Step("_", relativeWrite(dest, field, kernel(value))))
    )

  inline def swapRelative(dest: VectorRegisterIndex, src: VectorRegisterIndex)(inline kernel: Term => Term)
      : Option[Term.Steps] = {
    val (first, second) =
      (relativeRead(dest, RelativeField.Destination), relativeRead(src, RelativeField.Source))
    def within(index: Int, field: RelativeField): Term =
      Render.lessThan(relative(index, field), Literal(256L), signed = false)
    // gfx1036 writes neither register where either offset is beyond the last
    Some(Term.Steps(
      Term.Step("previous", first),
      Term.Step(
        "_",
        relativeWrite(
          dest,
          RelativeField.Destination,
          kernel(second),
          Some(within(src, RelativeField.Source))
        )
      ),
      Term.Step(
        "_",
        relativeWrite(
          src,
          RelativeField.Source,
          kernel(Variable("previous")),
          Some(within(dest, RelativeField.Destination))
        )
      )
    ))
  }

  inline def swap(dest: VectorRegisterIndex, a: InstructionSource)(inline kernel: Term => Term): Option[Term.Steps] =
    a match {
      case InstructionSource.VectorRegister(index) =>
        val (target, other) = (naming.vector(dest), naming.vector(index))
        Some(Term.Steps(
          Term.Step("previous", target),
          lane(target, kernel(other)),
          lane(other, kernel(Variable("previous")))
        ))
      case _ => None
    }

  inline def swapHalves(instruction: Instruction.VSwapHalves)(
      inline dest: (Term, Term) => Term,
      inline src: (Term, Term) => Term
  ): Option[Term.Steps] = {
    val (target, source) = (naming.vector(instruction.dest), naming.vector(instruction.src))
    if (instruction.dest == instruction.src) Some(Term.Steps(lane(target, src(dest(target, target), target))))
    else Some(Term.Steps(
      Term.Step("previous", target),
      lane(target, dest(target, source)),
      lane(source, src(source, Variable("previous")))
    ))
  }

  inline def modifiedUnary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: Term => Term): Option[Term.Steps] =
    Some(Term.Steps(lane(naming.vector(dest), kernel(modified32(a, modifiers, 0, width)))))

  inline def wideningUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Term => Term
  ): Option[Term.Steps] = Some(Term.Steps(lane(naming.vectorPair(dest), kernel(modified32(a, modifiers, 0, 32)))))

  inline def narrowingUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Term => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(lane(naming.vector(dest), kernel(modified(pair(a, PairRead.Floating), modifiers, 0, 64)))))

  inline def pairUnary(dest: VectorRegisterIndex, a: InstructionSource, read: PairRead, modifiers: SourceModifiers)(
      inline kernel: Term => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(lane(naming.vectorPair(dest), kernel(modified(pair(a, read), modifiers, 0, 64)))))

  inline def block(dest: VectorRegisterIndex, width: Int, sources: Vec[(InstructionSource, Int)])(
      inline kernel: (IndexedSeq[Term], IndexedSeq[Term]) => IndexedSeq[Term]
  ): Option[Term.Steps] = {
    val inputs = sources.flatMap {
      case (InstructionSource.VectorRegister(first), count) =>
        Vec.tabulate(count)(offset => naming.vector(first.offsetBy(offset.unsafe[Int >= 0])))
      case (other, _) => Vec(low32(other))
    }
    val targets = Vec.tabulate(width)(offset => naming.vector(dest.offsetBy(offset.unsafe[Int >= 0])))
    val writes  = kernel(inputs, targets).zip(targets).map((value, target) => lane(target, Render.truncate(value, 32)))
    Some(Term.Steps.parallel(naming, writes.map(_.name).toVec, writes.map(_.value).toVec))
  }

  inline def vectorBinary(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline kernel: (Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(lane(naming.vector(dest), kernel(low32(a), low32(b)))))

  inline def vectorFloat(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = {
    val target = naming.vector(dest)
    Some(Term.Steps(lane(
      target,
      kernel(modified32(a, modifiers, 0, width), modified32(b, modifiers, 1, width), target)
    )))
  }

  inline def vectorTernary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vector(dest),
      Render.truncate(
        kernel(
          modified32(a, modifiers, 0, width),
          modified32(b, modifiers, 1, width),
          modified32(c, modifiers, 2, width)
        ),
        32
      )
    )
  ))

  inline def vectorCarry(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val (x, y, carry) = (low32(a), low32(b), bit(naming.vcc))
    Some(Term.Steps(
      Term.Step("result", result(x, y, carry)),
      Term.Step("flag", flag(x, y, carry, Variable("result"))),
      lane(naming.vector(dest), Variable("result")),
      Term.Step(naming.name(naming.vcc), laneBits(naming.vcc, Variable("flag")))
    ))
  }

  inline def maskCarry(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      carryIn: Option[InstructionSource]
  )(inline result: (Term, Term, Term) => Term, inline flag: (Term, Term, Term, Term) => Term): Option[Term.Steps] = {
    val (x, y) = (low32(a), low32(b))
    val carry  = carryIn.fold(Literal(0L))(carries => bit(mask(carries)))
    Some(
      Term.Steps(
        Term.Step("result", result(x, y, carry)),
        Term.Step("flag", flag(x, y, carry, Variable("result"))),
        lane(naming.vector(dest), Variable("result"))
      ) ++ Term.Steps(maskDestination(carryDest).map(Term.Step(_, ballot(Variable("flag")))).toVec)
    )
  }

  private def placed(existing: Term, value: Term, instruction: Instruction.VBinarySubDword): Term =
    Render.placedPart(existing, value, instruction.destSelection, instruction.destUnused)

  private def selectedSources(instruction: Instruction.VBinarySubDword): (Term, Term) = (
    selected(source(instruction.srcA), instruction.srcASelection, instruction.srcASignExtend),
    selected(source(instruction.srcB), instruction.srcBSelection, instruction.srcBSignExtend)
  )

  inline def subDword(instruction: Instruction.VBinarySubDword)(inline kernel: (
      Term,
      Term
  ) => Term): Option[Term.Steps] = {
    val (x, y) = selectedSources(instruction)
    val target = naming.vector(instruction.dest)
    Some(Term.Steps(lane(target, placed(target, kernel(x, y), instruction))))
  }

  inline def subDwordFlagged(instruction: Instruction.VBinarySubDword)(
      inline result: (Term, Term, Term) => Term,
      inline flag: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val (a, b)        = selectedSources(instruction)
    val (x, y, carry) = (Render.truncate(a, 32), Render.truncate(b, 32), bit(naming.vcc))
    val target        = naming.vector(instruction.dest)
    Some(Term.Steps(
      Term.Step("result", result(x, y, carry)),
      Term.Step("flag", flag(x, y, carry, Variable("result"))),
      Term.Step(naming.name(naming.vcc), laneBits(naming.vcc, Variable("flag"))),
      lane(target, placed(target, Variable("result"), instruction))
    ))
  }

  inline def subDwordConditional(instruction: Instruction.VBinarySubDword)(
      inline result: (Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val (a, b) = selectedSources(instruction)
    val target = naming.vector(instruction.dest)
    Some(Term.Steps(
      lane(
        target,
        placed(target, result(Render.truncate(a, 32), Render.truncate(b, 32), bit(naming.vcc)), instruction)
      )
    ))
  }

  inline def packedBinary(instruction: Instruction.VPackedBinary)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] = {
    val (a, b)                                                  = (low32(instruction.srcA), low32(instruction.srcB))
    def half(value: Term, high: Boolean, negate: Boolean): Term = {
      val part = if (high) Render.shiftRightLogical(value, Literal(16L)) else Render.truncate(value, 16)
      if (negate) Render.xor(part, Literal(0x8000L)) else part
    }
    Some(Term.Steps(
      lane(
        naming.vector(instruction.dest),
        Render.or(
          kernel(
            half(a, instruction.srcAHighForLow, (instruction.negateLow & 1) != 0),
            half(b, instruction.srcBHighForLow, (instruction.negateLow & 2) != 0)
          ),
          Render.shiftLeft(
            kernel(
              half(a, instruction.srcAHighForHigh, (instruction.negateHigh & 1) != 0),
              half(b, instruction.srcBHighForHigh, (instruction.negateHigh & 2) != 0)
            ),
            Literal(16L)
          )
        )
      )
    ))
  }

  inline def select(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      condition: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vector(dest),
      kernel(modified(source(a), modifiers, 0, 32), modified(source(b), modifiers, 1, 32), bit(mask(condition)))
    )
  ))

  inline def maskCount(dest: VectorRegisterIndex, mask: InstructionSource, base: InstructionSource)(
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(lane(naming.vector(dest), kernel(source(mask), Lane, source(base)))))

  inline def dualFirst(half: DualVector)(inline kernel: (Term, Term, Term, Term) => Term): Option[Term.Steps] =
    Some(Term.Steps(Term.Step(
      "first",
      kernel(dual(half, half.left), dual(half, half.right), dual(half, half.addend), dualCondition(half))
    )))

  inline def dualSecond(half: DualVector, other: DualVector)(
      inline kernel: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = Some(Term.Steps(
    Term.Step(
      "second",
      kernel(dual(half, half.left), dual(half, half.right), dual(half, half.addend), dualCondition(half))
    ),
    lane(dualDestination(other), Variable("first")),
    lane(dualDestination(half), Variable("second"))
  ))

  private def dual(half: DualVector, value: InstructionSource): Term =
    if (half.operation.wide) pair(value, PairRead.Floating) else source(value)

  private def dualCondition(half: DualVector): Term = bit(half.condition.fold(naming.vcc)(mask(_)))

  private def dualDestination(half: DualVector): Term =
    if (half.operation.wide) naming.vectorPair(half.dest) else naming.vector(half.dest)

  inline def accumulating(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (Term, Term, Term, Term) => Term): Option[Term.Steps] = {
    val target = naming.vector(dest)
    Some(Term.Steps(lane(target, kernel(low32(a), low32(b), low32(c), target))))
  }

  inline def packedSingle(instruction: Instruction.VPackedF32)(inline kernel: (Term, Term, Term) => Term)
      : Option[Term.Steps] = {
    val (a, b, c) = (
      pair(instruction.srcA, PairRead.Packed),
      pair(instruction.srcB, PairRead.Packed),
      pair(instruction.srcC, PairRead.Packed)
    )
    val low = kernel(
      OperandSelection.packedOperand(Render, a, instruction.lowSelection, instruction.negateLow, index = 0, width = 32),
      OperandSelection.packedOperand(Render, b, instruction.lowSelection, instruction.negateLow, index = 1, width = 32),
      OperandSelection.packedOperand(Render, c, instruction.lowSelection, instruction.negateLow, index = 2, width = 32)
    )
    val high = kernel(
      OperandSelection.packedOperand(
        Render,
        a,
        instruction.highSelection,
        instruction.negateHigh,
        index = 0,
        width = 32
      ),
      OperandSelection.packedOperand(
        Render,
        b,
        instruction.highSelection,
        instruction.negateHigh,
        index = 1,
        width = 32
      ),
      OperandSelection.packedOperand(
        Render,
        c,
        instruction.highSelection,
        instruction.negateHigh,
        index = 2,
        width = 32
      )
    )
    Some(Term.Steps(
      lane(
        naming.vectorPair(instruction.dest),
        Render.or(Render.truncate(low, 32), Render.shiftLeft(Render.truncate(high, 32), Literal(32L)))
      )
    ))
  }

  inline def packedHalf(instruction: Instruction.VPackedTernary)(inline kernel: (Term, Term, Term) => Term)
      : Option[Term.Steps] = {
    val (a, b, c) = (low32(instruction.srcA), low32(instruction.srcB), low32(instruction.srcC))
    Some(Term.Steps(
      lane(
        naming.vector(instruction.dest),
        Render.or(
          kernel(
            OperandSelection.packedOperand(
              Render,
              a,
              instruction.lowSelection,
              instruction.negateLow,
              index = 0,
              width = 16
            ),
            OperandSelection.packedOperand(
              Render,
              b,
              instruction.lowSelection,
              instruction.negateLow,
              index = 1,
              width = 16
            ),
            OperandSelection.packedOperand(
              Render,
              c,
              instruction.lowSelection,
              instruction.negateLow,
              index = 2,
              width = 16
            )
          ),
          Render.shiftLeft(
            kernel(
              OperandSelection.packedOperand(
                Render,
                a,
                instruction.highSelection,
                instruction.negateHigh,
                index = 0,
                width = 16
              ),
              OperandSelection.packedOperand(
                Render,
                b,
                instruction.highSelection,
                instruction.negateHigh,
                index = 1,
                width = 16
              ),
              OperandSelection.packedOperand(
                Render,
                c,
                instruction.highSelection,
                instruction.negateHigh,
                index = 2,
                width = 16
              )
            ),
            Literal(16L)
          )
        )
      )
    ))
  }

  private def upper(term: Term, high: Boolean): Term = if (high) Render.shiftRightLogical(term, Literal(16L)) else term

  inline def halves(instruction: Instruction.VPackB32F16)(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    Some(Term.Steps(
      lane(
        naming.vector(instruction.dest),
        kernel(
          modified(upper(source(instruction.srcA), (instruction.opSelection & 1) != 0), instruction.modifiers, 0, 16),
          modified(upper(source(instruction.srcB), (instruction.opSelection & 2) != 0), instruction.modifiers, 1, 16)
        )
      )
    ))

  inline def pairBinary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vectorPair(dest),
      kernel(modified(pair(a, read), modifiers, 0, 64), modified(pair(b, read), modifiers, 1, 64))
    )
  ))

  inline def pairDword(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    Some(Term.Steps(lane(naming.vectorPair(dest), kernel(modified(pair(a, read), modifiers, 0, 64), source(b)))))

  inline def pairTernary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vectorPair(dest),
      kernel(
        modified(pair(a, PairRead.Floating), modifiers, 0, 64),
        modified(pair(b, PairRead.Floating), modifiers, 1, 64),
        modified(pair(c, PairRead.Floating), modifiers, 2, 64)
      )
    )
  ))

  inline def pairShiftAdd(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] =
    Some(Term.Steps(lane(
      naming.vectorPair(dest),
      kernel(pair(a, PairRead.Integer), source(b), pair(c, PairRead.Integer))
    )))

  inline def windowed(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource, c: InstructionSource)(
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] =
    Some(Term.Steps(lane(naming.vector(dest), kernel(pair(a, PairRead.Integer), low32(b), low32(c)))))

  inline def permuteLanes(instruction: Instruction.VPermuteLanes)(inline origin: (Term, Term, Term) => Term)(
      inline kernel: Term => Term
  ): Option[Term.Steps] = instruction.src match {
    case InstructionSource.VectorRegister(src) =>
      val target = naming.vector(instruction.dest)
      Some(Term.Steps(
        Term.Step("from", origin(Lane, low32(instruction.low), low32(instruction.high))),
        Term.Step("readable", readable(Variable("from"), instruction.fetchInactive)),
        lane(
          target,
          Select(
            Variable("readable"),
            kernel(read(naming.vector(src), Variable("from"))),
            if (instruction.boundControl) Literal(0L) else target
          )
        )
      ))
    case _ => None
  }

  inline def swapLanes(instruction: Instruction.VPermlaneSwap)(inline kernel: Term => Term): Option[Term.Steps] = {
    val (target, source) = (naming.vector(instruction.dest), naming.vector(instruction.src))
    val lower            = Render.equal(Render.and(Lane, Literal(instruction.rows.toLong)), Literal(0L))
    def taken(register: Term, other: Term): Term =
      Select(
        Variable("readable"),
        kernel(read(other, Variable("from"))),
        if (instruction.boundControl) Literal(0L) else register
      )
    Some(Term.Steps(
      Term.Step("from", Render.xor(Lane, Literal(instruction.rows.toLong))),
      Term.Step("readable", readable(Variable("from"), instruction.fetchInactive)),
      lane(source, Select(lower, taken(source, target), source)),
      lane(target, Select(lower, target, taken(target, source)))
    ))
  }

  inline def multiplyAdd64(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline result: (Term, Term, Term, Term) => Term, inline flag: (Term, Term, Term, Term, Term) => Term)
      : Option[Term.Steps] = {
    val (x, y, z) = (source(a), source(b), pair(c, PairRead.Integer))
    Some(
      Term.Steps(
        Term.Step("result", result(x, y, z, Literal(0L))),
        Term.Step("flag", flag(x, y, z, Literal(0L), Variable("result"))),
        lane(naming.vectorPair(dest), Variable("result"))
      ) ++ Term.Steps(maskDestination(carryDest).map(Term.Step(_, ballot(Variable("flag")))).toVec)
    )
  }

  inline def scaled(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Term, Term, Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vector(dest),
      Render.truncate(
        result(
          modified32(a, modifiers, 0, 32),
          modified32(b, modifiers, 1, 32),
          modified32(c, modifiers, 2, 32),
          bit(naming.vcc)
        ),
        32
      )
    )
  ))

  inline def scaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Term, Term, Term, Term) => Term, inline flag: (Term, Term, Term, Term, Term) => Term)
      : Option[Term.Steps] = {
    val (x, y, z) = (modified32(a, modifiers, 0, 32), modified32(b, modifiers, 1, 32), modified32(c, modifiers, 2, 32))
    val scale     = bit(naming.vcc)
    Some(Term.Steps(
      Term.Step("result", result(x, y, z, scale)),
      Term.Step("flag", flag(x, y, z, scale, Variable("result"))),
      lane(naming.vector(dest), Render.truncate(Variable("result"), 32)),
      Term.Step(naming.name(naming.scalarMask(conditionDest)), ballot(Variable("flag")))
    ))
  }

  inline def pairScaled(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Term, Term, Term, Term) => Term): Option[Term.Steps] = Some(Term.Steps(
    lane(
      naming.vectorPair(dest),
      result(
        modified(pair(a, PairRead.Floating), modifiers, 0, 64),
        modified(pair(b, PairRead.Floating), modifiers, 1, 64),
        modified(pair(c, PairRead.Floating), modifiers, 2, 64),
        bit(naming.vcc)
      )
    )
  ))

  inline def pairScaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Term, Term, Term, Term) => Term, inline flag: (Term, Term, Term, Term, Term) => Term)
      : Option[Term.Steps] = {
    val (x, y, z) = (
      modified(pair(a, PairRead.Floating), modifiers, 0, 64),
      modified(pair(b, PairRead.Floating), modifiers, 1, 64),
      modified(pair(c, PairRead.Floating), modifiers, 2, 64)
    )
    val scale = bit(naming.vcc)
    Some(Term.Steps(
      Term.Step("result", result(x, y, z, scale)),
      Term.Step("flag", flag(x, y, z, scale, Variable("result"))),
      lane(naming.vectorPair(dest), Variable("result")),
      Term.Step(naming.name(naming.scalarMask(conditionDest)), ballot(Variable("flag")))
    ))
  }

  /** A comparison's lane mask into `dest` and, when `updateExec`, EXEC. */
  private def compared(dest: ScalarRegisterIndex, updateExec: Boolean, value: Term): Option[Term.Steps] =
    Some(
      Term.Steps(Term.Step("mask", ballot(value))) ++
        Term.Steps(Option.when(updateExec)(Term.Step(naming.name(naming.exec), Variable("mask"))).toVec) ++
        Term.Steps(maskDestination(dest).map(Term.Step(_, Variable("mask"))).toVec)
    )

  private def operand(value: InstructionSource, selection: Option[SubDwordSources], first: Boolean): Term =
    selection match {
      case Some(sources) =>
        if (first) selected(source(value), sources.selectionA, sources.signExtendA)
        else selected(source(value), sources.selectionB, sources.signExtendB)
      case None => source(value)
    }

  inline def compare(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    compared(dest, updateExec, kernel(operand(a, selection, first = true), operand(b, selection, first = false)))

  inline def compareModified(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean,
      width: Int in (16, 32, 64)
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] = compared(
    dest,
    updateExec,
    kernel(
      modified(operand(a, selection, first = true), modifiers, 0, width),
      modified(operand(b, selection, first = false), modifiers, 1, width)
    )
  )

  inline def compareClass(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    compared(dest, updateExec, kernel(modified(operand(a, selection, first = true), modifiers, 0, 32), source(b)))

  inline def comparePair(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] = compared(
    dest,
    updateExec,
    kernel(modified(pair(a, read), modifiers, 0, 64), modified(pair(b, read), modifiers, 1, 64))
  )

  inline def comparePairClass(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    compared(dest, updateExec, kernel(modified(pair(a, PairRead.Floating), modifiers, 0, 64), source(b)))

  /** The `width`-bit result `kernel` rewrites in `dest`; a binary16 result keeps the register's high half. */
  private def rewritten(dest: VectorRegisterIndex, width: Int in (16, 32, 64))(kernel: Term => Term): Term.Step =
    width.raw match {
      case 64 =>
        val target = naming.vectorPair(dest)
        lane(target, kernel(target))
      case 32 =>
        val target = naming.vector(dest)
        lane(target, kernel(target))
      case _ =>
        val target = naming.vector(dest)
        lane(target, Render.or(Render.and(target, Literal(~0xffffL)), Render.truncate(kernel(target), 16)))
    }

  // whether OMOD applies depends on the MODE register, which definitions do not model
  inline def outputModified(instruction: Instruction.VOutputModified)(inline kernel: Term => Term): Option[Term.Steps] =
    None

  inline def clampResult(instruction: Instruction.VClampResult)(inline kernel: Term => Term): Option[Term.Steps] =
    nested(instruction.operation, guard).map(operation =>
      operation :+ rewritten(instruction.dest, instruction.width)(value => kernel(value))
    )

  inline def halfResult(instruction: Instruction.VHalfResult)(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    preserving(instruction.dest, instruction.operation)(kernel)

  inline def placedResult(instruction: Instruction.VSubDwordResult)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] = instruction.relative match {
    case None        => preserving(instruction.dest, instruction.operation)(kernel)
    case Some(field) =>
      val target = relativeRead(instruction.dest, field)
      nested(instruction.operation, guard).map(nested =>
        Term.Steps(Term.Step("preserved", target)) ++ nested :+
          Term.Step("_", relativeWrite(instruction.dest, field, kernel(Variable("preserved"), target)))
      )
  }

  /** The nested operation's assignments, then `dest` as `kernel` merges what it held before with what it holds after.
    */
  private inline def preserving(dest: VectorRegisterIndex, operation: Instruction)(
      inline kernel: (Term, Term) => Term
  ): Option[Term.Steps] = {
    val target = naming.vector(dest)
    nested(operation, guard).map(nested =>
      Term.Steps(Term.Step("preserved", target)) ++ nested :+ lane(target, kernel(Variable("preserved"), target))
    )
  }

  /** Whether lane `from` exists and EXEC, or fetching inactive lanes, lets it be read. */
  private def readable(from: Term, fetchInactive: Boolean): Term = {
    val exists =
      Render.and(Render.lessOrEqual(Literal(0L), from, signed = true), Render.lessThan(from, lanes, signed = true))
    if (fetchInactive) exists
    else Render.and(exists, Render.notEqual(Render.and(Render.shiftRightLogical(exec, from), Literal(1L)), Literal(0L)))
  }

  /** Whether the DPP16 row and bank masks enable this lane. */
  private def rowAndBank(rowMask: Int, bankMask: Int): Option[Term] =
    Option.when(rowMask != 15 || bankMask != 15)(
      Render.and(
        Render.notEqual(
          Render.and(
            Render.shiftRightLogical(Literal(rowMask.toLong), Render.shiftRightLogical(Lane, Literal(4L))),
            Literal(1L)
          ),
          Literal(0L)
        ),
        Render.notEqual(
          Render.and(
            Render.shiftRightLogical(
              Literal(bankMask.toLong),
              Render.and(Render.shiftRightLogical(Lane, Literal(2L)), Literal(3L))
            ),
            Literal(1L)
          ),
          Literal(0L)
        )
      )
    )

  /** The register a DPP instruction routes, as lane `from` holds it. */
  private def routedRead(instruction: Instruction.VDPP, from: Term): Term =
    instruction.relative.fold(read(bank(instruction.bank, instruction.src), from))(field =>
      Term.context(
        Wave,
        "vgprAt",
        Vec.empty,
        Vec((relative(instruction.src, field), Some(dword)), (from, Some(dword))),
        Some(dword)
      )
    )

  inline def dpp16(instruction: Instruction.VDPP, rowMask: Int, bankMask: Int, boundControl: Boolean)(
      inline origin: Term => Term
  )(inline kernel: Term => Term): Option[Term.Steps] = {
    val conditions = rowAndBank(rowMask, bankMask).toVec ++ Option.unless(boundControl)(Variable("readable")).toVec
    val routing    = Term.Steps(
      Term.Step("from", origin(Lane)),
      Term.Step("readable", readable(Variable("from"), instruction.fetchInactive)),
      Term.Step(
        Routed,
        Select(
          Variable("readable"),
          if (instruction.wide)
            Render.or(
              kernel(routedRead(instruction, Variable("from"))),
              Render.shiftLeft(
                kernel(read(naming.vector(instruction.src.offsetBy(1)), Variable("from"))),
                Literal(32L)
              )
            )
          else kernel(routedRead(instruction, Variable("from"))),
          Literal(0L)
        )
      )
    )
    nested(instruction.operation, conditions.reduceOption(Render.and(_, _))).map(routing ++ _)
  }

  inline def dpp8(instruction: Instruction.VDPP)(inline origin: Term => Term)(inline kernel: Term => Term)
      : Option[Term.Steps] = {
    val routing = Term.Steps(
      Term.Step("from", origin(Lane)),
      Term.Step(
        Routed,
        Select(
          readable(Variable("from"), instruction.fetchInactive),
          kernel(routedRead(instruction, Variable("from"))),
          Literal(0L)
        )
      )
    )
    nested(instruction.operation, guard).map(routing ++ _)
  }

  inline def permute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      registers: RegisterBank
  )(
      inline destination: (Term, Term, Term) => Term
  )(inline kernel: Term => Term): Option[Term.Steps] = {
    val target = bank(registers, dest)
    Some(Term.Steps(
      lane(
        target,
        Term.context(
          Wave,
          "permute",
          Vec.empty,
          Vec(
            (kernel(bank(registers, src)), Some(dword)),
            (destination(naming.vector(address), Lane, lanes), Some(dword)),
            (target, Some(dword))
          ),
          Some(dword)
        )
      )
    ))
  }

  inline def backwardPermute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      registers: RegisterBank,
      fetchInactive: Boolean
  )(inline origin: (Term, Term) => Term)(inline kernel: Term => Term): Option[Term.Steps] = Some(Term.Steps(
    Term.Step("from", origin(naming.vector(address), Lane)),
    lane(
      bank(registers, dest),
      Select(
        Render.lessThan(Variable("from"), lanes, signed = true),
        if (fetchInactive) kernel(read(bank(registers, src), Variable("from")))
        else Select(
          Render.notEqual(Render.and(Render.shiftRightLogical(exec, Variable("from")), Literal(1L)), Literal(0L)),
          kernel(read(bank(registers, src), Variable("from"))),
          Literal(0L)
        ),
        Literal(0L)
      )
    )
  ))

  inline def swizzle(dest: VectorRegisterIndex, src: VectorRegisterIndex, registers: RegisterBank)(
      inline origin: Term => Term
  )(
      inline kernel: Term => Term
  ): Option[Term.Steps] = Some(Term.Steps(
    Term.Step("from", origin(Lane)),
    lane(
      bank(registers, dest),
      Select(
        Render.and(
          Render.lessOrEqual(Literal(0L), Variable("from"), signed = true),
          Render.lessThan(Variable("from"), lanes, signed = true)
        ),
        kernel(read(naming.vector(src), Variable("from"))),
        Literal(0L)
      )
    )
  ))

  private def memoryOperand(
      register: VectorRegisterIndex,
      bytes: Int in (4, 8),
      registers: RegisterBank
  ): Term =
    if (bytes == 8) pairOf(registers, register) else bank(registers, register)

  /** A lane's writes of a returned value of `bytes` to the registers from `dest`. */
  private def returned(dest: VectorRegisterIndex, bytes: Int in (4, 8), registers: RegisterBank, value: Term)
      : Term.Steps =
    if (bytes == 4 || registers == RegisterBank.Vector)
      Term.Steps(lane(memoryOperand(dest, bytes, registers), value))
    else
      Term.Steps(
        lane(bank(registers, dest), Render.truncate(value, 32)),
        lane(bank(registers, dest.offsetBy(1)), Render.shiftRightLogical(value, Literal(32L)))
      )

  /** The memory and address a lane's access lands on, as execution locates it; buffer and image accesses are not
    * rendered.
    */
  private def located(address: MemoryAddress): Option[(Space, Term)] = address match {
    case MemoryAddress.Global(register, Some(base), offset) =>
      Some((
        Space.Global,
        Render.offsetAddress(naming.scalarPair(base), naming.vector(register), Literal(offset.toLong))
      ))
    case MemoryAddress.Global(register, None, offset) =>
      Some((Space.Global, Render.displaced(naming.vectorPair(register), Literal(offset.toLong))))
    case MemoryAddress.Flat(register, offset, None) =>
      Some((Space.Flat, Render.displaced(naming.vectorPair(register), Literal(offset.toLong))))
    case MemoryAddress.Flat(register, offset, Some(base)) =>
      Some((
        Space.Flat,
        Render.offsetAddress(naming.scalarPair(base), naming.vector(register), Literal(offset.toLong))
      ))
    case MemoryAddress.Scratch(register, base, offset) =>
      Some((
        Space.Private,
        Render.dwordAddress(
          register.fold(Literal(0L))(naming.vector(_)),
          base.fold(Literal(0L))(naming.scalar(_)),
          Literal(offset.toLong)
        )
      ))
    case MemoryAddress.Local(register, offset) =>
      Some((
        Space.Local,
        Render.ldsOffset(Render.dwordAddress(naming.vector(register), Literal(0L), Literal(offset.toLong)))
      ))
    case MemoryAddress.LaneLocal(offset) =>
      Some((Space.Local, Render.laneLocalAddress(naming.m0, offset.toLong, Lane)))
    case MemoryAddress.LaneGlobal(base, offset) =>
      Some((
        Space.Global,
        Render.laneGlobalAddress(base.fold(Literal(0L))(naming.scalarPair(_)), offset.toLong, Lane)
      ))
    case _: (MemoryAddress.Buffer | MemoryAddress.Texel) => None
  }

  /** Element `element` of a transposing load as this lane takes it: the lane its layout routes it from reads its own
    * address, and the element's bits lie from the element its lane bits name.
    */
  private def transposed(address: MemoryAddress, layout: TransposeLayout, element: Int): Option[Term] = {
    val origin = Render.add(
      Render.add(
        Render.and(Lane, Literal(~layout.groupMask.toLong & 0xffffffffL)),
        Literal(layout.offset(element).toLong)
      ),
      Render.multiply(
        Render.and(Render.shiftRightLogical(Lane, Literal(layout.laneShift.toLong)), Literal(layout.laneMask.toLong)),
        Literal(layout.laneScale.toLong)
      )
    )
    val bit   = Render.multiply(Render.and(Lane, Literal(layout.elementMask.toLong)), Literal(layout.bits.toLong))
    val shift = Render.and(bit, Literal(7L))
    def remote(value: VectorRegisterIndex): Term = read(naming.vector(value), origin)
    val located: Option[(Space, Term)]           = address match {
      case MemoryAddress.Local(register, offset) =>
        Some((
          Space.Local,
          Render.dwordAddress(remote(register), Literal(0L), Literal(offset.toLong))
        ))
      case MemoryAddress.Global(register, Some(base), offset) =>
        Some((
          Space.Global,
          Render.offsetAddress(naming.scalarPair(base), remote(register), Literal(offset.toLong))
        ))
      case MemoryAddress.Global(register, None, offset) =>
        val pair = Render.or(remote(register), Render.shiftLeft(remote(register.offsetBy(1)), Literal(32L)))
        Some((Space.Global, Render.displaced(pair, Literal(offset.toLong))))
      case _ => None
    }
    located.map { (space, base) =>
      def bytes(count: Int): Term = {
        val at = Render.add(base, Render.shiftRightLogical(bit, Literal(3L)))
        Render.loaded(
          load(space, if (space == Space.Local) Render.ldsOffset(at) else at, count),
          (count * 8).unsafe[Int in 1 ~ 64],
          signed = false
        )
      }
      val mask = Literal((1L << layout.bits) - 1L)
      layout.bits.raw match {
        case 16 | 8 => bytes(layout.bits / 8)
        case 4      => Render.and(Render.shiftRightLogical(bytes(1), shift), mask)
        case _      =>
          Select(
            Render.lessOrEqual(Render.add(shift, Literal(layout.bits.toLong)), Literal(8L), signed = false),
            Render.and(Render.shiftRightLogical(bytes(1), shift), mask),
            Render.and(Render.shiftRightLogical(bytes(2), shift), mask)
          )
      }
    }
  }

  private def at(address: Term, offset: Int): Term =
    if (offset == 0) address else Render.add(address, Literal(offset.toLong))

  private def registers(bank: RegisterBank, first: VectorRegisterIndex, offset: Int): Term =
    this.bank(bank, first.offsetBy(offset.unsafe[Int >= 0]))

  private def pairOf(bank: RegisterBank, first: VectorRegisterIndex): Term = bank match {
    case RegisterBank.Vector      => naming.vectorPair(first)
    case RegisterBank.Accumulator =>
      Render.or(registers(bank, first, 0), Render.shiftLeft(registers(bank, first, 1), Literal(32L)))
  }

  /** The dwords from `first` that `bytes` from `address` fill, as a lane loads them. */
  private def dwords(space: Space, address: Term, bank: RegisterBank, first: VectorRegisterIndex, bytes: Int)
      : Term.Steps =
    Term.Steps(Vec.tabulate((bytes + 3) / 4)(index =>
      lane(registers(bank, first, index), load(space, at(address, index * 4), math.min(4, bytes - index * 4)))
    ))

  /** The memory accesses outside the staged semantics, rendered as a lane makes them. */
  def access(instruction: Instruction): Option[Term.Steps] = instruction match {
    case Instruction.Load(dest, address, bytes, signed, placement, bank, _) =>
      located(address).map { (space, address) =>
        if (bytes > 8) dwords(space, address, bank, dest, bytes)
        else {
          val value = Render.loaded(
            load(space, address, bytes),
            (bytes * 8).unsafe[Int in 1 ~ 64],
            signed
          )
          def target = registers(bank, dest, 0)
          placement match {
            case LoadPlacement.Full if bytes > 4 && bank == RegisterBank.Vector =>
              Term.Steps(lane(naming.vectorPair(dest), value))
            case LoadPlacement.Full if bytes > 4 =>
              Term.Steps(
                lane(target, Render.truncate(value, 32)),
                lane(registers(bank, dest, 1), Render.shiftRightLogical(value, Literal(32L)))
              )
            case LoadPlacement.Full => Term.Steps(lane(target, Render.truncate(value, 32)))
            case LoadPlacement.Low  =>
              Term.Steps(lane(target, Render.mergeHalf(target, value, high = false)))
            case LoadPlacement.High =>
              Term.Steps(lane(target, Render.mergeHalf(target, value, high = true)))
            case LoadPlacement.LowZeroingHigh =>
              Term.Steps(lane(target, Render.zeroedHalf(value, high = false)))
            case LoadPlacement.HighZeroingLow =>
              Term.Steps(lane(target, Render.zeroedHalf(value, high = true)))
          }
        }
      }
    case Instruction.Store(src, address, bytes, high, bank) =>
      located(address).map { (space, address) =>
        if (bytes > 8)
          Term.Steps(Vec.tabulate(bytes / 4)(index =>
            Term.Step("_", store(space, at(address, index * 4), registers(bank, src, index), 4))
          ))
        else {
          val value =
            if (high) Render.high16(registers(bank, src, 0))
            else if (bytes <= 4) registers(bank, src, 0)
            else pairOf(bank, src)
          Term.Steps(Term.Step(
            "_",
            store(
              space,
              address,
              Render.truncate(value, (bytes * 8).unsafe[Int in 1 ~ 64]),
              bytes
            )
          ))
        }
      }
    case Instruction.LoadPair(dest, address, bytes, first, second, bank) =>
      val base = Render.dwordAddress(naming.vector(address), Literal(0L), Literal(first.toLong))
      val next = Render.dwordAddress(naming.vector(address), Literal(0L), Literal(second.toLong))
      Some(
        dwords(Space.Local, Render.ldsOffset(base), bank, dest, bytes) ++
          dwords(
            Space.Local,
            Render.ldsOffset(next),
            bank,
            dest.offsetBy(bytes.widen[Int >= 0] / 4),
            bytes
          )
      )
    case Instruction.LoadLocal(address, bytes, _, offset) =>
      located(address).filter(_._1 != Space.Local).map { (space, from) =>
        val target = Render.ldsOffset(Render.add(
          Render.add(naming.m0, Literal(offset.toLong)),
          Render.multiply(Lane, Literal(math.max(4, bytes).toLong))
        ))
        if (bytes > 8)
          Term.Steps(Vec.tabulate(bytes / 4)(index =>
            Term.Step("_", store(Space.Local, at(target, index * 4), load(space, at(from, index * 4), 4), 4))
          ))
        else Term.Steps(Term.Step("_", store(Space.Local, target, load(space, from, bytes), bytes)))
      }
    case Instruction.LoadTranspose(dest, address, layout) =>
      val dwords = (layout.bits * layout.elements + 31) / 32
      val values = Vec.tabulate(layout.elements)(element => transposed(address, layout, element))
      Option.when(values.forall(_.nonEmpty) && (!layout.wave32 || naming.waveSize == 32))(
        Term.Steps(Vec.tabulate(dwords) { dword =>
          val parts = values.flatten.zipWithIndex.collect {
            case (value, element)
                if layout.bits * element < 32 * dword + 32 && layout.bits * (element + 1) > 32 * dword =>
              val offset = layout.bits * element - 32 * dword
              if (offset >= 0) Render.shiftLeft(value, Literal(offset.toLong))
              else Render.shiftRightLogical(value, Literal((-offset).toLong))
          }
          lane(
            naming.vector(dest.offsetBy(dword.unsafe[Int >= 0])),
            Render.truncate(parts.reduce((left, right) => Render.or(left, right)), 32)
          )
        })
      )
    case Instruction.StorePair(address, srcA, srcB, bytes, first, second, bank) =>
      def stores(displacement: Int, src: VectorRegisterIndex): Term.Steps = {
        val base =
          Render.ldsOffset(Render.dwordAddress(naming.vector(address), Literal(0L), Literal(displacement.toLong)))
        Term.Steps(Vec.tabulate(bytes / 4)(index =>
          Term.Step("_", store(Space.Local, at(base, index * 4), registers(bank, src, index), 4))
        ))
      }
      Some(stores(first, srcA) ++ stores(second, srcB))
    case Instruction.SLoad(dest, count, base, offset, scalarOffset, elementBytes, signed) =>
      val unaligned = Render.scalarAddress(
        naming.scalarPair(base),
        Literal(offset),
        scalarOffset.fold(Literal(0L))(naming.scalar(_))
      )
      val address = if (elementBytes == 4) Render.scalarDword(unaligned) else unaligned
      Some(Term.Steps(Vec.tabulate(count)(index =>
        naming.scalarWrite(dest.offsetBy(index.unsafe[Int >= 0]))(
          if (signed)
            Render.truncate(
              Render.signExtend(
                load(Space.Global, at(address, index * elementBytes), elementBytes),
                (elementBytes * 8).unsafe[Int in 1 ~ 64]
              ),
              32
            )
          else load(Space.Global, at(address, index * elementBytes), elementBytes)
        )
      )))
    case _ => None
  }

  /** A read-modify-write of `bytes` at a lane's address: the old value, the value `update` stores over it, and the old
    * value into `dest` when the instruction returns it.
    */
  private def exchanged(
      address: MemoryAddress,
      dest: VectorRegisterIndex,
      bytes: Int in (4, 8),
      returnValue: Boolean,
      registers: RegisterBank,
      update: Term => Term
  ): Option[Term.Steps] = located(address).map { (space, unaligned) =>
    val at = aligned(space, unaligned, bytes)
    Term.Steps(
      Term.Step("old", load(space, at, bytes)),
      Term.Step("_", store(space, at, update(Variable("old")), bytes))
    ) ++
      (if (returnValue) returned(dest, bytes, registers, Variable("old")) else Term.Steps.empty)
  }.orElse(
    naming.isa.flatMap(ResourceRender(naming, _).exchanged(address, dest, bytes, returnValue, update, registers))
  )

  /** An atomic's address in `space`, which [[AtomicAlignment]] requires aligned to its `bytes`. */
  private def aligned(space: Space, at: Term, bytes: Int): Term =
    if (space == Space.Local) Render.primitive(at)((at: UInt[32]) => AtomicAlignment.offset(at, bytes))
    else Render.primitive(at)((at: UInt[64]) => AtomicAlignment.address(at, bytes))

  inline def atomic(instruction: Instruction.Atomic): AtomicTerms = AtomicTerms(instruction)

  class AtomicTerms(instruction: Instruction.Atomic) extends Sink.Binary[Term, Option[Term.Steps]] {
    inline def apply(inline kernel: (Term, Term) => Term): Option[Term.Steps] = exchanged(
      instruction.address,
      instruction.dest,
      instruction.bytes,
      instruction.returnValue,
      instruction.bank,
      old => kernel(old, memoryOperand(instruction.src, instruction.bytes, instruction.bank))
    )
  }

  inline def localSource2(instruction: Instruction.LocalSource2): Source2Terms = Source2Terms(instruction)

  class Source2Terms(instruction: Instruction.LocalSource2) extends Sink.Binary[Term, Option[Term.Steps]] {
    inline def apply(inline kernel: (Term, Term) => Term): Option[Term.Steps] = {
      val address                  = naming.vector(instruction.address)
      val bytes                    = instruction.bytes
      def local(value: Term): Term =
        Render.ldsOffset(Render.dwordAddress(value, Literal(0L), Literal(0L)))
      val at   = aligned(Space.Local, local(address), bytes)
      val from = local(Render.source2Address(address, instruction.displacement))
      Some(Term.Steps(
        Term.Step("other", load(Space.Local, from, bytes)),
        Term.Step("old", load(Space.Local, at, bytes)),
        Term.Step("_", store(Space.Local, at, kernel(Variable("old"), Variable("other")), bytes))
      ))
    }
  }

  inline def compareSwap(instruction: Instruction.CompareSwap)(inline kernel: (Term, Term, Term) => Term)
      : Option[Term.Steps] = exchanged(
    instruction.address,
    instruction.dest,
    instruction.bytes,
    instruction.returnValue,
    instruction.bank,
    old =>
      kernel(
        old,
        memoryOperand(instruction.comparand, instruction.bytes, instruction.bank),
        memoryOperand(instruction.data, instruction.bytes, instruction.bank)
      )
  )

  inline def scalarAtomic(instruction: Instruction.SAtomic): ScalarAtomicTerms = ScalarAtomicTerms(instruction)

  class ScalarAtomicTerms(instruction: Instruction.SAtomic) extends Sink.Binary[Term, Option[Term.Steps]] {
    inline def apply(inline kernel: (Term, Term) => Term): Option[Term.Steps] = naming.isa.flatMap(
      ResourceRender(naming, _).scalarExchanged(
        instruction.address,
        instruction.data,
        instruction.bytes,
        instruction.returnValue,
        old => kernel(old, scalarOperand(instruction.data, 0, instruction.bytes))
      )
    )
  }

  inline def scalarCompareSwap(instruction: Instruction.SCompareSwap)(inline kernel: (Term, Term, Term) => Term)
      : Option[Term.Steps] = naming.isa.flatMap(
    ResourceRender(naming, _).scalarExchanged(
      instruction.address,
      instruction.data,
      instruction.bytes,
      instruction.returnValue,
      old =>
        kernel(
          old,
          scalarOperand(instruction.data, instruction.bytes / 4, instruction.bytes),
          scalarOperand(instruction.data, 0, instruction.bytes)
        )
    )
  )

  /** The dword or pair of the SGPRs from `first` plus `words`. */
  private def scalarOperand(first: ScalarRegisterIndex, words: Int, bytes: Int in (4, 8)): Term = {
    val register = first.offsetBy(words.unsafe[Int >= 0])
    if (bytes == 8) naming.scalarPair(register) else naming.scalar(register)
  }

  /** Register `offset` of a matrix operand's registers, in the accumulator bank when `banked`, or a constant. */
  private def matrixRegister(value: InstructionSource, banked: Boolean, offset: Int): Term = value match {
    case InstructionSource.VectorRegister(index) =>
      val register = index.offsetBy(offset.unsafe[Int >= 0])
      if (banked) naming.accumulator(register) else naming.vector(register)
    case other => source(other)
  }

  /** The lane, as a term over this lane, that `function` of this lane's index computes; the matrix layouts move the
    * bits of a lane index to fixed positions over a constant.
    */
  private def matrixLane(function: Int => Int): Term = {
    val waves = naming.waveSize
    val width = Integer.numberOfTrailingZeros(waves)
    val base  = function(0)
    val moved = Vec.tabulate(width)(bit => function(1 << bit) ^ base)
    require(
      (0 until waves).forall(lane =>
        function(lane) == (0 until width).foldLeft(base)((value, bit) =>
          if (((lane >> bit) & 1) != 0) value ^ moved(bit) else value
        )
      ),
      "a matrix layout moves lane bits non-linearly"
    )
    @annotation.tailrec
    def fields(bit: Int, found: Vec[Term]): Vec[Term] =
      if (bit >= width) found
      else if (moved(bit) == 0) fields(bit + 1, found)
      else if (Integer.bitCount(moved(bit)) != 1)
        fields(
          bit + 1,
          found :+ Render.multiply(
            Render.and(Render.shiftRightLogical(Lane, Literal(bit.toLong)), Literal(1L)),
            Literal(moved(bit).toLong)
          )
        )
      else {
        val target = Integer.numberOfTrailingZeros(moved(bit))
        val length = (1 until width - bit).takeWhile(offset => moved(bit + offset) == 1 << (target + offset)).size + 1
        val field  = Render.and(
          if (bit == 0) Lane else Render.shiftRightLogical(Lane, Literal(bit.toLong)),
          Literal((1L << length) - 1L)
        )
        fields(bit + length, found :+ (if (target == 0) field else Render.shiftLeft(field, Literal(target.toLong))))
      }
    val terms = fields(0, Vec.empty)
    val union = moved.foldLeft(0)(_ | _)
    if (terms.isEmpty) Literal(base.toLong)
    else {
      val lanes = terms.reduce(Render.add(_, _))
      if (base == 0) lanes
      else if ((base & union) == 0) Render.add(lanes, Literal(base.toLong))
      else Render.xor(lanes, Literal(base.toLong))
    }
  }

  /** `width` bits from bit `bit` of a matrix operand's registers as lane `index` holds them, or as this lane does; a
    * constant operand is every element.
    */
  private def matrixBits(value: InstructionSource, registers: IndexedTerm, index: Option[Term], bit: Int, width: Int)
      : Term = value match {
    case InstructionSource.VectorRegister(_) => matrixBits(registers, index, bit, width)
    case other                               => Render.truncate(source(other), width.unsafe[Int in 1 ~ 64])
  }

  private def matrixBits(registers: IndexedTerm, index: Option[Term], bit: Int, width: Int): Term = {
    def at(register: Int): Term = index.fold(registers(register))(read(registers(register), _))
    val shift                   = bit & 31
    val low = if (shift == 0) at(bit >>> 5) else Render.shiftRightLogical(at(bit >>> 5), Literal(shift.toLong))
    if (shift + width > 32)
      Render.truncate(
        Render.or(low, Render.shiftLeft(at((bit >>> 5) + 1), Literal((32 - shift).toLong))),
        width.unsafe[Int in 1 ~ 64]
      )
    else if (width == 32) low
    else Render.truncate(low, width.unsafe[Int in 1 ~ 64])
  }

  /** An element of A (`operand` 0) or B (1) with the negation its register half takes. */
  private def matrixInput(controls: MatrixControls, bits: Term, bit: Int, width: Int, operand: Int): Term = {
    val negate = if (width == 16 && (bit & 16) != 0) controls.negateHigh else controls.negateLow
    if ((negate & (1 << operand)) != 0) Render.xor(bits, Literal(1L << (width - 1))) else bits
  }

  /** The E8M0 scale byte `selection` of `value` as lane `index` holds it; a float constant gives its exponent. */
  private def matrixScale(value: InstructionSource, selection: Int, index: Term): Term = value match {
    case InstructionSource.Floating(constant) =>
      Literal(Minifloats.exponent(prelude.Float32(constant.toFloat)).toLong)
    case InstructionSource.VectorRegister(register) =>
      Render.truncate(
        Render.shiftRightLogical(read(naming.vector(register), index), Literal(8L * selection)),
        8
      )
    case other => Render.truncate(Render.shiftRightLogical(source(other), Literal(8L * selection)), 8)
  }

  inline def multiplyMatrices(instruction: Instruction.VMatrix)(
      inline addend: Term => Term,
      inline inputA: Term => Term,
      inline inputB: Term => Term,
      inline step: (Term, Term, Term, Term, Term) => Term,
      inline result: Term => Term
  ): Option[Term.Steps] = {
    val shape            = instruction.shape
    val controls         = instruction.controls
    val banked           = (controls.banks & 4) != 0
    val c                = if (shape.sparse) InstructionSource.VectorRegister(instruction.dest) else instruction.srcC
    val cWidth           = shape.c.width
    val (aWidth, bWidth) = (shape.a.width, shape.b.width)
    def operand(value: InstructionSource, bank: Int)(register: Int): Term =
      matrixRegister(value, (controls.banks & bank) != 0, register)
    def rotated(lane: Term): Term =
      if (controls.laneGroups == 3) Render.and(Render.add(lane, Literal(16L)), Literal(63L)) else lane
    val laneGroups = if (controls.laneGroups == 3) 0 else controls.laneGroups
    val vectorB    = instruction.srcB match {
      case InstructionSource.VectorRegister(_) => true
      case _                                   => false
    }
    val steps = Vec.tabulate(shape.outputItems) { item =>
      def block(lane: Int): Int  = shape.outputBlock(lane, item)
      def row(lane: Int): Int    = shape.outputRow(lane, item)
      def column(lane: Int): Int = shape.outputColumn(lane, item)
      val bit                    = shape.outputBit(item) + (if (controls.high) 16 else 0)
      val held                   = matrixBits(c, matrixRegister(c, banked, _), None, bit, cWidth)
      val sign                   = Literal(1L << (cWidth - 1))
      val magnitude              = if ((controls.negateHigh & 4) != 0) Render.and(held, Render.not(sign)) else held
      val start                  = addend(if ((controls.negateLow & 4) != 0) Render.xor(magnitude, sign) else magnitude)
      val chain                  = Vec.newBuilder[Term.Step]
      def stepName(k: Int): String = if (k < 0) s"d$item" else s"d${item}k$k"
      chain += Term.Step(stepName(-1), start)
      (0 until shape.storedK).foreach { k =>
        val sum   = Variable(stepName(k - 1))
        val aLane =
          matrixLane(lane =>
            shape.aSource(
              block = block(lane),
              row = row(lane),
              k = k,
              lane = lane,
              blockBroadcast = controls.blockBroadcast,
              blockId = controls.blockId
            )
          )
        val aItems = (0 until naming.waveSize).map(lane =>
          shape.aItem(block = block(lane), row = row(lane), k = k)
        ).distinct
        require(aItems.size == 1, "a matrix element of A lies at different items across lanes")
        val aBit = aItems.head * aWidth
        val a    = matrixInput(
          controls,
          matrixBits(instruction.srcA, operand(instruction.srcA, 1), Some(aLane), aBit, aWidth),
          aBit,
          aWidth,
          0
        )
        val sparseB = shape.sparse && vectorB
        val first   = if (sparseB) 4 * (k / 2) else k
        val bLane   = rotated(matrixLane(lane =>
          shape.bSource(block = block(lane), k = first, column = column(lane), lane = lane, laneGroups = laneGroups)
        ))
        val bItems = (0 until naming.waveSize).map(lane =>
          shape.bItem(block = block(lane), k = first, column = column(lane))
        ).distinct
        require(bItems.size == 1, "a matrix element of B lies at different items across lanes")
        val bBit = bItems.head * bWidth
        val b    =
          if (sparseB) {
            val index = Render.truncate(
              Render.shiftRightLogical(
                read(matrixRegister(instruction.srcC, banked = false, 0), aLane),
                Literal(2L * shape.aItems * controls.indexSet + 2L * (aBit / aWidth))
              ),
              2
            )
            val span   = if ((bBit & 31) + 4 * bWidth <= 32) 32 else 64
            val window = matrixBits(instruction.srcB, operand(instruction.srcB, 2), Some(bLane), bBit & ~31, span)
            Render.truncate(
              Render.shiftRightLogical(
                window,
                Render.add(Literal((bBit & 31).toLong), Render.multiply(index, Literal(bWidth.toLong)))
              ),
              bWidth.unsafe[Int in 1 ~ 64]
            )
          } else matrixBits(instruction.srcB, operand(instruction.srcB, 2), Some(bLane), bBit, bWidth)
        val (scaleA, scaleB) = controls.scales.fold((Literal(0L): Term, Literal(0L): Term))(scales =>
          (
            matrixScale(scales.a, scales.selectionA, matrixLane(lane => row(lane) + shape.m * (k / 32))),
            matrixScale(scales.b, scales.selectionB, matrixLane(lane => column(lane) + shape.n * (k / 32)))
          )
        )
        chain +=
          Term.Step(
            stepName(k),
            step(sum, inputA(a), inputB(matrixInput(controls, b, bBit, bWidth, 1)), scaleA, scaleB)
          )
      }
      chain += Term.Step(s"element$item", result(Variable(stepName(shape.storedK - 1))))
      chain.result()
    }
    def element(item: Int): Term = Variable(s"element$item")
    val writes                   = Vec.tabulate(shape.outputRegisters) { register =>
      val target = matrixRegister(InstructionSource.VectorRegister(instruction.dest), banked, register)
      lane(
        target,
        if (shape.halfRegisters) Render.mergeHalf(target, element(register), controls.high)
        else if (cWidth == 64)
          if (register % 2 == 0) Render.truncate(element(register / 2), 32)
          else Render.shiftRightLogical(element(register / 2), Literal(32L))
        else if (cWidth == 16)
          Render.or(
            Render.truncate(element(2 * register), 16),
            Render.shiftLeft(Render.truncate(element(2 * register + 1), 16), Literal(16L))
          )
        else element(register)
      )
    }
    Some(Term.Steps.concat(steps.map(Term.Steps(_))) ++ Term.Steps(writes))
  }
}
