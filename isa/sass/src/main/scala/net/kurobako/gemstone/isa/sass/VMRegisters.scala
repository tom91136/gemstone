package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*

/** Disjoint dense register banks for lowered SASS, independent of its printed register spelling. */
private object VMRegisters {
  final val PhysicalPredicateCount = 7
  final val TemporaryCount         = 64
  private final val BarrierCount   = 16
  // Register operands beyond the highest one a kernel names, such as the rest of a pair or quad.
  private final val ExtentMargin = 8

  private def slot(base: Int, index: Int): VMIR.Register = VMIR.Register((base + index).unsafe[Int >= 0])

  object Layout {

    val Full: Layout = Layout(255, 255)

    /** The banks `functions` need: the larger of the kernel's declared register count and the highest register named.
      */
    def of(kernel: Cubin.Kernel, functions: Vec[DecodedFunction]): Layout = {
      def highest(bank: RegisterBank, operand: Operand): Int = operand match {
        case Operand.Register(`bank`, index) if !VMOperand.isZero(operand) => index.raw
        case Operand.Group(values)                             => values.map(highest(bank, _)).maxOption.getOrElse(-1)
        case Operand.ConstantBank(_, offset)                   => highest(bank, offset)
        case Operand.Address(base, index, _, _, _, descriptor) =>
          (base.toList ++ index.toList ++ descriptor.toList).map(highest(bank, _)).maxOption.getOrElse(-1)
        case Operand.Unary(value, _, _, _) => highest(bank, value)
        case Operand.Qualified(value, _)   => highest(bank, value)
        case _                             => -1
      }
      def count(bank: RegisterBank, declared: Int): Int in 0 ~ 255 = {
        val named = functions.flatMap(_.instructions.flatMap(_.operands.map(highest(bank, _)))).maxOption.getOrElse(-1)
        math.min(255, math.max(declared, if (named < 0) 0 else named + 1 + ExtentMargin)).unsafe[Int in 0 ~ 255]
      }
      Layout(count(RegisterBank.R, kernel.registerCount.raw), count(RegisterBank.UR, 0))
    }
  }

  /** Dense slots for one kernel: `generalCount` general registers, then its uniform registers, the instruction
    * temporaries, the convergence barriers and one range-reduction shadow per reduction. Callers reject register
    * operands outside a bank, so banks never alias.
    */
  class Layout(val generalCount: Int in 0 ~ 255, val uniformCount: Int in 0 ~ 255) {
    private val temporaryBase = generalCount.raw + uniformCount.raw
    private val barrierBase   = temporaryBase + TemporaryCount
    private val reductionBase = barrierBase + BarrierCount

    def general(index: Int in 0 ~ 255): VMIR.Register = slot(0, index.raw)
    def uniform(index: Int in 0 ~ 255): VMIR.Register = slot(generalCount.raw, index.raw)
    def temporary(index: Int): VMIR.Register          = slot(temporaryBase, index)
    def barrier(index: Int in 0 ~ 15): VMIR.Register  = slot(barrierBase, index.raw)

    /** Shadow registers holding range-reduced MUFU operands, one per reduction. */
    def reduction(index: Int): VMIR.Register = slot(reductionBase, index)

    def registerCount(reductions: Int >= 0): Int >= 0 =
      reductions + generalCount.widen[Int >= 0] + uniformCount.widen[Int >= 0] + (TemporaryCount + BarrierCount)
  }

  def predicate(index: Int in 0 ~ 6, uniform: Boolean): VMIR.Predicate =
    VMIR.Predicate((index.raw + (if (uniform) PhysicalPredicateCount else 0)).unsafe[Int >= 0])

  def temporaryPredicate(index: Int): VMIR.Predicate =
    VMIR.Predicate((PhysicalPredicateCount * 2 + index).unsafe[Int >= 0])

  enum Predicate {
    case True, False, Guard, Barrier, Carry, Zero, Sign, Overflow, Equal, LessThan

    val slot: VMIR.Predicate = VMIR.Predicate((PhysicalPredicateCount * 2 + TemporaryCount + ordinal).unsafe[Int >= 0])
  }

  val PredicateCount: Int >= 0 =
    Vec.from(Predicate.values).nonNegativeSize + (PhysicalPredicateCount * 2 + TemporaryCount)

  def condition(flag: VMConditionCodes.Flag): VMIR.Predicate = (flag match {
    case VMConditionCodes.Flag.Carry    => Predicate.Carry
    case VMConditionCodes.Flag.Zero     => Predicate.Zero
    case VMConditionCodes.Flag.Sign     => Predicate.Sign
    case VMConditionCodes.Flag.Overflow => Predicate.Overflow
    case VMConditionCodes.Flag.Equal    => Predicate.Equal
    case VMConditionCodes.Flag.LessThan => Predicate.LessThan
  }).slot
}
