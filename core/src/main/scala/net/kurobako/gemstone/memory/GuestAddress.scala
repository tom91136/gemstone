package net.kurobako.gemstone.memory

import net.kurobako.gemstone.constraint.*
import scala.annotation.targetName

/** A device-visible guest address. */
type GuestAddress = GuestAddress.Type
object GuestAddress extends Newtype[Long] {

  @targetName("fromNonNegativeLong")
  inline def apply(inline value: Long >= 0): GuestAddress = apply(value.raw)

  @targetName("fromPositiveLong")
  inline def apply(inline value: Long > 0): GuestAddress = apply(value.raw)

  extension (address: GuestAddress) {

    /** Device addresses use unsigned 64-bit modular arithmetic. Bounds are checked by the owning memory view. */
    def offset(bytes: Long): GuestAddress = GuestAddress(address.raw + bytes)

    def show: String = s"0x${java.lang.Long.toUnsignedString(address.raw, 16)}"
  }
}
