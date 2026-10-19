package net.kurobako.gemstone.isa.spirv

/** The staged semantics families of OpenCL.std, as [[CoreFamily]] orders them. */
enum OpenCLFamily derives CanEqual {
  case Integer, InverseTrigonometric, Trigonometric, Exponential, Elementary, Native, Binary, Order, Ternary, Mixed,
    Half
}
