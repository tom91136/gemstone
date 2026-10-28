package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class CodeObjectExecutionSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("dispatch pointer exposes launch dimensions and segment sizes to OpenMP kernels") {
    // LLVM gfx1030: copy the first eight AQL packet dwords to the output.
    // OpenMP uses grid_size_x at byte 12 as its loop stride.
    val dwords = Vec[Int](
      0xf40c0200, 0xfa000000, 0xf4040101, 0xfa000000, 0xbf8cc07f,
      0x7e000280, 0x7e020208, 0x7e040209, 0x7e06020a, 0x7e08020b,
      0x7e0a020c, 0x7e0c020d, 0x7e0e020e, 0x7e10020f,
      0xdc788000, 0x00040100, 0xdc788010, 0x00040500, 0xbf810000
    )
    val kernel = codeKernel(
      target = Target("gfx1030"),
      kernargSize = 8,
      groupSegmentSize = 32,
      privateSegmentSize = 16,
      kernelCodeProperties = 0x0a,
      code = words(dwords*)
    )
    val output = AllocationId(1)
    val memory = RegionMemory()
    memory.allocate(output, 32)
    val task = KernelTask.validated(
      "dispatch_packet",
      testDimensions(2, 3, 4),
      testDimensions(2, 2, 2),
      Vec(KernelTask.Argument.Memory(output, 0L)),
      globalSize = Some(GlobalDimensions(3L, 5L, 7L)),
      privateSegmentBytes = 64,
      groupSegmentBytes = 128
    ).value
    assertEquals(executeKernel(compileKernel(kernel), task, memory), Right(()))
    assertEquals(memory.getInt(output, 4), 2 | (3 << 16))
    assertEquals(memory.getInt(output, 8), 4)
    assertEquals(memory.getInt(output, 12), 3)
    assertEquals(memory.getInt(output, 16), 5)
    assertEquals(memory.getInt(output, 20), 7)
    assertEquals(memory.getInt(output, 24), 64)
    assertEquals(memory.getInt(output, 28), 160)
  }

  test("gfx1036 preserves a vcc_hi scratch counter across a vector comparison") {
    // HIP kernels keep loop counters in vcc_hi (s107) on wave32 targets. A
    // vector comparison only defines the wave's lane bits of VCC, so the
    // counter has to survive one. Without that the loop below never reaches
    // zero and the dispatch only ends by exhausting its instruction budget.
    val code = words(
      0xbeeb038a, // s_mov_b32 vcc_hi, 10
      0x7c1a1880, // v_cmp_neq_f32_e32 vcc_lo, 0, v12
      0x816bc16b, // s_add_i32 vcc_hi, vcc_hi, -1
      0xbf07806b, // s_cmp_lg_u32 vcc_hi, 0
      0xbf85fffc, // s_cbranch_scc1 -4 (displacement is in dwords)
      0xbf810000  // s_endpgm
    )
    val kernel = codeKernel(
      target = Target("gfx1036"),
      computePgmRsrc2 = 0x880,
      kernelCodeProperties = EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32,
      code = code
    )
    val dispatch = KernelTask.validated(
      "vcc_scratch_counter",
      testDimensions(32, 1, 1),
      Dimensions.One,
      Vec.empty
    ).value
    val control = TaskControl(TaskControl.Limits(
      maxInstructions = 4096L
    ))
    assertEquals(executeKernel(compileKernel(kernel), dispatch, RegionMemory(), control), Right(()))
  }

  test("self-contained linked entries do not decode unrelated unsupported kernels") {
    val end         = words(0xbf810000)
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = codeKernel(
      target = Target("gfx1030"),
      entrySize = Some(end.nonNegativeSize),
      entry = 0x1004L,
      textAddress = 0x1000L,
      code = unsupported ++ end,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 4)
    )
    val dispatch = singleTask()
    assertEquals(executeKernel(compileKernel(kernel), dispatch, RegionMemory()), Right(()))
    assert(VMCompiler().compile(kernel.copy(entrySize = unsupported.nonNegativeSize, entry = 0x1000L)).isLeft)
    val truncated = Vec[Byte](1, 2)
    assert(VMCompiler().compile(kernel.copy(
      entrySize = truncated.nonNegativeSize,
      text = unsupported ++ truncated,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 2)
    )).left.exists(_.message.contains("byte 4")))
    // A direct branch leaving the entry must use linked decoding and preserve the target failure.
    val branch = words(0xbf820000)
    assert(VMCompiler().compile(kernel.copy(
      entrySize = branch.nonNegativeSize,
      entry = 0x1000L,
      text = branch ++ unsupported
    )).isLeft)
  }

  test("linked kernels share decoded text while retaining distinct entry points") {
    // Two external branches: the first enters a trap, the second reaches endpgm.
    val text   = words(0xbf820002, 0xbf820000, 0xbf810000, 0xbf920002)
    val source = codeKernel(
      entrySize = Some(text.take(4).nonNegativeSize),
      entry = 0x1000L,
      textAddress = 0x1000L,
      code = text,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> 4, 0x1008L -> 4, 0x100cL -> 4)
    )
    val compiler = VMCompiler()
    val first    = compiler.compile(source).value
    val second   = compiler.compile(source.copy(name = "end_entry", entry = 0x1004L))
      .value
    assert(first.instructions eq second.instructions)
    assert(first.instructionIndices eq second.instructionIndices)
    assertEquals(first.entryInstruction, 0)
    assertEquals(second.entryInstruction, 1)
    val dispatch = singleTask()
    assert(executeKernel(first, dispatch, RegionMemory()).isLeft)
    assertEquals(executeKernel(second, dispatch, RegionMemory()), Right(()))
  }

  private val trapIds = List(2, 0x3141)

  for (architecture <- List("gfx900", "gfx1030"))
    for (trapId <- trapIds) test(s"$architecture executed s_trap $trapId is a runtime fault") {
      val code   = words(0xbf920000 | trapId, 0xbf810000)
      val kernel = codeKernel(target = architecture.unsafe[Target], entry = 0x1000L, textAddress = 0x1000L, code = code)
      val dispatch = singleTask()
      assertEquals(
        executeKernel(compileKernel(kernel), dispatch, RegionMemory()),
        Left(Fault.Trap(s"AMDGPU s_trap $trapId at pc 0x1000"))
      )
    }

  test("proven local PC-relative branches keep unrelated functions isolated") {
    val dwords = Vec[Int](0xbea01f00, 0x812284ff, 0x00000010, 0x80202220,
      0x82218021, 0xbe802020, 0xbf810000)
    val code        = words(dwords*)
    val unsupported = Vec.fill(4)(0xff.toByte)
    val kernel      = codeKernel(
      target = Target("gfx1030"),
      entrySize = Some(code.nonNegativeSize),
      entry = 0x1004L,
      textAddress = 0x1000L,
      code = unsupported ++ code,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> code.size)
    )
    val dispatch = singleTask()
    assertEquals(executeKernel(compileKernel(kernel), dispatch, RegionMemory()), Right(()))
    val crossing = kernel.copy(
      entry = 0xfffffff4L,
      textAddress = 0xfffffff0L,
      functionSizes = Map(0xfffffff0L -> 4, 0xfffffff4L -> code.size)
    )
    assertEquals(executeKernel(compileKernel(crossing), dispatch, RegionMemory()), Right(()))
    val aliased = words(dwords.updated(1, 0x812084ff)*)
    assert(VMCompiler().compile(kernel.copy(entrySize = aliased.nonNegativeSize, text = unsupported ++ aliased)).isLeft)

    // Entry in the middle invalidates the proof even though the instruction bytes still match.
    val intoMiddle = words(0xbf820005) ++ code
    assert(VMCompiler().compile(kernel.copy(
      entrySize = intoMiddle.nonNegativeSize,
      text = unsupported ++ intoMiddle,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> intoMiddle.size)
    )).isLeft)
    // An unproven indirect target must still require the complete linked program.
    val unknown = words(0xbe802024, 0xbf810000)
    assert(VMCompiler().compile(kernel.copy(
      entrySize = unknown.nonNegativeSize,
      text = unsupported ++ unknown,
      functionSizes = Map(0x1000L -> 4, 0x1004L -> unknown.size)
    )).isLeft)
  }

  test("gfx1030 buffer short stores keep their two-byte width") {
    val code = words(0xe0681000, 0x80010001)
    assertEquals(
      VMCompiler.decodeInstruction(code, Target("gfx1030")),
      Right(Instruction.Store(0, MemoryAddress.Buffer(Some(1), 4, None, 0), 2, false))
    )
    // Point the buffer descriptor at allocation 1 and store v0's low half at byte offset 1. s_brev_b32 s7, 12 selects
    // raw bounds checking (OOB_SELECT 3), as runtime descriptors do; RDNA checks a zero stride and drops the store
    // under OOB_SELECT 0.
    val dwords = Vec[Int](0xbe840380, 0xbe8503ff, 0x00004000, 0xbe860388, 0xbe870b8c,
      0x7e0002ff, 0x12345678, 0x7e020281, 0xe0681000, 0x80010001, 0xbf810000)
    val kernel     = codeKernel(target = Target("gfx1030"), code = words(dwords*))
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    memory.putInt(allocation, 0, 0xa5a5a5a5)
    memory.putInt(allocation, 4, 0xa5a5a5a5)
    val dispatch = singleTask()
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0xa55678a5)
    assertEquals(memory.getInt(allocation, 4), 0xa5a5a5a5)
    memory.putInt(allocation, 0, 0xa5a5a5a5)
    val high = kernel.copy(text = words(dwords.updated(8, 0xe06c1000)*))
    assertEquals(executeKernel(compileKernel(high), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0xa51234a5)
    assertEquals(memory.getInt(allocation, 4), 0xa5a5a5a5)
  }

  test("gfx1030 scratch buffer scalar offsets use wave-interleaved addresses") {
    // LLVM uses a 0x20080 scalar scratch offset for private byte 4100 on a
    // wave32 target. The instruction's immediate offset remains per-lane.

    val kernel = codeKernel(
      target = Target("gfx1030"),
      privateSegmentSize = 4752,
      kernelCodeProperties = 1 | EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32,
      code = words(
        0xbe8403ff, 0x00020080, 0x7e0002ff, 0x12345678, 0xe0700000, 0x04000000, 0x7e0002ff, 0x0fedcba9, 0xe0700004,
        0x04000000, 0xe0300000, 0x04000100, 0xe0300004, 0x04000200, 0xbf8c3f70, 0xbe800380, 0xbe8103ff, 0x00004000,
        0xbe820388, 0xbe830b8c, 0xe0700000, 0x80000100, 0xe0700004, 0x80000200, 0xbf810000
      )
    )
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    val dispatch = singleTask()
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.getInt(allocation, 0), 0x12345678)
    assertEquals(memory.getInt(allocation, 4), 0x0fedcba9)
  }

  // The optimised Polyregion math kernel emits v_frexp_mant_f32_e64 with |src|. Both conversions start from -6.0f so
  // dropping the modifier changes the stored result.
  for (
    (name, conversion, store, expected) <- Seq(
      ("frexp mantissa", 0xd5c00101, Seq(0xe0700000, 0x80000100), Seq(java.lang.Float.floatToRawIntBits(0.75f))),
      ("f32 to f64 conversion", 0xd5900102, Seq(0xe0740000, 0x80000200), Seq(0, 0x40180000))
    )
  ) test(s"gfx1030 VOP3 $name applies the absolute source modifier") {
    val prefix = Seq(0x7e0002ff, 0xc0c00000, conversion, 0x00000100, 0xbe800380, 0xbe8103ff, 0x00004000, 0xbe820388)
    // s_brev_b32 s3, 12 selects raw bounds checking
    val code       = words((prefix ++ Seq(0xbe830b8c) ++ store :+ 0xbf810000)*)
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8)
    assertEquals(
      executeKernel(compileKernel(codeKernel(target = Target("gfx1030"), code = code)), singleTask(), memory),
      Right(())
    )
    assertEquals(expected.indices.map(index => memory.getInt(allocation, (index * 4).unsafe[Int >= 0])), expected)
  }

  test("gfx900 transfers control through a linked device-function call and return") {
    // s_mov_b32 s4, 0x1018; s_mov_b32 s5, 0; s_swappc s[2:3], s[4:5]
    // calls the second text function. It writes s0 and returns through the
    // continuation saved by s_swappc before the caller reaches s_endpgm.
    // The four zero bytes between the functions model the alignment gap
    // emitted by the linker after the caller's s_endpgm.
    val code = words(0xbe8400ff, 0x00001018, 0xbe850080, 0xbe821e04, 0xbf810000, 0x00000000, 0xbe8000ff, 0x0000002a,
      0xbe841e02, 0xbf810000)
    val kernel = codeKernel(
      textAddress = 0x1000L,
      entry = 0x1000L,
      code = code,
      functionSizes = Map(0x1000L -> 20, 0x1018L -> 16)
    )
    val dispatch = singleTask()

    assertEquals(
      VMCompiler.decodeInstruction(code.take(8), Target("gfx900")),
      Right(Instruction.SMove(4, InstructionSource.Literal(0x1018L)))
    )
    assertEquals(
      VMCompiler.decodeInstruction(code.slice(8, 12), Target("gfx900")),
      Right(Instruction.SMove(5, InstructionSource.Constant(0L)))
    )
    assertEquals(executeKernel(compileKernel(kernel), dispatch, RegionMemory()), Right(()))
  }

  private val laneCounts = List(1, 33, 64)

  for {
    rawPointers <- List(false, true)
    activeLanes <- laneCounts
  } test(s"gfx900 vector copy batches external addresses: raw=$rawPointers active=$activeLanes") {

    val code = words(
      0xc0020242, 0x00000004, 0xc0020283, 0x00000018,
      0xc00a0003, 0x00000000, 0xbea10080, 0xbf8cc07f,
      0x8604ff09, 0x0000ffff, 0x92080408, 0x810a080a,
      0x6800000a, 0x2202009f, 0xd28f0000, 0x00020082,
      0x7e060201, 0x32040000, 0x38060303, 0xdc508000,
      0x027f0002, 0x7e060203, 0x32000002, 0x38020303,
      0xbf8c0f70, 0xdc708000, 0x007f0200, 0xbf810000
    )
    val target = Target("gfx900")
    val kernel = codeKernel(
      target = target,
      kernargSize = 80,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x0b,
      code = code
    )
    val memory = RegionMemory()
    val input  = AllocationId(1L)
    val output = AllocationId(2L)
    val unused = AllocationId(3L)
    memory.allocate(input, 256)
    memory.allocate(output, 256)
    memory.allocate(unused, 256)
    val expected = Array.tabulate(64)(index => if (index < activeLanes) index * 97 - 11 else 0)
    Array.tabulate(64)(index => index * 97 - 11).zipWithIndex.foreach { case (value, index) =>
      memory.putInt(input, (index * 4).unsafe[Int >= 0], value)
    }

    val batches = mutable.ArrayBuffer.empty[Vec[(GuestAddress, Int >= 0)]]
    class TrackedMemory extends ForwardingMemory(memory) {
      private val pending                                = AtomicBoolean(!rawPointers)
      override def flushHostReads(): Either[Fault, Unit] = { pending.set(false); Right(()) }
      override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
        awaitRead(readDeferred(region, offset, size))
      override def readDeferred(
          region: AllocationId,
          offset: Long >= 0,
          size: Int >= 0
      ): AddressSpace.ReadResult[Vec[Byte]] =
        if (pending.get()) Left(AddressSpace.Pending) else memory.read(region, offset, size)
      override def prepareReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] = {
        batches += Vec.tabulate(ranges.length)(index => (ranges.address(index), ranges.size(index)))
        Right(())
      }
    }
    val tracked                                            = TrackedMemory()
    def pointer(region: AllocationId): KernelTask.Argument =
      if (rawPointers) KernelTask.Argument.Scalar(LittleEndian.toVec(
        memory.address(region, 0L).value.raw,
        8
      ).unsafe)
      else KernelTask.Argument.Memory(region, 0L)
    val zero      = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte).unsafe)
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
      kernelName = "vector_copy",
      groupSize = testDimensions(64, 1, 1),
      groupCount = Dimensions.One,
      arguments = arguments,
      globalSize = Some(GlobalDimensions(activeLanes.toLong.unsafe[Long > 0], 1L, 1L))
    ).value

    assertEquals(executeKernel(compileKernel(kernel), dispatch, tracked), Right(()))
    if (rawPointers) {
      assertEquals(batches.size, 1)
      assertEquals(batches.head.size, activeLanes)
      assertEquals(batches.head.map(_._2.raw).sum, activeLanes * 4)
      assertEquals(
        batches.head.map(_._1.raw),
        (0 until activeLanes).map(i => memory.address(input, (i * 4L).unsafe[Long >= 0]).value.raw)
      )
    }
    assertEquals(
      Array.tabulate(64)(index => memory.getInt(output, (index * 4).unsafe[Int >= 0])).toSeq,
      expected.toSeq
    )
  }

}
