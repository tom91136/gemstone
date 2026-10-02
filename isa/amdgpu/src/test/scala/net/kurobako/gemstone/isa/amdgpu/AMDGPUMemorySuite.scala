package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class AMDGPUMemorySuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

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
