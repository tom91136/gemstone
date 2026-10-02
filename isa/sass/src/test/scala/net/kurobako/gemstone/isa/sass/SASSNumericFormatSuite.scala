package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMIRInstruction, *}

class SASSNumericFormatSuite extends munit.FunSuite {
  test("integer and floating set instructions lower to typed comparison formats") {
    val module   = SASSFixture.module(89, Vec(SASSFixture.Kernel("numeric_formats", 0, Vec.empty, registers = 5)))
    val function = DecodedFunction(
      "numeric_formats",
      Vec(
        SASSFixture.instruction(0, "ISET.GT.U32.AND", Vec("R3", "R0", "R1", "PT"), None),
        SASSFixture.instruction(16, "FSET.GTU.AND", Vec("R4", "|R0|", "|R1|", "PT"), None),
        SASSFixture.instruction(32, "EXIT", Vec.empty, None)
      )
    )
    val lowered = SASSFixture(module, module.kernels.head, Vec(function))
    val formats = lowered.instructions.collect {
      case predicate: VMIRInstruction.SetPredicate => predicate.format
    }

    assert(formats.contains(NumericFormat.Unsigned(32)))
    assert(formats.contains(NumericFormat.IEEE(32)))
  }
}
