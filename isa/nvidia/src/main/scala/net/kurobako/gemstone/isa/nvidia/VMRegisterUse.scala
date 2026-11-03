package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** The register and predicate slots one issue touched, collected as sets and named as the issuing routine names them.
  * The recorder is reused from one issue to the next, so collecting allocates only when a set grows.
  */
private class VMRegisterUse {
  private val registersRead     = mutable.BitSet.empty
  private val registersWritten  = mutable.BitSet.empty
  private val predicatesRead    = mutable.BitSet.empty
  private val predicatesWritten = mutable.BitSet.empty
  // A slot the routine does not name still needs a stable name, allocated once rather than per issue.
  private val registerFallbacks  = mutable.HashMap.empty[Int, String]
  private val predicateFallbacks = mutable.HashMap.empty[Int, String]

  def readRegister(register: Register): Unit     = registersRead += register.raw
  def writeRegister(register: Register): Unit    = registersWritten += register.raw
  def readPredicate(predicate: Predicate): Unit  = predicatesRead += predicate.raw
  def writePredicate(predicate: Predicate): Unit = predicatesWritten += predicate.raw

  /** Forgets the previous issue, so the recorder can collect the next one. */
  def clear(): Unit = {
    registersRead.clear()
    registersWritten.clear()
    predicatesRead.clear()
    predicatesWritten.clear()
  }

  /** What the issue touched, by the names `routine` declares its slots under. Slots the routine does not name read as
    * `R<n>` or `P<n>`, so a value can still be followed from its definition to its use. Predicates take no 32-bit
    * units, and a register takes the width the routine declares for its slot.
    */
  def result(routine: Routine): RegisterUse = {
    val units                                        = mutable.HashMap.empty[String, Int]
    def registers(bits: mutable.BitSet): Vec[String] =
      names(bits, routine.registerNames, "R", registerFallbacks, routine.registerUnits, 1, units)
    def predicates(bits: mutable.BitSet): Vec[String] =
      names(bits, routine.predicateNames, "P", predicateFallbacks, Vec.empty, 0, units)
    RegisterUse(
      registers(registersRead) ++ predicates(predicatesRead),
      registers(registersWritten) ++ predicates(predicatesWritten),
      if (units.isEmpty) Map.empty else units.toMap
    )
  }

  private def names(
      bits: mutable.BitSet,
      declared: Vec[NonEmpty[String]],
      prefix: String,
      fallbacks: mutable.HashMap[Int, String],
      widths: Vec[Int],
      default: Int,
      units: mutable.HashMap[String, Int]
  ): Vec[String] = {
    val builder = Vec.newBuilder[String]
    bits.foreach { slot =>
      val name  = declared.lift(slot).fold(fallbacks.getOrElseUpdate(slot, s"$prefix$slot"))(_.raw)
      val width = widths.lift(slot).getOrElse(default)
      if (width != 1) units(name) = width
      builder += name
    }
    builder.result()
  }
}
