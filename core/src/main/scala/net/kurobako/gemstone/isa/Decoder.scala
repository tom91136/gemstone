package net.kurobako.gemstone.isa

/** Decodes an input into an ISA-specific value, retaining its concrete diagnostic type. */
trait Decoder[-Input, +Output, +Error] {
  def decode(input: Input): Either[Error, Output]
}
