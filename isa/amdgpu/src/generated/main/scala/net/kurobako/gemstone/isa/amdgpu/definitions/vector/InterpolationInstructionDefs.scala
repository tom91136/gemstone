// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object InterpolationInstructionDefs {
  case object VInterpP10F16F32 extends InstructionDef(
        "v_interp_p10_f16_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10F16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VINTERP",
            2,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VINTERP",
            2,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VINTERP",
            2,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VInterpP10F32 extends InstructionDef(
        "v_interp_p10_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VINTERP",
            0,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VINTERP",
            0,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VINTERP",
            0,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VInterpP10RtzF16F32 extends InstructionDef(
        "v_interp_p10_rtz_f16_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VINTERP",
            4,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VINTERP",
            4,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VINTERP",
            4,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VInterpP2F16F32 extends InstructionDef(
        "v_interp_p2_f16_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP2F16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VINTERP",
            3,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VINTERP",
            3,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VINTERP",
            3,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VInterpP2RtzF16F32 extends InstructionDef(
        "v_interp_p2_rtz_f16_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP2RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VINTERP",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VINTERP",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VINTERP",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] =
    Col(VInterpP10F16F32, VInterpP10F32, VInterpP10RtzF16F32, VInterpP2F16F32, VInterpP2RtzF16F32)
}
