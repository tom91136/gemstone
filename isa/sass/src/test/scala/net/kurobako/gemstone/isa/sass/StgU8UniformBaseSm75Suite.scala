package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMI
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class StgU8UniformBaseSm75Suite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel("stg_u8_sys_u32_ur_probe", 352, Vec((0, 8), (8, 8), (16, 8), (24, 4)), registers = 8))
    )

  test("sm75 U8 SYS store adds the zero-extended R offset to the UR64 base") {
    val module = fixtureModule(75)
    val name   = "stg_u8_sys_u32_ur_probe"
    val kernel = module.kernels.find(_.name == name).get
    val text   = SharedMemoryFixtures.stgU8UniformBaseSm75ProbeSm75
    val sass   = ListingDecoder.parse(text, name, 75).fold(fail(_), _.head)
    val store  = sass.instructions.find(instruction =>
      instruction.mnemonic.raw == "STG" &&
        instruction.modifiers ==
        Vec(SASSInstruction.Modifier.`E`, SASSInstruction.Modifier.`U8`, SASSInstruction.Modifier.`SYS`)
    ).get
    assertEquals(store.operands, SASSFixture.operands("[R0.U32+UR4], R3"))
    assert(sass.instructions.exists(i =>
      i.mnemonic.raw == "ULDC" && i.modifiers == Vec(SASSInstruction.Modifier.`64`) &&
        i.operands == SASSFixture.operands("UR4, c[0x0][0x170]")
    ))
    def run(highBitOffset: Boolean): Unit = {
      val function = if (!highBitOffset) sass
      else {
        val addHighBit = SASSFixture.instruction(
          0x48,
          "IADD3",
          Vec("R0", "R0", "0x80000000", "RZ"),
          None
        )
        sass.copy(instructions = (sass.instructions :+ addHighBit).sortBy(_.address))
      }
      val lowered = SASSFixture(module, kernel, Vec(function))

      locally {
        val service               = SASSFixture.Execution()
        def allocation(size: Int) =
          service.allocate(size.toLong.unsafe[Long > 0])
        val output = allocation(128)
        val dummy0 = if (highBitOffset) output else allocation(4)
        val dummy1 = allocation(4)
        if (highBitOffset)
          assertEquals(service.registerAllocationAddress(output, GuestAddress(0x200000000L)), Right(()))
        val initial = Vec.from(Vec.fill(128)(0xa5.toByte))
        assert(service.write(output, 0L, initial).isRight)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        val count                               = 64
        val outputArgument: KernelTask.Argument = if (highBitOffset)
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x180000000L, 8))
        else KernelTask.Argument.Memory(output, 0L)
        val task = SASSFixture.task(
          name.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(dummy0, 0L),
            KernelTask.Argument.Memory(dummy1, 0L),
            outputArgument,
            KernelTask.Argument.Scalar(LittleEndian.toVec(count.toLong, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val actual   = service.read(output, 0L, 128).toOption.get
        val expected = Vec.from((0 until 128).map(i => if (i < count) i.toByte else 0xa5.toByte))
        assertEquals(actual, expected)
      }
    }

    run(highBitOffset = false)
    run(highBitOffset = true)
  }

  test("sm89 STRONG.SYS store becomes an explicit relaxed system-scope VM instruction") {
    val module = fixtureModule(89)
    val name   = "stg_u8_sys_u32_ur_probe"
    val kernel = module.kernels.find(_.name == name).get
    val sass   = ListingDecoder.parse(
      SharedMemoryFixtures.stgU8UniformBaseSm75ProbeSm89,
      name,
      89
    ).fold(fail(_), _.head)
    assert(sass.instructions.exists(instruction =>
      instruction.mnemonic.raw == "STG" && instruction.modifiers == Vec(
        SASSInstruction.Modifier.`E`,
        SASSInstruction.Modifier.`U8`,
        SASSInstruction.Modifier.`STRONG`,
        SASSInstruction.Modifier.`SYS`
      )
    ))
    val lowered = SASSFixture(module, kernel, Vec(sass))
    assert(lowered.instructions.exists {
      case VMI.Store(_, _, width, MemoryAccess.Global(true)) if width.raw == 8 => true
      case _                                                                   => false
    })
  }

  test("generic STRONG loads and stores preserve their scope without forcing global space") {
    val module = fixtureModule(89)
    val name   = "stg_u8_sys_u32_ur_probe"
    val kernel = module.kernels.find(_.name == name).get
    val sass   = ListingDecoder.parse(
      SharedMemoryFixtures.stgU8UniformBaseSm75ProbeSm89,
      name,
      89
    ).fold(fail(_), _.head)
    def lower(opcode: String, operands: Vec[String]): SASSFixture = {
      val address = sass.instructions.find(i =>
        (i.operation match { case _: SASSInstruction.Stg => true; case _ => false }) && i.modifiers.startsWith(Vec(
          SASSInstruction.Modifier.E,
          SASSInstruction.Modifier.U8,
          SASSInstruction.Modifier.STRONG,
          SASSInstruction.Modifier.SYS
        ))
      ).get.address
      val generic = SASSFixture.instruction(address.raw, opcode, operands, None)
      SASSFixture(module, kernel, Vec(sass.copy(instructions = Vec(generic))))
    }
    val gpuLoad = lower("LD.E.STRONG.GPU", Vec("R3", "[R2.64]"))
    assert(gpuLoad.instructions.exists {
      case VMI.Load(_, _, width, _, MemoryAccess.Generic(false)) if width.raw == 32 => true
      case _                                                                        => false
    })
    val sysStore = lower("ST.E.STRONG.SYS", Vec("[R2.64]", "R3"))
    assert(sysStore.instructions.exists {
      case VMI.Store(_, _, width, MemoryAccess.Generic(true)) if width.raw == 32 => true
      case _                                                                     => false
    })
  }

  test("generic STRONG.SYS load preserves generic space for runtime classification") {
    val module = fixtureModule(89)
    val name   = "stg_u8_sys_u32_ur_probe"
    val kernel = module.kernels.find(_.name == name).get
    val sass   = ListingDecoder.parse(
      SharedMemoryFixtures.stgU8UniformBaseSm75ProbeSm89,
      name,
      89
    ).fold(fail(_), _.head)
    val address = sass.instructions.find(i =>
      (i.operation match { case _: SASSInstruction.Stg => true; case _ => false }) && i.modifiers.startsWith(Vec(
        SASSInstruction.Modifier.E,
        SASSInstruction.Modifier.U8,
        SASSInstruction.Modifier.STRONG,
        SASSInstruction.Modifier.SYS
      ))
    ).get.address
    val load    = SASSFixture.instruction(address.raw, "LD.E.STRONG.SYS", Vec("R3", "[R2.64]"), None)
    val lowered = SASSFixture(module, kernel, Vec(sass.copy(instructions = Vec(load))))
    assert(lowered.instructions.exists {
      case VMI.Load(_, _, width, _, MemoryAccess.Generic(true)) if width.raw == 32 =>
        true
      case _ => false
    })
  }

  test("sm75 U8 SYS uniform-base store rejects other address widths and modifiers") {
    val module = fixtureModule(75)
    val kernel = module.kernels.find(_.name == "stg_u8_sys_u32_ur_probe").get
    val sass   = ListingDecoder.parse(
      SharedMemoryFixtures.stgU8UniformBaseSm75ProbeSm75,
      kernel.name,
      75
    ).fold(fail(_), _.head)
    val store = sass.instructions.find(instruction =>
      instruction.mnemonic.raw == "STG" &&
        instruction.modifiers ==
        Vec(SASSInstruction.Modifier.`E`, SASSInstruction.Modifier.`U8`, SASSInstruction.Modifier.`SYS`)
    ).get
    def rejected(instruction: DecodedInstruction, sm: Int >= 35 = 75): Boolean =
      SASSFixture.lower(
        module.copy(sm = sm),
        kernel,
        Vec(sass.copy(instructions = sass.instructions.map(i => if (i == store) instruction else i)))
      ).isLeft
    assert(rejected(store.rewrite(operands = SASSFixture.operands("[R0.U32+UR4+0x4], R3"))))
    assert(rejected(store.rewrite(modifiers =
      Vec(SASSInstruction.Modifier.E, SASSInstruction.Modifier.U16, SASSInstruction.Modifier.SYS)
    )))
    assert(rejected(store, sm = 86))
  }
}
