package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.constraint.*
import upickle.default.ReadWriter

/** `bits` value bits stored from word bit `offset`, after `padding` implied low zero bits of the value. */
case class BitRange(offset: Int >= 0, bits: Int in 1 ~ 64, padding: Int >= 0) derives ReadWriter
