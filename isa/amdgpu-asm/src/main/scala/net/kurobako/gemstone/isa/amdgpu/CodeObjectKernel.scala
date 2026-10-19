package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

private object CodeObjectKernel {

  /** The HSA defaults: IEEE mode with no FP32, FP16 or FP64 denormals flushed. */
  val DefaultComputePgmRsrc1: Int = (1 << 23) | (0xf << 16)
}

/** One dispatchable kernel: descriptor fields, its entry in the linked executable text, and the code-object globals.
  * The entry spans `entrySize` bytes from `entry`; device calls reach other functions of the text through
  * `functionSizes` (address to size).
  */
private case class CodeObjectKernel(
    name: NonEmpty[String],
    target: Target,
    kernargSize: Int >= 0,
    groupSegmentSize: Int >= 0,
    privateSegmentSize: Int >= 0,
    computePgmRsrc2: Int,
    kernelCodeProperties: Int,
    text: Vec[Byte],
    entrySize: Int >= 0,
    textAddress: Long = 0L,
    entry: Long = 0L,
    functionSizes: Map[Long, Int] = Map.empty,
    functionTargets: Map[Long, String] = Map.empty,
    readOnlyAddress: Long = 0L,
    // kernels are never compared; these images back the VM's memory without copying
    @SuppressWarnings(Array("scalafix:CaseClassEquality"))
    readOnly: Array[Byte] = Array.emptyByteArray,
    /** Device globals share these mutable section images across all kernels in one code object. */
    @SuppressWarnings(Array("scalafix:CaseClassEquality"))
    globalMemory: Array[(Long, Array[Byte])] = Array.empty,
    computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1
) {
  val waveSize: Int in (32, 64) =
    if (
      TargetDefaults.waveSize(target) == 32 &&
      (kernelCodeProperties & KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32) != 0
    ) 32
    else 64

  // Most kernels never execute a device call.
  lazy val emulatedCalls: Map[Long, EmulatedCall] = functionTargets.flatMap { case (address, function) =>
    EmulatedCall.matching(function).headOption.map(address -> _)
  }
  val globalMemoryLocks: Array[ReentrantLock] = Array.fill(globalMemory.length)(ReentrantLock())

  /** MODE as a wave starts: FP_DENORM from FLOAT_DENORM_MODE_32 and _16_64, and IEEE where GFX12 has not retired it. */
  val initialMode: Int = {
    val denormals = ((computePgmRsrc1 >>> 16) & 0xf) << ModeRegister.DenormalShift
    val ieee      = ModeRegister.ieeeSupported(target.isa) && (computePgmRsrc1 & (1 << 23)) != 0
    denormals | (if (ieee) ModeRegister.IEEE else 0)
  }
}
