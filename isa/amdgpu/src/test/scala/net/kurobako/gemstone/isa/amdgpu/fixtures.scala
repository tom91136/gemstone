package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object fixtures extends munit.Assertions {

  /** A kernel whose linked text is `code`, by default entered at address 0 and spanning all of it. */
  def codeKernel(
      name: NonEmpty[String] = "kernel",
      target: Target = Target("gfx900"),
      kernargSize: Int >= 0 = 0,
      groupSegmentSize: Int >= 0 = 0,
      privateSegmentSize: Int >= 0 = 0,
      computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1,
      computePgmRsrc2: Int = 0,
      kernelCodeProperties: Int = 0,
      code: Vec[Byte] = Vec.empty,
      textAddress: Long = 0L,
      entry: Long = 0L,
      entrySize: Option[Int >= 0] = None,
      functionSizes: Map[Long, Int] = Map.empty,
      functionTargets: Map[Long, String] = Map.empty,
      readOnlyAddress: Long = 0L,
      readOnly: Array[Byte] = Array.emptyByteArray,
      globalMemory: Array[(Long, Array[Byte])] = Array.empty
  ): CodeObjectKernel = CodeObjectKernel(
    name,
    target,
    kernargSize,
    groupSegmentSize,
    privateSegmentSize,
    computePgmRsrc2,
    kernelCodeProperties,
    text = code,
    entrySize = entrySize.getOrElse(code.nonNegativeSize),
    textAddress = textAddress,
    entry = entry,
    functionSizes = functionSizes,
    functionTargets = functionTargets,
    readOnlyAddress = readOnlyAddress,
    readOnly = readOnly,
    globalMemory = globalMemory,
    computePgmRsrc1 = computePgmRsrc1
  )

  /** Little-endian machine words, as LLVM prints instruction dwords. */
  def words(values: Int*): Vec[Byte] = Vec.unsafeWrapArray(bytes.words(values*))

  def decoded(target: Target, code: Vec[Byte])(using munit.Location): VMIR.Instruction =
    VMCompiler.decodeInstruction(code, target).fold(error => fail(error.raw), identity)

  def decoded(target: Target, code: Int*)(using munit.Location): VMIR.Instruction =
    decoded(target, Vec.from(code.map(_.toByte)))

  def singleTask(arguments: KernelTask.Argument*)(using munit.Location): KernelTask =
    KernelTask.validated("kernel", Dimensions.One, Dimensions.One, Vec.from(arguments)).value

  /** Lowered instructions laid out one dword apart, entered at the first. */
  def irKernel(operations: Vec[VMIR.Instruction], source: CodeObjectKernel = codeKernel()): VMIR.Kernel = {
    val offsets = Vec.tabulate(operations.size)(_ * 4)
    VMIR.Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
  }

  def runIR(
      operations: Vec[VMIR.Instruction],
      memory: AddressSpace,
      source: CodeObjectKernel = codeKernel(),
      task: KernelTask = singleTask()
  ): Either[Fault, Unit] = executeKernel(irKernel(operations, source), task, memory)

  def compileKernel(kernel: CodeObjectKernel): VMIR.Kernel =
    VMCompiler().compile(kernel).fold(error => fail(error.message), identity)

  def executeKernel(
      kernel: VMIR.Kernel,
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[Fault, Unit] =
    VMInterpreter.execute(kernel, task, memory, control, workgroups, Vec.empty, _ => Right(()))

  def minimalCodeObject(globalPointer: Boolean = false): Array[Byte] =
    elfImage.amdgpu(bytes.words(0xbf810000), kernargSize = 24, kernelCodeProperties = 0x0a, globalPointer)
}
