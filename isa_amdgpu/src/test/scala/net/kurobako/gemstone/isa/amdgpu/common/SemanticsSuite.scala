package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.execution.Effects
import net.kurobako.gemstone.execution.CancellationToken
import net.kurobako.gemstone.execution.ExecutionBudget
import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.LaneMask
import net.kurobako.gemstone.execution.Pc
import net.kurobako.gemstone.execution.Step
import net.kurobako.gemstone.execution.WaitKey
import net.kurobako.gemstone.execution.WaitScope
import net.kurobako.gemstone.isa.amdgpu.common.AmdSemantics.*
import net.kurobako.gemstone.isa.amdgpu.common.generated.Database

class SemanticsSuite extends munit.FunSuite {
  private val target = Target.unsafe("gfx1010")

  test("SOPP branch displacement is relative to the following instruction") {
    val state = wave(pc = 7)
    assertEquals(run(lower("s_branch 2"), state), Step.Jump(Pc.unsafe(10)))
    assertEquals(run(lower("s_branch -8"), state), Step.Jump(Pc.Entry))
    run(lower("s_branch -9"), state) match {
      case Step.Trap(ExecutionFault.InvalidProgram(_)) => ()
      case other                                       => fail(s"expected an invalid branch trap, found $other")
    }
  }

  test("conditional branches read SCC, VCC, and EXEC from the pre-instruction state") {
    val state   = wave(pc = 3)
    val effects = new Effects[AmdWrite]
    effects.stage(AmdWrite.Scc(true))
    effects.stage(AmdWrite.Vcc(LaneMask.one(2).toOption.get))
    assertEquals(state.commit(effects), Right(()))

    assertEquals(run(lower("s_cbranch_scc1 4"), state), Step.Jump(Pc.unsafe(8)))
    assertEquals(run(lower("s_cbranch_scc0 4"), state), Step.Advance)
    assertEquals(run(lower("s_cbranch_vccnz 4"), state), Step.Jump(Pc.unsafe(8)))
    assertEquals(run(lower("s_cbranch_vccz 4"), state), Step.Advance)
    assertEquals(run(lower("s_cbranch_execnz 4"), state), Step.Jump(Pc.unsafe(8)))

    val deactivate = new Effects[AmdWrite]
    deactivate.stage(AmdWrite.Exec(LaneMask.Empty))
    assertEquals(state.commit(deactivate), Right(()))
    assertEquals(run(lower("s_cbranch_execz 4"), state), Step.Jump(Pc.unsafe(8)))
    assertEquals(run(lower("s_cbranch_execnz 4"), state), Step.Advance)
  }

  test("wait, sleep, barrier, end, and trap produce explicit scheduler actions") {
    val state = wave()
    assertEquals(
      run(lower("s_waitcnt 42"), state),
      Step.Suspend(WaitKey(WaitScope.Warp, 42L, 0L))
    )
    assertEquals(
      run(lower("s_sleep 3"), state),
      Step.Suspend(WaitKey(WaitScope.Warp, 0L, 3L))
    )
    assertEquals(
      run(lower("s_barrier"), state),
      Step.Suspend(WaitKey(WaitScope.Workgroup, 0L, 0L))
    )
    assertEquals(run(lower("s_endpgm"), state), Step.Exit(state.exec))
    run(lower("s_trap 7"), state) match {
      case Step.Trap(ExecutionFault.ArchitecturalTrap("AMDGPU", 7L, pc)) => assertEquals(pc, Pc.Entry)
      case other => fail(s"expected an AMDGPU trap, found $other")
    }
  }

  test("executables are bound to an exact processor target") {
    val executable = lower("s_nop 0")
    val otherState = WaveState.create(Target.unsafe("gfx1030"), 32, 4, 4).toOption.get
    run(executable, otherState) match {
      case Step.Trap(ExecutionFault.InvalidState(message)) => assert(message.contains("gfx1010"))
      case other                                           => fail(s"expected a target mismatch trap, found $other")
    }
  }

  test("execution budget and cancellation fail before instruction behavior") {
    val state  = wave()
    val nop    = lower("s_nop 0")
    val budget = ExecutionBudget(1).toOption.get
    assertEquals(nop.execute(state, new Effects[AmdWrite], budget), Step.Advance)
    assertEquals(
      nop.execute(state, new Effects[AmdWrite], budget),
      Step.Trap(ExecutionFault.BudgetExceeded(1L))
    )

    val cancellation = new CancellationToken.Source
    cancellation.cancel()
    val untouched = ExecutionBudget(1).toOption.get
    assertEquals(
      nop.execute(state, new Effects[AmdWrite], untouched, cancellation),
      Step.Trap(ExecutionFault.Cancelled)
    )
    assertEquals(untouched.used, 0L)
  }

  test("all selected SOPP metadata lowers to either a declared semantic or explicit Unsupported") {
    val targets = Vector(
      "gfx600",
      "gfx700",
      "gfx803",
      "gfx900",
      "gfx908",
      "gfx90a",
      "gfx940",
      "gfx950",
      "gfx1010",
      "gfx1030",
      "gfx1100",
      "gfx1150",
      "gfx1200"
    ).map(Target.unsafe)

    var selected = 0
    targets.foreach { architecture =>
      val opcodes = Database.instructions.iterator
        .filter(info => info.target == architecture.metadataTarget && info.format.toUpperCase == "SOPP")
        .map(_.name)
        .toSet
      opcodes.foreach { opcode =>
        selected += 1
        val instruction = semanticInput(opcode)
        AmdLowering.lower(instruction, architecture, Database) match {
          case Left(error) => fail(s"$opcode on ${architecture.name} was not classified: $error")
          case Right(AmdExecutable.Unsupported(_, _, _, _)) =>
            assert(!SemanticsCoverage.implemented.contains(opcode), s"implemented $opcode lowered as Unsupported")
          case Right(_) =>
            assert(SemanticsCoverage.implemented.contains(opcode), s"undeclared $opcode acquired executable semantics")
        }
      }
    }
    assert(selected > 100)
  }

  private def semanticInput(opcode: String): Instruction = {
    val operands = opcode match {
      case "s_barrier" | "s_endpgm" => Vector.empty
      case _                        => Vector(Operand.IntegerLiteral(BigInt(0), 10))
    }
    Instruction(Opcode.unsafe(opcode), operands)
  }

  private def lower(source: String): AmdExecutable = {
    val instruction = Assembly.parseInstruction(source).fold(error => fail(error.toString), identity)
    AmdLowering.lower(instruction, target, Database).fold(error => fail(error.toString), identity)
  }

  private def wave(pc: Int = 0): WaveState =
    WaveState
      .create(target, laneCount = 32, sgprCount = 16, vgprCount = 8, agprCount = 4, pc = Pc.unsafe(pc))
      .toOption
      .get

  private def run(instruction: AmdExecutable, state: WaveState): Step = {
    val budget = ExecutionBudget(100).toOption.get
    instruction.execute(state, new Effects[AmdWrite], budget)
  }
}
