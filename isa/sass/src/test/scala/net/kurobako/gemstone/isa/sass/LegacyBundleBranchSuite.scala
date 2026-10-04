package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyBundleBranchSuite extends SyntheticSASSExecutionSuiteBase {
  for (sm <- Vec.nonEmpty(35, 50, 60)) {
    test(s"sm_$sm loop backedges retain the enclosing break token across divergent exits") {
      executeSynthetic(
        body = Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD", "R5,R0,1", None),
          ("MOV", "R4,RZ", None),
          ("PBK", "0xa0", None),
          ("IADD", "R4,R4,1", None),
          ("ISETP.GE.U32.AND", "P0,PT,R4,R5,PT", None),
          ("BRK", "", Some("@P0")),
          ("BRA", "0x40", None),
          ("NOP", "", None),
          ("BRK", "", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("ISCADD", "R2.CC,R0,R2,2", None),
          ("IADD.X", "R3,RZ,R3", None),
          ("STG.E", "[R2],R4", None),
          ("EXIT", "", None)
        ),
        expected = Vec.nonEmpty(1, 2, 3, 4),
        sm = sm
      )
    }

    test(s"sm_$sm an external late entry cannot bypass setup and loop back into a break") {
      val module   = SASSFixture.module(sm, Vec.nonEmpty(SASSFixture.Kernel("probe", 0, Vec.empty)))
      val function = DecodedFunction(
        "probe",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "BRA", Vec.nonEmpty("0x40")),
          SASSFixture.instruction(16, "PBK", Vec.nonEmpty("0x70")),
          SASSFixture.instruction(32, "NOP", Vec.empty),
          SASSFixture.instruction(48, "BRK", Vec.empty),
          SASSFixture.instruction(64, "BRA", Vec.nonEmpty("0x20")),
          SASSFixture.instruction(80, "NOP", Vec.empty),
          SASSFixture.instruction(96, "BRK", Vec.empty),
          SASSFixture.instruction(112, "EXIT", Vec.empty)
        )
      )
      assert(VMCompiler.compile(module, module.kernels.head, Vec.nonEmpty(function)).isLeft)
    }
  }

  for (sm <- Vec.nonEmpty(35, 50, 60))
    test(s"sm_$sm indirect branches resolve a control-word address") {
      val addresses = Vec.nonEmpty(8, 16, 24, 32, 72, 80, 88, 96)
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,64", None),
          ("BRX", "R4,-0x18", None),
          ("MOV", "R6,99", None),
          ("EXIT", "", None),
          ("MOV", "R6,7", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(7),
        sm = sm,
        rewrite = instruction => instruction.copy(address = addresses(instruction.address.raw / 16).unsafe)
      )
    }

  for ((sm, bundleBytes) <- Vec.nonEmpty(35 -> 64, 50 -> 32, 60 -> 32))
    test(s"sm_$sm branches can target the control word preceding an instruction") {
      val module   = SASSFixture.module(sm, Vec.nonEmpty(SASSFixture.Kernel("probe", 0, Vec.empty)))
      val function = DecodedFunction(
        "probe",
        Vec.nonEmpty(
          SASSFixture.instruction(8, "BRA", Vec.nonEmpty(s"0x${bundleBytes.toHexString}")),
          SASSFixture.instruction(bundleBytes + 8, "EXIT", Vec.empty)
        )
      )
      assert(VMCompiler.compile(module, module.kernels.head, Vec.nonEmpty(function)).isRight)
      val invalid = function.copy(instructions =
        function.instructions.updated(
          0,
          SASSFixture.instruction(8, "BRA", Vec.nonEmpty(s"0x${(bundleBytes + 4).toHexString}"))
        )
      )
      assert(VMCompiler.compile(module, module.kernels.head, Vec.nonEmpty(invalid)).isLeft)
    }
}
