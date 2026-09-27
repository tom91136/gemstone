package net.kurobako.gemstone.isa

/** A failure raised while decoding a program image or text.
  *
  * Every ISA's decoder error type implements this so callers can report a decode failure without knowing which
  * front-end produced it.
  */
trait DecodeError {
  def message: String
}
