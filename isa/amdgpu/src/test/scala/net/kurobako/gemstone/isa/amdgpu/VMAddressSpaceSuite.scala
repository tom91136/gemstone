package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VMAddressSpaceSuite extends munit.FunSuite {
  private def execute(
      operations: Vec[Instruction],
      globals: Array[Byte],
      queueEnabled: Boolean = false
  ): Either[Fault, Unit] = {
    val source = CodeObjectKernel(
      name = "apertures",
      target = Target("gfx1036"),
      kernargSize = 0,
      groupSegmentSize = 65536,
      privateSegmentSize = 4096,
      computePgmRsrc2 = 0,
      kernelCodeProperties = if (queueEnabled) EncodingConstants.KernelCodeProperty.ENABLE_SGPR_QUEUE_PTR else 0,
      code = Vec.empty,
      readOnlyAddress = 256L,
      readOnly = LittleEndian.toArray(0x78563412L, 4),
      globalMemory = Array(512L -> globals)
    )
    val instructions = operations :+ Instruction.End
    val offsets      = Vec.tabulate(instructions.size)(_ * 4)
    val kernel       = Kernel(source, instructions, offsets.zipWithIndex.toMap, offsets, 0)
    val task         = KernelTask.validated("apertures", Dimensions.One, Dimensions.One, Vec.empty).toOption.get
    executeKernel(kernel, task, TestMemory())
  }

  test("CDNA2 flat and global accumulator transfers preserve vector registers") {
    for (global <- Vec(false, true)) {
      val globals  = Array.ofDim[Byte](32)
      val original = 0x123456789abcdef0L
      val sentinel = 0x7654321089abcdefL
      LittleEndian.write(original, globals, 0, 8)
      def decode(store: Boolean): Instruction = {
        // llvm-mc gfx90a: transfer a[0:1] through address v[16:17].
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
        VMCompiler.decodeInstruction(bytes, Target("gfx90a")).fold(error => fail(error.raw), identity)
      }
      val operations = Vec(
        Instruction.VMove64(0, InstructionSource.Constant(sentinel)),
        Instruction.VMove64(16, InstructionSource.Constant(512)),
        decode(false),
        Instruction.VMove64(16, InstructionSource.Constant(520)),
        decode(true),
        Instruction.VAccumulatorRead(2, 0),
        Instruction.VAccumulatorRead(3, 1),
        Instruction.FlatStore(16, 2, 8, 8),
        Instruction.FlatStore(16, 0, 8, 16)
      )
      assertEquals(execute(operations, globals), Right(()))
      assertEquals(
        Vec.tabulate(4)(index => LittleEndian.decode(globals, index * 8, 8)),
        Vec(original, original, original, sentinel)
      )
    }
  }

  test("RDNA3 flat 64-bit compare exchange compares both words and returns the old pair") {
    for {
      target <- Vec(Target("gfx1100"), Target("gfx1103"))
      equal  <- Vec(false, true)
    } {
      val globals     = Array.ofDim[Byte](16)
      val original    = 0x123456789abcdef0L
      val replacement = 0x76543210fedcba98L
      LittleEndian.write(original, globals, 0, 8)
      // llvm-mc: flat_atomic_cmpswap_b64 v[8:9], v[2:3], v[4:7] glc.
      val operation = VMCompiler.decodeInstruction(Vec[Byte](0, 0x40, 8, 0xdd.toByte, 2, 4, 0x7c, 8), target)
        .fold(error => fail(error.raw), identity)
      val operations = Vec(
        Instruction.VMove64(2, InstructionSource.Constant(512)),
        Instruction.VMove64(4, InstructionSource.Constant(replacement)),
        Instruction.VMove64(6, InstructionSource.Constant(if (equal) original else original ^ (1L << 32))),
        operation,
        Instruction.FlatStore(2, 8, 8, 8)
      )
      assertEquals(execute(operations, globals), Right(()))
      assertEquals(LittleEndian.decode(globals, 0, 8), if (equal) replacement else original)
      assertEquals(LittleEndian.decode(globals, 8, 8), original)
    }
  }

  test("flat pointers distinguish low ELF globals, LDS and private memory at the same offset") {
    val globals    = Array.ofDim[Byte](32)
    val operations = Vec(
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.LocalBase + 256)),
      Instruction.VMove(2, InstructionSource.Constant(0x3333)),
      Instruction.FlatStore(0, 2, 4, 0),
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.PrivateBase + 256)),
      Instruction.VMove(2, InstructionSource.Constant(0x4444)),
      Instruction.FlatStore(0, 2, 4, 0),
      Instruction.VMove64(0, InstructionSource.Constant(256)),
      Instruction.FlatLoad(2, 0, 4, 0),
      Instruction.VMove64(4, InstructionSource.Constant(512)),
      Instruction.FlatStore(4, 2, 4, 0),
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.LocalBase + 256)),
      Instruction.FlatLoad(2, 0, 4, 0, true),
      Instruction.FlatStore(4, 2, 4, 4),
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.PrivateBase + 256)),
      Instruction.FlatLoad(2, 0, 4, 0),
      Instruction.FlatStore(4, 2, 4, 8),
      Instruction.FlatLoad(2, 4, 4, 0, true),
      Instruction.FlatStore(4, 2, 4, 12)
    )
    assertEquals(execute(operations, globals), Right(()))
    assertEquals(
      Vec.tabulate(4)(index => LittleEndian.decodeInt32(globals, index * 4)),
      Vec(0x78563412, 0x3333, 0x4444, 0x78563412)
    )
  }

  test("flat segment atomics preserve return values and update only their addressed segment") {
    for (base <- Vec(VMAddressSpace.LocalBase, VMAddressSpace.PrivateBase)) {
      val globals    = Array.ofDim[Byte](8)
      val operations = Vec(
        Instruction.VMove64(0, InstructionSource.Constant(base + 256)),
        Instruction.VMove(2, InstructionSource.Constant(41)),
        Instruction.FlatStore(0, 2, 4, 0),
        Instruction.VMove(2, InstructionSource.Constant(1)),
        Instruction.GlobalAtomicAdd(3, 0, None, 2, 4, 0, true),
        Instruction.FlatLoad(2, 0, 4, 0),
        Instruction.VMove64(4, InstructionSource.Constant(512)),
        Instruction.FlatStore(4, 3, 4, 0),
        Instruction.FlatStore(4, 2, 4, 4)
      )
      assertEquals(execute(operations, globals), Right(()))
      assertEquals(LittleEndian.decodeInt32(globals, 0), 41)
      assertEquals(LittleEndian.decodeInt32(globals, 4), 42)
    }
  }

  test("enabled queue SGPR exposes matching local and private aperture bases") {
    val globals = Array.ofDim[Byte](8)
    // ROCr queue ABI stores the two aperture upper words at byte offsets 64 and 68.
    val operations = Vec(
      Instruction.SLoad(2, 2, 0, 64, None),
      Instruction.VMove(0, InstructionSource.Constant(256)),
      Instruction.VMove(1, InstructionSource.Scalar(2)),
      Instruction.VMove(2, InstructionSource.Constant(0x3333)),
      Instruction.FlatStore(0, 2, 4, 0),
      Instruction.VMove(1, InstructionSource.Scalar(3)),
      Instruction.VMove(2, InstructionSource.Constant(0x4444)),
      Instruction.FlatStore(0, 2, 4, 0),
      Instruction.VMove64(4, InstructionSource.Constant(512)),
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.LocalBase + 256)),
      Instruction.FlatLoad(2, 0, 4, 0),
      Instruction.FlatStore(4, 2, 4, 0),
      Instruction.VMove64(0, InstructionSource.Constant(VMAddressSpace.PrivateBase + 256)),
      Instruction.FlatLoad(2, 0, 4, 0),
      Instruction.FlatStore(4, 2, 4, 4)
    )
    assertEquals(execute(operations, globals, true), Right(()))
    assertEquals(LittleEndian.decodeInt32(globals, 0), 0x3333)
    assertEquals(LittleEndian.decodeInt32(globals, 4), 0x4444)
    assert(execute(Vec(Instruction.SLoad(2, 1, 0, 60, None)), globals, true).isLeft)
  }

  test("flat aperture bounds cannot fall through into global memory") {
    for ((base, size) <- Vec(VMAddressSpace.LocalBase -> 65536, VMAddressSpace.PrivateBase -> 4096)) {
      val globals    = Array.ofDim[Byte](8)
      val operations = Vec(
        Instruction.VMove64(0, InstructionSource.Constant(base + size - 2)),
        Instruction.FlatLoad(2, 0, 4, 0)
      )
      assert(execute(operations, globals).isLeft)
    }
  }

  test("architectural aperture sources and SH_MEM_BASES agree for complete and sliced hardware reads") {
    val target     = Target("gfx1036")
    val definition = SOPKInstructionDefs.SGetregB32.meta.encodings.find(_.isa == target.isa).get
    val layout     = InstructionLayout.Catalogue.forFormat(target, EncodingFormat.SOPK).get
    val hwreg      = OperandEncoding.Catalogue.forKind(target.isa, OperandKind.HWREG).get
    val fields     = hwreg.fields.map(value => value.field.name -> value).toMap
    val id         = fields(EncodingField.ID).names("HW_REG_SH_MEM_BASES").toLong
    for ((offset, width) <- Vec(0 -> 32, 0 -> 16, 16 -> 16, 4 -> 8)) {
      val selector = fields(EncodingField.ID).field.put(BigInt(id)).get |
        fields(EncodingField.OFFSET).field.put(BigInt(offset)).get |
        fields(EncodingField.SIZE).field.put(BigInt(width - 1)).get
      val word = layout.prefixes.head |
        layout.byName(EncodingField.OP).put(BigInt(definition.opcode)).get |
        layout.byName(EncodingField.SDST).put(BigInt(22)).get |
        layout.byName(EncodingField.SIMM16).put(selector).get
      val expected = ((VMAddressSpace.HardwareBases >>> offset) & (-1L >>> (64 - width))).toInt
      assertEquals(
        VMCompiler.decodeInstruction(LittleEndian.toVec(word.toLong, 4), target),
        Right(Instruction.SMoveImmediate(22, expected))
      )
    }
    assertEquals(VMAddressSpace.HardwareBases & 0xffffL, VMAddressSpace.PrivateBase >>> 48)
    assertEquals(VMAddressSpace.HardwareBases >>> 16, VMAddressSpace.LocalBase >>> 48)
  }
}
