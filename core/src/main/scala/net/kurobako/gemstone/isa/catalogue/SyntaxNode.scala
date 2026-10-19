package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.collection.*
import upickle.default.ReadWriter

/** A FORMAT node as stored in a catalogue: strings and children are ids into the catalogue's tables. */
enum SyntaxNode derives ReadWriter {
  case Text(raw: Int)
  case Literal(value: Int)
  case Field(kind: Int, binding: Int, arguments: Option[Int], default: Option[Int])
  case Group(open: Int, close: Int, children: Vec[Int])
  case Optional(marker: Option[Int], children: Vec[Int])
}
