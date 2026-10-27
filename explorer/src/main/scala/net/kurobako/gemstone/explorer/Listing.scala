package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** A program's instructions, indexed by the location a step reports. */
case class Listing(lines: Vec[ProgramLine]) {

  /** The instruction the machine ran at `location`, preferring the routine the location names. */
  def find(location: Fault.Location): Option[ProgramLine] = location.instruction.flatMap { address =>
    find(location.function, address.raw)
  }

  /** The instruction at `address` in `function`, or at `address` in any routine when the ISA does not name one. */
  def find(function: Option[String], address: Long): Option[ProgramLine] =
    function.flatMap(name => lines.find(line => line.function.contains(name) && line.address == address))
      .orElse(lines.find(_.address == address))
}
