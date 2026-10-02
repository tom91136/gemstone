package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class BmovClearSuite extends munit.FunSuite {
  private def has(
      instruction: DecodedInstruction,
      mnemonic: String,
      modifiers: Vec[Instruction.Modifier] = Vec.empty
  ): Boolean                 = instruction.mnemonic.raw == mnemonic && instruction.modifiers == modifiers
  private val clearModifiers = Vec(Instruction.Modifier("32"), Instruction.Modifier("CLEAR"))

  private def input(sm: Int = 75): Cubin.Module =
    SASSFixture.module(sm, Vec(SASSFixture.Kernel("divergent_loop", 352, Vec(0 -> 8, 8 -> 4), registers = 8)))
  private val listing = Map(
    70 -> BmovFixtures.bmovClearSm70ProbeSm70,
    75 -> BmovFixtures.bmovClearSm75ProbeSm75,
    89 -> BmovFixtures.bmovClearSm75ProbeSm89
  )
  private def functions(sm: Int = 75) =
    ListingDecoder.parse(listing(sm), "divergent_loop", sm).fold(fail(_), identity)

  test("compiler BMOV clears B0 before first BSSY and after BSYNC before reuse") {
    val instructions = functions().head.instructions
    val clears       =
      instructions.filter(i => has(i, "BMOV", clearModifiers) && i.operands == SASSFixture.operands("RZ, B0"))
    val setups = instructions.filter(i => has(i, "BSSY") && i.operands.headOption.contains(SASSFixture.operand("B0")))
    val syncs  = instructions.filter(i => has(i, "BSYNC") && i.operands == SASSFixture.operands("B0"))
    assertEquals(clears.size, 2)
    assertEquals(setups.size, 2)
    assertEquals(syncs.size, 2)
    assert(clears(0).address < setups(0).address)
    assert(syncs(0).address < clears(1).address)
    assert(clears(1).address < setups(1).address)
    assert(syncs(1).address < instructions.find(i => has(i, "EXIT")).get.address)
    assert(SASSFixture.lower(input(), input().kernels.head, functions()).isRight)
  }

  test("BMOV clear rejects predicated and active-region forms") {
    val m          = input()
    val f          = functions().head
    val firstClear = f.instructions.find(i => has(i, "BMOV", clearModifiers)).get
    val predicated = firstClear.rewrite(
      guard = SASSFixture.instruction(firstClear.address.raw, "BMOV.32.CLEAR", Vec("RZ", "B0"), Some("@P0")).guard
    )
    val predicatedResult = SASSFixture.lower(
      m,
      m.kernels.head,
      Vec(f.copy(instructions = f.instructions.updated(f.instructions.indexOf(firstClear), predicated)))
    )
    assert(predicatedResult.left.exists(_.contains("unsupported discarded barrier clear")))

    val firstSetup         = f.instructions.find(i => has(i, "BSSY")).get
    val activeClear        = firstClear.copy(address = firstSetup.address + 16)
    val activeInstructions = f.instructions.map(i => if (i.address == activeClear.address) activeClear else i)
    val activeResult       = SASSFixture.lower(m, m.kernels.head, Vec(f.copy(instructions = activeInstructions)))
    assert(activeResult.left.exists(_.contains("inside an active convergence region")))
  }

  test("BSSY requires a forward target before treating a clear as inactive") {
    val m       = input()
    val f       = functions().head
    val setup   = f.instructions.find(i => has(i, "BSSY")).get
    val invalid =
      setup.rewrite(operands = setup.operands.updated(1, SASSFixture.operand(s"0x${setup.address.raw.toHexString}")))
    val changed = f.copy(instructions = f.instructions.map(i => if (i == setup) invalid else i))
    val result  = SASSFixture.lower(m, m.kernels.head, Vec(changed))
    assert(result.left.exists(_.contains("convergence barrier target must be forward")))
  }

  test("sm75 compiler fixture executes both divergent loops") {
    val m      = input()
    val source = SASSFixture(m, m.kernels.head, functions())
    locally {
      val service = SASSFixture.Execution()
      val count   = 128
      val output  = service.allocate((count * 4L).unsafe[Long > 0])
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val limit = 5
      val task  = SASSFixture.task(
        "divergent_loop": NonEmpty[String],
        LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(limit.toLong, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val expected = (0 until count).map { lane =>
        if (lane % 4 == 0 || lane % 4 == 1) limit + 2 else if (lane % 4 == 2) limit else 0
      }
      val expectedBytes = Vec.from(expected).flatMap(v => LittleEndian.toVec(v.toLong, 4))
      assertEquals(
        service.read(output, 0L, (count * 4).unsafe[Int >= 0]),
        Right(expectedBytes)
      )
    }
  }

  test("sm70 CUDA 11.8 fixture has the same inactive B0 clears and executes both loops") {
    val m            = input(70)
    val sassText     = listing(70)
    val instructions = functions(70).head.instructions
    val clears       =
      instructions.filter(i => has(i, "BMOV", clearModifiers) && i.operands == SASSFixture.operands("RZ, B0"))
    val setups = instructions.filter(i => has(i, "BSSY") && i.operands.headOption.contains(SASSFixture.operand("B0")))
    val syncs  = instructions.filter(i => has(i, "BSYNC") && i.operands == SASSFixture.operands("B0"))
    assertEquals(clears.size, 2)
    assertEquals(setups.size, 2)
    assertEquals(syncs.size, 2)
    assert(sassText.contains("0x0000000000ff7355"))
    assert(sassText.contains("0x000fe20000100000"))
    assert(clears(0).address < setups(0).address)
    assert(syncs(0).address < clears(1).address)
    assert(clears(1).address < setups(1).address)
    assertEquals(SASSFixture.lower(m, m.kernels.head, functions(70)).isRight, true)

    val source = SASSFixture(m, m.kernels.head, functions(70))
    locally {
      val service = SASSFixture.Execution()
      val count   = 128
      val output  = service.allocate((count * 4L).unsafe[Long > 0])
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "divergent_loop": NonEmpty[String],
        LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(5L, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val expected = (0 until count).map { lane =>
        if (lane % 4 == 0 || lane % 4 == 1) 7 else if (lane % 4 == 2) 5 else 0
      }
      val expectedBytes = Vec.from(expected).flatMap(v => LittleEndian.toVec(v.toLong, 4))
      assertEquals(service.read(output, 0L, (count * 4).unsafe[Int >= 0]), Right(expectedBytes))
    }
  }
}
