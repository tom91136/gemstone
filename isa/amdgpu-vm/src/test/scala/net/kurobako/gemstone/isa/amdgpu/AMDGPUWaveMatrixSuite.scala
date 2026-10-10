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
          (1024 + offset.raw).unsafe[Int >= 0],
          half(b(register * 2, column)) | (half(b(register * 2 + 1, column)) << 16)
        )
        val accumulator = if (halfResult) half(c(row, column)) << (if (high) 16 else 0)
        else java.lang.Float.floatToRawIntBits(c(row, column))
        memory.putInt(allocation, (2048 + offset.raw).unsafe[Int >= 0], accumulator)
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
            MemoryAddress.Global(30, Some(8), register.raw * 128),
            4,
            false,
            LoadPlacement.Full
          ),
          Instruction.Load(
            (48: VectorRegisterIndex).offsetBy(register),
            MemoryAddress.Global(30, Some(8), 1024 + register.raw * 128),
            4,
            false,
            LoadPlacement.Full
          ),
          Instruction.Load(
            (64: VectorRegisterIndex).offsetBy(register),
            MemoryAddress.Global(30, Some(8), 2048 + register.raw * 128),
            4,
            false,
            LoadPlacement.Full
          )
        )
      ) ++ Vec(matrix) ++ Vec.range[0, 8].map(register =>
        Instruction.Store(
          (32: VectorRegisterIndex).offsetBy(register),
          MemoryAddress.Global(30, Some(8), 3072 + register.raw * 128),
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
}
