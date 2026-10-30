package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.testing.Suite

class PTXMetadataBoundsSuite extends Suite {
  test("generated metadata stays within the declared modifier target and form bounds") {
    for {
      definition     <- Instruction.definitions
      (entry, index) <- definition.meta.entries.raw.zipWithIndex
    } {
      val entryName = s"${definition.opcode} entry $index"
      val targets   = entry.modifierTargets.size
      val forms     = entry.forms.size
      assert(targets <= 26, s"$entryName has $targets modifier targets, bound is 26")
      assert(forms <= 37, s"$entryName has $forms forms, bound is 37")
    }
  }
}
