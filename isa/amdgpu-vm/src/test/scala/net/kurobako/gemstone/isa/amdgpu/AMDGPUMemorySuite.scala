package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

class AMDGPUMemorySuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  for (
    (name, target, bytes, expected) <- Seq[(String, Target, Vec[Int], Instruction)](
      // ROCm's atomic probe emits buffer_inv sc0 sc1 and buffer_wbl2 sc0 sc1.
      (
        "cache invalidation with scope flags",
        Target("gfx942"),
        Vec(0, 0xc0, 0xa4, 0xe0, 0, 0, 0, 0),
        Instruction.CacheInvalidate
      ),
      ("cache writeback with scope flags", Target("gfx942"), Vec(0, 0xc0, 0xa0, 0xe0, 0, 0, 0, 0), Instruction.Wait),
      (
        "MUBUF byte store",
        Target("gfx1030"),
        Vec(0x07, 0x00, 0x60, 0xe0, 0x00, 0x02, 0x00, 0x80),
        Instruction.Store(2, MemoryAddress.Buffer(None, 0, None, 7), 1, false)
      ),
      (
        "MUBUF unsigned byte load",
        Target("gfx1030"),
        Vec(0x07, 0x00, 0x20, 0xe0, 0x00, 0x02, 0x00, 0x80),
        Instruction.Load(2, MemoryAddress.Buffer(None, 0, None, 7), 1, false, LoadPlacement.Full)
      ),
      (
        "MUBUF offen load",
        Target("gfx900"),
        Vec(0x00, 0x10, 0x50, 0xe0, 0x01, 0x07, 0x00, 0x80),
        Instruction.Load(7, MemoryAddress.Buffer(Some(1), 0, None, 0), 4, false, LoadPlacement.Full)
      ),
      (
        "MUBUF load with a scalar resource descriptor",
        Target("gfx900"),
        Vec(0x00, 0x10, 0x50, 0xe0, 0x12, 0x13, 0x05, 0x80),
        Instruction.Load(19, MemoryAddress.Buffer(Some(18), 20, None, 0), 4, false, LoadPlacement.Full)
      ),
      (
        "MUBUF short D16 low half",
        Target("gfx900"),
        Vec(0x10, 0x10, 0x90, 0xe0, 0x03, 0x07, 0x05, 0x80),
        Instruction.Load(7, MemoryAddress.Buffer(Some(3), 20, None, 16), 2, false, LoadPlacement.Low)
      ),
      (
        "MUBUF short D16 high half",
        Target("gfx900"),
        Vec(0x10, 0x10, 0x94, 0xe0, 0x03, 0x07, 0x05, 0x80),
        Instruction.Load(7, MemoryAddress.Buffer(Some(3), 20, None, 16), 2, false, LoadPlacement.High)
      ),
      (
        "DS load with an offset above the opcode bits",
        Target("gfx900"),
        Vec(0x00, 0x90, 0x6c, 0xd8, 0x00, 0x00, 0x00, 0x00),
        Instruction.Load(0, MemoryAddress.Local(0, 36864), 4, false, LoadPlacement.Full)
      ),
      (
        "DS store with the full 16-bit offset",
        Target("gfx900"),
        Vec(0xff, 0xff, 0xbe, 0xd9, 0x00, 0x01, 0x00, 0x00),
        Instruction.Store(1, MemoryAddress.Local(0, 65535), 16, false)
      ),
      (
        "DS permute",
        Target("gfx1036"),
        Vec(0x00, 0x00, 0xc8, 0xda, 0x01, 0x02, 0x00, 0x05),
        Instruction.DSPermute(5, 1, 2, 0)
      )
    )
  ) test(s"$target decodes $name") {
    assertEquals(VMCompiler.decodeInstruction(bytes.map(_.toByte), target), Right(expected))
  }

  for (
    (name, bytes) <- Seq(
      "MUBUF cache invalidation" -> Vec(0x00, 0x00, 0xfc, 0xe0, 0x00, 0x00, 0x00, 0x00),
      "stride-64 DS pairs"       -> Vec(0x00, 0x14, 0x1e, 0xd8, 0x05, 0x02, 0x01, 0x00),
      "DS bpermute"              -> Vec(0x00, 0x00, 0x7e, 0xd8, 0x04, 0x05, 0x00, 0x06)
    )
  ) test(s"gfx900 accepts $name") {
    assert(VMCompiler.decodeInstruction(bytes.map(_.toByte), Target("gfx900")).isRight)
  }

  test("DS lane permutes select lanes from byte addresses") {
    assertEquals(VMArithmetic.bpermuteSourceLane(address = 0L, offset = 0L).raw, 0)
    assertEquals(VMArithmetic.bpermuteSourceLane(4L * 37L, 0L).raw, 37)
    assertEquals(VMArithmetic.bpermuteSourceLane(4L * 69L, 0L).raw, 5)
    assertEquals(VMArithmetic.bpermuteSourceLane(address = 0L, offset = 124L).raw, 31)
    assertEquals(VMArithmetic.bpermuteSourceLane(4L * 37L, 124L).raw, 4)
    assertEquals(VMArithmetic.permuteDestinationLane(address = 0L, offset = 0L, 32).raw, 0)
    assertEquals(VMArithmetic.permuteDestinationLane(4L * 29L, 0L, 32).raw, 29)
    assertEquals(VMArithmetic.permuteDestinationLane(address = 0L, offset = 124L, 32).raw, 31)
    assertEquals(VMArithmetic.permuteDestinationLane(4L * 37L, 124L, 32).raw, 4)
  }

  test("DS operations strip the local address-space tag") {
    assertEquals(VMInterpreter.ldsOffset(0x18000018L), 0x18L)
    assertEquals(VMInterpreter.ldsOffset(0x00000018L), 0x18L)
    assertEquals(VMInterpreter.ldsOffset(0x00010018L), 0x18L)
  }

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
        Right(Instruction.Load(6, MemoryAddress.Flat(0, offset), 4, false, LoadPlacement.Full)),
        clues(target, offset)
      )
    }
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x0f, 0x70, 0xdc, 0, 6, 0, 0).map(_.toByte), Target("gfx90a")),
      Right(Instruction.Store(6, MemoryAddress.Flat(0, 4095), 4, false))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x0f, 0x09, 0xdd, 0, 6, 0, 2).map(_.toByte), Target("gfx90a")),
      Right(Instruction.Atomic(AtomicOperation.Add, 2, MemoryAddress.Flat(0, 4095), 6, 4, true))
    )
  }

  test("global scratch and RDNA4 flat offsets retain their signed interpretation") {
    assertEquals(
      VMCompiler.decodeInstruction(Vec(0xff, 0x9f, 0x50, 0xdc, 0, 0, 0x7f, 6).map(_.toByte), Target("gfx90a")),
      Right(Instruction.Load(6, MemoryAddress.Global(0, None, -1), 4, false, LoadPlacement.Full))
    )
    val scratch = VMCompiler.decodeInstruction(
      Vec(0xff, 0x5f, 0x50, 0xdc, 0, 0, 0x7f, 6).map(_.toByte),
      Target("gfx90a")
    )
    assertEquals(
      scratch.map {
        case Instruction.Load(_, MemoryAddress.Scratch(_, _, offset), _, _, _, _, _) => offset
        case other => fail(s"expected scratch load, found $other")
      },
      Right(-1)
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec(0x7c, 0, 5, 0xec, 1, 0, 0, 0, 0, 0xc0, 0xff, 0xff).map(_.toByte),
        Target("gfx1200")
      ),
      Right(Instruction.Load(1, MemoryAddress.Flat(0, -64), 4, false, LoadPlacement.Full))
    )
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
        Instruction.Load(1, MemoryAddress.Global(0, None, -64), 4, false, LoadPlacement.Full)
      )
    )
    val scalar = Vec[Byte](2, 0, 5, 0xee.toByte, 1, 0, 0, 0, 3, 0xff.toByte, 7, 0)
    assertEquals(
      VMCompiler.decodeInstruction(scalar, target),
      Right(
        Instruction.Load(1, MemoryAddress.Global(3, Some(2), 2047), 4, false, LoadPlacement.Full)
      )
    )
    val atomic = Vec[Byte](0x7c, 0x40, 0x0d, 0xee.toByte, 1, 0, 0x10, 1, 0, 0xc0.toByte, 0xff.toByte, 0xff.toByte)
    assertEquals(
      VMCompiler.decodeInstruction(atomic, target),
      Right(
        Instruction.Atomic(AtomicOperation.Add, 1, MemoryAddress.Global(0, None, -64), 2, 4, true)
      )
    )
    assert(VMCompiler.decodeInstruction(load.take(8), target).left.exists(_.raw.contains("truncated")))
  }

  test("RDNA single LDS loads and stores preserve byte offsets") {
    for (target <- Seq(Target("gfx1100"), Target("gfx1200"))) {
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](3, 7, 0x34, 0xd8.toByte, 1, 2, 0, 0), target),
        Right(Instruction.Store(2, MemoryAddress.Local(1, 1795), 4, false))
      )
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](3, 7, 0xd8.toByte, 0xd8.toByte, 1, 0, 0, 8), target),
        Right(Instruction.Load(8, MemoryAddress.Local(1, 1795), 4, false, LoadPlacement.Full))
      )
    }

  }

  test("RDNA paired LDS loads and stores preserve register pairs and scaled offsets") {
    // LLVM fixtures: ds_{load,store}_2addr[_stride64]_b{32,64}, with offset0:3 offset1:7.
    for {
      (opcode, stride) <- Seq((0x38, 1), (0x3c, 64))
      (high, width)    <- Seq[(Int, Int in (4, 8))]((0xd8, 4), (0xd9, 8))
    } {
      assertEquals(
        VMCompiler.decodeInstruction(
          Vec[Byte](3, 7, opcode.toByte, high.toByte, 1, 2, 4, 0),
          Target("gfx1100")
        ),
        Right(Instruction.StorePair(1, 2, 4, width, 3 * stride * width.raw, 7 * stride * width.raw))
      )
      assertEquals(
        VMCompiler.decodeInstruction(
          Vec[Byte](3, 7, (opcode + 0xa4).toByte, high.toByte, 1, 0, 0, 8),
          Target("gfx1100")
        ),
        Right(Instruction.LoadPair(8, 1, width, 3 * stride * width.raw, 7 * stride * width.raw))
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
          Right(Instruction.Load(
            2,
            MemoryAddress.Global(0, Option.when(scalar == 4)(4), 0),
            4,
            false,
            LoadPlacement.Full
          ))
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
        Right(Instruction.Load(2, MemoryAddress.Flat(0, 0), 4, false, LoadPlacement.Full))
      )
    }
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0x40, 0x52, 0xdc.toByte, 0, 0, 0x7c, 2), Target("gfx1100")),
      Right(Instruction.Load(2, MemoryAddress.Global(0, None, 16), 4, false, LoadPlacement.Full, coherent = true))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0, 0x6a, 0xdc.toByte, 0, 2, 0x7c, 0), Target("gfx1100")),
      Right(Instruction.Store(2, MemoryAddress.Global(0, None, 16), 4, false))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x10, 0, 0x68, 0xdc.toByte, 0, 2, 0x7c, 0), Target("gfx1100")),
      Right(Instruction.Store(2, MemoryAddress.Flat(0, 16), 4, false))
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
    }
      decoded(target, 0x02, second, third, 0xf4, 0x10, 0x00, 0x00, last) match {
        case Instruction.SLoad(destination, words, base, offset, registerOffset, _) =>
          assertEquals(
            (destination.raw, words.raw, base.raw, offset, registerOffset.map(_.raw)),
            (8, count, 4, 16L, scalarOffset)
          )
        case other => fail(s"unexpected instruction: $other")
      }
  }

  test("RDNA3 buffer offsets use the generated OFFEN field independently of cache policy") {
    for (cacheBits <- Seq(0, 0x70)) {
      // LLVM buffer_load_b32 v7,v1,s[20:23],s3 offen offset:12, optionally glc/slc/dlc.
      val bytes = Vec(0x0c, cacheBits, 0x50, 0xe0, 1, 7, 0x45, 3).map(_.toByte)
      assertEquals(
        VMCompiler.decodeInstruction(bytes, Target("gfx1100")),
        Right(Instruction.Load(7, MemoryAddress.Buffer(Some(1), 20, Some(3), 12), 4, false, LoadPlacement.Full))
      )
    }
    val indexed = Vec(0, 0, 0x50, 0xe0, 1, 7, 0x85, 3).map(_.toByte)
    assert(VMCompiler.decodeInstruction(indexed, Target("gfx1100")).isLeft)
  }

}
