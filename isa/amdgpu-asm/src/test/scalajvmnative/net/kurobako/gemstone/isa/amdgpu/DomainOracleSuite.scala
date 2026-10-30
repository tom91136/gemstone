package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.Suite

class DomainOracleSuite extends Suite {
  private val Operand =
    "(?:.*register not available on this GPU|invalid register alignment|expected a (?:20|21|24)-bit (?:un)?signed offset|invalid register class: vgpr tuples must be 64 bit aligned)".r

  test("LLVM diagnostics about registers, alignment and offsets never assemble") {
    // the GFX8 family keeps xnack_mask for gfx801 and gfx810 although tonga rejects it
    val cases = oracle.diagnostics.filter(diagnostic =>
      Operand.matches(diagnostic.message) &&
        !(diagnostic.target.isa == ISA.GFX8 && diagnostic.message.startsWith("xnack_mask"))
    )
    assert(cases.size >= 20, cases.size.toString)
    cases.foreach { diagnostic =>
      val statement = Parser.parse(diagnostic.assembly).toOption.flatMap(_.statements.headOption)
      statement.foreach { value =>
        assert(
          Binary.encodeStatement(value, TargetMode(diagnostic.target, 64)).isLeft,
          s"${diagnostic.source}: ${diagnostic.target.name}: ${diagnostic.assembly} (${diagnostic.message})"
        )
      }
    }
  }
}
