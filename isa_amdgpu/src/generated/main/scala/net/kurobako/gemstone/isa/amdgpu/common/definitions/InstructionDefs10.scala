// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

private[common] object InstructionDefs10 {
  case object SCmpGeF16 extends InstructionDef(
        "s_cmp_ge_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            86,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            86,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            86,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpGeF32 extends InstructionDef(
        "s_cmp_ge_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            70,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            70,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpGeI32 extends InstructionDef(
        "s_cmp_ge_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            3,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            3,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpGeU32 extends InstructionDef(
        "s_cmp_ge_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpGtF16 extends InstructionDef(
        "s_cmp_gt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            84,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            84,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            84,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpGtF32 extends InstructionDef(
        "s_cmp_gt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            68,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            68,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpGtI32 extends InstructionDef(
        "s_cmp_gt_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            2,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            2,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpGtU32 extends InstructionDef(
        "s_cmp_gt_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLeF16 extends InstructionDef(
        "s_cmp_le_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            83,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            83,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLeF32 extends InstructionDef(
        "s_cmp_le_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            67,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            67,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLeI32 extends InstructionDef(
        "s_cmp_le_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            5,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            5,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLeU32 extends InstructionDef(
        "s_cmp_le_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLgF16 extends InstructionDef(
        "s_cmp_lg_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLgF32 extends InstructionDef(
        "s_cmp_lg_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            69,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            69,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLgI32 extends InstructionDef(
        "s_cmp_lg_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            1,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            1,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            1,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            1,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            1,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            1,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLgU32 extends InstructionDef(
        "s_cmp_lg_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            7,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            7,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLgU64 extends InstructionDef(
        "s_cmp_lg_u64",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgU64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLtF16 extends InstructionDef(
        "s_cmp_lt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            81,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            81,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            81,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLtF32 extends InstructionDef(
        "s_cmp_lt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            65,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            65,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpLtI32 extends InstructionDef(
        "s_cmp_lt_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            4,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            4,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpLtU32 extends InstructionDef(
        "s_cmp_lt_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtU32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPC",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPC",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpNeqF16 extends InstructionDef(
        "s_cmp_neq_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNeqF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            93,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            93,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            93,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNeqF32 extends InstructionDef(
        "s_cmp_neq_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNeqF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            77,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            77,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            77,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNgeF16 extends InstructionDef(
        "s_cmp_nge_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgeF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            89,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            89,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            89,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNgeF32 extends InstructionDef(
        "s_cmp_nge_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgeF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            73,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            73,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            73,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNgtF16 extends InstructionDef(
        "s_cmp_ngt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgtF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            91,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            91,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            91,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNgtF32 extends InstructionDef(
        "s_cmp_ngt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgtF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            75,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            75,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNleF16 extends InstructionDef(
        "s_cmp_nle_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNleF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            92,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            92,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            92,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNleF32 extends InstructionDef(
        "s_cmp_nle_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNleF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            76,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            76,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            76,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNlgF16 extends InstructionDef(
        "s_cmp_nlg_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNlgF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            90,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            90,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            90,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNlgF32 extends InstructionDef(
        "s_cmp_nlg_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNlgF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            74,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            74,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            74,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNltF16 extends InstructionDef(
        "s_cmp_nlt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNltF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            94,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            94,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            94,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpNltF32 extends InstructionDef(
        "s_cmp_nlt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNltF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpOF16 extends InstructionDef(
        "s_cmp_o_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpOF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            87,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            87,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            87,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpOF32 extends InstructionDef(
        "s_cmp_o_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpOF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpUF16 extends InstructionDef(
        "s_cmp_u_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpUF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            88,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            88,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpUF32 extends InstructionDef(
        "s_cmp_u_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpUF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPC_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPC_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmpkEqI32 extends InstructionDef(
        "s_cmpk_eq_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkEqI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkEqI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            2,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkEqU32 extends InstructionDef(
        "s_cmpk_eq_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkEqU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkEqU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkGeI32 extends InstructionDef(
        "s_cmpk_ge_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGeI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGeI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkGeU32 extends InstructionDef(
        "s_cmpk_ge_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGeU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGeU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkGtI32 extends InstructionDef(
        "s_cmpk_gt_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGtI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGtI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            5,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkGtU32 extends InstructionDef(
        "s_cmpk_gt_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGtU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGtU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLeI32 extends InstructionDef(
        "s_cmpk_le_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLeI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLeI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLeU32 extends InstructionDef(
        "s_cmpk_le_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLeU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLeU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLgI32 extends InstructionDef(
        "s_cmpk_lg_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLgI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLgI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLgU32 extends InstructionDef(
        "s_cmpk_lg_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLgU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLgU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLtI32 extends InstructionDef(
        "s_cmpk_lt_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLtI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLtI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmpkLtU32 extends InstructionDef(
        "s_cmpk_lt_u32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLtU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLtU32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCodeEnd extends InstructionDef(
        "s_code_end",
        decoder(form(0)((_, mods) => Instruction.SCodeEnd(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            31,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCselectB32 extends InstructionDef(
        "s_cselect_b32",
        decoder(form(3)((operands, mods) => Instruction.SCselectB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCselectB64 extends InstructionDef(
        "s_cselect_b64",
        decoder(form(3)((operands, mods) => Instruction.SCselectB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            11,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            11,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            11,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            11,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCtzI32B32 extends InstructionDef(
        "s_ctz_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SCtzI32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCtzI32B64 extends InstructionDef(
        "s_ctz_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SCtzI32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtF16F32 extends InstructionDef(
        "s_cvt_f16_f32",
        decoder(form(2)((operands, mods) => Instruction.SCvtF16F32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            104,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtF32F16 extends InstructionDef(
        "s_cvt_f32_f16",
        decoder(form(2)((operands, mods) => Instruction.SCvtF32F16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            105,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtF32I32 extends InstructionDef(
        "s_cvt_f32_i32",
        decoder(form(2)((operands, mods) => Instruction.SCvtF32I32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            100,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtF32U32 extends InstructionDef(
        "s_cvt_f32_u32",
        decoder(form(2)((operands, mods) => Instruction.SCvtF32U32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            101,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtHiF32F16 extends InstructionDef(
        "s_cvt_hi_f32_f16",
        decoder(form(2)((operands, mods) => Instruction.SCvtHiF32F16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            106,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtI32F32 extends InstructionDef(
        "s_cvt_i32_f32",
        decoder(form(2)((operands, mods) => Instruction.SCvtI32F32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            102,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtPkRtzF16F32 extends InstructionDef(
        "s_cvt_pk_rtz_f16_f32",
        decoder(form(3)((operands, mods) => Instruction.SCvtPkRtzF16F32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            72,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCvtU32F32 extends InstructionDef(
        "s_cvt_u32_f32",
        decoder(form(2)((operands, mods) => Instruction.SCvtU32F32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            103,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SDcacheDiscard extends InstructionDef(
        "s_dcache_discard",
        decoder(form(2)((operands, mods) => Instruction.SDcacheDiscard(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            40,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheDiscardX2 extends InstructionDef(
        "s_dcache_discard_x2",
        decoder(form(2)((operands, mods) => Instruction.SDcacheDiscardX2(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            41,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            41,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            41,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            41,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            41,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheInv extends InstructionDef(
        "s_dcache_inv",
        decoder(form(0)((_, mods) => Instruction.SDcacheInv(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            32,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            32,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheInvVol extends InstructionDef(
        "s_dcache_inv_vol",
        decoder(form(0)((_, mods) => Instruction.SDcacheInvVol(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            29,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            34,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            34,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheWb extends InstructionDef(
        "s_dcache_wb",
        decoder(form(0)((_, mods) => Instruction.SDcacheWb(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            33,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            33,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheWbVol extends InstructionDef(
        "s_dcache_wb_vol",
        decoder(form(0)((_, mods) => Instruction.SDcacheWbVol(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            35,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            35,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDecperflevel extends InstructionDef(
        "s_decperflevel",
        decoder(form(1)((operands, mods) => Instruction.SDecperflevel(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            57,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            57,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            57,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            57,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            21,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            21,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            21,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            21,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDelayAlu extends InstructionDef(
        "s_delay_alu",
        decoder(form(1)((operands, mods) => Instruction.SDelayAlu(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            7,
            Col(
              OperandInfo(Some("SIMM16"), "DELAY", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            7,
            Col(
              OperandInfo(Some("SIMM16"), "DELAY", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            7,
            Col(
              OperandInfo(Some("SIMM16"), "DELAY", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            7,
            Col(
              OperandInfo(Some("SIMM16"), "DELAY", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SDenormMode extends InstructionDef(
        "s_denorm_mode",
        decoder(form(1)((operands, mods) => Instruction.SDenormMode(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            37,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            37,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SEndpgm extends InstructionDef(
        "s_endpgm",
        decoder(
          form(0)((_, mods) => Instruction.SEndpgm_0(mods)),
          form(1)((operands, mods) => Instruction.SEndpgm_1(operands(0), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            48,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            1,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            48,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            48,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            48,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            1,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            1,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            1,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            1,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SEndpgmOrderedPsDone extends InstructionDef(
        "s_endpgm_ordered_ps_done",
        decoder(form(0)((_, mods) => Instruction.SEndpgmOrderedPsDone(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            30,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            50,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            50,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            30,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SEndpgmSaved extends InstructionDef(
        "s_endpgm_saved",
        decoder(form(0)((_, mods) => Instruction.SEndpgmSaved(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            49,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            27,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            49,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            49,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            49,
            Col.empty,
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            27,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            27,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFf0I32B32 extends InstructionDef(
        "s_ff0_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SFf0I32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFf0I32B64 extends InstructionDef(
        "s_ff0_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SFf0I32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFf1I32B32 extends InstructionDef(
        "s_ff1_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SFf1I32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFf1I32B64 extends InstructionDef(
        "s_ff1_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SFf1I32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFlbitI32 extends InstructionDef(
        "s_flbit_i32",
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            23,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            23,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFlbitI32B32 extends InstructionDef(
        "s_flbit_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            21,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            21,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFlbitI32B64 extends InstructionDef(
        "s_flbit_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            19,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFlbitI32I64 extends InstructionDef(
        "s_flbit_i32_i64",
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32I64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            21,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            21,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SFloorF16 extends InstructionDef(
        "s_floor_f16",
        decoder(form(2)((operands, mods) => Instruction.SFloorF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            108,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SFloorF32 extends InstructionDef(
        "s_floor_f32",
        decoder(form(2)((operands, mods) => Instruction.SFloorF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            97,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SFmaakF32 extends InstructionDef(
        "s_fmaak_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.SFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SFmacF16 extends InstructionDef(
        "s_fmac_f16",
        decoder(form(3)((operands, mods) => Instruction.SFmacF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            78,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SFmacF32 extends InstructionDef(
        "s_fmac_f32",
        decoder(form(3)((operands, mods) => Instruction.SFmacF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SFmamkF32 extends InstructionDef(
        "s_fmamk_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.SFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SGetBarrierState extends InstructionDef(
        "s_get_barrier_state",
        decoder(form(2)((operands, mods) => Instruction.SGetBarrierState(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            80,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SGetPcI64 extends InstructionDef(
        "s_get_pc_i64",
        decoder(form(1)((operands, mods) => Instruction.SGetPcI64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SGetShaderCyclesU64 extends InstructionDef(
        "s_get_shader_cycles_u64",
        decoder(form(1)((operands, mods) => Instruction.SGetShaderCyclesU64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SGetWaveidInWorkgroup extends InstructionDef(
        "s_get_waveid_in_workgroup",
        decoder(form(1)((operands, mods) => Instruction.SGetWaveidInWorkgroup(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            42,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SGetpcB64 extends InstructionDef(
        "s_getpc_b64",
        decoder(form(1)((operands, mods) => Instruction.SGetpcB64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            28,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            28,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SGetregB32 extends InstructionDef(
        "s_getreg_b32",
        decoder(
          form(1)((operands, mods) => Instruction.SGetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SGetregB32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPK",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "HWREG", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            18,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            17,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SGl1Inv extends InstructionDef(
        "s_gl1_inv",
        decoder(form(0)((_, mods) => Instruction.SGl1Inv(mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            31,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            31,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SIcacheInv extends InstructionDef(
        "s_icache_inv",
        decoder(form(0)((_, mods) => Instruction.SIcacheInv(mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            60,
            Col.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            19,
            Col.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            60,
            Col.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            60,
            Col.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            60,
            Col.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            19,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            19,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            19,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            19,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SIncperflevel extends InstructionDef(
        "s_incperflevel",
        decoder(form(1)((operands, mods) => Instruction.SIncperflevel(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            56,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            56,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            56,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            56,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            20,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            20,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            20,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            20,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SInstPrefetch extends InstructionDef(
        "s_inst_prefetch",
        decoder(form(1)((operands, mods) => Instruction.SInstPrefetch(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            32,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            32,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadB128 extends InstructionDef(
        "s_load_b128",
        decoder(form(3)((operands, mods) => Instruction.SLoadB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadB256 extends InstructionDef(
        "s_load_b256",
        decoder(form(3)((operands, mods) => Instruction.SLoadB256(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SCmpGeF16,
    SCmpGeF32,
    SCmpGeI32,
    SCmpGeU32,
    SCmpGtF16,
    SCmpGtF32,
    SCmpGtI32,
    SCmpGtU32,
    SCmpLeF16,
    SCmpLeF32,
    SCmpLeI32,
    SCmpLeU32,
    SCmpLgF16,
    SCmpLgF32,
    SCmpLgI32,
    SCmpLgU32,
    SCmpLgU64,
    SCmpLtF16,
    SCmpLtF32,
    SCmpLtI32,
    SCmpLtU32,
    SCmpNeqF16,
    SCmpNeqF32,
    SCmpNgeF16,
    SCmpNgeF32,
    SCmpNgtF16,
    SCmpNgtF32,
    SCmpNleF16,
    SCmpNleF32,
    SCmpNlgF16,
    SCmpNlgF32,
    SCmpNltF16,
    SCmpNltF32,
    SCmpOF16,
    SCmpOF32,
    SCmpUF16,
    SCmpUF32,
    SCmpkEqI32,
    SCmpkEqU32,
    SCmpkGeI32,
    SCmpkGeU32,
    SCmpkGtI32,
    SCmpkGtU32,
    SCmpkLeI32,
    SCmpkLeU32,
    SCmpkLgI32,
    SCmpkLgU32,
    SCmpkLtI32,
    SCmpkLtU32,
    SCodeEnd,
    SCselectB32,
    SCselectB64,
    SCtzI32B32,
    SCtzI32B64,
    SCvtF16F32,
    SCvtF32F16,
    SCvtF32I32,
    SCvtF32U32,
    SCvtHiF32F16,
    SCvtI32F32,
    SCvtPkRtzF16F32,
    SCvtU32F32,
    SDcacheDiscard,
    SDcacheDiscardX2,
    SDcacheInv,
    SDcacheInvVol,
    SDcacheWb,
    SDcacheWbVol,
    SDecperflevel,
    SDelayAlu,
    SDenormMode,
    SEndpgm,
    SEndpgmOrderedPsDone,
    SEndpgmSaved,
    SFf0I32B32,
    SFf0I32B64,
    SFf1I32B32,
    SFf1I32B64,
    SFlbitI32,
    SFlbitI32B32,
    SFlbitI32B64,
    SFlbitI32I64,
    SFloorF16,
    SFloorF32,
    SFmaakF32,
    SFmacF16,
    SFmacF32,
    SFmamkF32,
    SGetBarrierState,
    SGetPcI64,
    SGetShaderCyclesU64,
    SGetWaveidInWorkgroup,
    SGetpcB64,
    SGetregB32,
    SGl1Inv,
    SIcacheInv,
    SIncperflevel,
    SInstPrefetch,
    SLoadB128,
    SLoadB256
  )
}
