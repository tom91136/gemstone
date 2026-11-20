package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class MovesSuite extends Suite {
  test("each maps every value when each is defined") {
    assertEquals(Moves.each(Vec(1, 2, 3))(value => Some(value * 2)), Some(Vec(2, 4, 6)))
  }

  test("each stops at the first undefined value and returns none") {
    val mapped = Moves.each(Vec(1, 2, 3)) { value =>
      if (value > 2) fail("each evaluated a value after the first undefined one")
      else Option.when(value < 2)(value * 2)
    }
    assertEquals(mapped, None)
  }
}
