package net.kurobako.gemstone.isa.spirv

object ExtendedSet {
  def of(name: String): Option[ExtendedSet] = values.find(_.name == name)
}

/** The extended instruction sets with staged semantics, by the name a module imports each under. */
enum ExtendedSet(val name: String) derives CanEqual {
  case GLSL   extends ExtendedSet("GLSL.std.450")
  case OpenCL extends ExtendedSet("OpenCL.std")
}
