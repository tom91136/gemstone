package net.kurobako.gemstone.isa

trait Decoder[-Input, +P <: Program] {
  type Error <: DecodeError
  def decode(input: Input): Either[Error, P]
}
