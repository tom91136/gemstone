package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.execution.CancellationToken
import net.kurobako.gemstone.execution.Effects
import net.kurobako.gemstone.execution.ExecutionBudget
import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.Pc
import net.kurobako.gemstone.execution.Step
import net.kurobako.gemstone.execution.WaitKey
import net.kurobako.gemstone.execution.WaitScope

enum BranchCondition {
  case Scc(expected: Boolean)
  case VccZero(expected: Boolean)
  case ExecZero(expected: Boolean)
}

enum WaitOperation {
  case Counters(mask: Int)
  case Sleep(cycles: Int)
}

/** Target-bound operations which the interpreter may execute. Unsupported is a first-class result, never a no-op. */
enum AmdExecutable {
  case Nop(target: Target, delay: Int)
  case Branch(target: Target, displacement: Int)
  case ConditionalBranch(target: Target, condition: BranchCondition, displacement: Int)
  case Wait(target: Target, operation: WaitOperation)
  case Barrier(target: Target, barrierId: Int)
  case EndProgram(target: Target)
  case Trap(target: Target, trapId: Int)
  case Unsupported(target: Target, opcode: String, formats: Vector[String], reason: String)

  def architecture: Target = this match {
    case Nop(target, _)                  => target
    case Branch(target, _)               => target
    case ConditionalBranch(target, _, _) => target
    case Wait(target, _)                 => target
    case Barrier(target, _)              => target
    case EndProgram(target)              => target
    case Trap(target, _)                 => target
    case Unsupported(target, _, _, _)    => target
  }
}

enum LoweringError {
  case WrongGeneration(target: Target, expected: Generation)
  case InvalidOperands(opcode: String, detail: String)
}

enum SemanticClass {
  case NoOperation, UnconditionalBranch, ConditionalBranch, Wait, Barrier, Exit, Trap
}

/** Auditable boundary between decoded SOPP metadata and implemented behavior. */
object SemanticsCoverage {
  val implemented: Map[String, SemanticClass] = Map(
    "s_nop"            -> SemanticClass.NoOperation,
    "s_branch"         -> SemanticClass.UnconditionalBranch,
    "s_cbranch_scc0"   -> SemanticClass.ConditionalBranch,
    "s_cbranch_scc1"   -> SemanticClass.ConditionalBranch,
    "s_cbranch_vccz"   -> SemanticClass.ConditionalBranch,
    "s_cbranch_vccnz"  -> SemanticClass.ConditionalBranch,
    "s_cbranch_execz"  -> SemanticClass.ConditionalBranch,
    "s_cbranch_execnz" -> SemanticClass.ConditionalBranch,
    "s_waitcnt"        -> SemanticClass.Wait,
    "s_sleep"          -> SemanticClass.Wait,
    "s_barrier"        -> SemanticClass.Barrier,
    "s_endpgm"         -> SemanticClass.Exit,
    "s_trap"           -> SemanticClass.Trap
  )
}

object AmdLowering {
  def lower(
      instruction: Instruction,
      target: Target
  ): Either[LoweringError, AmdExecutable] = {
    val opcode      = instruction.opcode
    val definitions = encodingsFor(instruction, target)
    val formats     = definitions.map(_.format.toUpperCase).distinct.sorted
    if (definitions.isEmpty)
      Right(AmdExecutable.Unsupported(target, opcode, Vector.empty, "no instruction metadata for the exact target"))
    else if (!formats.contains("SOPP"))
      Right(AmdExecutable.Unsupported(target, opcode, formats, "semantic lowering is implemented only for SOPP"))
    else instruction match {
      case Instruction.SNop(imm, mods) =>
        unsignedImmediate(opcode, imm, mods).map(AmdExecutable.Nop(target, _))
      case Instruction.SBranch(branchTarget, mods) =>
        branchImmediate(opcode, branchTarget, mods).map(AmdExecutable.Branch(target, _))
      case Instruction.SCbranchScc0(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.Scc(false))
      case Instruction.SCbranchScc1(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.Scc(true))
      case Instruction.SCbranchVccz(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.VccZero(true))
      case Instruction.SCbranchVccnz(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.VccZero(false))
      case Instruction.SCbranchExecz(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.ExecZero(true))
      case Instruction.SCbranchExecnz(branchTarget, mods) =>
        conditional(opcode, branchTarget, mods, target, BranchCondition.ExecZero(false))
      case Instruction.SWaitcnt(imm, mods) =>
        unsignedImmediate(opcode, imm, mods).map(value => AmdExecutable.Wait(target, WaitOperation.Counters(value)))
      case Instruction.SSleep(imm, mods) =>
        unsignedImmediate(opcode, imm, mods).map(value => AmdExecutable.Wait(target, WaitOperation.Sleep(value)))
      case Instruction.SBarrier(mods) =>
        noOperands(opcode, mods).map(_ => AmdExecutable.Barrier(target, 0))
      case Instruction.SEndpgm_0(mods) =>
        noOperands(opcode, mods).map(_ => AmdExecutable.EndProgram(target))
      case Instruction.SEndpgm_1(imm, mods) =>
        unsignedImmediate(opcode, imm, mods).map(_ => AmdExecutable.EndProgram(target))
      case Instruction.STrap(imm, mods) =>
        unsignedImmediate(opcode, imm, mods).map(AmdExecutable.Trap(target, _))
      case _ => Right(AmdExecutable.Unsupported(target, opcode, formats, "SOPP behavior is not implemented"))
    }
  }

