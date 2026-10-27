package net.kurobako.gemstone.explorer

/** One instruction of a loaded program: where the machine addresses it, and how it is written. `address` is the ISA's
  * own instruction address, the one `Fault.Location.instruction` reports, and `function` is the routine it belongs to
  * when the ISA numbers instructions within a routine.
  */
case class ProgramLine(function: Option[String], address: Long, text: String, mnemonic: String)
