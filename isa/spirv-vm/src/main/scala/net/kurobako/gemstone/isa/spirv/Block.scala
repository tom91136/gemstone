package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.vm.*

private object Block {
  def resolved(label: Int, pc: Either[Fault, Int]): Block = pc match {
    case Right(found) => Block(label, found, None)
    case Left(fault)  => Block(label, -1, Some(fault))
  }

  def named(label: Int): Block = Block(label, -1, None)
}

/** A block a branch names: its label, where it starts, and the fault finding it raised if the function has no such
  * block. A block that stands for a label alone, to render, starts nowhere.
  */
private class Block(val label: Int, val pc: Int, val fault: Option[Fault])
