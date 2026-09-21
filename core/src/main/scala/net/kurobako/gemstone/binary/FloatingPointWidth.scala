package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*

private[gemstone] type FloatingPointWidth = FloatingPointWidth.T
private[gemstone] object FloatingPointWidth extends RefinedType[Int, In[(16, 32, 64)]] {
  extension (width: FloatingPointWidth) {

    /** Every supported IEEE-754 interchange width is also a valid scalar bit width. */
    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    def bitWidth: BitWidth = BitWidth.assume(width.value)

    /** Every supported IEEE-754 interchange width is byte-aligned and at most eight bytes wide. */
    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    def scalarByteWidth: ScalarByteWidth = ScalarByteWidth.assume(width.value / 8)
  }
}
