package net.kurobako.gemstone.isa.spirv

/** The end of a pipe an operation claims packets at. */
private enum PipeEnd derives CanEqual {
  case Read, Write
}
