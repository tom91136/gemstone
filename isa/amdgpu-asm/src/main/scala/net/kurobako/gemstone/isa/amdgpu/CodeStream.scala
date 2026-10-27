package net.kurobako.gemstone.isa.amdgpu

/** The bytes of a code object and the mode they decode under. */
case class CodeStream(bytes: Array[Byte], mode: TargetMode) {
  def target: Target = mode.target
}
