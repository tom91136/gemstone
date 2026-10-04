package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*

/** Disjoint dense register banks for lowered SASS, independent of its printed register spelling. */
private object VMRegisters {
  val PhysicalPredicateCount = 7
  val TemporaryCount         = 64

  def general(index: Int): NonEmpty[String]                     = s"%r$index"
  def uniform(index: Int): NonEmpty[String]                     = s"%rd$index"
  def predicate(index: Int, uniform: Boolean): NonEmpty[String] =
    s"%p${index + (if (uniform) PhysicalPredicateCount else 0)}"
  def temporary(index: Int, predicate: Boolean): NonEmpty[String] =
    if (predicate) s"%p${PhysicalPredicateCount * 2 + index}" else s"%rs$index"

  enum Predicate {
    case True, False, Guard, Barrier, Carry, Zero, Sign, Overflow, Equal, LessThan

    val name: NonEmpty[String] = s"%p${PhysicalPredicateCount * 2 + TemporaryCount + ordinal}"
  }

  def condition(flag: VMConditionCodes.Flag): NonEmpty[String] = (flag match {
    case VMConditionCodes.Flag.Carry    => Predicate.Carry
    case VMConditionCodes.Flag.Zero     => Predicate.Zero
    case VMConditionCodes.Flag.Sign     => Predicate.Sign
    case VMConditionCodes.Flag.Overflow => Predicate.Overflow
    case VMConditionCodes.Flag.Equal    => Predicate.Equal
    case VMConditionCodes.Flag.LessThan => Predicate.LessThan
  }).name
}
