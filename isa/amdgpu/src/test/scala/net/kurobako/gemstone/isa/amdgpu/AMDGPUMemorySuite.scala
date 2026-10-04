package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class AMDGPUMemorySuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("legacy flat memory uses unsigned offsets through the top encoded bit") {
    // Fixed LLVM MC encodings. GFX9/11 have 12-bit flat offsets; GFX10 has 11-bit offsets.
    for {
      (target, opcode, scalar, offsets) <- Vec(
        (Target("gfx90a"), 0x50, 0x00, Vec(2047, 2048, 4095)),
        (Target("gfx1036"), 0x30, 0x7d, Vec(1023, 1024, 2047)),
        (Target("gfx1100"), 0x50, 0x7c, Vec(2047, 2048, 4095))
      )
      offset <- offsets
    } {
      val bytes = Vec(offset & 255, offset >>> 8, opcode, 0xdc, 0, 0, scalar, 6).map(_.toByte)
      assertEquals(
        VMCompiler.decodeInstruction(bytes, target),
        Right(Instruction.FlatLoad(6, 0, 4, offset, false)),
        clues(target, offset)
      )
    }
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x0f, 0x70, 0xdc, 0, 6, 0, 0).map(_.toByte), Target("gfx90a")),
      Right(Instruction.FlatStore(0, 6, 4, 4095))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x0f, 0x09, 0xdd, 0, 6, 0, 2).map(_.toByte), Target("gfx90a")),
      Right(Instruction.GlobalAtomicBinary(AtomicBinaryOperation.Add, 2, 0, None, 6, 4, 4095, true))
    )
  }

  test("global scratch and RDNA4 flat offsets retain their signed interpretation") {
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x9f, 0x50, 0xdc, 0, 0, 0x7f, 6).map(_.toByte), Target("gfx90a")),
      Right(Instruction.GlobalLoad(6, 0, None, 4, -1, false))
    )
    val scratch = VMCompiler.decodeInstruction(
      Vec(0xff, 0x5f, 0x50, 0xdc, 0, 0, 0x7f, 6).map(_.toByte),
      Target("gfx90a")
    )
    assertEquals(
      scratch.map {
        case Instruction.ScratchLoad(_, _, _, _, offset, _) => offset
        case other                                          => fail(s"expected scratch load, found $other")
      },
      Right(-1)
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec(0x7c, 0, 5, 0xec, 1, 0, 0, 0, 0, 0xc0, 0xff, 0xff).map(_.toByte),
        Target("gfx1200")
      ),
      Right(Instruction.FlatLoad(1, 0, 4, -64, false))
    )
  }

  test("RDNA3 flat 64-bit compare exchange retains comparison pair and return destination") {
    // llvm-mc: flat_atomic_cmpswap_b64 v[8:9], v[2:3], v[4:7] glc.
    val bytes = Vec[Byte](0, 0x40, 8, 0xdd.toByte, 2, 4, 0x7c, 8)
    for (target <- Vec(Target("gfx1100"), Target("gfx1103")))
      assertEquals(
        VMCompiler.decodeInstruction(bytes, target),
        Right(Instruction.GlobalAtomicCompareSwap(8, 2, None, 6, 4, 8, 0, true))
      )
  }

  test("CDNA2 VMEM preserves its accumulator bank without changing its vector address") {
    // llvm-mc: {flat,global}_{load,store}_dwordx2 a[0:1], v[16:17] (store operands reversed).
    for {
      global <- Vec(false, true)
      store  <- Vec(false, true)
    } {
      val bytes = Vec[Byte](
        0,
        if (global) 0x80.toByte else 0,
        if (store) 0x74 else 0x54,
        0xdc.toByte,
        16,
        0,
        if (global) 0xff.toByte else 0x80.toByte,
        0
      )
      val expected = (global, store) match {
        case (false, false) => Instruction.FlatLoad(0, 16, 8, 0, false, RegisterBank.Accumulator)
        case (false, true)  => Instruction.FlatStore(16, 0, 8, 0, RegisterBank.Accumulator)
        case (true, false)  => Instruction.GlobalLoad(0, 16, None, 8, 0, false, RegisterBank.Accumulator)
        case (true, true)   => Instruction.GlobalStore(16, None, 0, 8, 0, RegisterBank.Accumulator)
      }
      assertEquals(VMCompiler.decodeInstruction(bytes, Target("gfx90a")), Right(expected))
    }
  }

  test("RDNA4 cache maintenance retains its invalidation and writeback semantics") {
    for (
      (opcode, expected) <- Seq(
        43 -> Instruction.CacheInvalidate,
        44 -> Instruction.Wait,
        79 -> Instruction.CacheWritebackInvalidate
      )
    ) {
      val word  = 0xee00007cL | (opcode.toLong << 14)
      val bytes = LittleEndian.toVec(word, 4) ++ Vec[Byte](0, 0, 12, 0, 0, 0, 0, 0)
      assertEquals(VMCompiler.decodeInstruction(bytes, Target("gfx1200")), Right(expected))
    }
  }

  test("RDNA4 memory instructions retain their third dword, signed offset, and atomic return bit") {
    val target = Target("gfx1200")
    val load   = Vec[Byte](0x7c, 0, 5, 0xee.toByte, 1, 0, 0, 0, 0, 0xc0.toByte, 0xff.toByte, 0xff.toByte)
    assertEquals(
      VMCompiler.decodeInstruction(load, target),
      Right(
        Instruction.GlobalLoad(1, 0, None, 4, -64, false)
      )
    )
    val scalar = Vec[Byte](2, 0, 5, 0xee.toByte, 1, 0, 0, 0, 3, 0xff.toByte, 7, 0)
    assertEquals(
      VMCompiler.decodeInstruction(scalar, target),
      Right(
        Instruction.GlobalLoad(1, 3, Some(2), 4, 2047, false)
      )
    )
    val atomic = Vec[Byte](0x7c, 0x40, 0x0d, 0xee.toByte, 1, 0, 0x10, 1, 0, 0xc0.toByte, 0xff.toByte, 0xff.toByte)
    assertEquals(
      VMCompiler.decodeInstruction(atomic, target),
      Right(
        Instruction.GlobalAtomicAdd(1, 0, None, 2, 4, -64, true)
      )
    )
    assert(VMCompiler.decodeInstruction(load.take(8), target).left.exists(_.raw.contains("truncated")))
  }

  test("RDNA single LDS loads and stores preserve byte offsets") {
    for (target <- Seq(Target("gfx1100"), Target("gfx1200"))) {
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](3, 7, 0x34, 0xd8.toByte, 1, 2, 0, 0), target),
        Right(Instruction.DSWrite(1, 2, None, 4, 1795, None))
      )
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](3, 7, 0xd8.toByte, 0xd8.toByte, 1, 0, 0, 8), target),
        Right(Instruction.DSRead(8, 1, 4, 1795, None))
      )
    }

  }

  test("RDNA paired LDS loads and stores preserve register pairs and scaled offsets") {
    // LLVM fixtures: ds_{load,store}_2addr[_stride64]_b{32,64}, with offset0:3 offset1:7.
    for {
      (opcode, stride) <- Seq((0x38, 1), (0x3c, 64))
      (high, width)    <- Seq[(Int, Int where Between[1, 16])]((0xd8, 4), (0xd9, 8))
    } {
      assertEquals(
        VMCompiler.decodeInstruction(
          Vec[Byte](3, 7, opcode.toByte, high.toByte, 1, 2, 4, 0),
          Target("gfx1100")
        ),
        Right(Instruction.DSWrite(1, 2, Some(4), width, 3 * stride * width.raw, Some(7 * stride * width.raw)))
      )
      assertEquals(
        VMCompiler.decodeInstruction(
          Vec[Byte](3, 7, (opcode + 0xa4).toByte, high.toByte, 1, 0, 0, 8),
          Target("gfx1100")
        ),
        Right(Instruction.DSRead(8, 1, width, 3 * stride * width.raw, Some(7 * stride * width.raw)))
      )
    }
  }

  test("VMEM uses generation-specific segment, null address and coherence fields") {
    // Fixed llvm-mc encodings for the same flat/global operations across generations.
    for (
      (target, opcode, segment, absent) <- Seq(
        (Target("gfx900"), 0x50, 0x80, 0x7f),
        (Target("gfx1010"), 0x30, 0x80, 0x7d),
        (Target("gfx1100"), 0x50, 0x00, 0x7c)
      )
    ) {
      val globalOpcode = if (target == Target("gfx1100")) opcode + 2 else opcode
      for (scalar <- Seq(absent, 4))
        assertEquals(
          VMCompiler.decodeInstruction(
            Vec[Byte](
              0,
              segment.toByte,
              globalOpcode.toByte,
              0xdc.toByte,
              0,
              0,
              scalar.toByte,
              2
            ),
            target
          ),
          Right(Instruction.GlobalLoad(2, 0, Option.when(scalar == 4)(4), 4, 0, coherent = false))
        )
      assertEquals(
        VMCompiler.decodeInstruction(
          Vec[Byte](
            0,
            0,
            opcode.toByte,
            0xdc.toByte,
            0,
            0,
            (if (target == Target("gfx900")) 0 else absent).toByte,
            2
          ),
          target
        ),
        Right(Instruction.FlatLoad(2, 0, 4, 0))
      )
    }
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0x40, 0x52, 0xdc.toByte, 0, 0, 0x7c, 2), Target("gfx1100")),
      Right(Instruction.GlobalLoad(2, 0, None, 4, 16, coherent = true))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0, 0x6a, 0xdc.toByte, 0, 2, 0x7c, 0), Target("gfx1100")),
      Right(Instruction.GlobalStore(0, None, 2, 4, 16))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0, 0x68, 0xdc.toByte, 0, 2, 0x7c, 0), Target("gfx1100")),
      Right(Instruction.FlatStore(0, 2, 4, 16))
    )
  }

  test("RDNA dependency and scalar-memory waits decode to the synchronous VM wait") {
    // Fixed llvm-mc encodings: dependency counter, ALU dependency, scalar-memory counter.
    for (
      (
        target,
        low,
        opcode
      ) <- Seq((Target("gfx1010"), 0, 0xa3), (Target("gfx1100"), 1, 0x87), (Target("gfx1200"), 0, 0xc7))
    )
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](low.toByte, 0, opcode.toByte, 0xbf.toByte), target),
        Right(Instruction.Wait)
      )
  }

  test("RDNA scalar loads decode bit-width names with their register count and byte offset") {
    // Fixed llvm-mc gfx1100/gfx1200 encodings for s_load_b32 through s_load_b512.
    for {
      (target, encodings) <- Seq(
        Target("gfx1100") -> Seq((0x02, 0x00, 1), (0x02, 0x04, 2), (0x02, 0x08, 4), (0x02, 0x0c, 8), (0x02, 0x10, 16)),
        Target("gfx1200") ->
          Seq((0x02, 0x00, 1), (0x22, 0x00, 2), (0xa2, 0x00, 3), (0x42, 0x00, 4), (0x62, 0x00, 8), (0x82, 0x00, 16))
      )
      (second, third, count) <- encodings
      (last, scalarOffset)   <- Seq(0xf8 -> None, 0x0c -> Some(6))
    } {
      val decoded = VMCompiler.decodeInstruction(
        Vec[Byte](0x02, second.toByte, third.toByte, 0xf4.toByte, 0x10, 0x00, 0x00, last.toByte),
        target
      ).fold(error => fail(error.raw), identity)
      decoded match {
        case Instruction.SLoad(destination, words, base, offset, registerOffset, _) =>
          assertEquals(
            (destination.raw, words.raw, base.raw, offset, registerOffset.map(_.raw)),
            (8, count, 4, 16, scalarOffset)
          )
        case other => fail(s"unexpected instruction: $other")
      }
    }
  }

  test("gfx942 cache operations accept their scope flags") {
    // ROCm's atomic probe emits buffer_inv sc0 sc1 and buffer_wbl2 sc0 sc1.
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0xc0.toByte, 0xa4.toByte, 0xe0.toByte, 0x00, 0x00, 0x00, 0x00),
        Target("gfx942")
      ),
      Right(Instruction.CacheInvalidate)
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0xc0.toByte, 0xa0.toByte, 0xe0.toByte, 0x00, 0x00, 0x00, 0x00),
        Target("gfx942")
      ),
      Right(Instruction.Wait)
    )
  }

  test("gfx1030 MUBUF byte store decodes its resource and offset") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x07, 0x00, 0x60, 0xe0.toByte, 0x00, 0x02, 0x00, 0x80.toByte),
        Target("gfx1030")
      ),
      Right(Instruction.BufferStore(None, 0, None, 2, 1, 7))
    )
  }

  test("gfx1030 MUBUF unsigned byte load decodes its resource and offset") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x07, 0x00, 0x20, 0xe0.toByte, 0x00, 0x02, 0x00, 0x80.toByte),
        Target("gfx1030")
      ),
      Right(Instruction.BufferLoad(2, None, 0, None, 1, 7))
    )
  }

  test("gfx900 DS decoding keeps the opcode separate from the full 16-bit offset") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x90.toByte, 0x6c, 0xd8.toByte, 0x00, 0x00, 0x00, 0x00),
        Target("gfx900")
      ),
      Right(Instruction.DSRead(0, 0, 4, 36864, None))
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0xff.toByte, 0xff.toByte, 0xbe.toByte, 0xd9.toByte, 0x00, 0x01, 0x00, 0x00),
        Target("gfx900")
      ),
      Right(Instruction.DSWrite(0, 1, None, 16, 65535, None))
    )
  }

  test("gfx900 MUBUF cache invalidation instructions validate") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x00,
          0xfc.toByte,
          0xe0.toByte,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("RDNA3 buffer offsets use the generated OFFEN field independently of cache policy") {
    for (cacheBits <- Seq(0, 0x70)) {
      // LLVM buffer_load_b32 v7,v1,s[20:23],s3 offen offset:12, optionally glc/slc/dlc.
      val bytes = Vec(0x0c, cacheBits, 0x50, 0xe0, 1, 7, 0x45, 3).map(_.toByte)
      assertEquals(
        VMCompiler.decodeInstruction(bytes, Target("gfx1100")),
        Right(Instruction.BufferLoad(7, Some(1), 20, Some(3), 4, 12))
      )
    }
    val indexed = Vec(0, 0, 0x50, 0xe0, 1, 7, 0x85, 3).map(_.toByte)
    assert(VMCompiler.decodeInstruction(indexed, Target("gfx1100")).isLeft)
  }

  test("gfx900 MUBUF offen load decodes register and offset fields") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](
          0x00,
          0x10,
          0x50,
          0xe0.toByte,
          0x01,
          0x07,
          0x00,
          0x80.toByte
        ),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoad(7, Some(1), 0, None, 4, 0))
    )
  }

  test("gfx900 MUBUF load retains its scalar resource descriptor") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x10, 0x50, 0xe0.toByte, 0x12, 0x13, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoad(19, Some(18), 20, None, 4, 0))
    )
  }

  test("gfx900 MUBUF short D16 decoding preserves resource and half selection") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x10, 0x10, 0x90.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = false))
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x10, 0x10, 0x94.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = true))
    )
  }

  test("gfx900 MUBUF short D16 loads replace only the selected half") {
    assertEquals(VMArithmetic.mergeD16(0x11223344L, 0xaabbL, high = false), 0x1122aabbL)
    assertEquals(VMArithmetic.mergeD16(0x11223344L, 0xaabbL, high = true), 0xaabb3344L)
    assertEquals(VMArithmetic.mergeD16(0xfeed11223344L, 0xffffaabbL, high = false), 0x1122aabbL)
  }

}
