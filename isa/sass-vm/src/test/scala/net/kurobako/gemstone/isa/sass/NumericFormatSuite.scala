package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.sass.Harness.*

class NumericFormatSuite extends munit.FunSuite {
  test("integer and floating set instructions lower to typed comparison formats") {
    val formats = program(89, "ISET.GT.U32.AND R3, R0, R1, PT; FSET.GTU.AND R4, |R0|, |R1|, PT; EXIT", base = 0)
      .instructions()
      .collect { case predicate: VMInstruction.SetPredicate => predicate.format }
    assert(formats.contains(NumericFormat.Unsigned(32)))
    assert(formats.contains(NumericFormat.IEEE(32)))
  }
}
