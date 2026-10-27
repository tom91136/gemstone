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
    * `R<n>` or `P<n>`, so a value can still be followed from its definition to its use.
    */
  def result(routine: Routine): RegisterUse = RegisterUse(
    names(registersRead, routine.registerNames, "R", registerFallbacks) ++
      names(predicatesRead, routine.predicateNames, "P", predicateFallbacks),
    names(registersWritten, routine.registerNames, "R", registerFallbacks) ++
      names(predicatesWritten, routine.predicateNames, "P", predicateFallbacks)
  )

  private def names(
      bits: mutable.BitSet,
      declared: Vec[NonEmpty[String]],
      prefix: String,
      fallbacks: mutable.HashMap[Int, String]
  ): Vec[String] = {
    val builder = Vec.newBuilder[String]
    bits.foreach(slot =>
      builder += declared.lift(slot).fold(fallbacks.getOrElseUpdate(slot, s"$prefix$slot"))(_.raw)
    )
    builder.result()
  }
}
