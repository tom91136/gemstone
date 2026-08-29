// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object LDSDirectInstructionDefs {
  case object LdsDirectLoad extends InstructionDef(
        "lds_direct_load",
        decoder(form(1)((operands, mods) => Instruction.LdsDirectLoad(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "LDSDIR",
            1,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "LDSDIR",
            1,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object LdsParamLoad extends InstructionDef(
        "lds_param_load",
        decoder(form(2)((operands, mods) => Instruction.LdsParamLoad(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "LDSDIR",
            0,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ATTR"), "ATTR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "LDSDIR",
            0,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ATTR"), "ATTR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(LdsDirectLoad, LdsParamLoad)
}
