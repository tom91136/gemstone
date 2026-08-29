// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object VectorCompareGeF32ToLeU16InstructionDefs {
  case object VCmpxGeF32 extends InstructionDef(
        "v_cmpx_ge_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            86,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            86,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            86,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            86,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            86,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            86,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            86,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            86,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            22,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            22,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            22,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            86,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeF64 extends InstructionDef(
        "v_cmpx_ge_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            118,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            118,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            118,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            118,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            118,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            54,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            54,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            54,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            54,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            166,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            166,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            54,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            54,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            118,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeI16 extends InstructionDef(
        "v_cmpx_ge_i16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            182,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            182,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            182,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            182,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            158,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            158,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeI32 extends InstructionDef(
        "v_cmpx_ge_i32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            150,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            150,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            198,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            198,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            150,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeI64 extends InstructionDef(
        "v_cmpx_ge_i64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            182,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            182,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            182,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeU16 extends InstructionDef(
        "v_cmpx_ge_u16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            190,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            190,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            190,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeU32 extends InstructionDef(
        "v_cmpx_ge_u32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            222,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            222,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            222,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            222,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            214,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            214,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            206,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            206,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            214,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGeU64 extends InstructionDef(
        "v_cmpx_ge_u64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            254,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            254,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            254,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            254,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            254,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            246,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            246,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            222,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            222,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            246,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            254,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtF16 extends InstructionDef(
        "v_cmpx_gt_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            132,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            132,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtF32 extends InstructionDef(
        "v_cmpx_gt_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            84,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            84,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            84,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            84,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            84,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            84,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            84,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            84,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            20,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            20,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            20,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            84,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtF64 extends InstructionDef(
        "v_cmpx_gt_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            116,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            116,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            116,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            116,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            116,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            52,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            164,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            164,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            52,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            116,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtI16 extends InstructionDef(
        "v_cmpx_gt_i16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            180,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            180,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            180,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            180,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            156,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            156,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtI32 extends InstructionDef(
        "v_cmpx_gt_i32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            148,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            148,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            196,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            196,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            148,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtI64 extends InstructionDef(
        "v_cmpx_gt_i64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            180,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            180,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            180,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtU16 extends InstructionDef(
        "v_cmpx_gt_u16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            188,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            188,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            188,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtU32 extends InstructionDef(
        "v_cmpx_gt_u32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            220,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            212,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            212,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            204,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            204,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            212,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxGtU64 extends InstructionDef(
        "v_cmpx_gt_u64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            252,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            252,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            252,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            252,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            252,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            244,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            244,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            220,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            220,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            244,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            252,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeF16 extends InstructionDef(
        "v_cmpx_le_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            219,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            219,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            131,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            131,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeF32 extends InstructionDef(
        "v_cmpx_le_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            83,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            83,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            83,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            83,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            83,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            83,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            83,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            83,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            19,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            19,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            19,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            83,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeF64 extends InstructionDef(
        "v_cmpx_le_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            115,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            115,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            115,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            115,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            115,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            51,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            163,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            163,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            51,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            115,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeI16 extends InstructionDef(
        "v_cmpx_le_i16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            179,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            179,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            179,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            179,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            155,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            155,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeI32 extends InstructionDef(
        "v_cmpx_le_i32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            211,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            211,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            211,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            211,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            147,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            147,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            195,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            195,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            147,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeI64 extends InstructionDef(
        "v_cmpx_le_i64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            243,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            243,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            243,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            243,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            243,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            179,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            179,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            211,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            211,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "VOPC",
            179,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            243,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCmpxLeU16 extends InstructionDef(
        "v_cmpx_le_u16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "VCC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_INST_LITERAL64",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP16",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPC_VOP_DPP8",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "VOPC_VOP_SDWA_SDST_ENC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP16",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPC_VOP_DPP8",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP16",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPC_VOP_DPP8",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            187,
            Col(
              OperandInfo(Some("VDST"), "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_INST_LITERAL",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP16",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPC_VOP_DPP8",
            187,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "VOPC",
            187,
            Col(
              OperandInfo(None, "lanemask", 1, false, true, true),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    VCmpxGeF32,
    VCmpxGeF64,
    VCmpxGeI16,
    VCmpxGeI32,
    VCmpxGeI64,
    VCmpxGeU16,
    VCmpxGeU32,
    VCmpxGeU64,
    VCmpxGtF16,
    VCmpxGtF32,
    VCmpxGtF64,
    VCmpxGtI16,
    VCmpxGtI32,
    VCmpxGtI64,
    VCmpxGtU16,
    VCmpxGtU32,
    VCmpxGtU64,
    VCmpxLeF16,
    VCmpxLeF32,
    VCmpxLeF64,
    VCmpxLeI16,
    VCmpxLeI32,
    VCmpxLeI64,
    VCmpxLeU16
  )
}
