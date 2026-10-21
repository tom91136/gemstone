package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUWaveMatrixSuite extends munit.FunSuite {
  for ((halfResult, high) <- Seq((true, false), (true, true), (false, false)))
    test(s"RDNA3 WMMA half=$halfResult high=$high multiplies asymmetric matrices with overlapping destinations") {
      val memory     = RegionMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 4096)
      val address                         = memory.address(allocation, 0L).value.raw
      def half(value: Float): Int         = IEEE754.floatToBinary16(value).toInt
      def a(row: Int, k: Int): Float      = if (k == (row + 3) % 16) (row + 1).toFloat else 0.0f
      def b(k: Int, column: Int): Float   = (k - column).toFloat / 16.0f
      def c(row: Int, column: Int): Float = (row + column).toFloat / 8.0f
      for {
        register <- 0 until 8
        lane     <- 0 until 32
      } {
        val offset = (register * 128 + lane * 4).unsafe[Int >= 0]
        val row    = register * 2 + lane / 16
        val column = lane % 16
        memory.putInt(allocation, offset, half(a(column, register * 2)) | (half(a(column, register * 2 + 1)) << 16))
        memory.putInt(
          allocation,
          (1024 + offset).unsafe[Int >= 0],
          half(b(register * 2, column)) | (half(b(register * 2 + 1, column)) << 16)
        )
        val accumulator = if (halfResult) half(c(row, column)) << (if (high) 16 else 0)
        else java.lang.Float.floatToRawIntBits(c(row, column))
        memory.putInt(allocation, (2048 + offset).unsafe[Int >= 0], accumulator)
      }
      // LLVM encodings, with RDNA3's unpacked C/D layout and packed A/B fragments.
      val bytes =
        Vec(0x20, if (high) 0x60 else 0x40, if (halfResult) 0x42 else 0x40, 0xcc, 0x20, 0x61, 2, 0x1d).map(_.toByte)
      val matrix     = decoded(Target("gfx1100"), bytes)
      val operations = Vec(
        Instruction.SMove64(8, InstructionSource.Constant(address)),
        Instruction.VBinary(
          VectorBinaryOperation.ShiftLeft32,
          30,
          InstructionSource.Constant(2),
          InstructionSource.VectorRegister(0),
          false
        )
      ) ++ Vec.range[0, 8].flatMap(register =>
        Vec(
          Instruction.Load(
            (32: VectorRegisterIndex).offsetBy(register),
            MemoryAddress.Global(30, Some(8), register * 128),
            4,
            false,
            LoadPlacement.Full
          ),
          Instruction.Load(
            (48: VectorRegisterIndex).offsetBy(register),
            MemoryAddress.Global(30, Some(8), 1024 + register * 128),
            4,
            false,
            LoadPlacement.Full
          ),
          Instruction.Load(
            (64: VectorRegisterIndex).offsetBy(register),
            MemoryAddress.Global(30, Some(8), 2048 + register * 128),
            4,
            false,
            LoadPlacement.Full
          )
        )
      ) ++ Vec(matrix) ++ Vec.range[0, 8].map(register =>
        Instruction.Store(
          (32: VectorRegisterIndex).offsetBy(register),
          MemoryAddress.Global(30, Some(8), 3072 + register * 128),
          4,
          false
        )
      ) ++ Vec(Instruction.End)
      val source = codeKernel(
        target = Target("gfx1100"),
        kernelCodeProperties = EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32
      )
      val task = KernelTask.validated("kernel", Dimensions(32, 1, 1), Dimensions.One, Vec.empty).value
      assertEquals(runIR(operations, memory, source, task), Right(()))
      for {
        row    <- 0 until 16
        column <- 0 until 16
      } {
        val lane     = column + (row % 2) * 16
        val offset   = (3072 + row / 2 * 128 + lane * 4).unsafe[Int >= 0]
        val result   = memory.getInt(allocation, offset)
        val expected = (0 until 16).map(k => a(row, k) * b(k, column)).sum + c(row, column)
        if (halfResult) assertEquals((result >>> (if (high) 16 else 0)) & 0xffff, half(expected), clues(row, column))
        else assertEquals(result, java.lang.Float.floatToRawIntBits(expected), clues(row, column))
      }
    }

  // Wave64 layouts from AMD's matrix instruction calculator (-w 64): gfx11 replicates A and B in every 16 lanes and puts
  // row 4r + lane / 16 of D in its register r; gfx12 spreads K across the four 16-lane groups and puts rows r, 8 + r,
  // 4 + r and 12 + r of D in register r of each group. HIP's __builtin_amdgcn_wmma_f32_16x16x16_f16_w64 and _w64_gfx12
  // compile with -mwavefrontsize64 to these encodings (llvm-mc -mattr=+wavefrontsize64).
  for (
    (target, registers, k, row) <- Seq[(Target, Int, (Int, Int, Int) => Int, (Int, Int) => Int)](
      (Target("gfx1100"), 8, (_, register, half) => 2 * register + half, (lane, register) => 4 * register + lane / 16),
      (
        Target("gfx1201"),
        2,
        (lane, register, half) => 4 * (lane / 16) + 2 * register + half,
        (lane, register) => 8 * (lane / 16 % 2) + register + 4 * (lane / 32)
      )
    )
  ) test(s"$target wave64 WMMA f32 16x16x16 f16 takes the wave64 layouts") {
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 8192)
    val address                         = memory.address(allocation, 0L).value.raw
    def half(value: Float): Int         = IEEE754.floatToBinary16(value).toInt
    def a(row: Int, k: Int): Float      = ((row * 3 + k * 5)    % 7 - 3).toFloat / 4.0f
    def b(k: Int, column: Int): Float   = ((k * 2 + column * 3) % 5 - 2).toFloat / 2.0f
    def c(row: Int, column: Int): Float = (row - column).toFloat
    for (lane <- 0 until 64) {
      for (register <- 0 until registers) {
        val offset                         = (register * 256 + lane * 4).unsafe[Int >= 0]
        def pair(value: Int => Float): Int = half(value(k(lane, register, 0))) |
          (half(value(k(lane, register, 1))) << 16)
        memory.putInt(allocation, offset, pair(a(lane % 16, _)))
        memory.putInt(allocation, (2048 + offset).unsafe[Int >= 0], pair(b(_, lane % 16)))
      }
      for (register <- 0 until 4)
        memory.putInt(
          allocation,
          (4096 + register * 256 + lane * 4).unsafe[Int >= 0],
          java.lang.Float.floatToRawIntBits(c(row(lane, register), lane % 16))
        )
    }
    // v_wmma_f32_16x16x16_f16 v[16:19], v[0:n], v[8:m], v[16:19]
    val matrix = VMCompiler.decodeProgram(
      Vec(0x10, 0x40, 0x40, 0xcc, 0x00, 0x11, 0x42, 0x1c).map(_.toByte),
      TargetMode(target, 64)
    ).fold(error => fail(error.raw), _.operations)
    def load(dest: Int, at: Int) =
      Instruction.Load(
        dest.unsafe[VectorRegisterIndex],
        MemoryAddress.Global(30, Some(8), at),
        4,
        false,
        LoadPlacement.Full
      )
    val operations = Vec(
      Instruction.SMove64(8, InstructionSource.Constant(address)),
      Instruction.VBinary(
        VectorBinaryOperation.ShiftLeft32,
        30,
        InstructionSource.Constant(2),
        InstructionSource.VectorRegister(0),
        false
      )
    ) ++ Vec.from((0 until registers).flatMap(register =>
      Seq(load(register, register * 256), load(8 + register, 2048 + register * 256))
    )) ++ Vec.from((0 until 4).map(register => load(16 + register, 4096 + register * 256))) ++ matrix ++
      Vec.from((0 until 4).map(register =>
        Instruction.Store(
          (16 + register).unsafe[VectorRegisterIndex],
          MemoryAddress.Global(30, Some(8), 5120 + register * 256),
          4,
          false
        )
      )) ++ Vec(Instruction.End)
    val task = KernelTask.validated("kernel", Dimensions(64, 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(runIR(operations, memory, codeKernel(target = target), task), Right(()))
    for {
      lane     <- 0 until 64
      register <- 0 until 4
    } {
      val (r, column) = (row(lane, register), lane % 16)
      val expected    = (0 until 16).map(index => a(r, index) * b(index, column)).sum + c(r, column)
      assertEquals(
        memory.getInt(allocation, (5120 + register * 256 + lane * 4).unsafe[Int >= 0]),
        java.lang.Float.floatToRawIntBits(expected),
        clues(lane, register)
      )
    }
  }
}
