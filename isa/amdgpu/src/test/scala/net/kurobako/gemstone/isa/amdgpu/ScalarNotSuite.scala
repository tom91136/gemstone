package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ScalarNotSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  // AMD Vega ISA SOP1: complement all destination bits, SCC = (result != 0).
  // https://www.amd.com/content/dam/amd/en/documents/radeon-tech-docs/instruction-set-architectures/vega-shader-instruction-set-architecture.pdf
  private val words = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbf068080L, 0xbe8900ffL, 0x12345678L, 0xbe880504L, 0x850a8081L, 0x7e000280L,
    0x7e020208L, 0x7e040209L, 0xdc748000L, 0x00060100L, 0x7e000288L, 0x7e02020aL, 0xdc708000L, 0x00060100L, 0xbf810000L
  )
  private val output = AllocationId(1)
  private class Memory extends vm.Memory with TestAddressSpace {
    val bytes: Array[Byte]                                             = Array.ofDim[Byte](16)
    override protected val regionSizes: Iterable[(AllocationId, Long)] = Seq(output -> bytes.length.toLong)
    def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Right(Vec.from(bytes.slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
    def write(id: AllocationId, offset: Long >= 0, value: Vec[Byte]): Either[Fault, Unit] = {
      val _ = value.copyToArray(bytes, offset.raw.toInt, value.size); Right(())
    }
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
        .flatMap(value => LittleEndian.toVec(value, 4))
      val kernel = CodeObjectKernel(
        name = "kernel",
        target = Target("gfx900"),
        kernargSize = 16,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 0x80,
        kernelCodeProperties = 0x08,
        code = code
      )
      val dispatch = KernelTask.validated(
        "kernel",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Scalar(LittleEndian.toVec(input, 8)),
          KernelTask.Argument.Memory(output, 0L)
        )
      ).toOption.get
      val memory = Memory()
      assertEquals(
        executeKernel(
          VMCompiler.compile(kernel).toOption.get,
          dispatch,
          memory,
          TaskControl()
        ),
        Right(())
      )
      val result = if (width == 64) ~input else ~input & 0xffffffffL
      val stored = if (width == 64) result else 0x1234567800000000L | result
      assertEquals(LittleEndian.decode(memory.bytes, 0, 8), stored)
      assertEquals(
        LittleEndian.decode(memory.bytes, 8, 4),
        if (result == 0L) 0L else 1L
      )
    }
  }
}
