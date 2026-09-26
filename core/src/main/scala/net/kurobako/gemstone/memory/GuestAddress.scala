package net.kurobako.gemstone.memory

import net.kurobako.gemstone.{NonNegativeLong, PositiveLong}

import scala.annotation.targetName

/** A device-visible guest address. */
opaque type GuestAddress = Long

object GuestAddress {
  inline def apply(inline value: Long): GuestAddress = value

  @targetName("fromNonNegativeLong")
  inline def apply(inline value: NonNegativeLong): GuestAddress = value.raw

  @targetName("fromPositiveLong")
  inline def apply(inline value: PositiveLong): GuestAddress = value.raw

  given Ordering[GuestAddress] = Ordering.Long

  extension (address: GuestAddress) {
    inline def raw: Long = address

    /** Device addresses use unsigned 64-bit modular arithmetic. Bounds are checked by the owning memory view. */
    def offset(bytes: Long): GuestAddress = GuestAddress(address + bytes)

    def show: String = s"0x${java.lang.Long.toUnsignedString(address, 16)}"
  }
}
