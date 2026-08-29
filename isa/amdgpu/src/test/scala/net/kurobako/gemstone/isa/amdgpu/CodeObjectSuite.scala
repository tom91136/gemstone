package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

class CodeObjectSuite extends munit.FunSuite {
  test("gfx900 single-offset DS instructions retain all 16 offset bits") {
    assertEquals(DispatchInterpreter.singleDSOffset(0xd83e0200L), 512)
    assertEquals(DispatchInterpreter.singleDSOffset(0xd8781e00L), 7680)
  }

  test("gfx900 V_PERM_B32 selectors address the second source before the first") {
    assertEquals(
      DispatchInterpreter.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x05040100L),
      0xa1a0b1b0L
    )
  }

  test("gfx900 scalar unsigned multiply returns the high 32 bits") {
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh32(0xffffffffL, 0xffffffffL), 0xfffffffeL)
    assertEquals(DispatchInterpreter.unsignedMultiplyHigh32(0x12345678L, 0x100000000L), 0L)
  }

  test("gfx900 scalar unsigned minimum accepts a literal operand") {
    assertEquals(DispatchInterpreter.unsignedMin32(300L, 256L), 256L)
    assertEquals(DispatchInterpreter.unsignedMin32(0xffffffffL, 256L), 256L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x02,
          0xff.toByte,
          0x84.toByte,
          0x83.toByte,
          0x00,
          0x01,
          0x00,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 SOPK signed multiply uses a sign-extended immediate and reports overflow") {
    assertEquals(DispatchInterpreter.signedMultiplyImmediate(0xffffffffL, 0x300), (0xfffffd00L, false))
    assertEquals(DispatchInterpreter.signedMultiplyImmediate(Int.MaxValue.toLong, 2), (0xfffffffeL, true))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x00, 0x03, 0x82.toByte, 0xb7.toByte, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 unsigned subtraction returns the low word and no-borrow flag") {
    assertEquals(DispatchInterpreter.unsignedSubtract32(9L, 4L), (5L, true))
    assertEquals(DispatchInterpreter.unsignedSubtract32(0L, 1L), (0xffffffffL, false))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x0e, 0x02, 0x02, 0x34, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 chained subtraction consumes and replaces the no-borrow flag") {
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(9L, 4L, true), (5L, true))
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(9L, 4L, false), (4L, true))
    assertEquals(DispatchInterpreter.unsignedSubtractWithBorrow32(0L, 0xffffffffL, false), (0L, false))
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x03, 0x05, 0x04, 0x3a, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOPC accepts canonical 16-bit and 64-bit not-equal aliases") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x04, 0x0b, 0x5a, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x80.toByte, 0x02, 0xda.toByte, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 compares signed 64-bit VGPR pairs into an explicit scalar mask") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0xe1.toByte,
          0xd0.toByte,
          0x01,
          0x07,
          0x02,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 mask bit counts compose across both halves of a wave") {
    val executionMask = 0xf000000f0000000fL
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 0, false, 0L), 0L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 4, false, 0L), 4L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 32, false, 0L), 4L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 60, true, 8L), 12L)
    assertEquals(DispatchInterpreter.mbCount(-1L, executionMask, 64 - 1, true, 8L), 15L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x03,
          0x00,
          0x8c.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x00,
          0x01,
          0x00,
          0x03,
          0x00,
          0x8d.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x06,
          0x02,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 bit reverse accepts canonical inline floating constants as raw bits") {
    assertEquals(DispatchInterpreter.bitReverse32(0x3f000000L), 0x000000fcL)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xf0.toByte,
          0x58,
          0x08,
          0x7e,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 unsigned 24-bit vector multiply and multiply-add mask their multiplicands") {
    assertEquals(DispatchInterpreter.unsignedMultiply24(0x1ffffffL, 2L), 0x1fffffeL)
    assertEquals(DispatchInterpreter.unsignedMultiplyAdd24(0xffffffL, 2L, 3L), 0x2000001L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x94.toByte,
          0x00,
          0x08,
          0x10,
          0x05,
          0x00,
          0xc3.toByte,
          0xd1.toByte,
          0x00,
          0x29,
          0x15,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 stride-64 DS pairs decode their independently scaled offsets") {
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x14,
          0x1e,
          0xd8.toByte,
          0x05,
          0x02,
          0x01,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DS bpermute selects a source lane from a byte address") {
    assertEquals(DispatchInterpreter.bpermuteSourceLane(0L), 0)
    assertEquals(DispatchInterpreter.bpermuteSourceLane(4L * 37L), 37)
    assertEquals(DispatchInterpreter.bpermuteSourceLane(4L * 69L), 5)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0x00,
          0x00,
          0x7e,
          0xd8.toByte,
          0x04,
          0x05,
          0x00,
          0x06,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 reverse unsigned vector subtraction wraps to 32 bits") {
    assertEquals(DispatchInterpreter.unsignedSubtractReverse32(20L, 19L), 0xffffffffL)
    assertEquals(DispatchInterpreter.unsignedSubtractReverse32(20L, 99L), 79L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](0x94.toByte, 0x06, 0x0a, 0x6c, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 signed 24-bit vector multiply sign-extends both operands") {
    assertEquals(DispatchInterpreter.signedMultiply24(0xffffd8L, 0xfffffeL), 80L)
    assertEquals(DispatchInterpreter.signedMultiply24(0x1000001L, 3L), 3L)
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xff.toByte,
          0x00,
          0x12,
          0x0c,
          0xd8.toByte,
          0xff.toByte,
          0xff.toByte,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DPP routes quad, row-rotate, and row-broadcast sources") {
    assertEquals(
      Col.tabulate(8)(lane => DispatchInterpreter.dppQuadSourceLane(lane, 0xb1)),
      Col(1, 0, 3, 2, 5, 4, 7, 6)
    )
    assert((0 until 64).forall(lane => DispatchInterpreter.dppLaneEnabled(lane, 0xf, 0xf)))
    assert(!DispatchInterpreter.dppLaneEnabled(0, 0xe, 0xf))
    assert(!DispatchInterpreter.dppLaneEnabled(1, 0xf, 0xd))
    assertEquals(
      Col(0, 4, 15, 16).map(DispatchInterpreter.dppSourceLane(_, 0x124)),
      Col(Some(12), Some(0), Some(11), Some(28))
    )
    assertEquals(
      Col(0, 16, 32, 48).map(DispatchInterpreter.dppSourceLane(_, 0x142)),
      Col(None, Some(15), None, Some(47))
    )
    assertEquals(
      Col(0, 16, 32, 48).map(DispatchInterpreter.dppSourceLane(_, 0x143)),
      Col(None, None, Some(31), Some(31))
    )
    assertEquals(
      DispatchInterpreter.validate(
        Col[Byte](
          0xfa.toByte,
          0x02,
          0x06,
          0x7e,
          0x01,
          0xb1.toByte,
          0x00,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x24,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x28,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x42,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x43,
          0x01,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target.unsafe("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 resolves VCC branches by byte address across mixed instruction widths") {
    val code = Col[Byte](
      0x80.toByte,
      0x02,
      0x00,
      0x7e,
      0x81.toByte,
      0x00,
      0x98.toByte,
      0x7d,
      0x01,
      0x00,
      0x86.toByte,
      0xbf.toByte,
      0x01,
      0x00,
      0x82.toByte,
      0xbf.toByte,
      0xff.toByte,
      0xff.toByte,
      0x82.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte
    )
    val kernel   = CodeObjectKernel("branch", Target.unsafe("gfx900"), 0, 0, 0, 0x80, 0x0a, code)
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "branch",
      Dimensions.One,
      Dimensions.One,
      Col.empty
    ).toOption.get

    assertEquals(DispatchInterpreter.execute(kernel, dispatch, TestMemory()), Right(()))
    val limited = ExecutionControl.create(ExecutionLimits(maxInstructions = 1L, quantum = 1))
    assert(
      DispatchInterpreter.execute(kernel, dispatch, TestMemory(), limited).left
        .exists(_.message.contains("instruction budget of 1"))
    )
  }

  test("gfx900 executes the official ROCr vector-copy kernel subset") {
    val words = Col[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code   = words.flatMap(word => LittleEndian.toCol(word, 4))
    val target = Target.unsafe("gfx900")
    val kernel = CodeObjectKernel("vector_copy", target, 80, 0, 0, 0x80, 0x0b, code)
    val memory = TestMemory()
    val input  = AllocationId.fromLong(1L).toOption.get
    val output = AllocationId.fromLong(2L).toOption.get
    val unused = AllocationId.fromLong(3L).toOption.get
    memory.allocate(input, 256)
    memory.allocate(output, 256)
    memory.allocate(unused, 256)
    val expected = Array.tabulate(64)(index => index * 97 - 11)
    expected.zipWithIndex.foreach { case (value, index) => memory.putInt(input, index * 4, value) }

    val zero      = KernelArgument.Scalar(Col.fill(8)(0.toByte))
    val arguments = Col(
      KernelArgument.Allocation(input, 0L),
      KernelArgument.Allocation(output, 0L),
      KernelArgument.Allocation(unused, 0L),
      zero,
      zero,
      zero,
      zero,
      zero,
      zero,
      zero
    )
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "vector_copy",
      Dimensions.create(64, 1, 1).toOption.get,
      Dimensions.One,
      arguments
    ).toOption.get

    assertEquals(DispatchInterpreter.execute(kernel, dispatch, memory), Right(()))
    assertEquals(Array.tabulate(64)(index => memory.getInt(output, index * 4)).toSeq, expected.toSeq)
  }

  private final class TestMemory extends ExecutionMemory {
    private var allocations = Map.empty[AllocationId, Array[Byte]]

    def allocate(id: AllocationId, size: Int): Unit = allocations = allocations.updated(id, Array.ofDim[Byte](size))

    def putInt(id: AllocationId, offset: Int, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset, 4)

    def getInt(id: AllocationId, offset: Int): Int =
      LittleEndian.decode(allocations(id), offset, 4).toInt

    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          ExecutionFailure.InvalidRequest("test read range")
        )
      }

    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { destination =>
        Either.cond(
          ByteRange.contains(destination.length, offset, bytes.length.toLong), {
            val copied = bytes.copyToArray(destination, offset.toInt, bytes.length)
            assertEquals(copied, bytes.length)
            ()
          },
          ExecutionFailure.InvalidRequest("test write range")
        )
      }
  }
}
