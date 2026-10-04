package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUMatrixF32Suite extends munit.FunSuite {
  private def run(operations: Vec[Instruction], target: Target, memory: TestMemory, lanes: Int > 0): Unit = {
    val offsets = Vec.tabulate(operations.size)(_ * 4)
    val source  = CodeObjectKernel("matrix", target, 0, 0, 0, 0, 0, Vec.empty)
    val kernel  = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
    val task    = KernelTask.validated("matrix", Dimensions(lanes, 1, 1), Dimensions.One, Vec.empty).toOption.get
    assertEquals(executeKernel(kernel, task, memory), Right(()))
  }

  for ((fused, opcode) <- Vec((false, 0x16), (true, 0x3b)))
    test(s"VOP3 MAC fused=$fused preserves its destination addend and input/clamp modifiers") {
      val target     = Target("gfx90a")
      val memory     = TestMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 8)
      val address = memory.address(allocation, 0L).toOption.get.raw
      // LLVM: v_{f}mac_f32_e64 v3, -v1, |v2| clamp.
      val instruction = VMCompiler.decodeInstruction(
        Vec(3, 0x82, opcode, 0xd1, 1, 5, 2, 0x20).map(_.toByte),
        target
      ).fold(error => fail(error.raw), identity)
      def value(x: Float): InstructionSource = InstructionSource.Constant(java.lang.Float.floatToRawIntBits(x).toLong)
      run(
        Vec(
          Instruction.SMove64(8, InstructionSource.Constant(address)),
          Instruction.VMove(1, value(-0.25f)),
          Instruction.VMove(2, value(-0.5f)),
          Instruction.VMove(3, value(0.5f)),
          instruction,
          Instruction.GlobalStore(0, Some(8), 3, 4, 0),
          Instruction.VMove(3, value(1.0f)),
          instruction,
          Instruction.GlobalStore(0, Some(8), 3, 4, 4),
          Instruction.End
        ),
        target,
        memory,
        1
      )
      assertEquals(memory.getInt(allocation, 0), java.lang.Float.floatToRawIntBits(0.625f))
      assertEquals(memory.getInt(allocation, 4), java.lang.Float.floatToRawIntBits(1.0f))
    }

  for {
    architecture              <- Vec("gfx908", "gfx90a", "gfx942", "gfx950")
    dimension                 <- Vec(4, 16, 32)
    accumulators              <- if (architecture == "gfx908") Vec(true) else Vec(false, true)
    inputAccumulators         <- if (dimension == 16 && accumulators) Vec(false, true) else Vec(false)
    (blockBroadcast, blockId) <-
      if (architecture == "gfx90a" && dimension == 4 && accumulators) Vec((0, 0), (2, 1)) else Vec((0, 0))
    broadcast <-
      if (architecture == "gfx942" && dimension == 16 && accumulators && !inputAccumulators) Vec(0, 3, 7) else Vec(0)
  } test(
    s"$architecture ${dimension}x$dimension MFMA accumulator bank=$accumulators inputs=$inputAccumulators broadcast=$broadcast blocks=$blockBroadcast/$blockId"
  ) {
    val target     = Target(architecture.unsafe[NonEmpty[String]])
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 16384)
    val address                      = memory.address(allocation, 0L).toOption.get.raw
    val registers                    = if (dimension == 4) 4 else dimension * dimension / 64
    val depth                        = if (dimension == 4) 1 else 64 / dimension
    def a(row: Int, k: Int): Float   = (row - 2 * k + 1).toFloat / 4
    def b(k: Int, col: Int): Float   = (3 * k + col - 7).toFloat / 8
    def c(row: Int, col: Int): Float = (row - col).toFloat / 2
    def floatBits(value: Float): Int = java.lang.Float.floatToRawIntBits(value)
    for (lane <- 0 until 64) {
      memory.putInt(
        allocation,
        (lane * 4).unsafe[Int >= 0],
        floatBits(a(if (dimension == 4) lane else lane % dimension, if (dimension == 4) 0 else lane / dimension))
      )
      memory.putInt(
        allocation,
        (256 + lane * 4).unsafe[Int >= 0],
        floatBits(b(if (dimension == 4) 0 else lane / dimension, if (dimension == 4) lane else lane % dimension))
      )
    }
    // AMD's matrix calculator maps four consecutive output rows into each lane.
    def location(row: Int, col: Int): (Int, Int) = if (dimension == 4) (row % 4, col)
    else if (dimension == 16)
      (row    % 4, col + row / 4 * 16)
    else (row % 4 + row / 8 * 4, col + (row % 8) / 4 * 32)
    for {
      block    <- 0 until (if (dimension == 4) 16 else 1)
      localRow <- 0 until dimension
      localCol <- 0 until dimension
      row = block * dimension + localRow
      col = block * dimension + localCol
    } {
      val (register, lane) = location(row, col)
      memory.putInt(allocation, (512 + register * 256 + lane * 4).unsafe[Int >= 0], floatBits(c(row, col)))
    }
    def decode(word: Long, second: Long): Instruction =
      VMCompiler.decodeInstruction(LittleEndian.toVec(word, 4) ++ LittleEndian.toVec(second, 4), target)
        .fold(error => fail(error.raw), identity)
    // LLVM encodings for A=v8, B=v9, overlapping C/D=[32:...].
    val word = (if (dimension == 4) 0xd3c20020L else if (dimension == 16) 0xd3c50020L else 0xd3c40020L) |
      (if (accumulators && architecture != "gfx908") 0x8000L else 0L) | (blockBroadcast.toLong << 8) |
      (blockId.toLong << 11)
    val matrix = decode(word, 0x04821308L | (if (inputAccumulators) 0x18000000L else 0L) | (broadcast.toLong << 29))
    val load   = Vec(
      Instruction.SMove64(8, InstructionSource.Constant(address)),
      Instruction.VBinary(
        VectorBinaryOperation.ShiftLeft32,
        2,
        InstructionSource.Constant(2),
        InstructionSource.VectorRegister(0),
        false
      ),
      Instruction.GlobalLoad(8, 2, Some(8), 4, 0, false),
      Instruction.GlobalLoad(9, 2, Some(8), 4, 256, false)
    ) ++
      (if (inputAccumulators) Vec(
         decode(0xd3d94008L, 0x18000108L),
         decode(0xd3d94009L, 0x18000109L),
         Instruction.VMove(8, InstructionSource.Constant(0)),
         Instruction.VMove(9, InstructionSource.Constant(0))
       )
       else Vec.empty[Instruction]) ++ Vec.from(Vec.indices(registers.unsafe[Int >= 0]).flatMap { index =>
        val register = index.raw
        val load     =
          Instruction.GlobalLoad((32: VectorRegisterIndex).offsetBy(index), 2, Some(8), 4, 512 + register * 256, false)
        if (accumulators) Vec(
          load,
          decode(0xd3d94000L | (32 + register), 0x18000120L + register),
          Instruction.VMove((32: VectorRegisterIndex).offsetBy(index), InstructionSource.Constant(0xdeadbeefL))
        )
        else Vec(load)
      })
    val stores = Vec.from(Vec.indices(registers.unsafe[Int >= 0]).flatMap { index =>
      val register = index.raw
      val copy     = if (accumulators) Vec(decode(0xd3d84000L | (64 + register), 0x18000120L + register))
      else Vec(Instruction.VMove(
        (64: VectorRegisterIndex).offsetBy(index),
        InstructionSource.VectorRegister((32: VectorRegisterIndex).offsetBy(index))
      ))
      copy ++
        Vec(Instruction.GlobalStore(2, Some(8), (64: VectorRegisterIndex).offsetBy(index), 4, 8192 + register * 256)) ++
        (if (accumulators) Vec(Instruction.GlobalStore(
           2,
           Some(8),
           (32: VectorRegisterIndex).offsetBy(index),
           4,
           12288 + register * 256
         ))
         else Vec.empty[Instruction])
    })
    val operations = load ++ Vec(Instruction.Barrier, matrix, Instruction.Barrier) ++ stores ++ Vec(Instruction.End)
    run(operations, target, memory, 64)
    for {
      block    <- 0 until (if (dimension == 4) 16 else 1)
      localRow <- 0 until dimension
      localCol <- 0 until dimension
      row = block * dimension + localRow
      col = block * dimension + localCol
    } {
      val (register, lane) = location(row, col)
      val result           = memory.getInt(allocation, (8192 + register * 256 + lane * 4).unsafe[Int >= 0])
      val expected         = (0 until depth).map { k =>
        val sourceK = broadcast match {
          case 3 => (k + 1) % 4
          case 7 => 3
          case _ => k
        }
        val sourceRow =
          if (dimension == 4) ((row / 4 >> blockBroadcast << blockBroadcast) + blockId) * 4 + row % 4 else row
        a(sourceRow, k) * b(sourceK, col)
      }.sum + c(row, col)
      assertEquals(result, floatBits(expected), clues(row, col))
      if (accumulators)
        assertEquals(memory.getInt(allocation, (12288 + register * 256 + lane * 4).unsafe[Int >= 0]), 0xdeadbeef)
    }
  }
}
