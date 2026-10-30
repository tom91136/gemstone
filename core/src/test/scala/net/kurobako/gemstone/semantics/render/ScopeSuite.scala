package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class ScopeSuite extends Suite {
  private val u32 = Type.unsigned(32)
  private val u64 = Type.unsigned(64)

  private class Registers(parts: Map[Int, Naming.Name]) extends Naming[Int] {
    def resolve(operand: Int): Vec[Naming.Name] = Vec.from(parts.get(operand))
  }

  private val names = Registers(Map(
    0 -> Naming.Name("c", u32),
    1 -> Naming.Name("a+1", u32, "a", 1),
    2 -> Naming.Name("a", u32, "a", 0),
    3 -> Naming.Name("b", u64)
  ))

  test("declarations follow the order given with a later part after its operand, then first use") {
    val scope = Scope(names)
    Vec(0, 1, 3, 2).foreach(scope.operand(_))
    val _ = scope.declare("state", u32)
    assertEquals(scope.declared(Vec("b", "a")).map(_.name), Vec("b", "a", "a+1", "c", "state"))
  }

  test("a name spelt operand+part is a later part of the operand") {
    assertEquals(Naming.Name.spelt("SRSRC+2", u32), Naming.Name("SRSRC+2", u32, "SRSRC", 2))
    assertEquals(Naming.Name.spelt("a+b+1", u32), Naming.Name("a+b+1", u32, "a+b", 1))
    Vec("+2", "a+", "a+b", "a").foreach(name =>
      assertEquals(Naming.Name.spelt(name, u32), Naming.Name(name, u32))
    )
  }

  test("operands are touched whether or not they are named") {
    val scope = Scope(names)
    assertEquals(scope.operand(7), None)
    val _ = scope.operand(2)
    assertEquals(scope.touched, Set(7, 2))
    assertEquals(scope.declared().map(_.name), Vec("a"))
  }

  test("a name declared with two types conflicts") {
    val scope = Scope(names)
    val _     = scope.operand(3)
    val _     = scope.declare("b", u32)
    assertEquals(scope.conflicting, Vec("b"))
  }
}
