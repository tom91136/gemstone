package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import java.util.concurrent.locks.ReentrantLock

/** RDNA2 ISA section12.18: GLC gates return; DATA has replacement before comparison; unsigned INC resets when the
  * original value is at least its limit.
  */
final class AtomicOperationSuite extends munit.FunSuite with CodeObjectTestSupport {
  private val allocation = MemoryRegion(1)
  private final class Memory(groups: Int, old: Long) extends net.kurobako.gemstone.vm.Memory with TestAddressSpace {
    val bytes                                                          = Array.ofDim[Byte](16 + groups * 8)
    private val lock                                                   = ReentrantLock()
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] = Seq(allocation -> bytes.length.toLong)
    LittleEndian.write(old, bytes, 0, ScalarByteWidth.Qword)
    def read(id: MemoryRegion, offset: Long :| Positive0, size: Int :| Positive0) =
      Either.cond(
        id == allocation && ByteRange.contains(bytes.length, offset, size),
        Vec.from(bytes.slice(offset.toInt, offset.toInt + size)),
        Fault.InvalidTask("test read bounds")
      )
    def write(id: MemoryRegion, offset: Long :| Positive0, value: Vec[Byte]) = {
      assert(id == allocation && ByteRange.contains(bytes.length, offset, value.size))
      val _ = value.copyToArray(bytes, offset.toInt, value.size); Right(())
    }
    override def atomicModifyValue(
        id: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[Fault, Long] = lock.withLock {
      val scalarWidth = size.scalarByteWidth
      if (id == allocation && ByteRange.contains(bytes.length, offset, size.value)) {
        val arrayOffset = offset.toInt
        val previous    = LittleEndian.decode(bytes, arrayOffset, scalarWidth)
        LittleEndian.write(update(previous), bytes, arrayOffset, scalarWidth)
        Right(previous)
      } else Left(Fault.InvalidTask("test atomic bounds"))
    }
  }
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
      "inc"
    )
    width     <- if (op.startsWith("flat") && op != "flatcas" && op != "flatswap") Seq(32) else Seq(32, 64)
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
    val code     = AtomicOperationCode.bytes(op, width, returning)
    val kernel   = CodeObjectKernel("atomic", Target("gfx1030"), 24, 0, 0, 0x80, 0x08, code)
    val dispatch = KernelTask.validated(
      KernelName("atomic"),
      Dimensions.One,
      testDimensions(groups),
      Vec(
        KernelTask.Argument.Memory(allocation, 0),
        KernelTask.Argument.Scalar(LittleEndian.toVec(operand, ScalarByteWidth.Qword)),
        KernelTask.Argument.Scalar(LittleEndian.toVec(7, ScalarByteWidth.Qword))
      )
    ).toOption.get
    val memory   = Memory(groups, old)
    val expected = (0 until groups).scanLeft(old) { (previous, _) =>
      if (op == "add" || op == "flatadd") (previous + operand) & mask
      else if (op == "inc") { if (java.lang.Long.compareUnsigned(previous, operand) >= 0) 0L else previous + 1 }
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
      executeKernel(
        VMCompiler.compile(kernel).fold(error => fail(error.message), identity),
        dispatch,
        memory,
        TaskControl.default
      ),
      Right(())
    )
    val byteWidth = if (width == 64) ScalarByteWidth.Qword else ScalarByteWidth.Dword
    assertEquals(LittleEndian.decode(memory.bytes, 0, byteWidth), expected.last)
    val result = (0 until groups).map(index =>
      LittleEndian.decode(memory.bytes, 16 + index * 8, ScalarByteWidth.Qword)
    )
    val returns = expected.dropRight(1).map(value =>
      if (!returning) 0x7654321012345678L else if (width == 32) 0x7654321000000000L | value else value
    )
    assertEquals(result.sorted, returns.sorted)
  }
}
