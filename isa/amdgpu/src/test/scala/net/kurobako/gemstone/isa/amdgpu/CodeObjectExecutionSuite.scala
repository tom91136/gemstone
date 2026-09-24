package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*
import VMIR.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.binary.{Bits, ByteRange, ScalarByteWidth}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

final class CodeObjectExecutionSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("a linked code object fills its GOT slot from an absolute 64-bit relocation") {
    // HIP reaches a device global through a .got slot when the symbol is not
    // directly addressable, so the slot has to hold the symbol address before
    // the first kernel reads it.
    val indexed = CodeObject.index(Vec.unsafeFromArray(minimalCodeObject(globalPointer = true))).toOption.get
    val kernel  = indexed.kernel("kernel").toOption.get
    assertEquals(
      kernel.globalMemory.find(_._1 == 0x4000L).map(_._2.take(8).toSeq),
      Some(LittleEndian.toVec(0x3000L, ScalarByteWidth(8)).toSeq)
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
      "vcc_scratch_counter",
      Target("gfx1036"),
      0,
      0,
      0,
      computePgmRsrc2 = 0x880,
      kernelCodeProperties = 0,
      code
    )
    val dispatch = KernelTask.validated(
      KernelName("vcc_scratch_counter"),
      Dimensions(32, 1, 1),
      Dimensions.One,
      Vec.empty
    ).toOption.get
    val control = TaskControl.forScheduler(TaskLimits(maxInstructions = 4096L, quantum = 64))
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory(), control), Right(()))
  }

  test("self-contained linked entries do not decode unrelated unsupported kernels") {
    val end         = Vec[Byte](0x00, 0x00, 0x81.toByte, 0xbf.toByte)
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = CodeObjectKernel(
      "selected",
      Target("gfx1030"),
      0,
      0,
      0,
      0,
      0,
      end,
      codeAddress = 0x1004L,
      textAddress = 0x1000L,
      text = unsupported ++ end,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 4)
    )
    val dispatch = KernelTask.validated(
      KernelName("selected"),
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
      val trap   = LittleEndian.toVec(0xbf920002L, ScalarByteWidth(4))
      val branch = LittleEndian.toVec(0xbf820000L, ScalarByteWidth(4))
      val end    = LittleEndian.toVec(0xbf810000L, ScalarByteWidth(4))
      val kernel = CodeObjectKernel(
        "skip_linked_trap",
        Target(architecture),
        0,
        0,
        0,
        0,
        0,
        branch,
        codeAddress = 0x1004L,
        textAddress = 0x1000L,
        text = trap ++ branch ++ end,
        functionSizes = Map(0x1000L -> 4, 0x1004L -> 4, 0x1008L -> 4)
      )
      val dispatch = KernelTask.validated(
        KernelName("skip_linked_trap"),
        Dimensions.One,
        Dimensions.One,
        Vec.empty
      ).toOption.get
      assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
    }

    for (trapId <- List(2, 0x3141)) test(s"$architecture executed s_trap $trapId is a runtime fault") {
      val code = Vec[Long](0xbf920000L | trapId.toLong, 0xbf810000L)
        .flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
      val kernel = CodeObjectKernel(
        "execute_trap",
        Target(architecture),
        0,
        0,
        0,
        0,
        0,
        code,
        codeAddress = 0x1000L
      )
      val dispatch = KernelTask.validated(
        KernelName("execute_trap"),
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
    val code        = words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = CodeObjectKernel(
      "local_jump",
      Target("gfx1030"),
      0,
      0,
      0,
      0,
      0,
      code,
      codeAddress = 0x1004L,
      textAddress = 0x1000L,
      text = unsupported ++ code,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> code.size)
    )
    val dispatch = KernelTask.validated(
      KernelName("local_jump"),
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
    val aliased = words.updated(1, 0x812084ffL).flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    assert(VMCompiler.compile(kernel.copy(code = aliased, text = unsupported ++ aliased)).isLeft)

    // Entry in the middle invalidates the proof even though the instruction bytes still match.
    val intoMiddle = LittleEndian.toVec(0xbf820005L, ScalarByteWidth(4)) ++ code
    assert(VMCompiler.compile(kernel.copy(
      code = intoMiddle,
      text = unsupported ++ intoMiddle,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> intoMiddle.size)
    )).isLeft)
    // An unproven indirect target must still require the complete linked program.
    val unknown = Vec[Long](0xbe802024L, 0xbf810000L).flatMap(word =>
      LittleEndian.toVec(word, ScalarByteWidth(4))
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
      "store_short",
      Target("gfx1030"),
      0,
      0,
      0,
      0,
      0,
      words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    )
    val memory     = TestMemory()
    val allocation = MemoryRegion(1L)
    memory.allocate(allocation, 8)
    memory.putInt(allocation, 0, 0xa5a5a5a5)
    memory.putInt(allocation, 4, 0xa5a5a5a5)
    val dispatch = KernelTask.validated(
      KernelName("store_short"),
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
        LittleEndian.toVec(word, ScalarByteWidth(4))
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
      "scratch_swizzle",
      Target("gfx1030"),
      0,
      0,
      4752,
      0,
      1,
      words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    )
    val memory     = TestMemory()
    val allocation = MemoryRegion(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      KernelName("scratch_swizzle"),
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
      "frexp_mantissa_modified",
      Target("gfx1030"),
      0,
      0,
      0,
      0,
      0,
      words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    )
    val memory     = TestMemory()
    val allocation = MemoryRegion(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      KernelName("frexp_mantissa_modified"),
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
      "f64_from_f32_modified",
      Target("gfx1030"),
      0,
      0,
      0,
      0,
      0,
      words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    )
    val memory     = TestMemory()
    val allocation = MemoryRegion(1L)
    memory.allocate(allocation, 8)
    val dispatch = KernelTask.validated(
      KernelName("f64_from_f32_modified"),
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
      KernelName("linked_call"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    assertEquals(
      VMCompiler.decodeInstruction(code.take(8), Target("gfx900")),
      Right(Instruction.SMove(4, InstructionSource.Literal(0x1018L)))
    )
    assertEquals(
      VMCompiler.decodeInstruction(code.drop(8).take(4), Target("gfx900")),
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
    val code   = words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    val target = Target("gfx900")
    val kernel = CodeObjectKernel("vector_copy", target, 80, 0, 0, 0x80, 0x0b, code)
    val memory = TestMemory()
    val input  = MemoryRegion(1L)
    val output = MemoryRegion(2L)
    val unused = MemoryRegion(3L)
    memory.allocate(input, 256)
    memory.allocate(output, 256)
    memory.allocate(unused, 256)
    val expected = Array.tabulate(64)(index => index * 97 - 11)
    expected.zipWithIndex.foreach { case (value, index) =>
      memory.putInt(input, (index * 4).refineUnsafe[Positive0], value)
    }

    val batches = scala.collection.mutable.ArrayBuffer.empty[Vec[(GuestAddress, Int :| Positive0)]]
    final class TrackedMemory extends Memory with AddressSpace {
      private val pending                                = java.util.concurrent.atomic.AtomicBoolean(!rawPointers)
      override def flushHostReads(): Either[Fault, Unit] = { pending.set(false); Right(()) }
      override def read(region: MemoryRegion, offset: Long :| Positive0, size: Int :| Positive0) =
        if (pending.get()) Left(Fault.PendingMemory) else memory.read(region, offset, size)
      override def write(region: MemoryRegion, offset: Long :| Positive0, bytes: Vec[Byte]) =
        memory.write(region, offset, bytes)
      override def address(region: MemoryRegion, offset: Long :| Positive0)    = memory.address(region, offset)
      override def locate(address: GuestAddress, size: Int :| Positive0)       = memory.locate(address, size)
      override def prepareReads(ranges: Vec[(GuestAddress, Int :| Positive0)]) = { batches += ranges; Right(()) }
    }
    val tracked                                            = TrackedMemory()
    def pointer(region: MemoryRegion): KernelTask.Argument =
      if (rawPointers) KernelTask.Argument.Scalar(LittleEndian.toVec(
        memory.address(region, 0L).toOption.get.value,
        ScalarByteWidth(8)
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
      KernelName("vector_copy"),
      Dimensions(64, 1, 1),
      Dimensions.One,
      arguments
    ).toOption.get

    assertEquals(executeKernel(compileKernel(kernel), dispatch, tracked), Right(()))
    if (rawPointers) {
      assertEquals(batches.size, 1)
      assertEquals(batches.head.size, 64)
      assertEquals(batches.head.map(_._2.toInt).sum, 256)
      assertEquals(
        batches.head.map(_._1.value).toSeq,
        (0 until 64).map(i => memory.address(input, (i * 4L).refineUnsafe[Positive0]).toOption.get.value)
      )
    }
    assertEquals(
      Array.tabulate(64)(index => memory.getInt(output, (index * 4).refineUnsafe[Positive0])).toSeq,
      expected.toSeq
    )
  }

}
