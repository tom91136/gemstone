package net.kurobako.gemstone.isa

trait Encoder[-Input, +Output, +Error] {
  def encode(input: Input): Either[Error, Output]
}
