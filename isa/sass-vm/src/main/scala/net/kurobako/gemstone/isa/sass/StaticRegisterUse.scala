package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.RegisterUse

/** The registers each decoded instruction of a kernel reads and writes, from the lowered instructions
  * [[VMAssembler.source]] attributes to it. Slots the kernel names in its banks keep their spelling, `R` and `UR` for
  * the general and uniform ones and `P` and `UP` for the predicates, and each is one 32-bit unit: a wider operand
  * lowers as a slot per word. Slots the lowering keeps for itself, instruction temporaries and convergence barriers,
  * have no printed spelling and are left out: a temporary never outlives the instruction that made it, and no window
  * holds the instructions that name a barrier, as they have no checked cases. An instruction a predicate guards also
  * reads every slot it writes, so its write never ends a live range, and the condition flags an instruction reads or
  * writes are named as the fixed predicate slots they stand for.
  */
private object StaticRegisterUse {

  def of(
      program: ExecutableProgram,
      offsets: Vec[Long],
      layout: VMRegisters.Layout
  ): Vec[RegisterUse] = {
    val decoded = offsets.zipWithIndex.map((address, index) => address -> index).toMap
    val slots   = Array.fill(offsets.size)(RegisterUses.Slots.empty)
    program.instructions.indices.foreach { pc =>
      val attributed = program.source(Pc(pc.unsafe[Int >= 0])).flatMap(address => decoded.get(address.raw))
      attributed.foreach(index => slots(index) = slots(index).merge(RegisterUses.of(program.instructions(pc))))
    }
    Vec.from(slots).map { found =>
      val reads = found.registersRead.flatMap(layout.spelling) ++
        found.predicatesRead.flatMap(predicateName) ++ found.flagsRead.map(flagName)
      val writes = found.registersWritten.flatMap(layout.spelling) ++
        found.predicatesWritten.flatMap(predicateName) ++ found.flagsWritten.map(flagName)
      RegisterUse(reads.distinct, writes.distinct)
    }
  }

  private def predicateName(predicate: Predicate): Option[String] =
    if (predicate == VMRegisters.Predicate.Guard.slot) None
    else VMRegisters.spelling(predicate).filter(name => name != "PT" && name != "!PT")

  private def flagName(flag: VMConditionCodes.Flag): String =
    VMRegisters.spelling(VMRegisters.condition(flag)).getOrElse(flag.toString.toLowerCase)
}