  private def conditional(
      opcode: String,
      operand: Operand,
      mods: Vector[Modifier],
      target: Target,
      condition: BranchCondition
  ): Either[LoweringError, AmdExecutable] =
    branchImmediate(opcode, operand, mods).map(AmdExecutable.ConditionalBranch(target, condition, _))

  private def noOperands(opcode: String, mods: Vector[Modifier]): Either[LoweringError, Unit] =
    if (mods.isEmpty) Right(())
    else invalid(opcode, "instruction takes no operands or modifiers")

  private def unsignedImmediate(
      opcode: String,
      operand: Operand,
      mods: Vector[Modifier]
  ): Either[LoweringError, Int] =
    (operand, mods) match {
      case (Operand.IntegerLiteral(value, _), Vector()) if value >= 0 && value <= 65535 =>
        Right(value.toInt)
      case _ => invalid(opcode, "instruction requires one unsigned 16-bit immediate and no modifiers")
    }

  private def branchImmediate(
      opcode: String,
      operand: Operand,
      mods: Vector[Modifier]
  ): Either[LoweringError, Int] =
    (operand, mods) match {
      case (Operand.IntegerLiteral(value, _), Vector()) if value >= -32768 && value <= 65535 =>
        val bits = value.toInt & 0xffff
        Right(if ((bits & 0x8000) == 0) bits else bits - 0x10000)
      case _ => invalid(opcode, "branch requires one signed 16-bit displacement and no modifiers")
    }

  private def invalid[A](opcode: String, detail: String): Either[LoweringError, A] =
    Left(LoweringError.InvalidOperands(opcode, detail))
}

object AmdSemantics {
  extension (instruction: AmdExecutable) {

    /** Evaluate against a pre-instruction snapshot. PC and staged effects are applied later by the scheduler. */
    def execute(
        state: WaveState,
        effects: Effects[AmdWrite],
        budget: ExecutionBudget,
        cancellation: CancellationToken = CancellationToken.Never
    ): Step = {
      val _ = effects
      if (cancellation.isCancelled) Step.Trap(ExecutionFault.Cancelled)
      else
        budget.consume() match {
          case Left(fault) => Step.Trap(fault)
          case Right(())   => executeChecked(instruction, state)
        }
    }
  }

  private def executeChecked(instruction: AmdExecutable, state: WaveState): Step =
    if (instruction.architecture != state.target)
      Step.Trap(
        ExecutionFault.InvalidState(
          s"${instruction.architecture.name} executable cannot run on ${state.target.name} wave state"
        )
      )
    else instruction match {
      case AmdExecutable.Nop(_, _)                                     => Step.Advance
      case AmdExecutable.Branch(_, displacement)                       => branch(state.pc, displacement)
      case AmdExecutable.ConditionalBranch(_, condition, displacement) =>
        if (test(condition, state)) branch(state.pc, displacement) else Step.Advance
      case AmdExecutable.Wait(_, WaitOperation.Counters(mask)) =>
        Step.Suspend(WaitKey(WaitScope.Warp, mask.toLong, 0L))
      case AmdExecutable.Wait(_, WaitOperation.Sleep(cycles)) =>
        Step.Suspend(WaitKey(WaitScope.Warp, 0L, cycles.toLong))
      case AmdExecutable.Barrier(_, barrierId) =>
        Step.Suspend(WaitKey(WaitScope.Workgroup, barrierId.toLong, 0L))
      case AmdExecutable.EndProgram(_)   => Step.Exit(state.exec)
      case AmdExecutable.Trap(_, trapId) =>
        Step.Trap(ExecutionFault.ArchitecturalTrap("AMDGPU", trapId.toLong, state.pc))
      case AmdExecutable.Unsupported(_, opcode, _, reason) => Step.Trap(ExecutionFault.Unsupported(opcode, reason))
    }

  private def test(condition: BranchCondition, state: WaveState): Boolean = condition match {
    case BranchCondition.Scc(expected)      => state.scc == expected
    case BranchCondition.VccZero(expected)  => state.vcc.isEmpty == expected
    case BranchCondition.ExecZero(expected) => state.exec.isEmpty == expected
  }

  /** SOPP branch displacements are measured in 32-bit instructions from the instruction after the branch. */
  private def branch(pc: Pc, displacement: Int): Step = {
    val target = pc.value.toLong + 1L + displacement.toLong
    if (target < 0L || target > Int.MaxValue.toLong)
      Step.Trap(ExecutionFault.InvalidProgram(s"AMDGPU branch target $target is outside the PC range"))
    else Step.Jump(Pc.unsafe(target.toInt))
  }
}
