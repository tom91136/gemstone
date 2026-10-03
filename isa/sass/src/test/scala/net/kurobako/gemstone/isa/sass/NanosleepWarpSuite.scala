package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class NanosleepWarpSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    75,
    Vec(
      SASSFixture.Kernel("nanosleep_predicated", 352, Vec((0, 8), (8, 4)), registers = 8),
      SASSFixture.Kernel("nanosleep_immediates", 352, Vec((0, 8)), registers = 8),
      SASSFixture.Kernel("nanosleep_dynamic", 352, Vec((0, 8), (8, 4)), registers = 8)
    )
  )

  private val listing = Map(
    "nanosleep_predicated" -> NanosleepWarpFixtures.nanosleepWarpSm75ProbeSm75NanosleepPredicated,
    "nanosleep_immediates" -> NanosleepWarpFixtures.nanosleepWarpSm75ProbeSm75NanosleepImmediates,
    "nanosleep_dynamic"    -> NanosleepWarpFixtures.nanosleepWarpSm75ProbeSm75NanosleepDynamic
  )

  private def module: Cubin.Module                                  = fixtureModule
  private def instructions(kernel: String): Vec[DecodedInstruction] =
    ListingDecoder.parse(
      listing(kernel),
      kernel,
      75
    )
      .fold(fail(_), identity).head.instructions

  test("sm75 compiler emits immediate, uniform-constant, and register NANOSLEEP.WARP durations") {
    val m         = module
    val immediate = instructions("nanosleep_immediates")
      .filter(i =>
        (i.operation match { case _: Instruction.Nanosleep => true; case _ => false }) &&
          i.modifiers == Vec(Instruction.Modifier.WARP)
      ).map(_.operands.head)
    assertEquals(immediate, SASSFixture.operands("RZ, 0x1, 0x1c2, 0xffffffff"))

    val dynamic = instructions("nanosleep_dynamic")
      .filter(i =>
        (i.operation match { case _: Instruction.Nanosleep => true; case _ => false }) &&
          i.modifiers == Vec(Instruction.Modifier.WARP)
      ).map(_.operands.head)
    assertEquals(dynamic, SASSFixture.operands("c[0x0][0x168], R0"))

    // Predicated PTX nanosleep is compiled to a branch around an unpredicated
    // sleep; the conditional form therefore has no separate SASS predicate.
    val guarded       = instructions("nanosleep_predicated")
    val guardedSleeps = guarded.filter(i =>
      (i.operation match { case _: Instruction.Nanosleep => true; case _ => false }) &&
        i.modifiers == Vec(Instruction.Modifier.WARP)
    )
    assertEquals(guardedSleeps.map(_.operands.head), SASSFixture.operands("c[0x0][0x168]"))
    assert(guardedSleeps.forall(_.guard.isEmpty))
    assert(guarded.exists(_.operation match { case _: Instruction.Bra => true; case _ => false }))

    for (name <- Vec("nanosleep_immediates", "nanosleep_dynamic", "nanosleep_predicated")) {
      val kernel = m.kernels.find(_.name == name).get
      val sass   = ListingDecoder.parse(
        listing(name),
        name,
        75
      ).fold(fail(_), identity)
      assert(SASSFixture.lower(m, kernel, sass).isRight, name)
    }
  }

  test("NANOSLEEP.WARP rejects unsupported durations, predicates, and architectures") {
    val m     = module
    val sleep = instructions("nanosleep_immediates").find(i =>
      (i.operation match { case _: Instruction.Nanosleep => true; case _ => false }) &&
        i.modifiers == Vec(Instruction.Modifier.WARP)
    ).get
    def rejected(instruction: DecodedInstruction, m0: Cubin.Module): Boolean =
      SASSFixture.lower(
        m0,
        m0.kernels.find(_.name == "nanosleep_immediates").get,
        Vec(DecodedFunction("nanosleep_immediates", Vec(instruction)))
      ).left.exists(_.contains("unsupported NANOSLEEP.WARP form"))
    assert(rejected(sleep.rewrite(operands = SASSFixture.operands("0x100000000")), m))
    assert(rejected(sleep.rewrite(operands = SASSFixture.operands("-1")), m))
    assert(rejected(sleep.rewrite(operands = SASSFixture.operands("UR4")), m))
    assert(rejected(sleep.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), false, false)))), m))
    assert(rejected(sleep, m.copy(sm = 80)))
  }

  test("sm75 compiler nanosleep kernel stores every lane value") {
    val m      = module
    val name   = "nanosleep_dynamic"
    val kernel = m.kernels.find(_.name == name).get
    val sass   = ListingDecoder.parse(
      listing(name),
      name,
      75
    ).fold(fail(_), identity)
    val source = SASSFixture(m, kernel, sass)
    locally {
      val service = SASSFixture.Execution()
      val count   = 128
      val output  = service.allocate((count * 4L).unsafe[Long > 0])
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        name.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(450L, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val expected = Vec.from(1 to count).flatMap(v => LittleEndian.toVec(v.toLong, 4))
      assertEquals(
        service.read(output, 0L, (count * 4).unsafe[Int >= 0]),
        Right(expected)
      )
    }
  }
}
