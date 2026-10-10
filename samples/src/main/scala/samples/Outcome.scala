package samples

import net.kurobako.gemstone.collection.*

/** What one sample saw: its assembled kernel's outputs, its disassembly, and the outputs after changing the kernel's
  * constant and after adding an instruction to it.
  */
case class Outcome(assembled: Vec[Long], disassembly: String, edited: Vec[Long], extended: Vec[Long])
