package net.kurobako.gemstone.isa.amdgpu

/** The bytes of a code object and the mode they decode under. */
class CodeStream(val bytes: Array[Byte], val mode: TargetMode) {
  def target: Target = mode.target
}
