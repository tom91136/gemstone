package net.kurobako.gemstone.isa.amdgpu

/** Process settings sampled once for interpreter hot paths. */
private[amdgpu] object AMDGPUEnvironment {
  private val traceNames = sys.env.get("GEMSTONE_AMDGPU_TRACE").iterator
    .flatMap(_.split(','))
    .map(_.trim.toLowerCase)
    .toSet
  private val names = sys.env.keySet

  def trace(name: String): Boolean       = traceNames.contains("all") || traceNames.contains(name)
  def containsKey(name: String): Boolean = names.contains(name)
}
