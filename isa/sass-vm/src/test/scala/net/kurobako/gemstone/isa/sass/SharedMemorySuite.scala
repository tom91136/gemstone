package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class SharedMemorySuite extends munit.FunSuite {
  private def range(from: Long, count: Int): Vec[Long] = Vec.tabulate(count)(from + _)

  /** Patched probes add a 64-bit uniform pair whose high word must be ignored and whose low word wraps at 32 bits. */
  private def wraps(listing: String, sm: Int, shared: Int, output: Int, wrap: Int, input: Option[Vec[Long]] = None)(
      expected: Boolean => Vec[Long]
  ): Unit = {
    val kernel = load(sm, Fixture.listing(listing), "probe", shared.unsafe[Int >= 0])
    for ((low, high) <- Vec((0, 0), (0, 0x12345678), (wrap, 0), (wrap, -1))) {
      val inputs = input.fold(Vec.empty[Arg])(values => Vec(Arg.Buffer(words(values))))
      val run    = kernel.launch(32)((Arg.zeros(output) +: inputs) ++ Vec(Arg.u32(low), Arg.u32(high))*)
      assertEquals(run.words(0), expected(low != 0), s"low=$low high=$high")
    }
  }

  test("sm_86 LDS adds one uniform word and wraps the scaled shared address at 32 bits") {
    wraps("sharedUniformAddressSm86Patched", 86, shared = 512, output = 128, wrap = -128)(wrapped =>
      range(if (wrapped) 100 else 1100, 32)
    )
  }

  for (
    (variant, extend) <- Vec(
      ""   -> ((value: Int) => (if (value < 128) value else value - 256).toLong & 0xffffffffL),
      "U8" -> ((value: Int) => value.toLong)
    )
  )
    test(s"sm_75 LDS.U.${
        if (variant.isEmpty) "S8" else variant
      } adds the uniform address and wraps shared offsets at 32 bits") {
      wraps(s"sharedUniformAddress${variant}Sm75Patched", 75, shared = 512, output = 128, wrap = -128)(wrapped =>
        Vec.tabulate(32)(i => extend((if (wrapped) 100 else 200) + i))
      )
    }

  test("sm_86 LDGSTS wraps a scalar uniform offset in its shared destination") {
    wraps("sharedUniformCopySm86Patched", 86, shared = 256, output = 256, wrap = -128, Some(range(2100, 32)))(wrapped =>
      if (wrapped) range(2100, 32) ++ range(1100, 32) else range(100, 32) ++ range(2100, 32)
    )
  }

  for (
    (store, listing, sm) <- Vec(
      ("STS.U8", "sharedUniformStoreSm86Patched", 86),
      ("STS", "sharedUniformWordStoreSm89Patched", 89)
    )
  )
    test(s"sm_$sm $store wraps a scalar uniform offset and ignores the adjacent uniform register") {
      wraps(listing, sm, shared = 512, output = 256, wrap = -128)(wrapped =>
        if (wrapped) range(132, 32) ++ range(100, 32) else range(100, 32) ++ range(132, 32)
      )
    }

  test("sm_89 STS.128 wraps a scalar uniform offset and ignores the adjacent uniform register") {
    wraps("sharedUniformVectorStoreSm89Patched", 89, shared = 1024, output = 1024, wrap = -512) { wrapped =>
      for {
        block <- Vec(0, 1)
        lane  <- Vec.tabulate(32)(identity)
        word  <- Vec.tabulate(4)(identity)
      } yield lane + 100L * (word + 1) + (if (block == (if (wrapped) 0 else 1)) 1000L else 0L)
    }
  }

  test("compiler-produced sm_86 LDGSTS adds the 64-bit UR pair to its global base") {
    val kernel = load(86, Fixture.listing("sharedUniformAddressCudaSm86Patched"), "probe", 128)
    assert(kernel.instructions(8, 8, 4, 4).exists {
      case VMInstruction.Load(_, _, width, _, MemoryAccess.Global(false)) => width == 32
      case _                                                              => false
    })
    assert(kernel.instructions(8, 8, 4, 4).exists {
      case VMInstruction.Store(_, _, width, MemoryAccess.Shared) => width == 32
      case _                                                     => false
    })
    val input = words(range(0x12340000L, 36))
    // a nonzero low half moves the address with a zero high half; the negative pair is offset by the +4-byte base
    for ((offset, low, high, first) <- Vec((0L, 8, 0, 0x12340002L), (4L, -4, -1, 0x12340000L))) {
      val run =
        kernel.launch(32)(Arg.zeros(128), Arg.Buffer(input, offset.unsafe[Long >= 0]), Arg.u32(low), Arg.u32(high))
      assertEquals(run.words(0), range(first, 32))
    }
  }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm compiler LDS.U.32 preserves all data bits across a shared-memory barrier") {
      val kernel    = load(sm, Fixture.listing("lds32Sm50"), "probe", 8)
      val modifiers = Vec("U", "32")
      assert(kernel.function.instructions.exists(i =>
        i.mnemonic.raw == "LDS" && i.modifiers.map(_.name) == modifiers
      ))
      Vec(0, 1, Int.MinValue, -1, 0).foreach { input =>
        assertEquals(kernel.launch(1)(Arg.zeros(4), Arg.u32(input)).words(0), Vec(input.toLong & 0xffffffffL))
      }
    }

  test("a shared-memory load into RZ still emits a real load and discards only its result") {
    val kernel = program(50, "MOV R4, RZ; LDS.U.32 RZ, [R4+0x4]; EXIT", 8)
    val loads  = kernel.instructions(8, 4).collect {
      case VMInstruction.Load(destination, _, width, _, MemoryAccess.Shared) if width == 32 => destination
    }
    val image       = kernel.module(Vec(8, 4))
    val layout      = VMRegisters.Layout.of(image.kernels.head, kernel.functions)
    val temporaries = Vec.tabulate(VMRegisters.TemporaryCount)(layout.temporary(_))
    assertEquals(loads.size, 1)
    assert(temporaries.contains(loads.head), s"an RZ load must target a temporary: ${loads.head}")
  }

  test("a shared access masks the cluster rank and rebases the address in one step") {
    val kernel = program(50, "MOV R4, RZ; LDS.U.32 R5, [R4+0x4]; EXIT", 8)
    val steps  = kernel.instructions(8, 4)
    val mask   = Value.Immediate(VMAddressSpace.SharedOffsetMask)
    val base   = Value.Immediate(VMAddressSpace.SharedBase)
    assertEquals(
      steps.count {
        case VMInstruction.Logic3(_, _, `mask`, `base`, table, width) => table == 0xea && width == 64
        case _                                                        => false
      },
      1
    )
    assert(!steps.exists {
      case VMInstruction.IntegerBinary(right = `mask` | `base`) => true
      case _                                                    => false
    })
  }

  for (sm <- Vec(50, 60))
    test(s"sm$sm LDS.U.16 zero-extends shared halfwords and ignores adjacent bytes") {
      check(
        sm,
        "S2R R0, SR_TID.X; SHL R1, R0, 2; IADD32I R4, R0, 0xdead8000; STS [R1], R4; MEMBAR.GL.IVALLD; LDS.U.16 R4, [R1]; " +
          "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(0x8000 + _),
        shared = 128
      )
    }

  test("sm_75 LDS.U combines scaled lane and uniform offsets for a full word") {
    check(
      75,
      "MOV R0, 7; UMOV UR5, 12; MOV R6, 0x89abcdef; STS [0x40], R6; LDS.U R6, [R0.X4+UR5+0x18]; LDC.64 R2, c[0x0][0x160]; " +
        "STG.E [R2.64], R6; EXIT",
      Vec(0x89abcdef),
      shared = 128
    )
  }

  test("CUDA byte loads read one unsigned byte from shared memory") {
    for (sm <- Vec(50, 60))
      check(
        sm,
        "MOV R4, 0x123456fe; STS [0x18], R4; LDS.U.U8 R8, [0x18]; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R8; EXIT",
        Vec(254),
        shared = 28,
        rewrite = instruction =>
          instruction.operation.form match {
            case _: Form.Lds =>
              // nvdisasm prints LDS.U.U8; the recovered SIZE vocabulary spells unsigned bytes as 8
              val bytes = LittleEndian.toVec(0xef4810000187ff08L, 8).toArray ++ Array[Byte](0xfd.toByte, 0x07, 0x00)
              InstructionDecoder.decode(instruction.address, bytes, target(sm)).value
            case _ => instruction
          }
      )
  }

  test("register and absolute shared addresses refer to the same storage on legacy and modern SASS") {
    for (sm <- Vec(35, 86))
      check(
        sm,
        "MOV R4, 31; STS [RZ], R4; LDS R5, [0x0]; MOV R6, 11; STS [0x4], R6; MOV R7, 4; LDS R6, [R7]; IADD R8, R5, R6; " +
          "LDC.64 R2, c[0x0][0x160]; STG.E [R2], R8; EXIT",
        Vec(42),
        shared = 8
      )
  }

  test("LDS.U.128 reads a four-register vector from shared memory") {
    check(
      86,
      "MOV R4, 1; MOV R5, 2; MOV R6, 3; MOV R7, 4; STS.128 [0x0], R4; LDS.U.128 R8, [0x0]; LDC.64 R2, c[0x0][0x160]; STG.E.128 [R2.64], R8; EXIT",
      Vec(1, 2, 3, 4),
      shared = 16
    )
  }

  test("shared 64-bit stores add uniform address offsets before storing both words") {
    check(
      90,
      "LDC.64 R10, c[0x0][0x160]; MOV R0, 4; UMOV UR4, 4; MOV R4, 123; MOV R5, 456; STS.64 [R0+UR4], R4; ACQBULK; ACQSHMINIT; " +
        "FENCE.VIEW.ASYNC.S; LDS.64 R6, [8]; STG.E.64 [R10], R6; EXIT",
      Vec(123, 456),
      shared = 16
    )
  }

  for ((operation, initial, value, expected) <- Vec(("MIN", "RZ", "-R0, RZ", -31), ("MAX", "R5", "R0, -16", 15)))
    test(s"signed shared atomic ${
        if (operation == "MIN") "minimum includes negative lane values" else "maximum compares both signs"
      }") {
      check(
        86,
        s"S2R R0, SR_TID.X; ISETP.EQ.U32.AND P0, PT, R0, RZ, PT; MOV R5, 0x80000000; @P0 STS [RZ], $initial; BAR.SYNC 0; " +
          s"IADD3 R4, $value, RZ; ATOMS.$operation.S32 RZ, [RZ], R4; BAR.SYNC 0; LDS R4, [RZ]; LDC.64 R2, c[0x0][0x160]; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.fill(32)(expected),
        shared = 4
      )
    }

  test("shared-window high register constructs a generic alias of explicit shared storage") {
    check(
      100,
      """S2R R0, SR_TID.X
        |MOV R2, 16
        |IMAD R2, R0, 4, R2
        |S2R R3, SR_SWINHI
        |MOV R4, 42
        |ST.E [R2.64], R4
        |LDS R5, [R2]
        |MOV R4, 99
        |STS [R2], R4
        |LD.E R6, [R2.64]
        |IADD3 R4, R5, R6, RZ
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec.fill(4)(141),
      shared = 32
    )
  }

  test("sm_35 cvta shared base is nonnull and generic LD/ST dispatches to shared memory") {
    val kernel = load(35, Fixture.listing("sharedPointerSm35"), "shared_base_probe", 512)
    Vec(true, false).foreach { load =>
      assert(kernel.instructions(8, 8).exists {
        case VMInstruction.Load(_, _, width, _, MemoryAccess.Generic(false)) if load => width == 32
        case VMInstruction.Store(_, _, width, MemoryAccess.Generic(false)) if !load  => width == 32
        case _                                                                       => false
      })
    }
    val run = kernel.launch(128)(Arg.zeros(256 * 8), Arg.Buffer(words(range(100, 128))))
    assertEquals(run.words(argument = 0, width = 8), Vec.fill(128)(0x10000000L) ++ range(107, 128))
  }

  test("sm_89 opaque generic shared pointers alias direct shared-space accesses") {
    val run = load(89, Fixture.listing("sharedPointerSm89"), "shared_pointer_probe", 16)
      .launch(4)(Arg.zeros(16), Arg.u32(0x1234abcdL), Arg.u32(0x89abcdefL))
    val result = run.words(0)
    assert(result.take(2).exists(_ != 0L), s"expected a nonzero shared generic pointer: $result")
    assertEquals(result.drop(2), Vec(0x1234abcdL, 0x89abcdefL))
  }
}
