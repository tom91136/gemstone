package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.RegisterUse

object CodeKernels {

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

  /** Lowered instructions laid out one dword apart, entered at the first. */
  def irKernel(operations: Vec[VMIR.Instruction], source: CodeObjectKernel = codeKernel()): VMIR.Kernel = {
    val offsets = Vec.tabulate(operations.size)(_ * 4)
    VMIR.Kernel(
      source,
      operations,
      offsets.zipWithIndex.toMap,
      offsets,
      0,
      Vec.fill(operations.size)(RegisterUse.empty)
    )
  }
}
