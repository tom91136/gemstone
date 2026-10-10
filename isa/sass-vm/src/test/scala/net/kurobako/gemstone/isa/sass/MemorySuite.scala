package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class MemorySuite extends munit.FunSuite {
  private def operands(text: String): Vec[Operand] = OperandParser.parseMany(text).value

  private def statement(text: String, address: Int): DecodedInstruction =
    Listing.program(text).value.instructions.head.copy(address = address.unsafe[Int >= 0])

  test("compiler ATOMG returns the prior value through a predicated warp-aggregated atomic") {
    val kernel = load(86, Fixture.listing("atomicAddSm86"), "atomic_add")
    assert(kernel.function.instructions.exists(i => i.mnemonic.raw == "ATOMG" && i.guard.nonEmpty))
    val run = kernel.launch(32)(Arg.words(-16L & 0xffffffffL), Arg.zeros(128))
    assertEquals(run.words(1), Vec.tabulate(32)(_ - 16).map(_.toLong & 0xffffffffL))
    assertEquals(run.words(0), Vec(16L))
  }

  test("compiler-produced sm_35 cache invalidation preserves atomic-to-normal-load coherence") {
    val kernel = load(35, Fixture.listing("cacheInvalidateSm35"), "cache_probe")
    assert(kernel.function.instructions.exists(i =>
      i.mnemonic.raw == "CCTL" && i.modifiers.map(_.name) == Vec("E", "IV")
    ))
    for (initial <- Vec(0L, 42L, 0xfffffffcL)) {
      val run     = kernel.launch(1)(Arg.zeros(8), Arg.words(initial))
      val updated = (initial + 7L) & 0xffffffffL
      assertEquals(run.words(0), Vec(initial, updated))
      assertEquals(run.words(1), Vec(updated))
    }
  }

  private val store75 = load(75, Fixture.listing("stgU8UniformBaseSm75Patched"), "stg_u8_sys_u32_ur_probe")
  private val store89 = load(89, Fixture.listing("stgU8UniformBaseSm89"), "stg_u8_sys_u32_ur_probe")

  private def isStore(instruction: DecodedInstruction, modifiers: String*): Boolean =
    instruction.mnemonic.raw == "STG" && instruction.modifiers.map(_.name) == Vec.from(modifiers)

  test("sm75 U8 SYS store adds the zero-extended R offset to the UR64 base") {
    val store = store75.function.instructions.find(isStore(_, "E", "U8", "SYS")).get
    assertEquals(store.operands, operands("[R0.U32+UR4], R3"))
    assert(store75.function.instructions.exists(i =>
      i.mnemonic.raw == "ULDC" && i.operands == operands("UR4, c[0x0][0x170]")
    ))
    val count = 64
    // the high-bit offset variant rebases the output so that 0x180000000 + 0x80000000 + lane addresses it
    for (highBit <- Vec(false, true)) {
      val kernel =
        if (!highBit) store75
        else store75.copy(functions =
          Vec(store75.function.copy(instructions =
            (store75.function.instructions :+ statement("IADD3 R0, R0, 0x80000000, RZ", 0x48)).sortBy(_.address)
          ))
        )
      val memory = Memory()
      val output = memory.allocate(Vec.fill(128)(0xa5.toByte))
      val dummy  = memory.allocate(Vec.fill(4)(0.toByte))
      if (highBit) memory.rebase(output, GuestAddress(0x200000000L))
      val pointer: KernelTask.Argument =
        if (highBit) KernelTask.Argument.Scalar(LittleEndian.toVec(0x180000000L, 8).refine[NonEmpty[Vec[Byte]]].value)
        else KernelTask.Argument.Memory(output, 0L)
      val task = KernelTask.validated(
        kernel.name,
        LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(if (highBit) output else memory.allocate(Vec.fill(4)(0.toByte)), 0L),
          KernelTask.Argument.Memory(dummy, 0L),
          pointer,
          KernelTask.Argument.Scalar(LittleEndian.toVec(count.toLong, 4).refine[NonEmpty[Vec[Byte]]].value)
        )
      ).value
      memory.run(kernel.compile(8, 8, 8, 4).value, task).value
      assertEquals(
        memory.bytes(output),
        Vec.tabulate(128)(i => if (i < count) i.toByte else 0xa5.toByte),
        s"highBit=$highBit"
      )
    }
  }

  test("sm89 STRONG.SYS store becomes an explicit relaxed system-scope VM instruction") {
    assert(store89.function.instructions.exists(isStore(_, "E", "U8", "STRONG", "SYS")))
    assert(store89.instructions(8, 8, 8, 4).exists {
      case VMInstruction.Store(_, _, width, MemoryAccess.Global(true)) => width.raw == 8
      case _                                                           => false
    })
  }

  private def generic(text: String): Vec[VMInstruction] = {
    val address = store89.function.instructions.find(i =>
      i.mnemonic.raw == "STG" && i.modifiers.map(_.name).startsWith(Vec("E", "U8", "STRONG", "SYS"))
    ).get.address
    store89.copy(functions =
      Vec(store89.function.copy(instructions = Vec(statement(text, address.raw))))
    ).instructions(8, 8, 8, 4)
  }

  test("generic STRONG loads and stores preserve their scope without forcing global space") {
    assert(generic("LD.E.STRONG.GPU R3, [R2.64]").exists {
      case VMInstruction.Load(_, _, width, _, MemoryAccess.Generic(false)) => width.raw == 32
      case _                                                               => false
    })
    assert(generic("ST.E.STRONG.SYS [R2.64], R3").exists {
      case VMInstruction.Store(_, _, width, MemoryAccess.Generic(true)) => width.raw == 32
      case _                                                            => false
    })
  }

  test("generic STRONG.SYS load preserves generic space for runtime classification") {
    assert(generic("LD.E.STRONG.SYS R3, [R2.64]").exists {
      case VMInstruction.Load(_, _, width, _, MemoryAccess.Generic(true)) => width.raw == 32
      case _                                                              => false
    })
  }

  test("sm75 U8 SYS uniform-base store rejects other address widths and modifiers") {
    val store = store75.function.instructions.find(isStore(_, "E", "U8", "SYS")).get
    def rejected(instruction: DecodedInstruction, sm: Int = 75): Boolean =
      store75.copy(sm = sm).map(i => if (i == store) instruction else i).lower(8, 8, 8, 4).isLeft
    assert(rejected(store.rewrite(operands = operands("[R0.U32+UR4+0x4], R3"))))
    assert(rejected(store.rewrite(modifiers = Vec("E", "U16", "SYS").map(Instruction.Modifier(_)))))
    assert(rejected(store, sm = 86))
  }

  for (size <- 0 to 6) test(s"Kepler texture-pipe global load preserves size code $size") {
    val first = size match {
      case 0 => 0xef
      case 1 => -17
      case 2 => 0xcdef
      case 3 => 0xffffcdef
      case _ => 0x89abcdef
    }
    val count = if (size == 5) 2 else if (size == 6) 4 else 1
    val input = Vec(0x89abcdef, 0x12345678, 0xfedcba98, 0x76543210)
    check(
      35,
      "LDC.64 R10, c[0x0][0x160]; MOV R4, 0x89abcdef; MOV R5, 0x12345678; MOV R6, 0xfedcba98; MOV R7, 0x76543210; " +
        "STG.E.128 [R10], R4; MOV R16, 99; MOV R17, 99; MOV R18, 99; MOV R19, 99; LDG.E R16, [R10]; STG.E.128 [R10], R16; EXIT",
      (first +: input.slice(1, count)) ++ Vec.fill(4 - count)(99),
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Ldg =>
            // nvdisasm's LDG.E.P is a TEX encoding with the size in tid and the component mask in wmsk
            val base = 0x600210867f9c287dL & ~(0x1fffL << 47) & ~(15L << 34) & ~(255L << 2)
            val word = base | (size.toLong << 47) | (((1L << count) - 1) << 34) | (16L << 2)
            InstructionDecoder.decode(instruction.address, LittleEndian.toVec(word, 8).toArray, target(35)).value
          case _ => instruction
        }
    )
  }

  for (address <- Vec("[RZ.U32+UR8]", "[UR8.U32]"))
    test(s"Blackwell U32 address $address keeps the full uniform pointer") {
      check(
        120,
        "S2R R0, SR_TID.X; LDC.64 R2, c[0x0][0x160]; LDCU.64 UR8, c[0x0][0x160]; ISETP.EQ.U32.AND P0, PT, R0, 0, PT; MOV R4, 47; " +
          s"MOV R5, 89; @P0 STG.E.64.STRONG.GPU $address, R4; BAR.SYNC 0; LDG.E.64 R4, $address; IADD R4, R4, R5; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.fill(32)(136)
      )
    }

  test("CUDA generic loads accept the generated alias for GPU scope and uniform addresses") {
    check(
      75,
      "LDC.64 R4, c[0x0][0x160]; MOV R6, 42; STG.E [R4], R6; ULDC.64 UR4, c[0x0][0x160]; LD.E.STRONG.GPU R2, [UR4]; MOV R3, 7; " +
        "RED.E.MIN.STRONG.VC [UR4], R3; LD.E.STRONG.GPU R2, [UR4]; STG.E [R4], R2; EXIT",
      Vec(7),
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Ld =>
            // nvdisasm prints GPU; the generated SCO vocabulary also calls this scope VC
            val bytes = LittleEndian.toVec(0x00000004ff027980L, 8).toArray ++
              LittleEndian.toVec(0x000ea8000c114900L, 8).toArray
            InstructionDecoder.decode(instruction.address, bytes, target(75)).value
          case _ => instruction
        }
    )
  }

  test("zero-fill loads skip memory reads and preserve an inactive instruction destination") {
    check(
      86,
      "S2R R0, SR_TID.X; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; CS2R R4, SRZ; ISETP.EQ.AND P0, PT, R0, RZ, PT; " +
        "MOV R6, 17; @!P0 LDG.E R6, [R4.64], !PT; STG.E [R2.64], R6; EXIT",
      Vec(17, 0)
    )
  }

  test("local stack bounds permit a frame and preserve its spilled value") {
    check(
      86,
      "MOV R1, c[0x0][0x28]; IADD3 R1, R1, -0x20, RZ; S2R R0, SR_LMEMHIOFF; ISETP.GE.U32.AND P0, PT, R1, R0, PT; @P0 BRA `(frame); " +
        "BPT.TRAP 1; frame: MOV R4, 42; STL [R1+8], R4; LDL R5, [R1+8]; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R5; EXIT",
      Vec(42)
    )
  }

  test("local stack bounds reject an offset inside the shared-memory aperture") {
    check(
      86,
      "S2R R0, SR_LMEMHIOFF; MOV R1, 0x3ffffff0; ISETP.GE.U32.AND P0, PT, R1, R0, PT; MOV R4, 1; SEL R4, R4, RZ, P0; " +
        "LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
      Vec(0)
    )
  }
}
