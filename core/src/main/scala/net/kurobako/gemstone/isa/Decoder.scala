package net.kurobako.gemstone.isa

trait Decoder[-Input, +P <: Program] {
  type Error
  def decode(input: Input): Either[Error, P]
}
