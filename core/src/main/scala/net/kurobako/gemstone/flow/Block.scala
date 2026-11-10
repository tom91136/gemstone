package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*

case class Block(first: Int, last: Int, successors: Vec[Int], open: Boolean)
