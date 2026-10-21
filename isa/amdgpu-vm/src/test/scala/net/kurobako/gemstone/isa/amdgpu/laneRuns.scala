package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Runs lowered instructions on the lanes of one workgroup, each lane's inputs loaded into v2 onwards. */
private object laneRuns extends munit.Assertions {
  private val Stride = 64

  /** LLVM machine code, decoded as a program so that sequences lower together. */
  def program(target: Target, code: Vec[Byte])(using munit.Location): Vec[Instruction] =
    VMCompiler.decodeProgram(code, target, TargetDefaults.waveSize(target)).fold(error => fail(error.raw), _.operations)

  def octets(values: Int*): Vec[Byte] = Vec.from(values.map(_.toByte))

  /** Each lane's dword in `result` after `operations`, with lane `i` reading `inputs(i)` from `first` onwards, `offset`
    * holding its byte offset and `base` the base of the lanes' memory. EXEC starts as `exec`.
    */
  def laneResults(
      target: Target,
      inputs: Vec[Vec[Long]],
      operations: Vec[Instruction],
      result: VectorRegisterIndex = 2,
      exec: Long = -1L,
      wave32: Boolean = true,
      groupSegmentSize: Int >= 0 = 0,
      computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1,
      first: VectorRegisterIndex = 2,
      offset: VectorRegisterIndex = 1,
      base: ScalarRegisterIndex = 6
  )(using munit.Location): Vec[Long] = {
    val count      = inputs.size
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, count * Stride)
    inputs.zipWithIndex.foreach { (values, lane) =>
      values.zipWithIndex.foreach((value, index) =>
        memory.putInt(allocation, (lane * Stride + index * 4).unsafe[Int >= 0], value.toInt)
      )
    }
    val address = memory.address(allocation, 0L).value.raw
    import InstructionSource.{VectorRegister as Register, *}
    val width = inputs.map(_.size).maxOption.getOrElse(0)
    val loads = Vec.tabulate(width)(index =>
      Instruction.Load(
        (first + index).unsafe[VectorRegisterIndex],
        MemoryAddress.Global(offset, Some(base), index * 4),
        4,
        false,
        LoadPlacement.Full
      )
    )
    val waves  = wave32 && (target.isa.isRDNA || target.isa == ISA.CDNA5)
    val all    = if (waves) 0xffffffffL else -1L
    val prefix = Vec(
      Instruction.SMove64(base, Constant(address)),
      Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, offset, Constant(6), Register(0), false)
    ) ++ loads :+ Instruction.SMove64(Fixed.ExecLo, Constant(exec & all))
    val suffix = Vec(
      Instruction.SMove64(Fixed.ExecLo, Constant(all)),
      Instruction.Store(result, MemoryAddress.Global(offset, Some(base), Stride - 4), 4, false),
      Instruction.End
    )
    val source = codeKernel(
      target = target,
      groupSegmentSize = groupSegmentSize,
      computePgmRsrc1 = computePgmRsrc1,
      kernelCodeProperties = if (waves) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0
    )
    val task = KernelTask.validated("kernel", Dimensions(count.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(runIR(prefix ++ operations ++ suffix, memory, source, task), Right(()))
    Vec.tabulate(count)(lane =>
      memory.getInt(allocation, (lane * Stride + Stride - 4).unsafe[Int >= 0]).toLong & 0xffffffffL
    )
  }

  /** Each lane's dword in `result` after the LLVM machine code `code`, as [[laneResults]] runs it. */
  def encoded(target: Target, code: Vec[Int], inputs: Vec[Vec[Long]], result: Int = 5, wave32: Boolean = true)(using
      munit.Location
  ): Vec[Long] =
    laneResults(target, inputs, program(target, octets(code*)), result.unsafe[VectorRegisterIndex], wave32 = wave32)

  /** The dword in `result` after `operations` on one lane whose inputs are `inputs`. */
  def laneResult(target: Target, inputs: Vec[Long], operations: Vec[Instruction], result: VectorRegisterIndex = 2)(
      using munit.Location
  ): Long = laneResults(target, Vec(inputs), operations, result).head

  /** The scalar dword `result` after `operations` with s0 onwards holding `scalars`. */
  def scalarResult(target: Target, scalars: Vec[Long], operations: Vec[Instruction], result: ScalarRegisterIndex = 0)(
      using munit.Location
  ): Long = {
    import InstructionSource.*
    val setup = scalars.zipWithIndex.map((value, index) =>
      Instruction.SMove(index.unsafe[ScalarRegisterIndex], Constant(value & 0xffffffffL))
    )
    laneResult(target, Vec.empty, setup ++ operations :+ Instruction.VMove(2, Scalar(result)))
  }
}
