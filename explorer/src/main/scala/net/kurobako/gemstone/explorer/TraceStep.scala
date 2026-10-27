package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** One issue a launch made: its place in the launch's issue stream, where in the program it was, the unit that made it,
  * the values the machine held at that moment, and the registers its instruction read and wrote.
  */
case class TraceStep(
    issue: Long,
    location: Fault.Location,
    unit: MachineUnit,
    values: Vec[RegisterValue],
    use: RegisterUse = RegisterUse.empty
)
