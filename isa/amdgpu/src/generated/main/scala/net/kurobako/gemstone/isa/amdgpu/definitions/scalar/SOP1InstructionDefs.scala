// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object SOP1InstructionDefs {
  case object SAbsI32 extends InstructionDef(
        "s_abs_i32",
        decoder(form(2)((operands, mods) => Instruction.SAbsI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            52,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            52,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            48,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            48,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAddPcI64 extends InstructionDef(
        "s_add_pc_i64",
        decoder(form(1)((operands, mods) => Instruction.SAddPcI64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            75,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            75,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            75,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAllocVgpr extends InstructionDef(
        "s_alloc_vgpr",
        decoder(form(1)((operands, mods) => Instruction.SAllocVgpr(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            83,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            83,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            83,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot0SaveexecB32 extends InstructionDef(
        "s_and_not0_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot0SaveexecB64 extends InstructionDef(
        "s_and_not0_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot0WrexecB32 extends InstructionDef(
        "s_and_not0_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot0WrexecB64 extends InstructionDef(
        "s_and_not0_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1SaveexecB32 extends InstructionDef(
        "s_and_not1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1SaveexecB64 extends InstructionDef(
        "s_and_not1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1WrexecB32 extends InstructionDef(
        "s_and_not1_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1WrexecB64 extends InstructionDef(
        "s_and_not1_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndSaveexecB32 extends InstructionDef(
        "s_and_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            60,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            60,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            60,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            60,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndSaveexecB64 extends InstructionDef(
        "s_and_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            36,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            36,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            32,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            32,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAndn1SaveexecB32 extends InstructionDef(
        "s_andn1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn1SaveexecB64 extends InstructionDef(
        "s_andn1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            51,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAndn1WrexecB32 extends InstructionDef(
        "s_andn1_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            70,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            70,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            70,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn1WrexecB64 extends InstructionDef(
        "s_andn1_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            57,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            57,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            57,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            57,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            53,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAndn2SaveexecB32 extends InstructionDef(
        "s_andn2_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            63,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            63,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            63,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            63,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn2SaveexecB64 extends InstructionDef(
        "s_andn2_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            39,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            39,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            35,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            35,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAndn2WrexecB32 extends InstructionDef(
        "s_andn2_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            71,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            71,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn2WrexecB64 extends InstructionDef(
        "s_andn2_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            58,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            58,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            58,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            58,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            54,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBarrierInit extends InstructionDef(
        "s_barrier_init",
        decoder(form(1)((operands, mods) => Instruction.SBarrierInit(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            81,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBarrierJoin extends InstructionDef(
        "s_barrier_join",
        decoder(form(1)((operands, mods) => Instruction.SBarrierJoin(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            82,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBarrierSignal extends InstructionDef(
        "s_barrier_signal",
        decoder(form(1)((operands, mods) => Instruction.SBarrierSignal(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            78,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBarrierSignalIsfirst extends InstructionDef(
        "s_barrier_signal_isfirst",
        decoder(form(1)((operands, mods) => Instruction.SBarrierSignalIsfirst(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            79,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            79,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBcnt0I32B32 extends InstructionDef(
        "s_bcnt0_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            10,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBcnt0I32B64 extends InstructionDef(
        "s_bcnt0_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBcnt1I32B32 extends InstructionDef(
        "s_bcnt1_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            12,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBcnt1I32B64 extends InstructionDef(
        "s_bcnt1_i32_b64",
        decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            13,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBitreplicateB64B32 extends InstructionDef(
        "s_bitreplicate_b64_b32",
        decoder(form(2)((operands, mods) => Instruction.SBitreplicateB64B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            55,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            59,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            59,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            59,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            59,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            55,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBitset0B32 extends InstructionDef(
        "s_bitset0_b32",
        decoder(
          form(2)((operands, mods) => Instruction.SBitset0B32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset0B32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
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
            Family.CDNA5,
            "cdna5",
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
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
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
            Family.GFX11,
            "rdna3",
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
            Family.GFX11,
            "rdna3_5",
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
            Family.GFX11,
            "rdna3_5",
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
            Family.GFX12,
            "rdna4",
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
            Family.GFX12,
            "rdna4",
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
            Family.GFX6,
            "gfx6",
            "SOP1",
            27,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            27,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBitset0B64 extends InstructionDef(
        "s_bitset0_b64",
        decoder(
          form(2)((operands, mods) => Instruction.SBitset0B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset0B64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            28,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            28,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBitset1B32 extends InstructionDef(
        "s_bitset1_b32",
        decoder(
          form(2)((operands, mods) => Instruction.SBitset1B32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset1B32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
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
            Family.CDNA5,
            "cdna5",
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
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
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
            Family.GFX11,
            "rdna3",
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
            Family.GFX11,
            "rdna3_5",
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
            Family.GFX11,
            "rdna3_5",
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
            Family.GFX12,
            "rdna4",
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
            Family.GFX12,
            "rdna4",
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
            Family.GFX6,
            "gfx6",
            "SOP1",
            29,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            29,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBitset1B64 extends InstructionDef(
        "s_bitset1_b64",
        decoder(
          form(2)((operands, mods) => Instruction.SBitset1B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset1B64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            30,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            30,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            27,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            27,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBrevB32 extends InstructionDef(
        "s_brev_b32",
        decoder(form(2)((operands, mods) => Instruction.SBrevB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
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
            Family.CDNA1,
            "cdna1",
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
            Family.CDNA2,
            "cdna2",
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
            Family.CDNA2,
            "cdna2",
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
            Family.CDNA3,
            "cdna3",
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
            Family.CDNA3,
            "cdna3",
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
            Family.CDNA4,
            "cdna4",
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
            Family.CDNA4,
            "cdna4",
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
            Family.CDNA5,
            "cdna5",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            11,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBrevB64 extends InstructionDef(
        "s_brev_b64",
        decoder(form(2)((operands, mods) => Instruction.SBrevB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            12,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            12,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            9,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            9,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCbranchJoin extends InstructionDef(
        "s_cbranch_join",
        decoder(form(1)((operands, mods) => Instruction.SCbranchJoin(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("LITERAL"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("LITERAL"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("LITERAL"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("LITERAL"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            50,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            50,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            46,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            46,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCeilF16 extends InstructionDef(
        "s_ceil_f16",
        decoder(form(2)((operands, mods) => Instruction.SCeilF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            107,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCeilF32 extends InstructionDef(
        "s_ceil_f32",
        decoder(form(2)((operands, mods) => Instruction.SCeilF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            96,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SClsI32 extends InstructionDef(
        "s_cls_i32",
        decoder(form(2)((operands, mods) => Instruction.SClsI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SClsI32I64 extends InstructionDef(
        "s_cls_i32_i64",
        decoder(form(2)((operands, mods) => Instruction.SClsI32I64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SClzI32U32 extends InstructionDef(
        "s_clz_i32_u32",
        decoder(form(2)((operands, mods) => Instruction.SClzI32U32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SClzI32U64 extends InstructionDef(
        "s_clz_i32_u64",
        decoder(form(2)((operands, mods) => Instruction.SClzI32U64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCmovB32 extends InstructionDef(
        "s_cmov_b32",
        decoder(
          form(2)((operands, mods) => Instruction.SCmovB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SCmovB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            5,
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
            Family.GFX7,
            "gfx7",
            "SOP1",
            5,
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
            Family.GFX8,
            "gfx8",
            "SOP1",
            2,
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
            Family.GFX9,
            "gfx9",
            "SOP1",
            2,
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
  case object SCmovB64 extends InstructionDef(
        "s_cmov_b64",
        decoder(
          form(2)((operands, mods) => Instruction.SCmovB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SCmovB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            6,
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
            Family.GFX7,
            "gfx7",
            "SOP1",
            6,
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
            Family.GFX8,
            "gfx8",
            "SOP1",
            3,
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
            Family.GFX9,
            "gfx9",
            "SOP1",
            3,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
  case object SCvtU32F32 extends InstructionDef(
        "s_cvt_u32_f32",
        decoder(form(2)((operands, mods) => Instruction.SCvtU32F32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
  case object SFf0I32B32 extends InstructionDef(
        "s_ff0_i32_b32",
        decoder(form(2)((operands, mods) => Instruction.SFf0I32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX12,
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
  case object SGetBarrierState extends InstructionDef(
        "s_get_barrier_state",
        decoder(form(2)((operands, mods) => Instruction.SGetBarrierState(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
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
            Family.CDNA5,
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
            Family.CDNA5,
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
  case object SGetpcB64 extends InstructionDef(
        "s_getpc_b64",
        decoder(form(1)((operands, mods) => Instruction.SGetpcB64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
  case object SMovB32 extends InstructionDef(
        "s_mov_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            3,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovB64 extends InstructionDef(
        "s_mov_b64",
        decoder(form(2)((operands, mods) => Instruction.SMovB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            4,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            4,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            1,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            1,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovFedB32 extends InstructionDef(
        "s_mov_fed_b32",
        decoder(form(0)((_, mods) => Instruction.SMovFedB32(mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            53,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            53,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovreldB32 extends InstructionDef(
        "s_movreld_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovreldB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            48,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            48,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            44,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            44,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovreldB64 extends InstructionDef(
        "s_movreld_b64",
        decoder(form(2)((operands, mods) => Instruction.SMovreldB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            49,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            49,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            45,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            45,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovrelsB32 extends InstructionDef(
        "s_movrels_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovrelsB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            46,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            46,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            42,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            42,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovrelsB64 extends InstructionDef(
        "s_movrels_b64",
        decoder(form(2)((operands, mods) => Instruction.SMovrelsB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            47,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            47,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            43,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            43,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMovrelsd2B32 extends InstructionDef(
        "s_movrelsd_2_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovrelsd2B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            68,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SNandSaveexecB32 extends InstructionDef(
        "s_nand_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            65,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            65,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SNandSaveexecB64 extends InstructionDef(
        "s_nand_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            41,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            41,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            37,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            37,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNorSaveexecB32 extends InstructionDef(
        "s_nor_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            66,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SNorSaveexecB64 extends InstructionDef(
        "s_nor_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            42,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            42,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            38,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            38,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNotB32 extends InstructionDef(
        "s_not_b32",
        decoder(form(2)((operands, mods) => Instruction.SNotB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            4,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNotB64 extends InstructionDef(
        "s_not_b64",
        decoder(form(2)((operands, mods) => Instruction.SNotB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            8,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            8,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            5,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            5,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SOrNot0SaveexecB32 extends InstructionDef(
        "s_or_not0_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrNot0SaveexecB64 extends InstructionDef(
        "s_or_not0_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrNot1SaveexecB32 extends InstructionDef(
        "s_or_not1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrNot1SaveexecB64 extends InstructionDef(
        "s_or_not1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrSaveexecB32 extends InstructionDef(
        "s_or_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            61,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            61,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            61,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            61,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrSaveexecB64 extends InstructionDef(
        "s_or_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            35,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            37,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            37,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SOrn1SaveexecB32 extends InstructionDef(
        "s_orn1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            69,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            69,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            69,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrn1SaveexecB64 extends InstructionDef(
        "s_orn1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            56,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            56,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            56,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            56,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            52,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SOrn2SaveexecB32 extends InstructionDef(
        "s_orn2_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            64,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            64,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            64,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrn2SaveexecB64 extends InstructionDef(
        "s_orn2_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            40,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            40,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            36,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            36,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SQuadmaskB32 extends InstructionDef(
        "s_quadmask_b32",
        decoder(form(2)((operands, mods) => Instruction.SQuadmaskB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            40,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            44,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            44,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            40,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            40,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SQuadmaskB64 extends InstructionDef(
        "s_quadmask_b64",
        decoder(form(2)((operands, mods) => Instruction.SQuadmaskB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            41,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            45,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            45,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            41,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            41,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SRfeB64 extends InstructionDef(
        "s_rfe_b64",
        decoder(form(1)((operands, mods) => Instruction.SRfeB64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            31,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            34,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            34,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            31,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            31,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SRfeI64 extends InstructionDef(
        "s_rfe_i64",
        decoder(form(1)((operands, mods) => Instruction.SRfeI64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            74,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SRndneF16 extends InstructionDef(
        "s_rndne_f16",
        decoder(form(2)((operands, mods) => Instruction.SRndneF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            110,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SRndneF32 extends InstructionDef(
        "s_rndne_f32",
        decoder(form(2)((operands, mods) => Instruction.SRndneF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            99,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSendmsgRtnB32 extends InstructionDef(
        "s_sendmsg_rtn_b32",
        decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            76,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            76,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            76,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            76,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSendmsgRtnB64 extends InstructionDef(
        "s_sendmsg_rtn_b64",
        decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            77,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            77,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            77,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            77,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetGprIdxIdx extends InstructionDef(
        "s_set_gpr_idx_idx",
        decoder(form(1)((operands, mods) => Instruction.SSetGprIdxIdx(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            50,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            50,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            50,
            Col(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetPcI64 extends InstructionDef(
        "s_set_pc_i64",
        decoder(form(1)((operands, mods) => Instruction.SSetPcI64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetpcB64 extends InstructionDef(
        "s_setpc_b64",
        decoder(form(1)((operands, mods) => Instruction.SSetpcB64(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            32,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            72,
            Col(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            32,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            32,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            29,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            29,
            Col(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSextI32I16 extends InstructionDef(
        "s_sext_i32_i16",
        decoder(form(2)((operands, mods) => Instruction.SSextI32I16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
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
            Family.GFX9,
            "gfx9",
            "SOP1",
            23,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSextI32I8 extends InstructionDef(
        "s_sext_i32_i8",
        decoder(form(2)((operands, mods) => Instruction.SSextI32I8(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            25,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            25,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSleepVar extends InstructionDef(
        "s_sleep_var",
        decoder(form(1)((operands, mods) => Instruction.SSleepVar(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            88,
            Col(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            88,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            88,
            Col(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSwapPcI64 extends InstructionDef(
        "s_swap_pc_i64",
        decoder(form(2)((operands, mods) => Instruction.SSwapPcI64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSwappcB64 extends InstructionDef(
        "s_swappc_b64",
        decoder(form(2)((operands, mods) => Instruction.SSwappcB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            73,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            30,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            30,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object STruncF16 extends InstructionDef(
        "s_trunc_f16",
        decoder(form(2)((operands, mods) => Instruction.STruncF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            109,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object STruncF32 extends InstructionDef(
        "s_trunc_f32",
        decoder(form(2)((operands, mods) => Instruction.STruncF32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            98,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWakeupBarrier extends InstructionDef(
        "s_wakeup_barrier",
        decoder(form(1)((operands, mods) => Instruction.SWakeupBarrier(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            87,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWqmB32 extends InstructionDef(
        "s_wqm_b32",
        decoder(form(2)((operands, mods) => Instruction.SWqmB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SWqmB64 extends InstructionDef(
        "s_wqm_b64",
        decoder(form(2)((operands, mods) => Instruction.SWqmB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            10,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            10,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            7,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            7,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SXnorSaveexecB32 extends InstructionDef(
        "s_xnor_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            67,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            42,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SXnorSaveexecB64 extends InstructionDef(
        "s_xnor_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            39,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            43,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            43,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            43,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            39,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            39,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SXorSaveexecB32 extends InstructionDef(
        "s_xor_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            62,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            62,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            62,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SXorSaveexecB64 extends InstructionDef(
        "s_xor_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            34,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOP1",
            38,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOP1",
            38,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOP1",
            34,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOP1",
            34,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SAbsI32,
    SAddPcI64,
    SAllocVgpr,
    SAndNot0SaveexecB32,
    SAndNot0SaveexecB64,
    SAndNot0WrexecB32,
    SAndNot0WrexecB64,
    SAndNot1SaveexecB32,
    SAndNot1SaveexecB64,
    SAndNot1WrexecB32,
    SAndNot1WrexecB64,
    SAndSaveexecB32,
    SAndSaveexecB64,
    SAndn1SaveexecB32,
    SAndn1SaveexecB64,
    SAndn1WrexecB32,
    SAndn1WrexecB64,
    SAndn2SaveexecB32,
    SAndn2SaveexecB64,
    SAndn2WrexecB32,
    SAndn2WrexecB64,
    SBarrierInit,
    SBarrierJoin,
    SBarrierSignal,
    SBarrierSignalIsfirst,
    SBcnt0I32B32,
    SBcnt0I32B64,
    SBcnt1I32B32,
    SBcnt1I32B64,
    SBitreplicateB64B32,
    SBitset0B32,
    SBitset0B64,
    SBitset1B32,
    SBitset1B64,
    SBrevB32,
    SBrevB64,
    SCbranchJoin,
    SCeilF16,
    SCeilF32,
    SClsI32,
    SClsI32I64,
    SClzI32U32,
    SClzI32U64,
    SCmovB32,
    SCmovB64,
    SCtzI32B32,
    SCtzI32B64,
    SCvtF16F32,
    SCvtF32F16,
    SCvtF32I32,
    SCvtF32U32,
    SCvtHiF32F16,
    SCvtI32F32,
    SCvtU32F32,
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
    SGetBarrierState,
    SGetPcI64,
    SGetShaderCyclesU64,
    SGetpcB64,
    SMovB32,
    SMovB64,
    SMovFedB32,
    SMovreldB32,
    SMovreldB64,
    SMovrelsB32,
    SMovrelsB64,
    SMovrelsd2B32,
    SNandSaveexecB32,
    SNandSaveexecB64,
    SNorSaveexecB32,
    SNorSaveexecB64,
    SNotB32,
    SNotB64,
    SOrNot0SaveexecB32,
    SOrNot0SaveexecB64,
    SOrNot1SaveexecB32,
    SOrNot1SaveexecB64,
    SOrSaveexecB32,
    SOrSaveexecB64,
    SOrn1SaveexecB32,
    SOrn1SaveexecB64,
    SOrn2SaveexecB32,
    SOrn2SaveexecB64,
    SQuadmaskB32,
    SQuadmaskB64,
    SRfeB64,
    SRfeI64,
    SRndneF16,
    SRndneF32,
    SSendmsgRtnB32,
    SSendmsgRtnB64,
    SSetGprIdxIdx,
    SSetPcI64,
    SSetpcB64,
    SSextI32I16,
    SSextI32I8,
    SSleepVar,
    SSwapPcI64,
    SSwappcB64,
    STruncF16,
    STruncF32,
    SWakeupBarrier,
    SWqmB32,
    SWqmB64,
    SXnorSaveexecB32,
    SXnorSaveexecB64,
    SXorSaveexecB32,
    SXorSaveexecB64
  )
}
