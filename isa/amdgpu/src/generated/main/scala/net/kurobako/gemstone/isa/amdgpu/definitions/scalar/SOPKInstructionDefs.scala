// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object SOPKInstructionDefs {
  case object SAddkCoI32 extends InstructionDef(
        "s_addk_co_i32",
        decoder(form(2)((operands, mods) => Instruction.SAddkCoI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAddkI32 extends InstructionDef(
        "s_addk_i32",
        decoder(form(2)((operands, mods) => Instruction.SAddkI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
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
            "SOPK",
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
            "SOPK",
            14,
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
            "SOPK",
            14,
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
  case object SCallB64 extends InstructionDef(
        "s_call_b64",
        decoder(
          form(1)((operands, mods) => Instruction.SCallB64_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCallB64_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOPK",
            21,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOPK",
            21,
            Col(
              OperandInfo(None, "uint", 64, false, true, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCallI64 extends InstructionDef(
        "s_call_i64",
        decoder(form(2)((operands, mods) => Instruction.SCallI64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SCbranchIFork extends InstructionDef(
        "s_cbranch_i_fork",
        decoder(form(2)((operands, mods) => Instruction.SCbranchIFork(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, true, false, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, true, false, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, true, false, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, true, false, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            17,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOPK",
            17,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOPK",
            16,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOPK",
            16,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SCmovkI32 extends InstructionDef(
        "s_cmovk_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmovkI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            2,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            2,
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
            "SOPK",
            2,
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
            "SOPK",
            1,
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
            "SOPK",
            1,
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
  case object SCmpkEqI32 extends InstructionDef(
        "s_cmpk_eq_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkEqI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkEqI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
  case object SGetregB32 extends InstructionDef(
        "s_getreg_b32",
        decoder(
          form(1)((operands, mods) => Instruction.SGetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SGetregB32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.CDNA5,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.GFX6,
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
            Family.GFX7,
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
            Family.GFX8,
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
            Family.GFX9,
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
  case object SMovkI32 extends InstructionDef(
        "s_movk_i32",
        decoder(
          form(1)((operands, mods) => Instruction.SMovkI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SMovkI32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            0,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOPK",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOPK",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOPK",
            0,
            Col(
              OperandInfo(None, "uint", 32, false, true, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMulkI32 extends InstructionDef(
        "s_mulk_i32",
        decoder(form(2)((operands, mods) => Instruction.SMulkI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            16,
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
            "SOPK",
            16,
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
            "SOPK",
            15,
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
            "SOPK",
            15,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetregB32 extends InstructionDef(
        "s_setreg_b32",
        decoder(
          form(1)((operands, mods) => Instruction.SSetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SSetregB32_2(operands(0), operands(1), mods))
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            18,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            19,
            Col(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOPK",
            19,
            Col(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOPK",
            18,
            Col(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOPK",
            18,
            Col(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetregImm32B32 extends InstructionDef(
        "s_setreg_imm32_b32",
        decoder(form(2)((operands, mods) => Instruction.SSetregImm32B32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SOPK_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SOPK_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SOPK_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SOPK_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SOPK",
            21,
            Col(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SOPK",
            21,
            Col(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SOPK",
            20,
            Col(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SOPK",
            20,
            Col(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSubvectorLoopBegin extends InstructionDef(
        "s_subvector_loop_begin",
        decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopBegin(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSubvectorLoopEnd extends InstructionDef(
        "s_subvector_loop_end",
        decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopEnd(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SVersion extends InstructionDef(
        "s_version",
        decoder(form(1)((operands, mods) => Instruction.SVersion(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SOPK",
            1,
            Col(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcntExpcnt extends InstructionDef(
        "s_waitcnt_expcnt",
        decoder(form(2)((operands, mods) => Instruction.SWaitcntExpcnt(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcntLgkmcnt extends InstructionDef(
        "s_waitcnt_lgkmcnt",
        decoder(form(2)((operands, mods) => Instruction.SWaitcntLgkmcnt(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcntVmcnt extends InstructionDef(
        "s_waitcnt_vmcnt",
        decoder(form(2)((operands, mods) => Instruction.SWaitcntVmcnt(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcntVscnt extends InstructionDef(
        "s_waitcnt_vscnt",
        decoder(form(2)((operands, mods) => Instruction.SWaitcntVscnt(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SOPK",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SOPK",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SOPK",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SOPK",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SAddkCoI32,
    SAddkI32,
    SCallB64,
    SCallI64,
    SCbranchIFork,
    SCmovkI32,
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
    SGetregB32,
    SMovkI32,
    SMulkI32,
    SSetregB32,
    SSetregImm32B32,
    SSubvectorLoopBegin,
    SSubvectorLoopEnd,
    SVersion,
    SWaitcntExpcnt,
    SWaitcntLgkmcnt,
    SWaitcntVmcnt,
    SWaitcntVscnt
  )
}
