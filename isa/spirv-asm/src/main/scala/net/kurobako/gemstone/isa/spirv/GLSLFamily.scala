package net.kurobako.gemstone.isa.spirv

/** The staged semantics families of GLSL.std.450, as [[CoreFamily]] orders them. */
enum GLSLFamily derives CanEqual {
  case Integer, Trigonometric, Elementary, Floating, Vector, Geometric
}
