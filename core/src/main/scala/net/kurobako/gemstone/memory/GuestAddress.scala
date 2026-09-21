package net.kurobako.gemstone.memory

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type GuestAddress = GuestAddress.T
object GuestAddress extends RefinedType[Long, Pure] {
  extension (address: GuestAddress) {

    /** Device addresses use unsigned 64-bit modular arithmetic. Bounds are checked by the owning memory view. */
    def offset(bytes: Long): GuestAddress = GuestAddress(address.value + bytes)
    def show: String                      = s"0x${java.lang.Long.toUnsignedString(address.value, 16)}"
  }
}
