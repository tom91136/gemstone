package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite

/** Memory, atomic and mbarrier forms as ptxas emits them (fixtures in `MemoryFormFixtures`), with results an RTX 4060
  * Ti produced for the sm_89 kernels.
  */
class MemoryFormSuite extends Suite {
  import Harness.*

  private def opcodes(kernel: Kernel): Vec[String] =
    kernel.function.instructions.map(i => (i.mnemonic.raw +: i.modifiers.map(_.name)).mkString("."))

  test("sm_89 ATOMS.POPC.INC adds one for each lane, as atomicAdd(p, 1u) on shared memory compiles") {
    val kernel = load(89, Fixture.listing("sharedCountersSm89"), "shared_counters")
    assert(opcodes(kernel).contains("ATOMS.POPC.INC.32"))
    assertEquals(kernel.launch(64)(Arg.zeros(16)).words(0), Vec.fill(4)(16L))
  }

  test("sm_89 mbarrier words follow ATOMS.ARRIVE.64 and ATOMS.POPC.INC as the hardware stores them") {
    val kernel = load(89, Fixture.listing("mbarrierWordsSm89"), "mbarrier_words")
    assert(opcodes(kernel).contains("ATOMS.ARRIVE.64"))
    // initialised for three arrivals, then seven arrivals, a two-count arrival without completion and a drop
    val expected = Vec(
      0x3ffffffdL,
      0x7ffffffdL,
      0x3ffffffdL,
      0x7ffffffeL,
      0x3ffffffdL,
      0x7fffffffL,
      0x3ffffffdL,
      0xfffffffdL,
      0x3ffffffdL,
      0xfffffffeL,
      0x3ffffffdL,
      0xffffffffL,
      0x3ffffffdL,
      0x7ffffffdL,
      0x3ffffffdL,
      0x7ffffffeL,
      0x3ffffffdL,
      0x80000000L,
      0x3ffffffeL,
      0x80000001L,
      0x80000000L
    )
    assertEquals(kernel.launch(1)(Arg.zeros(expected.size * 4)).words(0), expected)
  }

  test("sm_100 shared accesses and ATOMS add a uniform register offset to the lane's register") {
    val kernel = load(100, Fixture.listing("sharedOffsetsSm100"), "uniform_offsets", shared = 1024)
    assert(opcodes(kernel).contains("ATOMS.MAX.S32"))
    val k     = 3
    val slots = Vec.tabulate(128) { slot =>
      val thread = (0 until 128).find(t => (t * 5) % 128 == slot).get
      math.max(slot * 3, thread)
    }
    assertEquals(
      kernel.launch(128)(Arg.zeros(512), Arg.u32(k.toLong)).words(0),
      Vec.tabulate(128)(t => slots((t + 7) % 128).toLong)
    )
  }

  test("sm_75 global accesses add a zero-extended .U32 register to a uniform 64-bit base") {
    val kernel = load(75, Fixture.listing("u32OffsetsSm75"), "u32_offsets")
    assert(kernel.function.instructions.exists(_.operands.exists(operand =>
      OperandPrinter.print(operand).toOption.exists(_.contains(".U32+UR"))
    )))
    val k       = 5
    val input   = Vec.tabulate(256)(i => (i * 7 % 256).toByte)
    val output  = kernel.launch(32)(Arg.Buffer(input), Arg.zeros(256), Arg.u32(k.toLong)).bytes(1)
    val written = (0 until 32).map(t => t * 3 + k -> ((input(t * 5 + k) + 1) & 0xff).toByte).toMap
    assertEquals(output, Vec.tabulate(256)(i => written.getOrElse(i, 0.toByte)))
  }

