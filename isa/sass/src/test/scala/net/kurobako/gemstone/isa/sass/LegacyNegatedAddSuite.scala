package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyNegatedAddSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("arith_probe", 320, Vec((0, 8), (8, 8), (16, 8), (24, 4)), registers = 24))
  )

  test("legacy paired IADD with negated sources carries correctly across 64-bit words") {
    // Exercise the carry between low-word subtraction and high-word subtraction with negated sources.
    val module       = fixtureModule
    val kernel       = module.kernels.find(_.name == "arith_probe").get
    val instructions = Vec(
      SASSFixture.instruction(0x08, "LDC.64", Vec("R4", "c[0x0][0x140]"), None),
      SASSFixture.instruction(0x10, "MOV", Vec("R17", "c[0x0][0x148]"), None),
      SASSFixture.instruction(0x18, "MOV", Vec("R15", "c[0x0][0x14c]"), None),
      SASSFixture.instruction(0x20, "MOV", Vec("R18", "c[0x0][0x150]"), None),
      SASSFixture.instruction(0x28, "MOV", Vec("R19", "c[0x0][0x154]"), None),
      SASSFixture.instruction(0x30, "IADD", Vec("R13.CC", "-R18", "R17"), None),
      SASSFixture.instruction(0x38, "IADD.X", Vec("R16", "-R19", "R15"), None),
      SASSFixture.instruction(0x48, "ST.E", Vec("[R4]", "R13"), None),
      SASSFixture.instruction(0x50, "ST.E", Vec("[R4+0x4]", "R16"), None),
      SASSFixture.instruction(0x58, "EXIT", Vec.empty, None)
    )
    val sass  = SASSFixture(module, kernel, Vec(DecodedFunction("arith_probe", instructions)))
    val cases = Vec(
      (0L, 0L),
      (0L, 1L),
      (0x0000000100000000L, 1L),
      (0x0000000100000000L, 0x0000000100000000L),
      (0x0000000100000000L, 0x0000000100000001L),
      (0x8000000000000000L, 1L),
      (-1L, Long.MinValue),
      (Long.MinValue, -1L)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (a, b) =>
        val task = SASSFixture.task(
          "arith_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes  = service.read(output, 0L, 8).toOption.get
        val actual = LittleEndian.decode(bytes, 0, 8)
        assertEquals(actual, a - b)
      }
    }
  }
}
