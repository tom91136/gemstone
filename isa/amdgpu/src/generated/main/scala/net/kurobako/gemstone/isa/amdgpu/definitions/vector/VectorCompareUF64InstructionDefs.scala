// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object VectorCompareUF64InstructionDefs {
  case object VCmpxUF64 extends InstructionDef(
        "v_cmpx_u_f64",
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3",
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            120,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            56,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            168,
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
            56,
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
            56,
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
            120,
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
            120,
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

  val definitions: Col[InstructionDef] = Col(VCmpxUF64)
}