  test("sm_120 local accesses add the uniform frame base to the lane's register") {
    val kernel = load(120, Fixture.listing("localOffsetsSm120"), "local_offsets")
    assert(kernel.function.instructions.exists(i =>
      i.mnemonic.raw == "LDL" && i.operands.exists(o => OperandPrinter.print(o).toOption.exists(_.contains("+UR")))
    ))
    val k        = 3
    val expected = Vec.tabulate(32) { t =>
      val a = Array.tabulate(32)(_ * k)
      a((t * k) & 31) = (0 until k).map(i => a((t + i * k) & 31)).sum
      a((t + k) & 31).toLong
    }
    assertEquals(kernel.launch(32)(Arg.zeros(128), Arg.u32(k.toLong)).words(0), expected)
  }

  test(
    "sm_100 256-bit loads fill the second group first and only the masked words; stores take the first group first"
  ) {
    val kernel = load(100, Fixture.listing("wideSm100"), "wide")
    assert(opcodes(kernel).exists(_.endsWith(".256")))
    val input = Vec.tabulate(32 * 8)(i => (i * 13 + 1).toLong)
    val run   = kernel.launch(32)(Arg.words(input*), Arg.zeros((64 + 8 * 32) * 4))
    val words = run.words(1)
    assertEquals(
      words.take(32),
      Vec.tabulate(32)(t => (input(8 * t) * 3 + input(8 * t + 4) * 5 + input(8 * t + 1) * 7) & 0xffffffffL)
    )
    assertEquals(
      words.drop(64),
      Vec.tabulate(32)(t => Vec(t, t + 1, t + 2, t + 3, t * 5, t * 5 + 1, t * 5 + 2, t * 5 + 3).map(_.toLong)).flatten
    )
  }

  test("sm_35 LDSLK and STSCUL serialise every lane's shared atomic") {
    val kernel = load(35, Fixture.listing("keplerSharedAtomicsSm35"), "kepler_shared_atomics")
    assert(opcodes(kernel).contains("LDSLK.64") && opcodes(kernel).contains("STSCUL"))
    val sum = (0 until 64).sum.toLong
    assertEquals(
      kernel.launch(64)(Arg.zeros(24)).words(0),
      Vec(sum, (0 until 64).map(_ ^ 21).max.toLong, 32L, 32L, (0xffffff00L + sum) & 0xffffffffL, 1L)
    )
  }

  // QSPC.E.x P, Rd with Rd patched in from RZ in ptxas output, measured on an RTX 4060 Ti: the register names the window
  // whatever space is queried.
  test("sm_89 QSPC register result is 1 for global, 2 for shared and 4 for local addresses") {
    check(
      89,
      "LDC.64 R2, c[0x0][0x160]; QSPC.E.G P0, R6, [R2]; MOV R4, c[0x0][0x18]; MOV R5, c[0x0][0x1c]; " +
        "QSPC.E.S P1, R7, [R4]; MOV R8, c[0x0][0x20]; MOV R9, c[0x0][0x24]; QSPC.E.G P2, R10, [R8]; " +
        "LEA R6, R7, R6, 0x4; LEA R6, R10, R6, 0x8; @P0 IADD3 R6, R6, 0x1000, RZ; " +
        "@P1 IADD3 R6, R6, 0x2000, RZ; @P2 IADD3 R6, R6, 0x4000, RZ; " +
        "STG.E [R2.64], R6; EXIT",
      Vec(0x3421)
    )
  }

  // ATOMS.CAST without SPIN, patched from ptxas's ATOMS.CAST.SPIN, stored and reported alike on an RTX 4060 Ti.
  test("sm_89 ATOMS.CAST stores over the expected value and reports whether it did, as CAST.SPIN does") {
    check(
      89,
      "MOV R6, 0x3f800000; STS [0x4], R6; MOV R8, 0x40400000; ATOMS.CAST R7, [0x4], R6, R8; " +
        "ATOMS.CAST R9, [0x4], R6, R6; LDS R10, [0x4]; LEA R7, R9, R7, 0x1; LDC.64 R2, c[0x0][0x160]; " +
        "STG.E [R2.64], R7; STG.E [R2.64+0x4], R10; EXIT",
      Vec(1, 0x40400000),
      block = Some(1),
      shared = 8
    )
  }
}
