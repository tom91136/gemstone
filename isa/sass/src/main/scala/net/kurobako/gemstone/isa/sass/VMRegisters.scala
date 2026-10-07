package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*

/** Disjoint dense register banks for lowered SASS, independent of its printed register spelling. */
private object VMRegisters {
  val PhysicalPredicateCount = 7
  val TemporaryCount         = 64
  private val BankSize       = 256
  private val BarrierCount   = 16

  private def slot(base: Int, index: Int): VMIR.Register = VMIR.Register((base + index).unsafe[Int >= 0])

  def general(index: Int in 0 ~ 255): VMIR.Register = slot(0, index.raw)
  def uniform(index: Int in 0 ~ 255): VMIR.Register = slot(BankSize, index.raw)
  def temporary(index: Int): VMIR.Register          = slot(BankSize * 2, index)

  def barrier(index: Int in 0 ~ 15): VMIR.Register = slot(BankSize * 2 + TemporaryCount, index.raw)

  /** Shadow registers holding range-reduced MUFU operands, one per reduction. */
  def reduction(index: Int): VMIR.Register = slot(BankSize * 2 + TemporaryCount + BarrierCount, index)

  def predicate(index: Int in 0 ~ 6, uniform: Boolean): VMIR.Predicate =
    VMIR.Predicate((index.raw + (if (uniform) PhysicalPredicateCount else 0)).unsafe[Int >= 0])

  def temporaryPredicate(index: Int): VMIR.Predicate =
    VMIR.Predicate((PhysicalPredicateCount * 2 + index).unsafe[Int >= 0])

  enum Predicate {
    case True, False, Guard, Barrier, Carry, Zero, Sign, Overflow, Equal, LessThan

    val slot: VMIR.Predicate = VMIR.Predicate((PhysicalPredicateCount * 2 + TemporaryCount + ordinal).unsafe[Int >= 0])
  }

  def condition(flag: VMConditionCodes.Flag): VMIR.Predicate = (flag match {
    case VMConditionCodes.Flag.Carry    => Predicate.Carry
    case VMConditionCodes.Flag.Zero     => Predicate.Zero
    case VMConditionCodes.Flag.Sign     => Predicate.Sign
    case VMConditionCodes.Flag.Overflow => Predicate.Overflow
    case VMConditionCodes.Flag.Equal    => Predicate.Equal
    case VMConditionCodes.Flag.LessThan => Predicate.LessThan
  }).slot
}
