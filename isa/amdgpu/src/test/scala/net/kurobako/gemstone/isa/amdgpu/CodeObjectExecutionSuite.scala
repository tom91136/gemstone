package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.AddressSpace.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class CodeObjectExecutionSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("dispatch pointer exposes launch dimensions and segment sizes to OpenMP kernels") {
    // LLVM gfx1030: copy the first eight AQL packet dwords to the output.
    // OpenMP uses grid_size_x at byte 12 as its loop stride.
    val words = Vec[Long](
      0xf40c0200L, 0xfa000000L, 0xf4040101L, 0xfa000000L, 0xbf8cc07fL,
      0x7e000280L, 0x7e020208L, 0x7e040209L, 0x7e06020aL, 0x7e08020bL,
      0x7e0a020cL, 0x7e0c020dL, 0x7e0e020eL, 0x7e10020fL,
      0xdc788000L, 0x00040100L, 0xdc788010L, 0x00040500L, 0xbf810000L
    )
    val kernel = CodeObjectKernel(
      name = "dispatch_packet",
      target = Target("gfx1030"),
      kernargSize = 8,
      groupSegmentSize = 32,
      privateSegmentSize = 16,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0x0a,
      code = words.flatMap(LittleEndian.toVec(_, 4))
    )
    val output = AllocationId(1)
    val memory = TestMemory()
    memory.allocate(output, 32)
    val task = KernelTask.validated(
      "dispatch_packet",
      testDimensions(2, 3, 4),
      testDimensions(2, 2, 2),
      Vec(KernelTask.Argument.Memory(output, 0L)),
      globalSize = Some(GlobalDimensions(3L, 5L, 7L)),
      privateSegmentBytes = 64,
      groupSegmentBytes = 128
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), task, memory), Right(()))
    assertEquals(memory.getInt(output, 4), 2 | (3 << 16))
    assertEquals(memory.getInt(output, 8), 4)
    assertEquals(memory.getInt(output, 12), 3)
    assertEquals(memory.getInt(output, 16), 5)
    assertEquals(memory.getInt(output, 20), 7)
    assertEquals(memory.getInt(output, 24), 64)
    assertEquals(memory.getInt(output, 28), 160)
  }

  test("a linked code object fills its GOT slot from an absolute 64-bit relocation") {
    // HIP reaches a device global through a .got slot when the symbol is not
    // directly addressable, so the slot has to hold the symbol address before
    // the first kernel reads it.
    val indexed = ELF.index(Vec.unsafeWrapArray(minimalCodeObject(globalPointer = true))).toOption.get
    val kernel  = indexed.kernel("kernel").toOption.get
    assertEquals(
      kernel.globalMemory.find(_._1 == 0x4000L).map(_._2.take(8).toSeq),
      Some(LittleEndian.toVec(0x3000L, 8))
    )
  }

  test("gfx1036 preserves a vcc_hi scratch counter across a vector comparison") {
    // HIP kernels keep loop counters in vcc_hi (s107) on wave32 targets. A
    // vector comparison only defines the wave's lane bits of VCC, so the
    // counter has to survive one. Without that the loop below never reaches
    // zero and the dispatch only ends by exhausting its instruction budget.
    val code = Vec[Byte](
      0x8a.toByte,
      0x03.toByte,
      0xeb.toByte,
      0xbe.toByte, // s_mov_b32 vcc_hi, 10
      0x80.toByte,
      0x18.toByte,
      0x1a.toByte,
      0x7c.toByte, // v_cmp_neq_f32_e32 vcc_lo, 0, v12
      0x6b.toByte,
      0xc1.toByte,
      0x6b.toByte,
      0x81.toByte, // s_add_i32 vcc_hi, vcc_hi, -1
      0x6b.toByte,
      0x80.toByte,
      0x07.toByte,
      0xbf.toByte, // s_cmp_lg_u32 vcc_hi, 0
      0xfc.toByte,
      0xff.toByte,
      0x85.toByte,
      0xbf.toByte, // s_cbranch_scc1 -4 (displacement is in dwords)
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte // s_endpgm
    )
    val kernel = CodeObjectKernel(
      name = "vcc_scratch_counter",
      target = Target("gfx1036"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0x880,
      kernelCodeProperties = 0,
      code = code
    )
    val dispatch = KernelTask.validated(
      "vcc_scratch_counter",
      testDimensions(32, 1, 1),
      Dimensions.One,
      Vec.empty
    ).toOption.get
    val control = TaskControl(TaskControl.Limits(
      maxInstructions = 4096L
    ))
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory(), control), Right(()))
  }

  test("self-contained linked entries do not decode unrelated unsupported kernels") {
    val end         = Vec[Byte](0x00, 0x00, 0x81.toByte, 0xbf.toByte)
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = CodeObjectKernel(
      name = "selected",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = end,
      codeAddress = 0x1004L,
      textAddress = 0x1000L,
      text = unsupported ++ end,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 4)
    )
    val dispatch = KernelTask.validated(
      "selected",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
    assert(VMCompiler.compile(kernel.copy(code = unsupported, codeAddress = 0x1000L)).isLeft)
    val truncated = Vec[Byte](1, 2)
    assert(VMCompiler.compile(kernel.copy(
      code = truncated,
      text = unsupported ++ truncated,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 2)
    )).left.exists(_.message.contains("byte 4")))
    // A direct branch leaving the entry must use linked decoding and preserve the target failure.
    val branch = Vec[Byte](0x00, 0x00, 0x82.toByte, 0xbf.toByte)
    assert(VMCompiler.compile(kernel.copy(
      code = branch,
      codeAddress = 0x1000L,
      text = branch ++ unsupported
    )).isLeft)
  }

  for (architecture <- List("gfx900", "gfx1030")) {
    test(s"$architecture decodes an unreachable linked s_trap without executing it") {
      // The entry branches into another function, requiring linked decoding.
      // The trap models an uncalled __cxa_pure_virtual at the start of .text.
      val trap   = LittleEndian.toVec(0xbf920002L, 4)
      val branch = LittleEndian.toVec(0xbf820000L, 4)
      val end    = LittleEndian.toVec(0xbf810000L, 4)
      val kernel = CodeObjectKernel(
        name = "skip_linked_trap",
        target = architecture.unsafe[Target],
        kernargSize = 0,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 0,
        kernelCodeProperties = 0,
        code = branch,
        codeAddress = 0x1004L,
        textAddress = 0x1000L,
        text = trap ++ branch ++ end,
        functionSizes = Map(0x1000L -> 4, 0x1004L -> 4, 0x1008L -> 4)
      )
      val dispatch = KernelTask.validated(
        "skip_linked_trap",
        Dimensions.One,
        Dimensions.One,
        Vec.empty
      ).toOption.get
      assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
    }

    for (trapId <- List(2, 0x3141)) test(s"$architecture executed s_trap $trapId is a runtime fault") {
      val code = Vec[Long](0xbf920000L | trapId.toLong, 0xbf810000L)
        .flatMap(word => LittleEndian.toVec(word, 4))
      val kernel = CodeObjectKernel(
        name = "execute_trap",
        target = architecture.unsafe[Target],
        kernargSize = 0,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 0,
        kernelCodeProperties = 0,
        code = code,
        codeAddress = 0x1000L
      )
      val dispatch = KernelTask.validated(
        "execute_trap",
        Dimensions.One,
        Dimensions.One,
        Vec.empty
      ).toOption.get
      assertEquals(
        executeKernel(compileKernel(kernel), dispatch, TestMemory()),
        Left(Fault.Backend(s"AMDGPU s_trap $trapId at pc 0x1000"))
      )
    }
  }

  test("proven local PC-relative branches keep unrelated functions isolated") {
    val words = Vec[Long](0xbea01f00L, 0x812284ffL, 0x00000010L, 0x80202220L,
      0x82218021L, 0xbe802020L, 0xbf810000L)
    val code        = words.flatMap(word => LittleEndian.toVec(word, 4))
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = CodeObjectKernel(
      name = "local_jump",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = code,
      codeAddress = 0x1004L,
      textAddress = 0x1000L,
      text = unsupported ++ code,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> code.size)
    )
    val dispatch = KernelTask.validated(
      "local_jump",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
    val crossing = kernel.copy(
      codeAddress = 0xfffffff4L,
      textAddress = 0xfffffff0L,
      functionSizes = Map(0xfffffff0L -> 4, 0xfffffff4L -> code.size)
    )
    assertEquals(executeKernel(compileKernel(crossing), dispatch, TestMemory()), Right(()))
    val aliased = words.updated(1, 0x812084ffL).flatMap(word => LittleEndian.toVec(word, 4))
    assert(VMCompiler.compile(kernel.copy(code = aliased, text = unsupported ++ aliased)).isLeft)

    // Entry in the middle invalidates the proof even though the instruction bytes still match.
    val intoMiddle = LittleEndian.toVec(0xbf820005L, 4) ++ code
    assert(VMCompiler.compile(kernel.copy(
      code = intoMiddle,
      text = unsupported ++ intoMiddle,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> intoMiddle.size)
    )).isLeft)
    // An unproven indirect target must still require the complete linked program.
    val unknown = Vec[Long](0xbe802024L, 0xbf810000L).flatMap(word =>
      LittleEndian.toVec(word, 4)
    )
    assert(VMCompiler.compile(kernel.copy(
      code = unknown,
      text = unsupported ++ unknown,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> unknown.size)
    )).isLeft)
  }

  test("gfx1030 buffer short stores keep their two-byte width") {
    val code = Vec[Byte](0x00, 0x10, 0x68, 0xe0.toByte, 0x01, 0x00, 0x01, 0x80.toByte)
    assertEquals(
      VMCompiler.decodeInstruction(code, Target("gfx1030")),
      Right(Instruction.BufferStore(Some(1), 4, None, 0, 2, 0))
    )
    // Point the buffer descriptor at allocation 1 and store v0's low half at byte offset 1.
    val words = Vec[Long](0xbe840380L, 0xbe8503ffL, 0x00004000L, 0xbe860388L, 0xbe870380L,
      0x7e0002ffL, 0x12345678L, 0x7e020281L, 0xe0681000L, 0x80010001L, 0xbf810000L)
    val kernel = CodeObjectKernel(
      name = "store_short",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = words.flatMap(word => LittleEndian.toVec(word, 4))
    )
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    memory.putInt(allocation, 0, 0xa5a5a5a5)
    memory.putInt(allocation, 4, 0xa5a5a5a5)
    val dispatch = KernelTask.validated(
      "store_short",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0xa55678a5)
    assertEquals(memory.getInt(allocation, 4), 0xa5a5a5a5)
    memory.putInt(allocation, 0, 0xa5a5a5a5)
    val high = kernel.copy(code =
      words.updated(8, 0xe06c1000L).flatMap(word =>
        LittleEndian.toVec(word, 4)
      )
    )
    assertEquals(executeKernel(compileKernel(high), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0xa51234a5)
    assertEquals(memory.getInt(allocation, 4), 0xa5a5a5a5)
  }

  test("gfx1030 scratch buffer scalar offsets use wave-interleaved addresses") {
    // LLVM uses a 0x20080 scalar scratch offset for private byte 4100 on a
    // wave32 target. The instruction's immediate offset remains per-lane.
    val words = Vec[Long](
      0xbe8403ffL, 0x00020080L, 0x7e0002ffL, 0x12345678L,
      0xe0700000L, 0x04000000L, 0x7e0002ffL, 0x0fedcba9L,
      0xe0700004L, 0x04000000L, 0xe0300000L, 0x04000100L,
      0xe0300004L, 0x04000200L,
      0xbf8c3f70L, 0xbe800380L, 0xbe8103ffL, 0x00004000L,
      0xbe820388L, 0xbe830380L, 0xe0700000L, 0x80000100L,
      0xe0700004L, 0x80000200L,
      0xbf810000L
    )
    val kernel = CodeObjectKernel(
      name = "scratch_swizzle",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 4752,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 1,
      code = words.flatMap(word => LittleEndian.toVec(word, 4))
    )
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      "scratch_swizzle",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0x12345678)
    assertEquals(memory.getInt(allocation, 4), 0x0fedcba9)
  }

  test("gfx1030 VOP3 frexp mantissa applies the absolute source modifier") {
    // The optimized Polyregion math kernel emits v_frexp_mant_f32_e64 with |src|.
    // Start with -6.0f so dropping the modifier changes the stored result.
    val words = Vec[Long](
      0x7e0002ffL, 0xc0c00000L, 0xd5c00101L, 0x00000100L,
      0xbe800380L, 0xbe8103ffL, 0x00004000L, 0xbe820388L,
      0xbe830380L, 0xe0700000L, 0x80000100L, 0xbf810000L
    )
    val kernel = CodeObjectKernel(
      name = "frexp_mantissa_modified",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = words.flatMap(word => LittleEndian.toVec(word, 4))
    )
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      "frexp_mantissa_modified",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), java.lang.Float.floatToRawIntBits(0.75f))
  }

  test("gfx1030 VOP3 f32 to f64 conversion applies the absolute source modifier") {
    val words = Vec[Long](
      0x7e0002ffL, 0xc0c00000L, 0xd5900102L, 0x00000100L,
      0xbe800380L, 0xbe8103ffL, 0x00004000L, 0xbe820388L,
      0xbe830380L, 0xe0740000L, 0x80000200L, 0xbf810000L
    )
    val kernel = CodeObjectKernel(
      name = "f64_from_f32_modified",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = words.flatMap(word => LittleEndian.toVec(word, 4))
    )
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      "f64_from_f32_modified",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0)
    assertEquals(memory.getInt(allocation, 4), 0x40180000)
  }

  test("gfx900 transfers control through a linked device-function call and return") {
    // s_mov_b32 s4, 0x1018; s_mov_b32 s5, 0; s_swappc s[2:3], s[4:5]
    // calls the second text function. It writes s0 and returns through the
    // continuation saved by s_swappc before the caller reaches s_endpgm.
    // The four zero bytes between the functions model the alignment gap
    // emitted by the linker after the caller's s_endpgm.
    val code = Vec[Byte](
      0xff.toByte,
      0x00,
      0x84.toByte,
      0xbe.toByte,
      0x18,
      0x10,
      0x00,
      0x00,
      0x80.toByte,
      0x00,
      0x85.toByte,
      0xbe.toByte,
      0x04,
      0x1e,
      0x82.toByte,
      0xbe.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x00,
      0x00,
      0xff.toByte,
      0x00,
      0x80.toByte,
      0xbe.toByte,
      0x2a,
      0x00,
      0x00,
      0x00,
      0x02,
      0x1e,
      0x84.toByte,
      0xbe.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte
    )
    val kernel = CodeObjectKernel(
      name = "linked_call",
      target = Target("gfx900"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = code,
      codeAddress = 0x1000L,
      textAddress = 0x1000L,
      text = code,
      functionSizes = Map(0x1000L -> 20, 0x1018L -> 16)
    )
    val dispatch = KernelTask.validated(
      "linked_call",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    assertEquals(
      VMCompiler.decodeInstruction(code.take(8), Target("gfx900")),
      Right(Instruction.SMove(4, InstructionSource.Literal(0x1018L)))
    )
    assertEquals(
      VMCompiler.decodeInstruction(code.slice(8, 12), Target("gfx900")),
      Right(Instruction.SMove(5, InstructionSource.Constant(0L)))
    )
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
  }

  for (rawPointers <- List(false, true)) test(s"gfx900 vector copy batches external addresses: $rawPointers") {
    val words = Vec[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code   = words.flatMap(word => LittleEndian.toVec(word, 4))
    val target = Target("gfx900")
    val kernel = CodeObjectKernel(
      name = "vector_copy",
      target = target,
      kernargSize = 80,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x0b,
      code = code
    )
    val memory = TestMemory()
    val input  = AllocationId(1L)
    val output = AllocationId(2L)
    val unused = AllocationId(3L)
    memory.allocate(input, 256)
    memory.allocate(output, 256)
    memory.allocate(unused, 256)
    val expected = Array.tabulate(64)(index => index * 97 - 11)
    expected.zipWithIndex.foreach { case (value, index) =>
      memory.putInt(input, (index * 4).unsafe[Int >= 0], value)
    }

    val batches = scala.collection.mutable.ArrayBuffer.empty[Vec[(GuestAddress, Int >= 0)]]
    class TrackedMemory extends AddressSpace {
      private val pending                                = java.util.concurrent.atomic.AtomicBoolean(!rawPointers)
      override def flushHostReads(): Either[Fault, Unit] = { pending.set(false); Right(()) }
      override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
        awaitRead(readDeferred(region, offset, size))
      override def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
        if (pending.get()) Left(Memory.Pending) else memory.read(region, offset, size)
      override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
        memory.write(region, offset, bytes)
      override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
        memory.address(region, offset)
      override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, Location] = memory.locate(address, size)
      override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = {
        batches += ranges; Right(())
      }
    }
    val tracked                                            = TrackedMemory()
    def pointer(region: AllocationId): KernelTask.Argument =
      if (rawPointers) KernelTask.Argument.Scalar(LittleEndian.toVec(
        memory.address(region, 0L).toOption.get.raw,
        8
      ))
      else KernelTask.Argument.Memory(region, 0L)
    val zero      = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    val arguments = Vec(
      pointer(input),
      pointer(output),
      pointer(unused),
      zero,
      zero,
      zero,
      zero,
      zero,
      zero,
      zero
    )
    val dispatch = KernelTask.validated(
      "vector_copy",
      testDimensions(64, 1, 1),
      Dimensions.One,
      arguments
    ).toOption.get

    assertEquals(executeKernel(compileKernel(kernel), dispatch, tracked), Right(()))
    if (rawPointers) {
      assertEquals(batches.size, 1)
      assertEquals(batches.head.size, 64)
      assertEquals(batches.head.map(_._2.raw).sum, 256)
      assertEquals(
        batches.head.map(_._1.raw),
        (0 until 64).map(i => memory.address(input, (i * 4L).unsafe[Long >= 0]).toOption.get.raw)
      )
    }
    assertEquals(
      Array.tabulate(64)(index => memory.getInt(output, (index * 4).unsafe[Int >= 0])).toSeq,
      expected.toSeq
    )
  }

}
