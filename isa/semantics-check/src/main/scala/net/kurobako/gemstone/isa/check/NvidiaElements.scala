package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*

object NvidiaElements {
  extension (element: Element) {

    /** The container `code` reads back from, its other bits clear. */
    def container(code: Long): Long = element match {
      case Element.TF32                => (code & 0x7ffffL) << 13
      case Element.E3M2 | Element.E2M3 => code & 0x3fL
      case Element.E2M1Byte            => (code & 0xfL) << 2
      case _                           => Bits.truncate(code, element.bits.widen)
    }
  }
}
