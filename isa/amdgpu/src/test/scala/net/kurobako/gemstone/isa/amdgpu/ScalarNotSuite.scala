package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.execution.*

final class ScalarNotSuite extends munit.FunSuite {
  // AMD Vega ISA SOP1: complement all destination bits, SCC = (result != 0).
  // https://www.amd.com/content/dam/amd/en/documents/radeon-tech-docs/instruction-set-architectures/vega-shader-instruction-set-architecture.pdf
  private val words = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbf068080L, 0xbe8900ffL, 0x12345678L, 0xbe880504L, 0x850a8081L, 0x7e000280L,
    0x7e020208L, 0x7e040209L, 0xdc748000L, 0x00060100L, 0x7e000288L, 0x7e02020aL, 0xdc708000L, 0x00060100L, 0xbf810000L
  )
  private val output = AllocationId(1)
  private final class Memory extends ExecutionMemory {
    val bytes                                                                     = Array.ofDim[Byte](16)
    def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) =
      Right(Vec.from(bytes.slice(offset.toInt, offset.toInt + size)))
    def write(id: AllocationId, offset: Long :| Positive0, value: Vec[Byte]) = {
      val _ = value.copyToArray(bytes, offset.toInt, value.size); Right(())
    }
    def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }
  test("scalar NOT writes full width and sets or clears SCC independently of its previous value") {
    for {
      width   <- Seq(32, 64)
      initial <- Seq(false, true)
      input   <- Seq(-1L, 0L, 0xaaaaaaaa55555555L, 0xffffffffL)
    } {
      val instruction = if (width == 64) 0xbe880504L else 0xbe880404L
      val cmp         = if (initial) 0xbf068080L else 0xbf078080L
      val code        = words
        .updated(3, cmp)
        .updated(6, instruction)
        .flatMap(value => LittleEndian.toVec(value, ScalarByteWidth(4)))
      val kernel   = CodeObjectKernel("kernel", Target("gfx900"), 16, 0, 0, 0x80, 0x08, code)
      val dispatch = KernelDispatch.validated(
        ProgramId(1),
        KernelName("kernel"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Scalar(LittleEndian.toVec(input, ScalarByteWidth(8))), KernelArgument.Allocation(output, 0))
      ).toOption.get
      val memory = Memory()
      assertEquals(
        DispatchInterpreter.execute(
          DispatchInterpreter.prepare(kernel).toOption.get,
          dispatch,
          memory,
          ExecutionControl.default
        ),
        Right(())
      )
      val result = if (width == 64) ~input else ~input & 0xffffffffL
      val stored = if (width == 64) result else 0x1234567800000000L | result
      assertEquals(LittleEndian.decode(memory.bytes, 0, ScalarByteWidth(8)), stored)
      assertEquals(LittleEndian.decode(memory.bytes, 8, ScalarByteWidth(4)), if (result == 0L) 0L else 1L)
    }
  }
}
