// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

private[common] object InstructionDefs23 {
  case object VSqrtF16 extends InstructionDef(
        "v_sqrt_f16",
        decoder(form(2)((operands, mods) => Instruction.VSqrtF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_DPP",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_SDWA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            382,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            382,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            382,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            382,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_SDWA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_SDWA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP16",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP8",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            469,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            62,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            62,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSqrtF32 extends InstructionDef(
        "v_sqrt_f32",
        decoder(form(2)((operands, mods) => Instruction.VSqrtF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_DPP",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_SDWA",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            359,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            359,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            359,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            359,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_SDWA",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_SDWA",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            435,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP1",
            51,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP1",
            51,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            39,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            39,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu_transcendental32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSqrtF64 extends InstructionDef(
        "v_sqrt_f64",
        decoder(form(2)((operands, mods) => Instruction.VSqrtF64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            360,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            360,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            360,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            360,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            436,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP1",
            52,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_transcendental"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP1",
            52,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_transcendental"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            40,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_transcendental"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            40,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_transcendental"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubCoCiU32 extends InstructionDef(
        "v_sub_co_ci_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VSubCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP16",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP8",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            297,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP16",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP8",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP16",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP8",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP16",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP8",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSubCoU32 extends InstructionDef(
        "v_sub_co_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VSubCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3_SDST_ENC",
            282,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3_SDST_ENC",
            282,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA_SDST_ENC",
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3_SDST_ENC",
            282,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
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
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA_SDST_ENC",
            26,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3_SDST_ENC",
            282,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP16",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP8",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            784,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP16",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP8",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP16",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP8",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP16",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP8",
            769,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
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
            38,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            38,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubF16 extends InstructionDef(
        "v_sub_f16",
        decoder(form(3)((operands, mods) => Instruction.VSubF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            32,
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
            32,
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
            32,
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
            32,
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
            288,
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
            32,
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
            32,
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
            32,
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
            32,
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
            288,
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
            32,
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
            32,
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
            32,
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
            32,
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
            288,
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
            32,
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
            32,
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
            32,
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
            32,
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
            288,
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
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            307,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            307,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            "VOP2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP16",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP8",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            307,
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
            32,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            32,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubF32 extends InstructionDef(
        "v_sub_f32",
        decoder(form(3)((operands, mods) => Instruction.VSubF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            2,
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
            2,
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
            2,
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
            2,
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
            258,
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
            2,
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
            2,
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
            2,
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
            2,
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
            258,
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
            2,
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
            2,
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
            2,
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
            2,
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
            258,
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
            2,
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
            2,
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
            2,
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
            2,
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
            258,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
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
            4,
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
            4,
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
            4,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            260,
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
            4,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            4,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            2,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            2,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubI16 extends InstructionDef(
        "v_sub_i16",
        decoder(form(3)((operands, mods) => Instruction.VSubI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            671,
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
            "VOP3",
            671,
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
            "VOP3",
            671,
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
            "VOP3",
            671,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            671,
            Col(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubI32 extends InstructionDef(
        "v_sub_i32",
        decoder(
          form(3)((operands, mods) => Instruction.VSubI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VSubI32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            669,
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
            669,
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
            669,
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
            669,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            669,
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
  case object VSubNcI16 extends InstructionDef(
        "v_sub_nc_i16",
        decoder(form(3)((operands, mods) => Instruction.VSubNcI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
            782,
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
  case object VSubNcI32 extends InstructionDef(
        "v_sub_nc_i32",
        decoder(form(3)((operands, mods) => Instruction.VSubNcI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            886,
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
            886,
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
            886,
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
            886,
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
            886,
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
            886,
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
            886,
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
            886,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
            805,
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
  case object VSubNcU16 extends InstructionDef(
        "v_sub_nc_u16",
        decoder(form(3)((operands, mods) => Instruction.VSubNcU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
            772,
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
  case object VSubNcU32 extends InstructionDef(
        "v_sub_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VSubNcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            38,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            38,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            38,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            38,
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
            38,
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
            38,
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
            38,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
            294,
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
  case object VSubNcU64 extends InstructionDef(
        "v_sub_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.VSubNcU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            297,
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
            297,
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
            297,
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
            297,
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
            297,
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
  case object VSubU16 extends InstructionDef(
        "v_sub_u16",
        decoder(form(3)((operands, mods) => Instruction.VSubU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            39,
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
            39,
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
            39,
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
            39,
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
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            39,
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
            39,
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
  case object VSubU32 extends InstructionDef(
        "v_sub_u32",
        decoder(
          form(3)((operands, mods) => Instruction.VSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VSubU32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            53,
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
            53,
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
            53,
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
            53,
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
            309,
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
            53,
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
            53,
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
            53,
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
            53,
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
            309,
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
            53,
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
            53,
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
            53,
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
            53,
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
            309,
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
            53,
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
            53,
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
            53,
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
            53,
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
            309,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            53,
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
  case object VSubbCoU32 extends InstructionDef(
        "v_subb_co_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VSubbCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3_SDST_ENC",
            285,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3_SDST_ENC",
            285,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA_SDST_ENC",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3_SDST_ENC",
            285,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA_SDST_ENC",
            29,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3_SDST_ENC",
            285,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            41,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            41,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            29,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            29,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubbrevCoU32 extends InstructionDef(
        "v_subbrev_co_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VSubbrevCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3_SDST_ENC",
            286,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3_SDST_ENC",
            286,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA_SDST_ENC",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3_SDST_ENC",
            286,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA_SDST_ENC",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3_SDST_ENC",
            286,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            42,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            42,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "lanemask", 1, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubrevCoCiU32 extends InstructionDef(
        "v_subrev_co_ci_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VSubrevCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP8",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP16",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP8",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            298,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP16",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP8",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP16",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP8",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP16",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP8",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP16",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP8",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP16",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP8",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP16",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP8",
            290,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSubrevCoU32 extends InstructionDef(
        "v_subrev_co_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VSubrevCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_SDWA_SDST_ENC",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3_SDST_ENC",
            283,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_SDWA_SDST_ENC",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3_SDST_ENC",
            283,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_SDWA_SDST_ENC",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3_SDST_ENC",
            283,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_SDWA_SDST_ENC",
            27,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3_SDST_ENC",
            283,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP16",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_SDST_ENC_VOP_DPP8",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_SDST_ENC_INST_LITERAL",
            793,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP16",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_SDST_ENC_VOP_DPP8",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP16",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_SDST_ENC_VOP_DPP8",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_INST_LITERAL",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP16",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_SDST_ENC_VOP_DPP8",
            770,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            39,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            39,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            27,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
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
            27,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "lanemask", 1, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubrevF16 extends InstructionDef(
        "v_subrev_f16",
        decoder(form(3)((operands, mods) => Instruction.VSubrevF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            33,
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
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            33,
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
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            33,
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
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            33,
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
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            289,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_INST_LITERAL64",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            308,
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
            308,
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
            308,
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
            308,
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
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            308,
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
            308,
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
            308,
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
            308,
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
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            308,
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
            308,
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
            308,
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
            308,
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
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP16",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_VOP_DPP8",
            52,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            308,
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
            308,
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
            308,
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
            308,
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
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            308,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            33,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            33,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubrevF32 extends InstructionDef(
        "v_subrev_f32",
        decoder(form(3)((operands, mods) => Instruction.VSubrevF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            3,
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
            3,
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
            3,
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
            3,
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
            259,
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
            3,
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
            3,
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
            3,
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
            3,
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
            259,
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
            3,
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
            3,
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
            3,
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
            3,
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
            259,
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
            3,
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
            3,
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
            3,
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
            3,
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
            259,
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
            5,
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
            5,
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
            5,
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
            5,
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
            5,
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
            261,
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
            261,
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
            261,
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
            261,
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
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            5,
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
            5,
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
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            5,
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
            5,
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
            5,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            5,
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
            5,
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
            5,
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
            5,
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
            261,
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
            261,
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
            261,
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
            261,
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
            261,
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
            261,
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
            5,
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
            5,
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
            5,
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
            5,
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
            261,
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
            261,
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
            261,
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
            261,
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
            261,
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
            "VOP3_VOP_DPP8",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            5,
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
            5,
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
            5,
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
            5,
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
            261,
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
            261,
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
            261,
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
            261,
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
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            261,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            5,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            5,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            3,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            3,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSubrevNcU32 extends InstructionDef(
        "v_subrev_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VSubrevNcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            39,
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
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            295,
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
            295,
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
            295,
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
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            39,
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
            39,
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
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            39,
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
            39,
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
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP2",
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            295,
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
            295,
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
            295,
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
            295,
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
            295,
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
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            295,
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
            295,
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
            295,
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
            295,
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
            "VOP3_VOP_DPP8",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            39,
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
            39,
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
            39,
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
            39,
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
            295,
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
            295,
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
            295,
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
            295,
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
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            295,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSubrevU16 extends InstructionDef(
        "v_subrev_u16",
        decoder(form(3)((operands, mods) => Instruction.VSubrevU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            40,
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
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            296,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            40,
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
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            296,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            40,
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
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            296,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            40,
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
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            296,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP2",
            40,
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
            40,
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
  case object VSubrevU32 extends InstructionDef(
        "v_subrev_u32",
        decoder(
          form(3)((operands, mods) => Instruction.VSubrevU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VSubrevU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2_VOP_DPP",
            54,
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
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            310,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP2_VOP_DPP",
            54,
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
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            310,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP2_VOP_DPP",
            54,
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
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            310,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP2_VOP_DPP",
            54,
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
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            310,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP2",
            54,
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
  case object VSwapB16 extends InstructionDef(
        "v_swap_b16",
        decoder(form(2)((operands, mods) => Instruction.VSwapB16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 16, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwapB32 extends InstructionDef(
        "v_swap_b32",
        decoder(
          form(2)((operands, mods) => Instruction.VSwapB32_2(operands(0), operands(1), mods)),
          form(4)((operands, mods) => Instruction.VSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            401,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            401,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            401,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            401,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            81,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VSwaprelB32 extends InstructionDef(
        "v_swaprel_b32",
        decoder(form(2)((operands, mods) => Instruction.VSwaprelB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, false, true, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf1616x16x32Bf16 extends InstructionDef(
        "v_swmmac_bf16_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            83,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf1616x16x64Bf16 extends InstructionDef(
        "v_swmmac_bf16_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf16f3216x16x64Bf16 extends InstructionDef(
        "v_swmmac_bf16f32_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf16f3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            105,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Bf8Bf8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            122,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Bf8Fp8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            121,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Fp8Bf8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            120,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Fp8Fp8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x32F16 extends InstructionDef(
        "v_swmmac_f16_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x64F16 extends InstructionDef(
        "v_swmmac_f16_16x16x64_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            103,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Bf8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Bf8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Fp8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Fp8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf16 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            90,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            89,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32F16 extends InstructionDef(
        "v_swmmac_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Fp8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            88,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Fp8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x64Bf16 extends InstructionDef(
        "v_swmmac_f32_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x64F16 extends InstructionDef(
        "v_swmmac_f32_16x16x64_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x128Iu8 extends InstructionDef(
        "v_swmmac_i32_16x16x128_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x128Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            123,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x32Iu4 extends InstructionDef(
        "v_swmmac_i32_16x16x32_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x32Iu8 extends InstructionDef(
        "v_swmmac_i32_16x16x32_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x64Iu4 extends InstructionDef(
        "v_swmmac_i32_16x16x64_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x64Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VTanhBf16 extends InstructionDef(
        "v_tanh_bf16",
        decoder(form(2)((operands, mods) => Instruction.VTanhBf16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            458,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            458,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            458,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            458,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VTanhF16 extends InstructionDef(
        "v_tanh_f16",
        decoder(form(2)((operands, mods) => Instruction.VTanhF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            415,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            415,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            415,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            415,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VTanhF32 extends InstructionDef(
        "v_tanh_f32",
        decoder(form(2)((operands, mods) => Instruction.VTanhF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            414,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            414,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            414,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            414,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VTrigPreopF64 extends InstructionDef(
        "v_trig_preop_f64",
        decoder(form(3)((operands, mods) => Instruction.VTrigPreopF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            658,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            658,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            658,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            658,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            372,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            815,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            372,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            372,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            658,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            658,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VTruncF16 extends InstructionDef(
        "v_trunc_f16",
        decoder(form(2)((operands, mods) => Instruction.VTruncF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_DPP",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_SDWA",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            390,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            390,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            390,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            390,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_SDWA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_SDWA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP16",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP8",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            477,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            70,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            70,
            Col(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VTruncF32 extends InstructionDef(
        "v_trunc_f32",
        decoder(form(2)((operands, mods) => Instruction.VTruncF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_DPP",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_SDWA",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            348,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            348,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            348,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            28,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            348,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_SDWA",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_SDWA",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP16",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_VOP_DPP8",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            417,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP1",
            33,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP1",
            33,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            28,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            28,
            Col(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VTruncF64 extends InstructionDef(
        "v_trunc_f64",
        decoder(form(2)((operands, mods) => Instruction.VTruncF64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            343,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_INST_LITERAL64",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP1_VOP_DPP16",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            407,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP1",
            23,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            23,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            23,
            Col(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VWmmaBf1616x16x16Bf16 extends InstructionDef(
        "v_wmma_bf16_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaBf1616x16x32Bf16 extends InstructionDef(
        "v_wmma_bf16_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            99,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaBf16f3216x16x32Bf16 extends InstructionDef(
        "v_wmma_bf16f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf16f3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Bf8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            135,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Bf8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            134,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Fp8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            133,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Fp8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            132,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x16F16 extends InstructionDef(
        "v_wmma_f16_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x32F16 extends InstructionDef(
        "v_wmma_f16_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Bf8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Bf8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Fp8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Fp8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            131,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            130,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_f32_16x16x128_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            129,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            128,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf16 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16F16 extends InstructionDef(
        "v_wmma_f32_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x16_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            71,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x16_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x32Bf16 extends InstructionDef(
        "v_wmma_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            98,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x32F16 extends InstructionDef(
        "v_wmma_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x4F32 extends InstructionDef(
        "v_wmma_f32_16x16x4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            109,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            107,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            106,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3232x16x128F4 extends InstructionDef(
        "v_wmma_f32_32x16x128_f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3232x16x128F4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x16Iu4 extends InstructionDef(
        "v_wmma_i32_16x16x16_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x16Iu8 extends InstructionDef(
        "v_wmma_i32_16x16x16_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x32Iu4 extends InstructionDef(
        "v_wmma_i32_16x16x32_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3P",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x64Iu8 extends InstructionDef(
        "v_wmma_i32_16x16x64_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x64Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3P",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScale16F3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_scale16_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3216x16x128F8f6f4(
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
            Generation.CDNA5,
            "cdna5",
            "VOP3PX3",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScale16F3232x16x128F4 extends InstructionDef(
        "v_wmma_scale16_f32_32x16x128_f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3232x16x128F4(
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
            Generation.CDNA5,
            "cdna5",
            "VOP3PX3",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScaleF3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_scale_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3216x16x128F8f6f4(
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
            Generation.CDNA5,
            "cdna5",
            "VOP3PX2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScaleF3232x16x128F4 extends InstructionDef(
        "v_wmma_scale_f32_32x16x128_f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3232x16x128F4(
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
            Generation.CDNA5,
            "cdna5",
            "VOP3PX2",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    VSqrtF16,
    VSqrtF32,
    VSqrtF64,
    VSubCoCiU32,
    VSubCoU32,
    VSubF16,
    VSubF32,
    VSubI16,
    VSubI32,
    VSubNcI16,
    VSubNcI32,
    VSubNcU16,
    VSubNcU32,
    VSubNcU64,
    VSubU16,
    VSubU32,
    VSubbCoU32,
    VSubbrevCoU32,
    VSubrevCoCiU32,
    VSubrevCoU32,
    VSubrevF16,
    VSubrevF32,
    VSubrevNcU32,
    VSubrevU16,
    VSubrevU32,
    VSwapB16,
    VSwapB32,
    VSwaprelB32,
    VSwmmacBf1616x16x32Bf16,
    VSwmmacBf1616x16x64Bf16,
    VSwmmacBf16f3216x16x64Bf16,
    VSwmmacF1616x16x128Bf8Bf8,
    VSwmmacF1616x16x128Bf8Fp8,
    VSwmmacF1616x16x128Fp8Bf8,
    VSwmmacF1616x16x128Fp8Fp8,
    VSwmmacF1616x16x32F16,
    VSwmmacF1616x16x64F16,
    VSwmmacF3216x16x128Bf8Bf8,
    VSwmmacF3216x16x128Bf8Fp8,
    VSwmmacF3216x16x128Fp8Bf8,
    VSwmmacF3216x16x128Fp8Fp8,
    VSwmmacF3216x16x32Bf16,
    VSwmmacF3216x16x32Bf8Bf8,
    VSwmmacF3216x16x32Bf8Fp8,
    VSwmmacF3216x16x32F16,
    VSwmmacF3216x16x32Fp8Bf8,
    VSwmmacF3216x16x32Fp8Fp8,
    VSwmmacF3216x16x64Bf16,
    VSwmmacF3216x16x64F16,
    VSwmmacI3216x16x128Iu8,
    VSwmmacI3216x16x32Iu4,
    VSwmmacI3216x16x32Iu8,
    VSwmmacI3216x16x64Iu4,
    VTanhBf16,
    VTanhF16,
    VTanhF32,
    VTrigPreopF64,
    VTruncF16,
    VTruncF32,
    VTruncF64,
    VWmmaBf1616x16x16Bf16,
    VWmmaBf1616x16x32Bf16,
    VWmmaBf16f3216x16x32Bf16,
    VWmmaF1616x16x128Bf8Bf8,
    VWmmaF1616x16x128Bf8Fp8,
    VWmmaF1616x16x128Fp8Bf8,
    VWmmaF1616x16x128Fp8Fp8,
    VWmmaF1616x16x16F16,
    VWmmaF1616x16x32F16,
    VWmmaF1616x16x64Bf8Bf8,
    VWmmaF1616x16x64Bf8Fp8,
    VWmmaF1616x16x64Fp8Bf8,
    VWmmaF1616x16x64Fp8Fp8,
    VWmmaF3216x16x128Bf8Bf8,
    VWmmaF3216x16x128Bf8Fp8,
    VWmmaF3216x16x128F8f6f4,
    VWmmaF3216x16x128Fp8Bf8,
    VWmmaF3216x16x128Fp8Fp8,
    VWmmaF3216x16x16Bf16,
    VWmmaF3216x16x16Bf8Bf8,
    VWmmaF3216x16x16Bf8Fp8,
    VWmmaF3216x16x16F16,
    VWmmaF3216x16x16Fp8Bf8,
    VWmmaF3216x16x16Fp8Fp8,
    VWmmaF3216x16x32Bf16,
    VWmmaF3216x16x32F16,
    VWmmaF3216x16x4F32,
    VWmmaF3216x16x64Bf8Bf8,
    VWmmaF3216x16x64Bf8Fp8,
    VWmmaF3216x16x64Fp8Bf8,
    VWmmaF3216x16x64Fp8Fp8,
    VWmmaF3232x16x128F4,
    VWmmaI3216x16x16Iu4,
    VWmmaI3216x16x16Iu8,
    VWmmaI3216x16x32Iu4,
    VWmmaI3216x16x64Iu8,
    VWmmaScale16F3216x16x128F8f6f4,
    VWmmaScale16F3232x16x128F4,
    VWmmaScaleF3216x16x128F8f6f4,
    VWmmaScaleF3232x16x128F4
  )
}
