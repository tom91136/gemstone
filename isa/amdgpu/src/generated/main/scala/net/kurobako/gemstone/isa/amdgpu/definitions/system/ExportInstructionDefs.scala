// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object ExportInstructionDefs {
  case object Exp extends InstructionDef(
        "exp",
        decoder(form(5)((operands, mods) =>
          Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "EXP",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "EXP",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "EXP",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "EXP",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "EXP",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "EXP",
            0,
            Col.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "EXP",
            0,
            Col.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "EXP",
            0,
            Col.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "EXP",
            0,
            Col.empty,
            Some("exp"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object Export extends InstructionDef(
        "export",
        decoder(form(5)((operands, mods) =>
          Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VEXPORT",
            0,
            Col(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(Exp, Export)
}
