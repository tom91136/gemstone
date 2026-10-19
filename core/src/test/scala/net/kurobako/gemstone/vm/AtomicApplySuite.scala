package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import scala.util.Random

class AtomicApplySuite extends munit.FunSuite {
  private val floating = Set(AtomicOperation.FloatAdd, AtomicOperation.FloatMinimum, AtomicOperation.FloatMaximum)

  test("applying an operation directly gives what its staged execution gives") {
    val random = Random(11)
    val widths = Seq[ScalarBitWidth](8, 16, 32, 64)
    for (_ <- 0 until 20000) {
      val old, operand = if (random.nextBoolean()) random.nextLong() else random.nextInt(4).toLong - 2L
      for {
        width     <- widths
        operation <- AtomicOperation.values.toSeq
        if width >= 16 || !floating.contains(operation)
      } assertEquals(
        operation(old, operand, width),
        operation.staged(Execute, width)(Applied(old, operand)(identity)),
        s"$operation $width $old $operand"
      )
    }
  }
}
