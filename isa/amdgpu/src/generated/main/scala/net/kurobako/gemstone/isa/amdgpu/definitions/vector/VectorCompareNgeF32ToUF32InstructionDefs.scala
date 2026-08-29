// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object VectorCompareNgeF32ToUF32InstructionDefs {
  case object VCmpxNgeF32 extends InstructionDef(
        "v_cmpx_nge_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            89,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            25,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            153,
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
            25,
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
            25,
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
            89,
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
            89,
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
  case object VCmpxNgeF64 extends InstructionDef(
        "v_cmpx_nge_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            121,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            57,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            169,
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
            57,
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
            57,
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
            121,
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
            121,
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
  case object VCmpxNgtF16 extends InstructionDef(
        "v_cmpx_ngt_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            251,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            139,
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
            59,
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
            59,
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
  case object VCmpxNgtF32 extends InstructionDef(
        "v_cmpx_ngt_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            91,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            27,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            155,
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
            27,
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
            27,
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
            91,
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
            91,
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
  case object VCmpxNgtF64 extends InstructionDef(
        "v_cmpx_ngt_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            123,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            59,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            171,
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
            59,
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
            59,
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
            123,
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
            123,
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
  case object VCmpxNleF16 extends InstructionDef(
        "v_cmpx_nle_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            252,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            140,
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
            60,
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
            60,
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
  case object VCmpxNleF32 extends InstructionDef(
        "v_cmpx_nle_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            92,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            28,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            156,
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
            28,
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
            28,
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
            92,
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
            92,
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
  case object VCmpxNleF64 extends InstructionDef(
        "v_cmpx_nle_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            124,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            60,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            172,
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
            60,
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
            60,
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
            124,
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
            124,
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
  case object VCmpxNlgF16 extends InstructionDef(
        "v_cmpx_nlg_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            250,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            138,
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
            58,
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
            58,
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
  case object VCmpxNlgF32 extends InstructionDef(
        "v_cmpx_nlg_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            90,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            26,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            154,
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
            26,
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
            26,
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
            90,
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
            90,
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
  case object VCmpxNlgF64 extends InstructionDef(
        "v_cmpx_nlg_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            122,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            58,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            170,
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
            58,
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
            58,
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
            122,
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
            122,
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
  case object VCmpxNltF16 extends InstructionDef(
        "v_cmpx_nlt_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            254,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            142,
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
            62,
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
            62,
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
  case object VCmpxNltF32 extends InstructionDef(
        "v_cmpx_nlt_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            94,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            158,
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
            30,
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
            30,
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
            94,
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
            94,
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
  case object VCmpxNltF64 extends InstructionDef(
        "v_cmpx_nlt_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            126,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            174,
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
            62,
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
            62,
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
            126,
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
            126,
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
  case object VCmpxOF16 extends InstructionDef(
        "v_cmpx_o_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            135,
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
            55,
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
            55,
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
  case object VCmpxOF32 extends InstructionDef(
        "v_cmpx_o_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            23,
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
            23,
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
            87,
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
            87,
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
  case object VCmpxOF64 extends InstructionDef(
        "v_cmpx_o_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            119,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            167,
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
            55,
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
            55,
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
            119,
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
            119,
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
  case object VCmpxTF16 extends InstructionDef(
        "v_cmpx_t_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
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
            143,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTF32 extends InstructionDef(
        "v_cmpx_t_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
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
            159,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTF64 extends InstructionDef(
        "v_cmpx_t_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3",
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
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
            175,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTI16 extends InstructionDef(
        "v_cmpx_t_i16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTI32 extends InstructionDef(
        "v_cmpx_t_i32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            151,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
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
            199,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTI64 extends InstructionDef(
        "v_cmpx_t_i64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            183,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTU16 extends InstructionDef(
        "v_cmpx_t_u16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
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
            191,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTU32 extends InstructionDef(
        "v_cmpx_t_u32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            215,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
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
            207,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTU64 extends InstructionDef(
        "v_cmpx_t_u64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            247,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
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
            223,
            Col(
              OperandInfo(None, "EXEC", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCmpxTruF16 extends InstructionDef(
        "v_cmpx_tru_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            255,
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
            Family.GFX8,
            "gfx8",
            "VOPC",
            63,
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
            63,
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
  case object VCmpxTruF32 extends InstructionDef(
        "v_cmpx_tru_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            95,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
            Family.GFX6,
            "gfx6",
            "VOPC",
            31,
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
            31,
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
            95,
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
            95,
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
  case object VCmpxTruF64 extends InstructionDef(
        "v_cmpx_tru_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            127,
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
            Family.GFX10,
            "rdna1",
            "VOP3",
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            63,
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
            Family.GFX6,
            "gfx6",
            "VOPC",
            63,
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
            63,
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
            127,
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
            127,
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
  case object VCmpxTruI16 extends InstructionDef(
        "v_cmpx_tru_i16",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            183,
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
            183,
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
  case object VCmpxTruI32 extends InstructionDef(
        "v_cmpx_tru_i32",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            151,
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
            151,
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
            215,
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
            215,
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
  case object VCmpxTruI64 extends InstructionDef(
        "v_cmpx_tru_i64",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            183,
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
            183,
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
            247,
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
            247,
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
  case object VCmpxTruU16 extends InstructionDef(
        "v_cmpx_tru_u16",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX8,
            "gfx8",
            "VOPC",
            191,
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
            191,
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
  case object VCmpxTruU32 extends InstructionDef(
        "v_cmpx_tru_u32",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            215,
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
            215,
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
            223,
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
            223,
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
  case object VCmpxTruU64 extends InstructionDef(
        "v_cmpx_tru_u64",
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "VOPC",
            247,
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
            247,
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
            255,
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
            255,
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
  case object VCmpxUF16 extends InstructionDef(
        "v_cmpx_u_f16",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            248,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            136,
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
            56,
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
            56,
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
  case object VCmpxUF32 extends InstructionDef(
        "v_cmpx_u_f32",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            88,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            24,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            152,
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
            24,
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
            24,
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
            88,
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
            88,
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

  val definitions: Col[InstructionDef] = Col(
    VCmpxNgeF32,
    VCmpxNgeF64,
    VCmpxNgtF16,
    VCmpxNgtF32,
    VCmpxNgtF64,
    VCmpxNleF16,
    VCmpxNleF32,
    VCmpxNleF64,
    VCmpxNlgF16,
    VCmpxNlgF32,
    VCmpxNlgF64,
    VCmpxNltF16,
    VCmpxNltF32,
    VCmpxNltF64,
    VCmpxOF16,
    VCmpxOF32,
    VCmpxOF64,
    VCmpxTF16,
    VCmpxTF32,
    VCmpxTF64,
    VCmpxTI16,
    VCmpxTI32,
    VCmpxTI64,
    VCmpxTU16,
    VCmpxTU32,
    VCmpxTU64,
    VCmpxTruF16,
    VCmpxTruF32,
    VCmpxTruF64,
    VCmpxTruI16,
    VCmpxTruI32,
    VCmpxTruI64,
    VCmpxTruU16,
    VCmpxTruU32,
    VCmpxTruU64,
    VCmpxUF16,
    VCmpxUF32
  )
}
