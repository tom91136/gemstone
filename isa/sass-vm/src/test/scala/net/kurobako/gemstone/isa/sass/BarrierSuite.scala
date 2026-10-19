package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class BarrierSuite extends munit.FunSuite {
  private val Flags         = Vec(false, true)
  private val OutOfRangeIds = Vec("4294967296", "-4294967296")

  test("register-counted block barriers synchronise participants without waiting for exited lanes") {
    check(
      100,
      """S2R R0, SR_TID.X
        |ISETP.GE.U32.AND P0, PT, R0, 32, PT
        |@P0 EXIT
        |MOV R36, 0x200001
        |STS [R0.X4], R0
        |BAR.SYNC.DEFER_BLOCKING R36, R36
        |LOP3.LUT R6, R0, 1, RZ, 0x3c, !PT
        |LDS R4, [R6.X4]
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec.tabulate(32)(_ ^ 1) ++ Vec.fill(32)(0),
      shared = 128
    )
  }

  // bar.sync, bar.arrive and bar.red with a register id and count: ptxas from sm_50 to sm_120 packs both into one
  // register, the id in the low bits and the count in bits 16 to 27, and names it twice (memory_atomics.cu barriers).
  for (sm <- Vec(75, 89, 100))
    test(s"sm_$sm a register id and count share one register, the count in bits 16 to 27") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |ISETP.GE.U32.AND P0, PT, R0, 0x40, PT
          |MOV R7, 0x800001
          |MOV R9, 0x800006
          |@!P0 STS [R0.X4], R0
          |@!P0 BAR.ARV R7, R7
          |@P0 BAR.SYNC.DEFER_BLOCKING R7, R7
          |@P0 LDS R4, [R0.X4+-0x100]
          |@!P0 MOV R4, R0
          |LOP3.LUT P1, RZ, R0, 0x1, RZ, 0xc0, !PT
          |BAR.RED.POPC.DEFER_BLOCKING R9, R9, P1
          |B2R.RESULT R5
          |IMAD R4, R5, 0x100, R4
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.tabulate(128)(thread => 64 * 0x100 + thread % 64),
        shared = 512
      )
    }

  test("block predicate reduction and per-lane local spills preserve values") {
    check(
      86,
      """S2R R0, SR_TID.X
        |STL [RZ], R0
        |ISETP.EQ.U32.AND P0, PT, R0, 17, PT
        |BAR.RED.OR.DEFER_BLOCKING 0, P0
        |B2R.RESULT RZ, P1
        |LDL R4, [RZ]
        |SEL R4, R4, 0xffffffff, P1
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.tabulate(64)(identity)
    )
  }

  test("SASS barrier IDs reject values that would wrap during Int conversion") {
    Vec(("BAR.SYNC", "3"), ("BAR.RED.OR.DEFER_BLOCKING", "3, P0")).foreach { (opcode, valid) =>
      assert(program(86, s"$opcode $valid").lower().isRight, s"$opcode $valid")
      OutOfRangeIds.foreach { outOfRange =>
        val operands = valid.replace("3", outOfRange)
        val result   = program(86, s"$opcode $operands").lower()
        assert(result.left.exists(_.contains("barrier id is outside Int range")), s"$opcode $operands: $result")
      }
    }
  }

  test("compiler-produced sm_35 MEMBAR.GL reaches the generic global memory fence") {
    val listing = Fixture.listing("membarSm35")
    assert(listing.contains("MEMBAR.GL"))
    val kernel = load(35, listing, "probe")
    assert(kernel.instructions(8).contains(VMInstruction.Fence))
    assertEquals(kernel.launch(1)(Arg.zeros(8)).words(0), Vec(7L, 7L))
  }

  test("dependency barriers accept completed copy scoreboards and explicit wait masks") {
    check(
      86,
      """LDC.64 R2, c[0x0][0x160]
        |MOV R5, 41
        |STG.E [R2.64], R5
        |LDGSTS.E [RZ], [R2.64]
        |DEPBAR.LE SB1, 0x0, {3, 2}
        |LDS R6, [0x0]
        |IADD3 R6, R6, 1, RZ
        |STG.E [R2.64], R6
        |EXIT""".stripMargin,
      Vec(42),
      shared = 4
    )
  }

  for {
    sm     <- Vec(50, 60)
    binary <- Flags
  }
    test(s"sm_$sm mask-only dependency barriers observe completed loads binary=$binary") {
      check(
        sm,
        "LDC.64 R2, c[0x0][0x160]; MOV R5, 41; STG.E [R2], R5; LDG.E R6, [R2]; DEPBAR {0}; IADD R6, R6, 1; DEPBAR {0, 3, 5}; " +
          "STG.E [R2], R6; EXIT",
        Vec(42),
        rewrite = instruction =>
          instruction.operation.form match {
            case _: Form.Depbar if binary =>
              // CUDA emits this mask-only DEPBAR {0} word; the decoder exposes its bitmask
              val bytes = LittleEndian.toVec(0xf0f0000000070001L, 8).toArray ++ Array[Byte](0xfd.toByte, 0x07, 0x00)
              InstructionDecoder.decode(instruction.address, bytes, target(sm)).value
            case _ => instruction
          }
      )
    }

  test("shared memory barrier arrivals advance parity and reset their count across phases") {
    check(
      90,
      """S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |ISETP.EQ.U32.AND P0, PT, R0, RZ, PT
        |UMOV UR4, 0x1ffffc
        |UMOV UR5, 0x7ffff000
        |@P0 SYNCS.EXCH.64 URZ, [0], UR4
        |BAR.SYNC 0
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [0], RZ
        |SEL R4, RZ, 1, P1
        |SYNCS.ARRIVE.TRANS64 RZ, [0], RZ
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [0], RZ
        |SEL R4, R4, RZ, P1
        |MOV R5, 0x80000000
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [0], R5
        |SEL R4, RZ, R4, P1
        |SYNCS.ARRIVE.TRANS64 RZ, [0], RZ
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [0], R5
        |SEL R4, R4, RZ, P1
        |@P0 SYNCS.CCTL.IV [0]
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec(1, 1),
      shared = 8
    )
  }

  test("stored TRANS64 initialisation and same-value rewrite reset the barrier phase") {
    // ptxas TRANS64 initialisation for one arrival: q = 2^20 - 1; words are q<<1, q<<11
    check(
      100,
      """MOV R4, 0x1ffffe
        |MOV R5, 0x7ffff800
        |STS.64 [16], R4
        |FENCE.VIEW.ASYNC.S
        |SYNCS.CCTL.IVALL
        |SYNCS.ARRIVE.TRANS64.A1T0 RZ, [16], RZ
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [16], RZ
        |MOV R6, 1
        |SEL R6, R6, RZ, P0
        |STS.64 [16], R4
        |FENCE.VIEW.ASYNC.S
        |SYNCS.CCTL.IVALL
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [16], RZ
        |SEL R6, RZ, R6, P0
        |SYNCS.ARRIVE.TRANS64.A1T0 RZ, [16], RZ
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [16], RZ
        |SEL R6, R6, RZ, P0
        |LDC.64 R10, c[0x0][0x160]
        |STG.E [R10], R6
        |EXIT""".stripMargin,
      Vec(1),
      shared = 24
    )
  }

  test("uniform bulk copies transfer each byte once and complete the destination barrier") {
    check(
      120,
      """S2R R0, SR_TID.X
        |LDCU.64 UR6, c[0x0][0x160]
        |MOV.64 R10, UR6
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |IADD3 R4, R0, 101, RZ
        |STG.E [R10], R4
        |ISETP.EQ.U32.AND P0, PT, R0, RZ, PT
        |UMOV UR8, 0x1ffffe
        |UMOV UR9, 0x7ffff800
        |@P0 SYNCS.EXCH.64 URZ, [16], UR8
        |BAR.SYNC 0
        |UMOV UR4, 0
        |UMOV UR5, 16
        |UMOV UR10, 1
        |UBLKCP.S.G [UR4], [UR6], UR10
        |MOV R4, 16
        |@P0 SYNCS.ARRIVE.TRANS64 RZ, [16], R4
        |BAR.SYNC 0
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [16], RZ
        |IMAD.SHL.U32 R0, R0, 4, RZ
        |LDS R4, [R0]
        |SEL R4, R4, RZ, P1
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec(101, 102, 103, 104),
      shared = 24
    )
  }

  test("single-CTA cluster cancellation completes a failed response transaction") {
    check(
      100,
      """S2R R0, SR_TID.X
        |ISETP.EQ.U32.AND P0, PT, R0, RZ, PT
        |UMOV UR4, 0x1ffffe
        |UMOV UR5, 0x7ffff800
        |@P0 SYNCS.EXCH.64 URZ, [0], UR4
        |UCGABAR_ARV
        |UCGABAR_WAIT
        |UMOV UR4, 16
        |UMOV UR5, 0
        |UGETNEXTWORKID.SELFCAST [UR4], [UR5]
        |MOV R4, 16
        |@P0 SYNCS.ARRIVE.TRANS64 RZ, [0], R4
        |BAR.SYNC 0
        |SYNCS.PHASECHK.TRANS64.TRYWAIT P1, [0], RZ
        |MOV R4, 1
        |SEL R4, R4, RZ, P1
        |LDS R5, [24]
        |ISETP.EQ.U32.AND P1, PT, R5, RZ, PT
        |SEL R4, R4, RZ, P1
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec(1, 1, 1, 1),
      shared = 32
    )
  }

  test("ordinary Hopper blocks have cluster CTA rank zero and clustered kernels keep their cluster shape") {
    check(
      90,
      "LDC.64 R2, c[0x0][0x160]; S2UR UR4, SR_CgaCtaId; WARPSYNC.ALL; MOV R4, UR4; STG.E [R2], R4; EXIT",
      Vec(0)
    )
    val kernel  = program(90, "EXIT")
    val module  = kernel.module(Vec.empty)
    val shape   = LaunchGeometry.Dimensions(2, 1, 1)
    val compile = VMCompiler.compile(module, module.kernels.head.copy(requiredCluster = shape), kernel.functions)
    assertEquals(compile.map(_.cluster), Right(Some(shape)))
  }

  test("sm_70 NANOSLEEP.WARP preserves following memory effects") {
    check(70, "NANOSLEEP.WARP 450; LDC.64 R2, c[0x0][0x160]; MOV R6, 29; STG.E [R2.64], R6; EXIT", Vec(29))
  }

  private def nanosleep(name: String): Kernel =
    load(75, Fixture.listing(s"nanosleep${name.capitalize}Sm75"), s"nanosleep_$name")

  private def sleeps(kernel: Kernel): Vec[DecodedInstruction] = kernel.function.instructions.filter(i =>
    (i.operation.form match {
      case _: Form.Nanosleep => true
      case _                 => false
    }) && i.modifiers == Vec(Instruction.Modifier.WARP)
  )

  test("sm75 compiler emits immediate, uniform-constant, and register NANOSLEEP.WARP durations") {
    assertEquals(sleeps(nanosleep("immediates")).map(_.operands.head), operands("RZ, 0x1, 0x1c2, 0xffffffff"))
    assertEquals(sleeps(nanosleep("dynamic")).map(_.operands.head), operands("c[0x0][0x168], R0"))
    // predicated PTX nanosleep compiles to a branch around an unpredicated sleep
    val guarded = nanosleep("predicated")
    assertEquals(sleeps(guarded).map(_.operands.head), operands("c[0x0][0x168]"))
    assert(sleeps(guarded).forall(_.guard.isEmpty))
    assert(guarded.function.instructions.exists(_.operation.form match {
      case _: Form.Bra => true
      case _           => false
    }))
    assert(nanosleep("immediates").lower(8).isRight)
    assert(nanosleep("dynamic").lower(8, 4).isRight)
    assert(guarded.lower(8, 4).isRight)
  }

  test("NANOSLEEP.WARP rejects unsupported durations, predicates, and architectures") {
    val kernel                                                           = nanosleep("immediates")
    val sleep                                                            = sleeps(kernel).head
    def rejected(instruction: DecodedInstruction, sm: Int = 75): Boolean =
      kernel.copy(sm = sm, functions = Vec(kernel.function.copy(instructions = Vec(instruction)))).lower(8)
        .left.exists(_.contains("unsupported NANOSLEEP.WARP form"))
    assert(rejected(sleep.rewrite(operands = operands("0x100000000"))))
    assert(rejected(sleep.rewrite(operands = operands("-1"))))
    assert(rejected(sleep.rewrite(operands = operands("UR4"))))
    assert(rejected(sleep.rewrite(guard = Parser.parse("@P0 NOP;").value.guard)))
    assert(rejected(sleep, sm = 60))
  }

  test("sm75 compiler nanosleep kernel stores every lane value") {
    assertEquals(nanosleep("dynamic").launch(128)(Arg.zeros(512), Arg.u32(450)).words(0), Vec.tabulate(128)(_ + 1L))
  }
}
