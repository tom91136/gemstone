package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** RDNA2 ISA section12.18: GLC gates return; DATA has replacement before comparison; unsigned INC resets when the
  * original value is at least its limit.
  */
class AtomicOperationSuite extends munit.FunSuite {
  private val allocation = AllocationId(1)

  private val bothWidths   = Seq(32, 64)
  private val returnFlags  = Seq(false, true)
  private val groupCounts  = Vec[Int > 0](1, 5)
  private val incScenarios = Seq("below", "equal", "above", "high")
  private val casScenarios = Seq("equal", "different", "same-operands")

  for {
    op <- Seq(
      "add",
      "cas",
      "flatcas",
      "flatswap",
      "flatadd",
      "flatsub",
      "flatmin",
      "flatmax",
      "flatand",
      "flator",
      "flatxor",
      "flatinc",
      "inc"
    )
    width <-
      if (op.startsWith("flat") && op != "flatcas" && op != "flatswap" && op != "flatinc") Seq(32) else bothWidths
    returning <- returnFlags
    groups    <- groupCounts
    scenario  <- if (op.endsWith("inc")) incScenarios else if (op.endsWith("cas")) casScenarios else Seq("add")
  } test(s"$op width=$width return=$returning groups=$groups scenario=$scenario") {
    val mask = if (width == 64) -1L else 0xffffffffL
    val old  =
      (scenario match {
        case "below" => 9L; case "equal" if op.endsWith("inc")        => 10L; case "above" => 11L;
        case "high"  => Long.MinValue | 0x80000000L; case "different" => 9L; case _        => 7L
      }) & mask
    val operand = if (op.endsWith("inc")) 10L else if (scenario == "same-operands") 7L else 9L
    val code    = atomic.bytes(op, width, returning)
    val kernel  = codeKernel(
      target = Target("gfx1030"),
      kernargSize = 24,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code
    )
    val dispatch = KernelTask.validated(
      "atomic",
      Dimensions.One,
      testDimensions(groups),
      Vec(
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(operand, 8).unsafe),
        KernelTask.Argument.Scalar(LittleEndian.toVec(7, 8).unsafe)
      )
    ).value
    val memory = RegionMemory(allocation -> (16 + groups.raw * 8))
    LittleEndian.write(old, memory.array(allocation), 0, 8)
    // The reference model boxes one seed per test case.
    @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
    val expected = (0 until groups).scanLeft(old) { (previous, _) =>
      if (op == "add" || op == "flatadd") (previous + operand) & mask
      else if (op.endsWith("inc")) { if (java.lang.Long.compareUnsigned(previous, operand) >= 0) 0L else previous + 1 }
      else if (op == "flatswap") operand & mask
      else if (op == "flatsub") (previous - operand) & mask
      else if (op == "flatmin") Math.min(previous, operand)
      else if (op == "flatmax") Math.max(previous, operand)
      else if (op == "flatand") previous & operand
      else if (op == "flator") previous | operand
      else if (op == "flatxor") previous ^ operand
      else if (previous == 7L) operand else previous
    }
    assertEquals(
      executeKernel(compileKernel(kernel), dispatch, memory),
      Right(())
    )
    val byteWidth: Int in 0 ~ 8 = if (width == 64) 8 else 4
    assertEquals(LittleEndian.decode(memory.array(allocation), 0, byteWidth), expected.last)
    val result = (0 until groups).map(index =>
      LittleEndian.decode(memory.array(allocation), 16 + index * 8, 8)
    )
    val returns = expected.dropRight(1).map(value =>
      if (!returning) 0x7654321012345678L else if (width == 32) 0x7654321000000000L | value else value
    )
    assertEquals(result.sorted, returns.sorted)
  }
}
