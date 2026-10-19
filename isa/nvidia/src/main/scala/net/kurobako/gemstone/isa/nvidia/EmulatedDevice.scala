package net.kurobako.gemstone.isa.nvidia

/** The emulated device's multiprocessors and the warps each holds, as the CUDA runtime reports them. */
private[isa] object EmulatedDevice {
  val Multiprocessors: Int        = 4
  val WarpsPerMultiprocessor: Int = 48
}
