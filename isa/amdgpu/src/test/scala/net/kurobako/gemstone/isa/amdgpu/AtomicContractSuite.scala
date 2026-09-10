package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.execution.*

/** RDNA2 ISA section12.18: GLC gates return; DATA has replacement before comparison; unsigned INC resets when the
  * original value is at least its limit. Binary provenance: atomic-contract/kernel.s.
  */
final class AtomicContractSuite extends munit.FunSuite {
  private val allocation = AllocationId.fromLong(1).toOption.get
  private class Memory(groups: Int, old: Long) extends ExecutionMemory {
    val bytes = Array.ofDim[Byte](16 + groups * 8)
    LittleEndian.write(old, bytes, 0, 8)
    def read(id: AllocationId, offset: Long, size: Int) =
      Either.cond(
        id == allocation && ByteRange.contains(bytes.length, offset, size),
        Col.from(bytes.slice(offset.toInt, offset.toInt + size)),
        ExecutionFailure.InvalidRequest("test read bounds")
      )
    def write(id: AllocationId, offset: Long, value: Col[Byte]) = {
      assert(id == allocation && ByteRange.contains(bytes.length, offset, value.size))
      val _ = value.copyToArray(bytes, offset.toInt, value.size); Right(())
    }
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  for {
    op        <- Seq("add", "cas", "flatcas", "inc")
    width     <- Seq(32, 64)
    returning <- Seq(false, true)
    groups    <- Seq(1, 5)
    scenario  <- if (op == "inc") Seq("below", "equal", "above", "high")
    else if (op.endsWith("cas")) Seq("equal", "different", "same-operands") else Seq("add")
  } test(s"$op width=$width return=$returning groups=$groups scenario=$scenario") {
    val mask = if (width == 64) -1L else 0xffffffffL
    val old  =
      (scenario match {
        case "below" => 9L; case "equal" if op == "inc"               => 10L; case "above" => 11L;
        case "high"  => Long.MinValue | 0x80000000L; case "different" => 9L; case _        => 7L
      }) & mask
    val operand  = if (op == "inc") 10L else if (scenario == "same-operands") 7L else 9L
    val code     = AtomicContractCode.bytes(op, width, returning)
    val kernel   = CodeObjectKernel("atomic", Target.unsafe("gfx1030"), 24, 0, 0, 0x80, 0x08, code)
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1).toOption.get,
      "atomic",
      Dimensions.One,
      Dimensions.create(groups, 1, 1).toOption.get,
      Col(
        KernelArgument.Allocation(allocation, 0),
        KernelArgument.Scalar(LittleEndian.toCol(operand, 8)),
        KernelArgument.Scalar(LittleEndian.toCol(7, 8))
      )
    ).toOption.get
    val memory   = Memory(groups, old)
    val expected = (0 until groups).scanLeft(old) { (previous, _) =>
      if (op == "add") (previous + operand) & mask
      else if (op == "inc") { if (java.lang.Long.compareUnsigned(previous, operand) >= 0) 0L else previous + 1 }
      else if (previous == 7L) operand else previous
    }
    assertEquals(
      DispatchInterpreter.execute(
        DispatchInterpreter.prepare(kernel).toOption.get,
        dispatch,
        memory,
        ExecutionControl.default
      ),
      Right(())
    )
    assertEquals(LittleEndian.decode(memory.bytes, 0, width / 8), expected.last)
    val result  = (0 until groups).map(index => LittleEndian.decode(memory.bytes, 16 + index * 8, 8))
    val returns = expected.dropRight(1).map(value =>
      if (!returning) 0x7654321012345678L else if (width == 32) 0x7654321000000000L | value else value
    )
    assertEquals(result.sorted, returns.sorted)
  }
}
