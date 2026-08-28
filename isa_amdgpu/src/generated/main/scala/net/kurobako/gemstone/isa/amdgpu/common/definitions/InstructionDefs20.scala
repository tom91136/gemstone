// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

private[common] object InstructionDefs20 {
  case object VMaxU16 extends InstructionDef(
        "v_max_u16",
        decoder(form(3)((operands, mods) => Instruction.VMaxU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            303,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            303,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            303,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA",
            47,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            303,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            777,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            47,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            47,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMaxU32 extends InstructionDef(
        "v_max_u32",
        decoder(form(3)((operands, mods) => Instruction.VMaxU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            271,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            271,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            271,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            271,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP16",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP8",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            276,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMaxU64 extends InstructionDef(
        "v_max_u64",
        decoder(form(3)((operands, mods) => Instruction.VMaxU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximum3F16 extends InstructionDef(
        "v_maximum3_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximum3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            560,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximum3F32 extends InstructionDef(
        "v_maximum3_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximum3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            681,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            558,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximumF16 extends InstructionDef(
        "v_maximum_f16",
        decoder(form(3)((operands, mods) => Instruction.VMaximumF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            872,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximumF32 extends InstructionDef(
        "v_maximum_f32",
        decoder(form(3)((operands, mods) => Instruction.VMaximumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximumF64 extends InstructionDef(
        "v_maximum_f64",
        decoder(form(3)((operands, mods) => Instruction.VMaximumF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            834,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximumminimumF16 extends InstructionDef(
        "v_maximumminimum_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximumminimumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            623,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaximumminimumF32 extends InstructionDef(
        "v_maximumminimum_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximumminimumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            621,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminF16 extends InstructionDef(
        "v_maxmin_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            608,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminF32 extends InstructionDef(
        "v_maxmin_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            606,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminI32 extends InstructionDef(
        "v_maxmin_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            612,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminNumF16 extends InstructionDef(
        "v_maxmin_num_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminNumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            619,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminNumF32 extends InstructionDef(
        "v_maxmin_num_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            617,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMaxminU32 extends InstructionDef(
        "v_maxmin_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            610,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMbcntHiU32B32 extends InstructionDef(
        "v_mbcnt_hi_u32_b32",
        decoder(form(3)((operands, mods) => Instruction.VMbcntHiU32B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            653,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            653,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            653,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            653,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            870,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            800,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            36,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            36,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMbcntHiU32B32E64 extends InstructionDef(
        "v_mbcnt_hi_u32_b32_e64",
        decoder(form(3)((operands, mods) =>
          Instruction.VMbcntHiU32B32E64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            653,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            653,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMbcntLoU32B32 extends InstructionDef(
        "v_mbcnt_lo_u32_b32",
        decoder(form(3)((operands, mods) => Instruction.VMbcntLoU32B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            652,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            652,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            652,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            652,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            869,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            799,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            291,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            291,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            652,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            652,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3F16 extends InstructionDef(
        "v_med3_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            506,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            506,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            506,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            506,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            855,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            591,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            506,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3F32 extends InstructionDef(
        "v_med3_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            470,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            470,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            470,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            470,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            543,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            343,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            343,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            470,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            470,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3I16 extends InstructionDef(
        "v_med3_i16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            507,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            507,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            507,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            507,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            856,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            592,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            507,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3I32 extends InstructionDef(
        "v_med3_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            471,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            471,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            471,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            471,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            344,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            544,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            344,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            344,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            471,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            471,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3NumF16 extends InstructionDef(
        "v_med3_num_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            562,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMed3NumF32 extends InstructionDef(
        "v_med3_num_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            561,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMed3U16 extends InstructionDef(
        "v_med3_u16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            508,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            508,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            508,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            508,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            857,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            593,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            508,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMed3U32 extends InstructionDef(
        "v_med3_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            472,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            472,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            472,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            472,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            345,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            545,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            345,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            345,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            472,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            472,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMfmaF3216x16x128F8f6f4 extends InstructionDef(
        "v_mfma_f32_16x16x128_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            45,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16Bf16 extends InstructionDef(
        "v_mfma_f32_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16F16_1 extends InstructionDef(
        "v_mfma_f32_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16bf161k extends InstructionDef(
        "v_mfma_f32_16x16x16bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            103,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16f16_2 extends InstructionDef(
        "v_mfma_f32_16x16x16f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x14bF32 extends InstructionDef(
        "v_mfma_f32_16x16x1_4b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x14bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x1f32 extends InstructionDef(
        "v_mfma_f32_16x16x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x2bf16 extends InstructionDef(
        "v_mfma_f32_16x16x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            105,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            105,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf16 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            53,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf8Bf8 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf8Fp8 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32F16 extends InstructionDef(
        "v_mfma_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Fp8Bf8 extends InstructionDef(
        "v_mfma_f32_16x16x32_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Fp8Fp8 extends InstructionDef(
        "v_mfma_f32_16x16x32_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x44bBf16 extends InstructionDef(
        "v_mfma_f32_16x16x4_4b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            94,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            94,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x44bF16 extends InstructionDef(
        "v_mfma_f32_16x16x4_4b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4F32_1 extends InstructionDef(
        "v_mfma_f32_16x16x4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4bf161k extends InstructionDef(
        "v_mfma_f32_16x16x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4f16 extends InstructionDef(
        "v_mfma_f32_16x16x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4f32_2 extends InstructionDef(
        "v_mfma_f32_16x16x4f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x8Xf32 extends InstructionDef(
        "v_mfma_f32_16x16x8_xf32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x8bf16 extends InstructionDef(
        "v_mfma_f32_16x16x8bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            109,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            109,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf16 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            55,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf8Bf8 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf8Fp8 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16F16 extends InstructionDef(
        "v_mfma_f32_32x32x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Fp8Bf8 extends InstructionDef(
        "v_mfma_f32_32x32x16_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Fp8Fp8 extends InstructionDef(
        "v_mfma_f32_32x32x16_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x12bF32 extends InstructionDef(
        "v_mfma_f32_32x32x1_2b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x12bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x1f32 extends InstructionDef(
        "v_mfma_f32_32x32x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2F32_1 extends InstructionDef(
        "v_mfma_f32_32x32x2_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2bf16 extends InstructionDef(
        "v_mfma_f32_32x32x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2f32_2 extends InstructionDef(
        "v_mfma_f32_32x32x2f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x42bBf16 extends InstructionDef(
        "v_mfma_f32_32x32x4_2b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x42bF16 extends InstructionDef(
        "v_mfma_f32_32x32x4_2b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4Xf32 extends InstructionDef(
        "v_mfma_f32_32x32x4_xf32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            63,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4bf16 extends InstructionDef(
        "v_mfma_f32_32x32x4bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4bf161k extends InstructionDef(
        "v_mfma_f32_32x32x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            99,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4f16 extends InstructionDef(
        "v_mfma_f32_32x32x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x64F8f6f4 extends InstructionDef(
        "v_mfma_f32_32x32x64_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x64F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            46,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8Bf16 extends InstructionDef(
        "v_mfma_f32_32x32x8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8F16_1 extends InstructionDef(
        "v_mfma_f32_32x32x8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8bf161k extends InstructionDef(
        "v_mfma_f32_32x32x8bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8f16_2 extends InstructionDef(
        "v_mfma_f32_32x32x8f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x116bF32 extends InstructionDef(
        "v_mfma_f32_4x4x1_16b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x116bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x1f32 extends InstructionDef(
        "v_mfma_f32_4x4x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x2bf16 extends InstructionDef(
        "v_mfma_f32_4x4x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            107,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            107,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x416bBf16 extends InstructionDef(
        "v_mfma_f32_4x4x4_16b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            95,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            95,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x416bF16 extends InstructionDef(
        "v_mfma_f32_4x4x4_16b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x4bf161k extends InstructionDef(
        "v_mfma_f32_4x4x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x4f16 extends InstructionDef(
        "v_mfma_f32_4x4x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF6416x16x4F64_1 extends InstructionDef(
        "v_mfma_f64_16x16x4_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4F64_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF6416x16x4f64_2 extends InstructionDef(
        "v_mfma_f64_16x16x4f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4f64_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF644x4x44bF64 extends InstructionDef(
        "v_mfma_f64_4x4x4_4b_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x44bF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF644x4x4f64 extends InstructionDef(
        "v_mfma_f64_4x4x4f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x4f64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x16i8 extends InstructionDef(
        "v_mfma_i32_16x16x16i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x16i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x32I8 extends InstructionDef(
        "v_mfma_i32_16x16x32_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x44bI8 extends InstructionDef(
        "v_mfma_i32_16x16x4_4b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x44bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x4i8 extends InstructionDef(
        "v_mfma_i32_16x16x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x64I8 extends InstructionDef(
        "v_mfma_i32_16x16x64_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x16I8 extends InstructionDef(
        "v_mfma_i32_32x32x16_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x16I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x32I8 extends InstructionDef(
        "v_mfma_i32_32x32x32_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            56,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x42bI8 extends InstructionDef(
        "v_mfma_i32_32x32x4_2b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x42bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x4i8 extends InstructionDef(
        "v_mfma_i32_32x32x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x8i8 extends InstructionDef(
        "v_mfma_i32_32x32x8i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x8i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI324x4x416bI8 extends InstructionDef(
        "v_mfma_i32_4x4x4_16b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x416bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI324x4x4i8 extends InstructionDef(
        "v_mfma_i32_4x4x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaScaleF3216x16x128F8f6f4 extends InstructionDef(
        "v_mfma_scale_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3PX2",
            45,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaScaleF3232x32x64F8f6f4 extends InstructionDef(
        "v_mfma_scale_f32_32x32x64_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3232x32x64F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3PX2",
            46,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMin3F16 extends InstructionDef(
        "v_min3_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            500,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            500,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            500,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            500,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            849,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            585,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            500,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMin3F32 extends InstructionDef(
        "v_min3_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            464,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            464,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            464,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            464,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            337,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            537,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            337,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            337,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            464,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            464,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMin3I16 extends InstructionDef(
        "v_min3_i16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            501,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            501,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            501,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            501,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            850,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            586,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            501,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VMin3I32 extends InstructionDef(
        "v_min3_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            465,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            465,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            465,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            465,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            338,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            538,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            338,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            338,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            465,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            465,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    VMaxU16,
    VMaxU32,
    VMaxU64,
    VMaximum3F16,
    VMaximum3F32,
    VMaximumF16,
    VMaximumF32,
    VMaximumF64,
    VMaximumminimumF16,
    VMaximumminimumF32,
    VMaxminF16,
    VMaxminF32,
    VMaxminI32,
    VMaxminNumF16,
    VMaxminNumF32,
    VMaxminU32,
    VMbcntHiU32B32,
    VMbcntHiU32B32E64,
    VMbcntLoU32B32,
    VMed3F16,
    VMed3F32,
    VMed3I16,
    VMed3I32,
    VMed3NumF16,
    VMed3NumF32,
    VMed3U16,
    VMed3U32,
    VMfmaF3216x16x128F8f6f4,
    VMfmaF3216x16x16Bf16,
    VMfmaF3216x16x16F16_1,
    VMfmaF3216x16x16bf161k,
    VMfmaF3216x16x16f16_2,
    VMfmaF3216x16x14bF32,
    VMfmaF3216x16x1f32,
    VMfmaF3216x16x2bf16,
    VMfmaF3216x16x32Bf16,
    VMfmaF3216x16x32Bf8Bf8,
    VMfmaF3216x16x32Bf8Fp8,
    VMfmaF3216x16x32F16,
    VMfmaF3216x16x32Fp8Bf8,
    VMfmaF3216x16x32Fp8Fp8,
    VMfmaF3216x16x44bBf16,
    VMfmaF3216x16x44bF16,
    VMfmaF3216x16x4F32_1,
    VMfmaF3216x16x4bf161k,
    VMfmaF3216x16x4f16,
    VMfmaF3216x16x4f32_2,
    VMfmaF3216x16x8Xf32,
    VMfmaF3216x16x8bf16,
    VMfmaF3232x32x16Bf16,
    VMfmaF3232x32x16Bf8Bf8,
    VMfmaF3232x32x16Bf8Fp8,
    VMfmaF3232x32x16F16,
    VMfmaF3232x32x16Fp8Bf8,
    VMfmaF3232x32x16Fp8Fp8,
    VMfmaF3232x32x12bF32,
    VMfmaF3232x32x1f32,
    VMfmaF3232x32x2F32_1,
    VMfmaF3232x32x2bf16,
    VMfmaF3232x32x2f32_2,
    VMfmaF3232x32x42bBf16,
    VMfmaF3232x32x42bF16,
    VMfmaF3232x32x4Xf32,
    VMfmaF3232x32x4bf16,
    VMfmaF3232x32x4bf161k,
    VMfmaF3232x32x4f16,
    VMfmaF3232x32x64F8f6f4,
    VMfmaF3232x32x8Bf16,
    VMfmaF3232x32x8F16_1,
    VMfmaF3232x32x8bf161k,
    VMfmaF3232x32x8f16_2,
    VMfmaF324x4x116bF32,
    VMfmaF324x4x1f32,
    VMfmaF324x4x2bf16,
    VMfmaF324x4x416bBf16,
    VMfmaF324x4x416bF16,
    VMfmaF324x4x4bf161k,
    VMfmaF324x4x4f16,
    VMfmaF6416x16x4F64_1,
    VMfmaF6416x16x4f64_2,
    VMfmaF644x4x44bF64,
    VMfmaF644x4x4f64,
    VMfmaI3216x16x16i8,
    VMfmaI3216x16x32I8,
    VMfmaI3216x16x44bI8,
    VMfmaI3216x16x4i8,
    VMfmaI3216x16x64I8,
    VMfmaI3232x32x16I8,
    VMfmaI3232x32x32I8,
    VMfmaI3232x32x42bI8,
    VMfmaI3232x32x4i8,
    VMfmaI3232x32x8i8,
    VMfmaI324x4x416bI8,
    VMfmaI324x4x4i8,
    VMfmaScaleF3216x16x128F8f6f4,
    VMfmaScaleF3232x32x64F8f6f4,
    VMin3F16,
    VMin3F32,
    VMin3I16,
    VMin3I32
  )
}
